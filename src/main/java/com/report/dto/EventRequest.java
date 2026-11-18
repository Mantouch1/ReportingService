package com.report.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRequest {

	@NotBlank
	@Size(max = 100)
	private String source;

	@NotBlank
	@Size(max = 100)
	private String eventType;

	/** Optional: defaults to the authenticated account that sends the event. */
	@Size(max = 200)
	private String actor;

	@Size(max = 100)
	private String entityType;

	@Size(max = 100)
	private String entityId;

	@Size(max = 4000)
	private String details;

	/** Optional: defaults to now. */
	private LocalDateTime occurredAt;
}
