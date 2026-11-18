package com.report.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audit_events", indexes = {
		@Index(name = "idx_audit_occurred_at", columnList = "occurred_at"),
		@Index(name = "idx_audit_event_type", columnList = "event_type"),
		@Index(name = "idx_audit_source", columnList = "source")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Service that produced the event (e.g. document-service). */
	@Column(name = "source", nullable = false)
	private String source;

	@Column(name = "event_type", nullable = false)
	private String eventType;

	private String actor;
	private String entityType;
	private String entityId;

	@Column(columnDefinition = "text")
	private String details;

	@Column(name = "occurred_at", nullable = false)
	private LocalDateTime occurredAt;

	@Column(nullable = false, updatable = false)
	private LocalDateTime receivedAt;

	@PrePersist
	private void onCreate() {
		this.receivedAt = LocalDateTime.now();
	}
}
