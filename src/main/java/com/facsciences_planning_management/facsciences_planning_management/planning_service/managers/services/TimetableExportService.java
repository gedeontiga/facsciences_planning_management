package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services;

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
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

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
import java.util.stream.Collectors;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TimetableExportService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");

    public ByteArrayInputStream generateTimetablePdf(TimetableDTO timetable, String levelCode) throws IOException {
        boolean isExamTimetable = timetable.schedules().stream().anyMatch(ExamSchedulingDTO.class::isInstance);
        return isExamTimetable ? generateExamTimetablePdf(timetable, levelCode)
                : generateCourseTimetablePdf(timetable, levelCode);
    }

    private ByteArrayInputStream generateCourseTimetablePdf(TimetableDTO timetable, String levelCode)
            throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(new PdfDocument(new PdfWriter(out)), PageSize.A4.rotate());
        addHeader(document, "Course Timetable", timetable, levelCode);

        Map<DayOfWeek, Map<LocalTime, List<SchedulingDTO>>> groupedSchedules = timetable.schedules().stream()
                .filter(CourseSchedulingDTO.class::isInstance)
                .collect(Collectors.groupingBy(
                        s -> DayOfWeek.valueOf(((CourseSchedulingDTO) s).day()),
                        Collectors.groupingBy(s -> LocalTime.parse(s.startTime()), TreeMap::new, Collectors.toList())));

        List<LocalTime> timeSlots = timetable.schedules().stream().map(s -> LocalTime.parse(s.startTime())).distinct()
                .sorted().toList();
        List<DayOfWeek> days = List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY, DayOfWeek.SATURDAY);

        Table pdfTable = new Table(UnitValue.createPercentArray(new float[] { 1.5f, 3f, 3f, 3f, 3f, 3f, 3f }));
        pdfTable.setWidth(UnitValue.createPercentValue(100)).setMarginTop(20);

        pdfTable.addHeaderCell(createHeaderCell("Time"));
        days.forEach(day -> {
            try {
                pdfTable.addHeaderCell(createHeaderCell(day.getDisplayName(TextStyle.FULL, Locale.ENGLISH)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        for (LocalTime startTime : timeSlots) {
            LocalTime endTime = timetable.schedules().stream()
                    .filter(s -> LocalTime.parse(s.startTime()).equals(startTime)).findFirst()
                    .map(s -> LocalTime.parse(s.endTime())).orElse(startTime);
            pdfTable.addCell(createTimeCell(startTime.format(TIME_FORMATTER) + " - " + endTime.format(TIME_FORMATTER)));

            for (DayOfWeek day : days) {
                List<SchedulingDTO> schedulesForSlot = groupedSchedules.getOrDefault(day, Collections.emptyMap())
                        .getOrDefault(startTime, Collections.emptyList());
                Cell contentCell = new Cell().setPadding(5).setBorder(new SolidBorder(new DeviceRgb(230, 230, 230), 1));
                if (!schedulesForSlot.isEmpty()) {
                    for (SchedulingDTO schedule : schedulesForSlot) {
                        contentCell.add(createScheduleParagraph(schedule));
                    }
                }
                pdfTable.addCell(contentCell);
            }
        }
        document.add(pdfTable);
        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private ByteArrayInputStream generateExamTimetablePdf(TimetableDTO timetable, String levelCode) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(new PdfDocument(new PdfWriter(out)), PageSize.A4);
        addHeader(document, "Exam Timetable", timetable, levelCode);

        Map<LocalDate, List<SchedulingDTO>> groupedExams = timetable.schedules().stream()
                .filter(ExamSchedulingDTO.class::isInstance)
                .collect(Collectors.groupingBy(
                        s -> LocalDate.parse(((ExamSchedulingDTO) s).date()),
                        TreeMap::new,
                        Collectors.toList()));

        for (Map.Entry<LocalDate, List<SchedulingDTO>> entry : groupedExams.entrySet()) {
            document.add(new Paragraph(entry.getKey().format(DATE_FORMATTER))
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(14).setMarginTop(20)
                    .setMarginBottom(10).setBorderBottom(new SolidBorder(ColorConstants.BLACK, 1)));

            Table examTable = new Table(UnitValue.createPercentArray(new float[] { 2, 4, 3, 4 }));
            examTable.setWidth(UnitValue.createPercentValue(100));
            examTable.addHeaderCell(createHeaderCell("Time Slot"));
            examTable.addHeaderCell(createHeaderCell("Course (UE)"));
            examTable.addHeaderCell(createHeaderCell("Room(s)"));
            examTable.addHeaderCell(createHeaderCell("Proctor(s)"));

            entry.getValue().sort(Comparator.comparing(s -> LocalTime.parse(s.startTime())));
            entry.getValue().forEach(exam -> {
                try {
                    examTable.addCell(createCell(LocalTime.parse(exam.startTime()).format(TIME_FORMATTER) + " - "
                            + LocalTime.parse(exam.endTime()).format(TIME_FORMATTER)));
                    examTable.addCell(createCell(exam.ueCode()));
                    examTable.addCell(createCell(exam.roomCode()));
                    // Using pattern matching in a cleaner way
                    if (exam instanceof ExamSchedulingDTO es) {
                        examTable.addCell(createCell(es.proctorName()));
                    } else {
                        examTable.addCell(createCell("")); // Should not happen due to filter
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            document.add(examTable);
        }
        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    // ===================================================================================
    // =================================== PDF HELPERS
    // ===================================
    // ===================================================================================

    private void addHeader(Document document, String title, TimetableDTO timetable, String levelCode)
            throws IOException {
        // ... (this method remains the same as before, no changes needed)
        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 1, 4, 1 }))
                .setWidth(UnitValue.createPercentValue(100));
        headerTable.addCell(new Cell()
                .add(new Paragraph("UNIVERSITE DE YAOUNDE I\nFACULTE DES SCIENCES").setFont(regularFont).setFontSize(8))
                .setBorder(null));
        Image logo = new Image(ImageDataFactory.create("src/main/resources/images/uy1_logo.png")).setHeight(50);
        headerTable.addCell(
                new Cell().add(logo.setAutoScale(true)).setTextAlignment(TextAlignment.CENTER).setBorder(null));
        headerTable.addCell(new Cell()
                .add(new Paragraph("UNIVERSITY OF YAOUNDE I\nFACULTY OF SCIENCE").setFont(regularFont).setFontSize(8))
                .setTextAlignment(TextAlignment.RIGHT).setBorder(null));
        document.add(headerTable);

        document.add(new Paragraph(title).setFont(boldFont).setFontSize(18).setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10));
        document.add(new Paragraph(levelCode).setFont(boldFont).setFontSize(14).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph(
                String.format("Academic Year: %s | Semester: %s", timetable.academicYear(), timetable.semester()))
                .setFont(regularFont).setFontSize(10).setTextAlignment(TextAlignment.CENTER).setMarginBottom(10));
    }

    private Paragraph createScheduleParagraph(SchedulingDTO schedule) throws IOException {
        Paragraph p = new Paragraph().setMargin(0).setPadding(0).setMultipliedLeading(1.2f);
        p.add(new Text(schedule.ueCode() + "\n").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(10));

        // Pattern matching for specific details
        switch (schedule) {
            case CourseSchedulingDTO cs -> {
                p.add(new Text(cs.roomCode() + " - ").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                        .setFontSize(8).setFontColor(new DeviceRgb(0, 153, 0)));
                p.add(new Text(cs.teacherName()).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                        .setFontSize(8).setFontColor(ColorConstants.DARK_GRAY));
            }
            case ExamSchedulingDTO es -> {
                p.add(new Text(es.roomCode() + " - ").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                        .setFontSize(8).setFontColor(new DeviceRgb(204, 0, 0))); // Red for exams
                p.add(new Text(es.proctorName()).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                        .setFontSize(8).setFontColor(ColorConstants.DARK_GRAY));
            }
        }
        return p;
    }

    private Cell createHeaderCell(String text) throws IOException {
        return new Cell().add(new Paragraph(text)).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(9).setBackgroundColor(new DeviceRgb(240, 240, 240)).setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(8);
    }

    private Cell createTimeCell(String text) throws IOException {
        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        return createCell(text)
                .setFont(boldFont)
                .setFontColor(ColorConstants.BLACK);
    }

    private Cell createCell(String text) throws IOException {
        return new Cell().add(new Paragraph(text)).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontSize(9).setPadding(5).setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(new SolidBorder(new DeviceRgb(220, 220, 220), 1));
    }

    // ===================================================================================
    // ================================= CSV EXPORT LOGIC
    // =================================

    public ByteArrayInputStream generateTimetableCsv(TimetableDTO timetable) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out)) {
            writer.println("DateOrDay,StartTime,EndTime,UE_Code,RoomCode,SessionType,PersonnelName");
            List<SchedulingDTO> sortedSchedules = timetable.schedules().stream()
                    .sorted(Comparator.comparing(this::getScheduleSortKey)
                            .thenComparing(s -> LocalTime.parse(s.startTime())))
                    .collect(Collectors.toList());

            for (SchedulingDTO schedule : sortedSchedules) {
                // Use pattern matching switch expression for concise data extraction
                String row = switch (schedule) {
                    case CourseSchedulingDTO cs -> String.join(",",
                            cs.day(),
                            cs.startTime(), cs.endTime(),
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
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    private LocalDate getScheduleSortKey(SchedulingDTO schedule) {
        return switch (schedule) {
            case CourseSchedulingDTO cs -> LocalDate.MIN; // Courses don't have a specific date, sort them first
            case ExamSchedulingDTO es -> LocalDateTime.parse(es.date()).toLocalDate();
        };
    }
}