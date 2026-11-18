package com.report.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {

	private Long id;
	private String source;
	private String eventType;
	private String actor;
	private String entityType;
	private String entityId;
	private String details;
	private LocalDateTime occurredAt;
}
