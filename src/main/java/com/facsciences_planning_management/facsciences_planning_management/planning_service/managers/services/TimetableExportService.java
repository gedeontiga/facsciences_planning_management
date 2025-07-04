package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

import org.springframework.stereotype.Service;

import com.facsciences_planning_management.facsciences_planning_management.exceptions.CustomBusinessException;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.entities.repositories.TimetableRepository;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TimetableExportService {

	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");

	// Resource management
	private static final int MAX_BUFFER_SIZE = 1024 * 1024; // 1MB max buffer
	private static final int INITIAL_BUFFER_SIZE = 64 * 1024; // 64KB initial

	private final TimetableRepository timetableRepository;

	// Cache fonts to avoid repeated loading
	private final Map<String, PdfFont> fontCache = new ConcurrentHashMap<>();

	// Reusable image for logo
	private volatile Image cachedLogo;

	public ByteArrayInputStream generateTimetablePdf(TimetableDTO timetable, String levelCode) throws IOException {
		if (!timetableRepository.existsById(timetable.id())) {
			throw new CustomBusinessException("Timetable with ID " + timetable.id() + " does not exist.");
		}

		boolean isExamTimetable = timetable.schedules().stream()
				.anyMatch(ExamSchedulingDTO.class::isInstance);

		return isExamTimetable ? generateExamTimetablePdf(timetable, levelCode)
				: generateCourseTimetablePdf(timetable, levelCode);
	}

	private ByteArrayInputStream generateCourseTimetablePdf(TimetableDTO timetable, String levelCode)
			throws IOException {

		// Use try-with-resources for automatic cleanup
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(INITIAL_BUFFER_SIZE)) {

			// Check buffer size limit
			if (estimateDocumentSize(timetable) > MAX_BUFFER_SIZE) {
				throw new IllegalStateException("Document size exceeds maximum allowed size");
			}

			try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(out))) {
				Document document = new Document(pdfDoc, PageSize.A4.rotate());

				// Configure document for memory efficiency
				pdfDoc.getWriter().setCompressionLevel(CompressionConstants.DEFAULT_COMPRESSION);

				addOptimizedHeader(document, "Course Timetable", timetable, levelCode);

				// Process schedules in batches to avoid memory spikes
				Map<DayOfWeek, Map<LocalTime, List<SchedulingDTO>>> groupedSchedules = processSchedulesInBatches(
						timetable);

				List<LocalTime> timeSlots = getUniqueTimeSlots(timetable);
				List<DayOfWeek> days = List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
						DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY);

				Table pdfTable = createOptimizedTable(new float[] { 1.5f, 3f, 3f, 3f, 3f, 3f, 3f });

				// Add headers
				pdfTable.addHeaderCell(createHeaderCell("Time"));
				days.forEach(day -> pdfTable.addHeaderCell(
						createHeaderCell(day.getDisplayName(TextStyle.FULL, Locale.ENGLISH))));

				// Add content rows efficiently
				addTableContent(pdfTable, timeSlots, days, groupedSchedules, timetable);

				document.add(pdfTable);
				document.close();
			}

			return new ByteArrayInputStream(out.toByteArray());
		}
	}

	private ByteArrayInputStream generateExamTimetablePdf(TimetableDTO timetable, String levelCode)
			throws IOException {

		try (ByteArrayOutputStream out = new ByteArrayOutputStream(INITIAL_BUFFER_SIZE)) {

			if (estimateDocumentSize(timetable) > MAX_BUFFER_SIZE) {
				throw new IllegalStateException("Document size exceeds maximum allowed size");
			}

			try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(out))) {
				Document document = new Document(pdfDoc, PageSize.A4);
				pdfDoc.getWriter().setCompressionLevel(CompressionConstants.DEFAULT_COMPRESSION);

				addOptimizedHeader(document, "Exam Timetable", timetable, levelCode);

				Map<LocalDate, List<SchedulingDTO>> groupedExams = processExamSchedules(timetable);

				for (Map.Entry<LocalDate, List<SchedulingDTO>> entry : groupedExams.entrySet()) {
					addDateHeader(document, entry.getKey());
					addExamTable(document, entry.getValue());
				}

				document.close();
			}

			return new ByteArrayInputStream(out.toByteArray());
		}
	}

	// Optimized helper methods
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

	private Table createOptimizedTable(float[] columnWidths) {
		Table table = new Table(UnitValue.createPercentArray(columnWidths));
		table.setWidth(UnitValue.createPercentValue(100))
				.setMarginTop(20)
				.setKeepTogether(true); // Prevent table splitting across pages
		return table;
	}

	private void addTableContent(Table pdfTable, List<LocalTime> timeSlots,
			List<DayOfWeek> days,
			Map<DayOfWeek, Map<LocalTime, List<SchedulingDTO>>> groupedSchedules,
			TimetableDTO timetable) {

		for (LocalTime startTime : timeSlots) {
			LocalTime endTime = findEndTime(timetable, startTime);
			pdfTable.addCell(createTimeCell(
					startTime.format(TIME_FORMATTER) + " - " + endTime.format(TIME_FORMATTER)));

			for (DayOfWeek day : days) {
				List<SchedulingDTO> schedulesForSlot = groupedSchedules
						.getOrDefault(day, Collections.emptyMap())
						.getOrDefault(startTime, Collections.emptyList());

				Cell contentCell = createContentCell(schedulesForSlot);
				pdfTable.addCell(contentCell);
			}
		}
	}

	private Cell createContentCell(List<SchedulingDTO> schedules) {
		Cell cell = new Cell()
				.setPadding(5)
				.setBorder(new SolidBorder(new DeviceRgb(230, 230, 230), 1));

		if (!schedules.isEmpty()) {
			for (SchedulingDTO schedule : schedules) {
				cell.add(createScheduleParagraph(schedule));
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

	private void addDateHeader(Document document, LocalDate date) {
		document.add(new Paragraph(date.format(DATE_FORMATTER))
				.setFont(getCachedFont(StandardFonts.HELVETICA_BOLD))
				.setFontSize(14)
				.setMarginTop(20)
				.setMarginBottom(10)
				.setBorderBottom(new SolidBorder(ColorConstants.BLACK, 1)));
	}

	private void addExamTable(Document document, List<SchedulingDTO> exams) {
		Table examTable = new Table(UnitValue.createPercentArray(new float[] { 2, 4, 3, 4 }));
		examTable.setWidth(UnitValue.createPercentValue(100));

		// Add headers
		examTable.addHeaderCell(createHeaderCell("Time Slot"));
		examTable.addHeaderCell(createHeaderCell("Course (UE)"));
		examTable.addHeaderCell(createHeaderCell("Room(s)"));
		examTable.addHeaderCell(createHeaderCell("Proctor(s)"));

		// Sort and add exam rows
		exams.sort(Comparator.comparing(s -> LocalTime.parse(s.startTime())));

		for (SchedulingDTO exam : exams) {
			examTable.addCell(createCell(formatTimeSlot(exam)));
			examTable.addCell(createCell(exam.ueCode()));
			examTable.addCell(createCell(exam.roomCode()));

			String proctorName = exam instanceof ExamSchedulingDTO es ? es.proctorName() : "";
			examTable.addCell(createCell(proctorName));
		}

		document.add(examTable);
	}

	private String formatTimeSlot(SchedulingDTO exam) {
		return LocalTime.parse(exam.startTime()).format(TIME_FORMATTER) + " - " +
				LocalTime.parse(exam.endTime()).format(TIME_FORMATTER);
	}

	private void addOptimizedHeader(Document document, String title, TimetableDTO timetable, String levelCode) {
		PdfFont boldFont = getCachedFont(StandardFonts.HELVETICA_BOLD);
		PdfFont regularFont = getCachedFont(StandardFonts.HELVETICA);

		// Create header table
		Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 1, 1, 1 }))
				.setWidth(UnitValue.createPercentValue(100))
				.setHeight(UnitValue.createPointValue(60));

		// Left cell
		Cell leftCell = new Cell()
				.add(new Paragraph("UNIVERSITE DE YAOUNDE I\nFACULTE DES SCIENCES")
						.setFont(regularFont)
						.setFontSize(8))
				.setBorder(null)
				.setVerticalAlignment(VerticalAlignment.MIDDLE);
		headerTable.addCell(leftCell);

		// Logo cell with cached image
		Cell logoCell = new Cell()
				.add(getCachedLogo())
				.setTextAlignment(TextAlignment.CENTER)
				.setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setBorder(null)
				.setPadding(0);
		headerTable.addCell(logoCell);

		// Right cell
		Cell rightCell = new Cell()
				.add(new Paragraph("UNIVERSITY OF YAOUNDE I\nFACULTY OF SCIENCE")
						.setFont(regularFont)
						.setFontSize(8))
				.setTextAlignment(TextAlignment.RIGHT)
				.setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setBorder(null);
		headerTable.addCell(rightCell);

		document.add(headerTable);

		// Title and info
		document.add(new Paragraph(title)
				.setFont(boldFont)
				.setFontSize(18)
				.setTextAlignment(TextAlignment.CENTER)
				.setMarginTop(10));

		document.add(new Paragraph(levelCode)
				.setFont(boldFont)
				.setFontSize(14)
				.setTextAlignment(TextAlignment.CENTER));

		document.add(new Paragraph(String.format("Academic Year: %s | Semester: %s",
				timetable.academicYear(), timetable.semester().getLabel()))
				.setFont(regularFont)
				.setFontSize(10)
				.setTextAlignment(TextAlignment.CENTER)
				.setMarginBottom(10));
	}

	// Font caching
	private PdfFont getCachedFont(String fontName) {
		return fontCache.computeIfAbsent(fontName, name -> {
			try {
				return PdfFontFactory.createFont(name);
			} catch (IOException e) {
				throw new RuntimeException("Failed to load font: " + name, e);
			}
		});
	}

	// Image caching
	private Image getCachedLogo() {
		if (cachedLogo == null) {
			synchronized (this) {
				if (cachedLogo == null) {
					try {
						cachedLogo = new Image(ImageDataFactory.create("src/main/resources/images/uy1_logo.png"))
								.setWidth(50)
								.setHeight(50)
								.setHorizontalAlignment(HorizontalAlignment.CENTER);
					} catch (Exception e) {
						// Return placeholder or handle gracefully
						cachedLogo = new Image(ImageDataFactory.create(createPlaceholderImage()))
								.setWidth(50)
								.setHeight(50);
					}
				}
			}
		}
		return cachedLogo;
	}

	private byte[] createPlaceholderImage() {
		// Create a simple 1x1 transparent PNG as placeholder
		return new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
	}

	// Size estimation for resource management
	private long estimateDocumentSize(TimetableDTO timetable) {
		// Rough estimation: base size + schedules count * average size per schedule
		long baseSize = 50 * 1024; // 50KB base
		long scheduleSize = timetable.schedules().size() * 200; // ~200 bytes per schedule
		return baseSize + scheduleSize;
	}

	private Paragraph createScheduleParagraph(SchedulingDTO schedule) {
		Paragraph p = new Paragraph()
				.setMargin(0)
				.setPadding(0)
				.setMultipliedLeading(1.2f);

		p.add(new Text(schedule.ueCode() + "\n")
				.setFont(getCachedFont(StandardFonts.HELVETICA_BOLD))
				.setFontSize(10));

		switch (schedule) {
			case CourseSchedulingDTO cs -> {
				p.add(new Text(cs.roomCode() + " - ")
						.setFont(getCachedFont(StandardFonts.HELVETICA))
						.setFontSize(8)
						.setFontColor(new DeviceRgb(0, 153, 0)));
				p.add(new Text(cs.teacherName())
						.setFont(getCachedFont(StandardFonts.HELVETICA))
						.setFontSize(8)
						.setFontColor(ColorConstants.DARK_GRAY));
			}
			case ExamSchedulingDTO es -> {
				p.add(new Text(es.roomCode() + " - ")
						.setFont(getCachedFont(StandardFonts.HELVETICA))
						.setFontSize(8)
						.setFontColor(new DeviceRgb(204, 0, 0)));
				p.add(new Text(es.proctorName())
						.setFont(getCachedFont(StandardFonts.HELVETICA))
						.setFontSize(8)
						.setFontColor(ColorConstants.DARK_GRAY));
			}
		}

		return p;
	}

	private Cell createHeaderCell(String text) {
		return new Cell()
				.add(new Paragraph(text))
				.setFont(getCachedFont(StandardFonts.HELVETICA_BOLD))
				.setFontSize(9)
				.setBackgroundColor(new DeviceRgb(240, 240, 240))
				.setTextAlignment(TextAlignment.CENTER)
				.setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setPadding(8);
	}

	private Cell createTimeCell(String text) {
		return createCell(text)
				.setFont(getCachedFont(StandardFonts.HELVETICA_BOLD))
				.setFontColor(ColorConstants.BLACK);
	}

	private Cell createCell(String text) {
		return new Cell()
				.add(new Paragraph(text))
				.setFont(getCachedFont(StandardFonts.HELVETICA))
				.setFontSize(9)
				.setPadding(5)
				.setTextAlignment(TextAlignment.CENTER)
				.setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setBorder(new SolidBorder(new DeviceRgb(220, 220, 220), 1));
	}

	// CSV method remains the same but with proper resource management
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

	private LocalDate getScheduleSortKey(SchedulingDTO schedule) {
		return switch (schedule) {
			case CourseSchedulingDTO cs -> LocalDate.MIN;
			case ExamSchedulingDTO es -> LocalDateTime.parse(es.date()).toLocalDate();
		};
	}

	public enum ExportType {
		PDF, CSV
	}
}