package com.report.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.report.dto.SummaryResponse;
import com.report.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('REPORT_READ')")
public class ReportController {

	private final ReportService service;

	@GetMapping("/summary")
	public SummaryResponse summary(@RequestParam(defaultValue = "30") int days) {
		return service.summary(days);
	}
}
