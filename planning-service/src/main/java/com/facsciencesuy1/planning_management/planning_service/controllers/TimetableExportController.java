package com.facsciencesuy1.planning_management.planning_service.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.ByteArrayResource;
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
	public ResponseEntity<ByteArrayResource> exportTimetableToPdf(
			@PathVariable String timetableId,
			@RequestParam @Valid ExportType type) throws IOException {

		TimetableDTO timetable = timetableService.getTimetableById(timetableId);

		MediaType mediaType = switch (type) {
			case PDF -> MediaType.APPLICATION_PDF;
			case CSV -> MediaType.parseMediaType("text/csv");
		};

		ByteArrayInputStream bis = switch (type) {
			case PDF -> exportService.generateTimetablePdf(timetable, timetable.levelCode());
			case CSV -> exportService.generateTimetableCsv(timetable);
		};

		HttpHeaders headers = new HttpHeaders();
		String filename = String.format("%s_%s_%s_Timetable.%s",
				timetable.levelCode().replace(" ", "_"),
				timetable.academicYear(),
				timetable.semester(),
				type.name().toLowerCase());

		// Key fixes:
		headers.add("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		headers.add("Content-Length", String.valueOf(bis.available()));
		headers.setCacheControl("no-cache, no-store, must-revalidate");
		headers.setPragma("no-cache");
		headers.setExpires(0);

		byte[] data = bis.readAllBytes();
		return ResponseEntity.ok()
				.headers(headers)
				.contentType(mediaType)
				.body(new ByteArrayResource(data));
	}
}