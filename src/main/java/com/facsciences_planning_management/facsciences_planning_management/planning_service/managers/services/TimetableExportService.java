package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.CourseSchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.ExamSchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.SchedulingDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimetableDTO;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TimetableExportService {
	public enum ExportType {
		PDF, CSV
	}

	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
	private static final int MAX_BUFFER_SIZE = 1024 * 1024;
	private static final int INITIAL_BUFFER_SIZE = 64 * 1024;

	// Remove font cache to avoid PDF object conflicts
	private volatile byte[] cachedLogoData;

	public ByteArrayInputStream generateTimetablePdf(TimetableDTO timetable, String levelCode) throws IOException {
		boolean isExamTimetable = timetable.schedules().stream().anyMatch(ExamSchedulingDTO.class::isInstance);
		return isExamTimetable ? generateExamTimetablePdf(timetable, levelCode)
				: generateCourseTimetablePdf(timetable, levelCode);
	}

	public ByteArrayInputStream generateTimetableCsv(TimetableDTO timetable) {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(INITIAL_BUFFER_SIZE);
				PrintWriter writer = new PrintWriter(out)) {

			writer.println("DateOrDay,StartTime,EndTime,UE_Code,RoomCode,SessionType,PersonnelName");

			List<SchedulingDTO> sortedSchedules = timetable.schedules().stream()
					.sorted(Comparator.comparing(this::getScheduleSortKey)
							.thenComparing(s -> LocalTime.parse(s.startTime())))
					.collect(Collectors.toList());

			for (SchedulingDTO schedule : sortedSchedules) {
				String row = switch (schedule) {
					case CourseSchedulingDTO cs -> String.join(",",
							cs.day(), cs.startTime(), cs.endTime(),
							"\"" + cs.ueCode() + "\"", "\"" + cs.roomCode() + "\"",
							"COURSE", "\"" + cs.teacherName() + "\"");
					case ExamSchedulingDTO es -> String.join(",",
							LocalDateTime.parse(es.date()).toLocalDate().toString(),
							es.startTime(), es.endTime(),
							"\"" + es.ueCode() + "\"", "\"" + es.roomCode() + "\"",
							"EXAM", "\"" + es.proctorName() + "\"");
				};
				writer.println(row);
			}

			writer.flush();
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException("Failed to generate CSV", e);
		}
	}

	private ByteArrayInputStream generateCourseTimetablePdf(TimetableDTO timetable, String levelCode)
			throws IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(INITIAL_BUFFER_SIZE)) {

			if (estimateDocumentSize(timetable) > MAX_BUFFER_SIZE) {
				throw new IllegalStateException("Document size exceeds maximum allowed size");
			}

			try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(out))) {
				Document document = new Document(pdfDoc, PageSize.A4.rotate());
				pdfDoc.getWriter().setCompressionLevel(CompressionConstants.DEFAULT_COMPRESSION);

				// Create fresh fonts for this document
				PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

				addOptimizedHeader(document, "Course Timetable", timetable, levelCode, boldFont, regularFont);

				Map<DayOfWeek, Map<LocalTime, List<SchedulingDTO>>> groupedSchedules = processSchedulesInBatches(
						timetable);
				List<LocalTime> timeSlots = getUniqueTimeSlots(timetable);
				List<DayOfWeek> days = List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
						DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY);

				// Create table that fits on one page
				Table pdfTable = createSinglePageTable(new float[] { 1.5f, 3f, 3f, 3f, 3f, 3f, 3f });
				pdfTable.addHeaderCell(createHeaderCell("Time", boldFont));
				days.forEach(day -> pdfTable
						.addHeaderCell(createHeaderCell(day.getDisplayName(TextStyle.FULL, Locale.ENGLISH), boldFont)));

				addTableContent(pdfTable, timeSlots, days, groupedSchedules, timetable, boldFont, regularFont);

				document.add(pdfTable);
				document.close();
			}

			return new ByteArrayInputStream(out.toByteArray());
		}
	}

	private ByteArrayInputStream generateExamTimetablePdf(TimetableDTO timetable, String levelCode) throws IOException {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(INITIAL_BUFFER_SIZE)) {

			if (estimateDocumentSize(timetable) > MAX_BUFFER_SIZE) {
				throw new IllegalStateException("Document size exceeds maximum allowed size");
			}

			try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(out))) {
				Document document = new Document(pdfDoc, PageSize.A4);
				pdfDoc.getWriter().setCompressionLevel(CompressionConstants.DEFAULT_COMPRESSION);

				// Create fresh fonts for this document
				PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

				addOptimizedHeader(document, "Exam Timetable", timetable, levelCode, boldFont, regularFont);

				Map<LocalDate, List<SchedulingDTO>> groupedExams = processExamSchedules(timetable);

				for (Map.Entry<LocalDate, List<SchedulingDTO>> entry : groupedExams.entrySet()) {
					addDateHeader(document, entry.getKey(), boldFont);
					addExamTable(document, entry.getValue(), boldFont, regularFont);
				}

				document.close();
			}

			return new ByteArrayInputStream(out.toByteArray());
		}
	}

	private Map<DayOfWeek, Map<LocalTime, List<SchedulingDTO>>> processSchedulesInBatches(TimetableDTO timetable) {
		return timetable.schedules().stream()
				.filter(CourseSchedulingDTO.class::isInstance)
				.collect(Collectors.groupingBy(
						s -> DayOfWeek.valueOf(((CourseSchedulingDTO) s).day()),
						Collectors.groupingBy(
								s -> LocalTime.parse(s.startTime()),
								TreeMap::new,
								Collectors.toList())));
	}

	private List<LocalTime> getUniqueTimeSlots(TimetableDTO timetable) {
		return timetable.schedules().stream()
				.map(s -> LocalTime.parse(s.startTime()))
				.distinct()
				.sorted()
				.collect(Collectors.toList());
	}

	// Modified to fit on single page
	private Table createSinglePageTable(float[] columnWidths) {
		Table table = new Table(UnitValue.createPercentArray(columnWidths));
		table.setWidth(UnitValue.createPercentValue(100))
				.setMarginTop(15)
				.setKeepTogether(true)
				.setFontSize(8); // Smaller font for single page fit
		return table;
	}

	private void addTableContent(Table pdfTable, List<LocalTime> timeSlots,
			List<DayOfWeek> days,
			Map<DayOfWeek, Map<LocalTime, List<SchedulingDTO>>> groupedSchedules,
			TimetableDTO timetable, PdfFont boldFont, PdfFont regularFont) {

		for (LocalTime startTime : timeSlots) {
			LocalTime endTime = findEndTime(timetable, startTime);
			pdfTable.addCell(createTimeCell(startTime.format(TIME_FORMATTER) + " - " + endTime.format(TIME_FORMATTER),
					boldFont));

			for (DayOfWeek day : days) {
				List<SchedulingDTO> schedulesForSlot = groupedSchedules
						.getOrDefault(day, Collections.emptyMap())
						.getOrDefault(startTime, Collections.emptyList());

				Cell contentCell = createContentCell(schedulesForSlot, boldFont, regularFont);
				pdfTable.addCell(contentCell);
			}
		}
	}

	private Cell createContentCell(List<SchedulingDTO> schedules, PdfFont boldFont, PdfFont regularFont) {
		Cell cell = new Cell()
				.setPadding(3) // Reduced padding for single page
				.setBorder(new SolidBorder(new DeviceRgb(230, 230, 230), 1));

		if (!schedules.isEmpty()) {
			for (SchedulingDTO schedule : schedules) {
				cell.add(createScheduleParagraph(schedule, boldFont, regularFont));
			}
		}

		return cell;
	}

	private LocalTime findEndTime(TimetableDTO timetable, LocalTime startTime) {
		return timetable.schedules().stream()
				.filter(s -> LocalTime.parse(s.startTime()).equals(startTime))
				.findFirst()
				.map(s -> LocalTime.parse(s.endTime()))
				.orElse(startTime);
	}

	private Map<LocalDate, List<SchedulingDTO>> processExamSchedules(TimetableDTO timetable) {
		return timetable.schedules().stream()
				.filter(ExamSchedulingDTO.class::isInstance)
				.collect(Collectors.groupingBy(
						s -> LocalDate.parse(((ExamSchedulingDTO) s).date()),
						TreeMap::new,
						Collectors.toList()));
	}

	private void addDateHeader(Document document, LocalDate date, PdfFont boldFont) {
		document.add(new Paragraph(date.format(DATE_FORMATTER))
				.setFont(boldFont)
				.setFontSize(14)
				.setMarginTop(20)
				.setMarginBottom(10)
				.setBorderBottom(new SolidBorder(ColorConstants.BLACK, 1)));
	}

	private void addExamTable(Document document, List<SchedulingDTO> exams, PdfFont boldFont, PdfFont regularFont) {
		Table examTable = new Table(UnitValue.createPercentArray(new float[] { 2, 4, 3, 4 }));
		examTable.setWidth(UnitValue.createPercentValue(100));

		examTable.addHeaderCell(createHeaderCell("Time Slot", boldFont));
		examTable.addHeaderCell(createHeaderCell("Course (UE)", boldFont));
		examTable.addHeaderCell(createHeaderCell("Room(s)", boldFont));
		examTable.addHeaderCell(createHeaderCell("Proctor(s)", boldFont));

		exams.sort(Comparator.comparing(s -> LocalTime.parse(s.startTime())));

		for (SchedulingDTO exam : exams) {
			examTable.addCell(createCell(formatTimeSlot(exam), regularFont));
			examTable.addCell(createCell(exam.ueCode(), regularFont));
			examTable.addCell(createCell(exam.roomCode(), regularFont));

			String proctorName = exam instanceof ExamSchedulingDTO es ? es.proctorName() : "";
			examTable.addCell(createCell(proctorName, regularFont));
		}

		document.add(examTable);
	}

	private String formatTimeSlot(SchedulingDTO exam) {
		return LocalTime.parse(exam.startTime()).format(TIME_FORMATTER) + " - " +
				LocalTime.parse(exam.endTime()).format(TIME_FORMATTER);
	}

	private void addOptimizedHeader(Document document, String title, TimetableDTO timetable, String levelCode,
			PdfFont boldFont, PdfFont regularFont) {
		Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 1, 1, 1 }))
				.setWidth(UnitValue.createPercentValue(100))
				.setHeight(UnitValue.createPointValue(50)); // Reduced height for single page

		Cell leftCell = new Cell()
				.add(new Paragraph("UNIVERSITE DE YAOUNDE I\nFACULTE DES SCIENCES")
						.setFont(regularFont)
						.setFontSize(7)) // Smaller font
				.setBorder(null)
				.setVerticalAlignment(VerticalAlignment.MIDDLE);
		headerTable.addCell(leftCell);

		Cell logoCell = new Cell()
				.add(createFreshLogo())
				.setTextAlignment(TextAlignment.CENTER)
				.setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setBorder(null)
				.setPadding(0);
		headerTable.addCell(logoCell);

		Cell rightCell = new Cell()
				.add(new Paragraph("UNIVERSITY OF YAOUNDE I\nFACULTY OF SCIENCE")
						.setFont(regularFont)
						.setFontSize(7)) // Smaller font
				.setTextAlignment(TextAlignment.RIGHT)
				.setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setBorder(null);
		headerTable.addCell(rightCell);

		document.add(headerTable);

		document.add(new Paragraph(title)
				.setFont(boldFont)
				.setFontSize(16) // Smaller title
				.setTextAlignment(TextAlignment.CENTER)
				.setMarginTop(5));

		document.add(new Paragraph(levelCode)
				.setFont(boldFont)
				.setFontSize(12)
				.setTextAlignment(TextAlignment.CENTER));

		document.add(new Paragraph(String.format("Academic Year: %s | Semester: %s",
				timetable.academicYear(), timetable.semester().getLabel()))
				.setFont(regularFont)
				.setFontSize(9)
				.setTextAlignment(TextAlignment.CENTER)
				.setMarginBottom(5));
	}

	private byte[] createPlaceholderImage() {
		// Create a minimal PNG header for a 1x1 transparent pixel
		return new byte[] {
				(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
				0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
				0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
				0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte) 0xC4,
				(byte) 0x89, 0x00, 0x00, 0x00, 0x0B, 0x49, 0x44, 0x41,
				0x54, 0x78, (byte) 0xDA, 0x63, 0x00, 0x01, 0x00, 0x00,
				0x05, 0x00, 0x01, 0x0D, 0x0A, 0x2D, (byte) 0xB4,
				0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44,
				(byte) 0xAE, 0x42, 0x60, (byte) 0x82
		};
	}

	private long estimateDocumentSize(TimetableDTO timetable) {
		long baseSize = 50 * 1024;
		long scheduleSize = timetable.schedules().size() * 200;
		return baseSize + scheduleSize;
	}

	private Paragraph createScheduleParagraph(SchedulingDTO schedule, PdfFont boldFont, PdfFont regularFont) {
		Paragraph p = new Paragraph()
				.setMargin(0)
				.setPadding(0)
				.setMultipliedLeading(1.1f) // Tighter line spacing
				.setFontSize(7); // Smaller font for single page

		p.add(new Text(schedule.ueCode() + "\n")
				.setFont(boldFont)
				.setFontSize(8));

		switch (schedule) {
			case CourseSchedulingDTO cs -> {
				p.add(new Text(cs.roomCode() + " - ")
						.setFont(regularFont)
						.setFontSize(7)
						.setFontColor(new DeviceRgb(0, 153, 0)));
				p.add(new Text(cs.teacherName())
						.setFont(regularFont)
						.setFontSize(7)
						.setFontColor(ColorConstants.DARK_GRAY));
			}
			case ExamSchedulingDTO es -> {
				p.add(new Text(es.roomCode() + " - ")
						.setFont(regularFont)
						.setFontSize(7)
						.setFontColor(new DeviceRgb(204, 0, 0)));
				p.add(new Text(es.proctorName())
						.setFont(regularFont)
						.setFontSize(7)
						.setFontColor(ColorConstants.DARK_GRAY));
			}
		}

		return p;
	}

	private Cell createHeaderCell(String text, PdfFont boldFont) {
		return new Cell()
				.add(new Paragraph(text))
				.setFont(boldFont)
				.setFontSize(8) // Smaller header font
				.setBackgroundColor(new DeviceRgb(240, 240, 240))
				.setTextAlignment(TextAlignment.CENTER)
				.setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setPadding(4); // Reduced padding
	}

	private Cell createTimeCell(String text, PdfFont boldFont) {
		return createCell(text, boldFont)
				.setFont(boldFont)
				.setFontColor(ColorConstants.BLACK);
	}

	private Cell createCell(String text, PdfFont font) {
		return new Cell()
				.add(new Paragraph(text))
				.setFont(font)
				.setFontSize(8) // Smaller font
				.setPadding(3) // Reduced padding
				.setTextAlignment(TextAlignment.CENTER)
				.setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setBorder(new SolidBorder(new DeviceRgb(220, 220, 220), 1));
	}

	// Create a completely fresh Image object each time
	private Image createFreshLogo() {
		byte[] logoData = getCachedLogoData();
		try {
			// Create completely fresh ImageData and Image objects
			ImageData imageData = ImageDataFactory.create(logoData);
			return new Image(imageData)
					.setWidth(40) // Smaller logo for single page
					.setHeight(40)
					.setHorizontalAlignment(HorizontalAlignment.CENTER);
		} catch (Exception e) {
			// Create fresh placeholder
			ImageData placeholderData = ImageDataFactory.create(createPlaceholderImage());
			return new Image(placeholderData)
					.setWidth(40)
					.setHeight(40)
					.setHorizontalAlignment(HorizontalAlignment.CENTER);
		}
	}

	/**
	 * Cache only the raw image data, not any PDF objects
	 */
	private byte[] getCachedLogoData() {
		if (cachedLogoData == null) {
			synchronized (this) {
				if (cachedLogoData == null) {
					try {
						ClassPathResource logoResource = new ClassPathResource("images/uy1_logo.png");

						if (logoResource.exists()) {
							try (InputStream logoStream = logoResource.getInputStream()) {
								cachedLogoData = logoStream.readAllBytes();
							}
						} else {
							cachedLogoData = createPlaceholderImage();
						}
					} catch (Exception e) {
						System.err.println("Failed to load logo data: " + e.getMessage());
						cachedLogoData = createPlaceholderImage();
					}
				}
			}
		}
		return cachedLogoData;
	}

	private LocalDate getScheduleSortKey(SchedulingDTO schedule) {
		return switch (schedule) {
			case CourseSchedulingDTO cs -> LocalDate.MIN;
			case ExamSchedulingDTO es -> LocalDateTime.parse(es.date()).toLocalDate();
		};
	}
}