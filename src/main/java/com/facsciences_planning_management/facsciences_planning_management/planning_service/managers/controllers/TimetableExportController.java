package com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.dtos.TimetableDTO;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.TimetableExportService;
import com.facsciences_planning_management.facsciences_planning_management.planning_service.managers.services.interfaces.TimetableService;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/export/timetables")
@RequiredArgsConstructor
public class TimetableExportController {

	private final TimetableExportService exportService;
	private final TimetableService timetableService;

	@GetMapping("/pdf/{timetableId}")
	@Operation(summary = "Export a timetable as a PDF file")
	public ResponseEntity<InputStreamResource> exportTimetableToPdf(@PathVariable String timetableId)
			throws IOException {

		TimetableDTO timetable = timetableService.getTimetableById(timetableId);

		ByteArrayInputStream bis = exportService.generateTimetablePdf(timetable, timetable.levelCode());

		HttpHeaders headers = new HttpHeaders();
		String filename = String.format("%s_%s_%s_Timetable.pdf",
				timetable.levelCode().replace(" ", "_"),
				timetable.academicYear(),
				timetable.semester());
		headers.add("Content-Disposition", "inline; filename=" + filename);

		return ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_PDF)
				.body(new InputStreamResource(bis));
	}

	@GetMapping("/csv/{timetableId}")
	@Operation(summary = "Export a timetable as a CSV file")
	public ResponseEntity<InputStreamResource> exportTimetableToCsv(@PathVariable String timetableId) {
		TimetableDTO timetable = timetableService.getTimetableById(timetableId);

		ByteArrayInputStream bis = exportService.generateTimetableCsv(timetable);

		HttpHeaders headers = new HttpHeaders();
		String filename = String.format("%s_%s_%s_Timetable.csv",
				timetable.levelCode().replace(" ", "_"),
				timetable.academicYear(),
				timetable.semester());
		headers.add("Content-Disposition", "attachment; filename=" + filename);

		return ResponseEntity
				.ok()
				.headers(headers)
				.contentType(MediaType.parseMediaType("text/csv"))
				.body(new InputStreamResource(bis));
	}
}