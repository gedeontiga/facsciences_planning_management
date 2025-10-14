package com.facsciencesuy1.planning_management.planning_service.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.facsciencesuy1.planning_management.dtos.CourseSchedulingDTO;
import com.facsciencesuy1.planning_management.dtos.ExamSchedulingDTO;
import com.facsciencesuy1.planning_management.dtos.SchedulingDTO;
import com.facsciencesuy1.planning_management.dtos.TimetableDTO;

import lombok.RequiredArgsConstructor;

import java.awt.Color;
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

	// Professional color scheme
	private static final Color HEADER_BG = new Color(41, 128, 185); // Professional blue
	private static final Color HEADER_TEXT = Color.WHITE;
	private static final Color ALT_ROW_BG = new Color(245, 247, 250); // Light gray-blue
	private static final Color BORDER_COLOR = new Color(220, 223, 230);
	private static final Color COURSE_COLOR = new Color(46, 204, 113); // Green
	private static final Color EXAM_COLOR = new Color(231, 76, 60); // Red
	private static final Color TEXT_PRIMARY = new Color(44, 62, 80);
	private static final Color TEXT_SECONDARY = new Color(127, 140, 141);

	private volatile byte[] cachedLogoData;

	public ByteArrayInputStream generateTimetablePdf(TimetableDTO timetable, String levelCode) throws IOException {
		boolean isExamTimetable = timetable.schedules().stream().anyMatch(ExamSchedulingDTO.class::isInstance);
		return isExamTimetable ? generateExamTimetablePdf(timetable, levelCode)
				: generateCourseTimetablePdf(timetable, levelCode);
	}

	public ByteArrayInputStream generateTimetableCsv(TimetableDTO timetable) {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
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
		try (PDDocument document = new PDDocument();
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			PDPage page = new PDPage(PDRectangle.A4);
			page.setRotation(90); // Landscape
			document.addPage(page);

			try (PDPageContentStream content = new PDPageContentStream(document, page)) {
				// Get rotated dimensions
				float pageWidth = page.getMediaBox().getHeight();
				float pageHeight = page.getMediaBox().getWidth();
				float margin = 40;

				float yPosition = pageHeight - margin;

				// Draw header
				yPosition = drawHeader(content, document, page, "Course Timetable", timetable,
						levelCode, pageWidth, yPosition);

				yPosition -= 30;

				// Draw timetable
				drawCourseTimetableTable(content, timetable, margin, yPosition, pageWidth - 2 * margin);
			}

			document.save(out);
			return new ByteArrayInputStream(out.toByteArray());
		}
	}

	private ByteArrayInputStream generateExamTimetablePdf(TimetableDTO timetable, String levelCode) throws IOException {
		try (PDDocument document = new PDDocument();
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			PDPage page = new PDPage(PDRectangle.A4);
			document.addPage(page);

			try (PDPageContentStream content = new PDPageContentStream(document, page)) {
				float pageWidth = page.getMediaBox().getWidth();
				float pageHeight = page.getMediaBox().getHeight();
				float margin = 50;

				float yPosition = pageHeight - margin;

				// Draw header
				yPosition = drawHeader(content, document, page, "Exam Timetable", timetable,
						levelCode, pageWidth, yPosition);

				yPosition -= 40;

				// Group exams by date
				Map<LocalDate, List<SchedulingDTO>> groupedExams = processExamSchedules(timetable);

				for (Map.Entry<LocalDate, List<SchedulingDTO>> entry : groupedExams.entrySet()) {
					// Check if we need a new page
					if (yPosition < 150) {
						content.close();
						page = new PDPage(PDRectangle.A4);
						document.addPage(page);
						PDPageContentStream newContent = new PDPageContentStream(document, page);
						yPosition = pageHeight - margin;
						yPosition = drawExamDateSection(newContent, entry.getKey(), entry.getValue(),
								margin, yPosition, pageWidth - 2 * margin);
						newContent.close();
					} else {
						yPosition = drawExamDateSection(content, entry.getKey(), entry.getValue(),
								margin, yPosition, pageWidth - 2 * margin);
					}
					yPosition -= 30;
				}
			}

			document.save(out);
			return new ByteArrayInputStream(out.toByteArray());
		}
	}

	private float drawHeader(PDPageContentStream content, PDDocument document, PDPage page,
			String title, TimetableDTO timetable, String levelCode,
			float pageWidth, float yPosition) throws IOException {

		float margin = page.getRotation() == 90 ? 40 : 50;
		float centerX = pageWidth / 2;

		// Draw logo
		try {
			PDImageXObject logo = loadLogo(document);
			float logoSize = 50;
			content.drawImage(logo, centerX - logoSize / 2, yPosition - logoSize, logoSize, logoSize);
			yPosition -= logoSize + 5;
		} catch (Exception e) {
			yPosition -= 10;
		}

		// University name
		content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
		content.setNonStrokingColor(TEXT_PRIMARY);
		drawCenteredText(content, "UNIVERSITÉ DE YAOUNDÉ I", centerX, yPosition);
		yPosition -= 12;

		content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
		drawCenteredText(content, "FACULTÉ DES SCIENCES", centerX, yPosition);
		yPosition -= 25;

		// Title with background
		content.setNonStrokingColor(HEADER_BG);
		float titleBoxHeight = 35;
		content.addRect(margin, yPosition - titleBoxHeight + 5, pageWidth - 2 * margin, titleBoxHeight);
		content.fill();

		content.setNonStrokingColor(HEADER_TEXT);
		content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
		drawCenteredText(content, title, centerX, yPosition - 20);
		yPosition -= titleBoxHeight;

		// Level code
		content.setNonStrokingColor(TEXT_PRIMARY);
		content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
		drawCenteredText(content, levelCode, centerX, yPosition);
		yPosition -= 18;

		// Academic info
		content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
		content.setNonStrokingColor(TEXT_SECONDARY);
		String academicInfo = String.format("Academic Year: %s | Semester: %s",
				timetable.academicYear(), timetable.semester().getLabel());
		drawCenteredText(content, academicInfo, centerX, yPosition);
		yPosition -= 15;

		return yPosition;
	}

	private void drawCourseTimetableTable(PDPageContentStream content, TimetableDTO timetable,
			float x, float y, float tableWidth) throws IOException {

		List<DayOfWeek> days = List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
				DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY);
		List<LocalTime> timeSlots = getUniqueTimeSlots(timetable);
		Map<DayOfWeek, Map<LocalTime, List<SchedulingDTO>>> groupedSchedules = processSchedulesInBatches(timetable);

		float[] colWidths = new float[8];
		colWidths[0] = tableWidth * 0.10f; // Time column
		for (int i = 1; i < 7; i++) {
			colWidths[i] = tableWidth * 0.15f; // Day columns
		}

		float headerHeight = 25;

		// Draw header row with gradient effect
		content.setNonStrokingColor(HEADER_BG);
		content.addRect(x, y - headerHeight, tableWidth, headerHeight);
		content.fill();

		content.setNonStrokingColor(HEADER_TEXT);
		content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 9);

		float currentX = x;
		drawWrappedText(content, "Time", currentX + 4, y - 14, colWidths[0] - 8, 10, 9, true);
		currentX += colWidths[0];

		for (int i = 0; i < days.size(); i++) {
			String dayName = days.get(i).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
			drawWrappedText(content, dayName, currentX + 4, y - 14, colWidths[i + 1] - 8, 10, 9, true);
			currentX += colWidths[i + 1];
		}

		y -= headerHeight;

		// Draw data rows with dynamic heights
		boolean alternateRow = false;
		for (LocalTime startTime : timeSlots) {
			LocalTime endTime = findEndTime(timetable, startTime);
			String timeSlot = startTime.format(TIME_FORMATTER) + "\n" + endTime.format(TIME_FORMATTER);

			// Calculate row height based on content
			float maxContentHeight = 40; // minimum height
			for (DayOfWeek day : days) {
				List<SchedulingDTO> schedules = groupedSchedules
						.getOrDefault(day, Collections.emptyMap())
						.getOrDefault(startTime, Collections.emptyList());

				float cellHeight = calculateCellHeight(schedules, colWidths[1]);
				maxContentHeight = Math.max(maxContentHeight, cellHeight);
			}

			float rowHeight = Math.min(maxContentHeight + 10, 120); // cap at 120

			// Alternate row background
			if (alternateRow) {
				content.setNonStrokingColor(ALT_ROW_BG);
				content.addRect(x, y - rowHeight, tableWidth, rowHeight);
				content.fill();
			}
			alternateRow = !alternateRow;

			// Draw borders
			content.setStrokingColor(BORDER_COLOR);
			content.setLineWidth(0.5f);
			content.addRect(x, y - rowHeight, tableWidth, rowHeight);
			content.stroke();

			currentX = x;

			// Time cell
			content.setNonStrokingColor(TEXT_PRIMARY);
			content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 8);
			drawMultilineCell(content, timeSlot, currentX, y - 10, colWidths[0], rowHeight);
			currentX += colWidths[0];

			// Day cells
			for (int i = 0; i < days.size(); i++) {
				DayOfWeek day = days.get(i);
				List<SchedulingDTO> schedules = groupedSchedules
						.getOrDefault(day, Collections.emptyMap())
						.getOrDefault(startTime, Collections.emptyList());

				drawScheduleCell(content, schedules, currentX, y - 10, colWidths[i + 1], rowHeight);
				currentX += colWidths[i + 1];

				// Vertical borders
				content.setStrokingColor(BORDER_COLOR);
				content.moveTo(currentX, y);
				content.lineTo(currentX, y - rowHeight);
				content.stroke();
			}

			y -= rowHeight;
		}
	}

	/**
	 * Calculate the height needed for a schedule cell
	 * 
	 * @throws IOException
	 */
	private float calculateCellHeight(List<SchedulingDTO> schedules, float cellWidth) throws IOException {
		if (schedules.isEmpty())
			return 30;

		float totalHeight = 8; // initial padding
		float padding = 8;
		float lineHeight = 9;

		for (SchedulingDTO schedule : schedules) {
			// Course code
			totalHeight += calculateWrappedTextHeight(schedule.ueCode(),
					cellWidth - padding, lineHeight, 8, true);
			totalHeight += 2;

			// Room and personnel
			switch (schedule) {
				case CourseSchedulingDTO cs -> {
					totalHeight += calculateWrappedTextHeight(cs.roomCode(),
							cellWidth - padding, lineHeight, 7, false);
					totalHeight += calculateWrappedTextHeight(cs.teacherName(),
							cellWidth - padding, lineHeight, 7, false);
				}
				case ExamSchedulingDTO es -> {
					totalHeight += calculateWrappedTextHeight(es.roomCode(),
							cellWidth - padding, lineHeight, 7, false);
					totalHeight += calculateWrappedTextHeight(es.proctorName(),
							cellWidth - padding, lineHeight, 7, false);
				}
			}
			totalHeight += lineHeight + 4; // spacing between schedules
		}

		return totalHeight;
	}

	private void drawScheduleCell(PDPageContentStream content, List<SchedulingDTO> schedules,
			float x, float y, float width, float height) throws IOException {
		if (schedules.isEmpty())
			return;

		float padding = 4;
		float lineHeight = 9;
		float currentY = y;

		for (SchedulingDTO schedule : schedules) {
			// Course code in bold
			content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 8);
			content.setNonStrokingColor(TEXT_PRIMARY);
			currentY = drawWrappedText(content, schedule.ueCode(), x + padding, currentY,
					width - 2 * padding, lineHeight, 8, true);
			currentY -= 2;

			// Room and teacher
			content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 7);
			switch (schedule) {
				case CourseSchedulingDTO cs -> {
					content.setNonStrokingColor(COURSE_COLOR);
					currentY = drawWrappedText(content, cs.roomCode(), x + padding, currentY,
							width - 2 * padding, lineHeight, 7, false);

					content.setNonStrokingColor(TEXT_SECONDARY);
					currentY = drawWrappedText(content, cs.teacherName(), x + padding, currentY,
							width - 2 * padding, lineHeight, 7, false);
				}
				case ExamSchedulingDTO es -> {
					content.setNonStrokingColor(EXAM_COLOR);
					currentY = drawWrappedText(content, es.roomCode(), x + padding, currentY,
							width - 2 * padding, lineHeight, 7, false);

					content.setNonStrokingColor(TEXT_SECONDARY);
					currentY = drawWrappedText(content, es.proctorName(), x + padding, currentY,
							width - 2 * padding, lineHeight, 7, false);
				}
			}
			currentY -= lineHeight + 2;
		}
	}

	private float drawExamDateSection(PDPageContentStream content, LocalDate date,
			List<SchedulingDTO> exams, float x, float y,
			float tableWidth) throws IOException {

		// Date header with accent bar
		content.setNonStrokingColor(HEADER_BG);
		content.addRect(x, y - 25, 4, 25);
		content.fill();

		content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
		content.setNonStrokingColor(TEXT_PRIMARY);
		content.beginText();
		content.newLineAtOffset(x + 10, y - 17);
		content.showText(date.format(DATE_FORMATTER));
		content.endText();

		y -= 35;

		// Table header
		float[] colWidths = { tableWidth * 0.20f, tableWidth * 0.35f,
				tableWidth * 0.20f, tableWidth * 0.25f };
		String[] headers = { "Time Slot", "Course (UE)", "Room(s)", "Proctor(s)" };

		content.setNonStrokingColor(HEADER_BG);
		content.addRect(x, y - 20, tableWidth, 20);
		content.fill();

		content.setNonStrokingColor(HEADER_TEXT);
		content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 9);

		float currentX = x;
		for (int i = 0; i < headers.length; i++) {
			drawWrappedText(content, headers[i], currentX + 4, y - 14, colWidths[i] - 8, 10, 9, true);
			currentX += colWidths[i];
		}

		y -= 20;

		// Table rows with dynamic heights
		exams.sort(Comparator.comparing(s -> LocalTime.parse(s.startTime())));
		boolean alternateRow = false;

		for (SchedulingDTO exam : exams) {
			String timeSlot = LocalTime.parse(exam.startTime()).format(TIME_FORMATTER) +
					" - " + LocalTime.parse(exam.endTime()).format(TIME_FORMATTER);
			String proctorName = exam instanceof ExamSchedulingDTO es ? es.proctorName() : "";

			// Calculate row height based on content
			float maxHeight = Math.max(
					calculateWrappedTextHeight(exam.ueCode(), colWidths[1] - 8, 9, 8, false),
					Math.max(
							calculateWrappedTextHeight(exam.roomCode(), colWidths[2] - 8, 9, 8, false),
							calculateWrappedTextHeight(proctorName, colWidths[3] - 8, 9, 8, false)));
			float rowHeight = Math.max(20, maxHeight + 10);

			if (alternateRow) {
				content.setNonStrokingColor(ALT_ROW_BG);
				content.addRect(x, y - rowHeight, tableWidth, rowHeight);
				content.fill();
			}
			alternateRow = !alternateRow;

			content.setStrokingColor(BORDER_COLOR);
			content.setLineWidth(0.5f);
			content.addRect(x, y - rowHeight, tableWidth, rowHeight);
			content.stroke();

			content.setNonStrokingColor(TEXT_PRIMARY);
			content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 8);

			currentX = x;

			drawWrappedText(content, timeSlot, currentX + 4, y - 10, colWidths[0] - 8, 9, 8, false);
			currentX += colWidths[0];

			drawWrappedText(content, exam.ueCode(), currentX + 4, y - 10, colWidths[1] - 8, 9, 8, false);
			currentX += colWidths[1];

			drawWrappedText(content, exam.roomCode(), currentX + 4, y - 10, colWidths[2] - 8, 9, 8, false);
			currentX += colWidths[2];

			drawWrappedText(content, proctorName, currentX + 4, y - 10, colWidths[3] - 8, 9, 8, false);

			y -= rowHeight;
		}

		return y;
	}

	private void drawCenteredText(PDPageContentStream content, String text, float centerX, float y) throws IOException {
		float textWidth = new PDType1Font(Standard14Fonts.FontName.HELVETICA).getStringWidth(text) / 1000 * 10;
		content.beginText();
		content.newLineAtOffset(centerX - textWidth / 2, y);
		content.showText(text);
		content.endText();
	}

	private void drawMultilineCell(PDPageContentStream content, String text, float x, float y,
			float width, float height) throws IOException {
		String[] lines = text.split("\n");
		float lineHeight = 10;
		float startY = y + ((lines.length - 1) * lineHeight) / 2;

		for (String line : lines) {
			drawWrappedText(content, line, x + 4, startY, width - 8, lineHeight, 8, false);
			startY -= lineHeight;
		}
	}

	/**
	 * Draws text with automatic word wrapping within the specified width
	 * 
	 * @return the Y position after drawing all lines
	 */
	private float drawWrappedText(PDPageContentStream content, String text, float x, float y,
			float maxWidth, float lineHeight, float fontSize,
			boolean isBold) throws IOException {
		if (text == null || text.isEmpty())
			return y;

		PDType1Font font = isBold ? new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD)
				: new PDType1Font(Standard14Fonts.FontName.HELVETICA);

		content.setFont(font, fontSize);

		String[] words = text.split("\\s+");
		StringBuilder currentLine = new StringBuilder();
		float currentY = y;

		for (String word : words) {
			String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
			float textWidth = font.getStringWidth(testLine) / 1000 * fontSize;

			if (textWidth > maxWidth && !currentLine.isEmpty()) {
				// Draw current line and start new one
				content.beginText();
				content.newLineAtOffset(x, currentY);
				content.showText(currentLine.toString());
				content.endText();
				currentY -= lineHeight;
				currentLine = new StringBuilder(word);
			} else {
				currentLine = new StringBuilder(testLine);
			}
		}

		// Draw remaining text
		if (!currentLine.isEmpty()) {
			content.beginText();
			content.newLineAtOffset(x, currentY);
			content.showText(currentLine.toString());
			content.endText();
			currentY -= lineHeight;
		}

		return currentY;
	}

	/**
	 * Calculate the height needed for wrapped text
	 * 
	 * @throws IOException
	 */
	private float calculateWrappedTextHeight(String text, float maxWidth, float lineHeight,
			float fontSize, boolean isBold) throws IOException {
		if (text == null || text.isEmpty())
			return 0;

		PDType1Font font = isBold ? new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD)
				: new PDType1Font(Standard14Fonts.FontName.HELVETICA);

		String[] words = text.split("\\s+");
		StringBuilder currentLine = new StringBuilder();
		int lineCount = 0;

		for (String word : words) {
			String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
			float textWidth = font.getStringWidth(testLine) / 1000 * fontSize;

			if (textWidth > maxWidth && !currentLine.isEmpty()) {
				lineCount++;
				currentLine = new StringBuilder(word);
			} else {
				currentLine = new StringBuilder(testLine);
			}
		}

		if (!currentLine.isEmpty()) {
			lineCount++;
		}

		return lineCount * lineHeight;
	}

	private PDImageXObject loadLogo(PDDocument document) throws IOException {
		byte[] logoData = getCachedLogoData();
		return PDImageXObject.createFromByteArray(document, logoData, "logo");
	}

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

	private byte[] createPlaceholderImage() {
		return new byte[] {
				(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
				0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
				0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
				0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte) 0xC4,
				(byte) 0x89, 0x00, 0x00, 0x00, 0x0B, 0x49, 0x44,
				0x41, 0x54, 0x78, (byte) 0xDA, 0x63, 0x00, 0x01,
				0x00, 0x00, 0x05, 0x00, 0x01, 0x0D, 0x0A, 0x2D,
				(byte) 0xB4, 0x00, 0x00, 0x00, 0x00, 0x49, 0x45,
				0x4E, 0x44, (byte) 0xAE, 0x42, 0x60, (byte) 0x82
		};
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

	private LocalDate getScheduleSortKey(SchedulingDTO schedule) {
		return switch (schedule) {
			case CourseSchedulingDTO cs -> LocalDate.MIN;
			case ExamSchedulingDTO es -> LocalDateTime.parse(es.date()).toLocalDate();
		};
	}
}