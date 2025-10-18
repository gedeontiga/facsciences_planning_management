package com.facsciencesuy1.planning_management.planning_service.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.facsciencesuy1.planning_management.dtos.TimetableDTO;
import com.facsciencesuy1.planning_management.planning_service.services.TimetableExportService;
import com.facsciencesuy1.planning_management.planning_service.services.TimetableExportService.ExportType;
import com.facsciencesuy1.planning_management.planning_service.services.interfaces.TimetableService;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/export/timetables")
public class TimetableExportController {
	private final TimetableExportService exportService;
	private final TimetableService timetableService;

	@GetMapping("/{timetableId}")
	public ResponseEntity<InputStreamResource> exportTimetable(
			@PathVariable String timetableId,
			@RequestParam @Valid ExportType type) throws IOException {

		TimetableDTO timetable = timetableService.getTimetableById(timetableId);

		// Generate the appropriate export
		ByteArrayInputStream bis = switch (type) {
			case PDF -> exportService.generateTimetablePdf(timetable, timetable.levelCode());
			case CSV -> exportService.generateTimetableCsv(timetable);
		};

		// Determine media type
		MediaType mediaType = switch (type) {
			case PDF -> MediaType.APPLICATION_PDF;
			case CSV -> MediaType.parseMediaType("text/csv");
		};

		// Generate filename
		String filename = String.format("%s_%s_%s_Timetable.%s",
				timetable.levelCode().replace(" ", "_"),
				timetable.academicYear(),
				timetable.semester(),
				type.name().toLowerCase());

		// Get content length before creating InputStreamResource
		int contentLength = bis.available();

		// Set headers for file download
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		headers.setContentDispositionFormData("attachment", filename);
		headers.setContentLength(contentLength);
		headers.setCacheControl("no-cache, no-store, must-revalidate");
		headers.setPragma("no-cache");
		headers.setExpires(0);

		// Use InputStreamResource for better streaming support
		return ResponseEntity.ok()
				.headers(headers)
				.body(new InputStreamResource(bis));
	}
}