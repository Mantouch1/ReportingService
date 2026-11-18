package com.report.controller;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.report.dto.EventRequest;
import com.report.dto.EventResponse;
import com.report.dto.PageResponse;
import com.report.service.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

	private final EventService service;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAuthority('AUDIT_WRITE')")
	public EventResponse record(@Valid @RequestBody EventRequest request, Authentication auth) {
		return service.record(request, auth.getName());
	}

	@GetMapping
	@PreAuthorize("hasAuthority('REPORT_READ')")
	public PageResponse<EventResponse> search(
			@RequestParam(required = false) String source,
			@RequestParam(required = false) String eventType,
			@RequestParam(required = false) String actor,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "50") int size) {
		return service.search(source, eventType, actor, from, to, page, size);
	}
}
