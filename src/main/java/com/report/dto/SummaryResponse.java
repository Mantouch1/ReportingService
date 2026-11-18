package com.report.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record SummaryResponse(LocalDateTime since, int days, long totalEvents,
		Map<String, Long> byEventType, Map<String, Long> bySource,
		Map<String, Long> topActors, Map<String, Long> perDay) {
}
