package com.report.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.report.dto.EventRequest;
import com.report.dto.EventResponse;
import com.report.dto.PageResponse;
import com.report.entity.AuditEvent;
import com.report.exception.BadRequestException;
import com.report.repository.AuditEventRepository;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

	private final AuditEventRepository repository;

	/** Stores one event. actor / occurredAt default to the caller / now. */
	public EventResponse record(EventRequest r, String callerName) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime when = r.getOccurredAt() != null ? r.getOccurredAt() : now;
		if (when.isAfter(now.plusMinutes(5))) {
			throw new BadRequestException("occurredAt cannot be in the future");
		}
		AuditEvent saved = repository.save(AuditEvent.builder()
				.source(r.getSource().trim())
				.eventType(r.getEventType().trim().toUpperCase())
				.actor(r.getActor() != null && !r.getActor().isBlank() ? r.getActor().trim() : callerName)
				.entityType(r.getEntityType())
				.entityId(r.getEntityId())
				.details(r.getDetails())
				.occurredAt(when)
				.build());
		return toResponse(saved);
	}

	public PageResponse<EventResponse> search(String source, String eventType, String actor,
			LocalDateTime from, LocalDateTime to, int page, int size) {
		if (from != null && to != null && from.isAfter(to)) {
			throw new BadRequestException("'from' must be before 'to'");
		}
		Specification<AuditEvent> spec = (root, query, cb) -> {
			List<Predicate> ps = new ArrayList<>();
			if (hasText(source)) {
				ps.add(cb.equal(root.get("source"), source.trim()));
			}
			if (hasText(eventType)) {
				ps.add(cb.equal(root.get("eventType"), eventType.trim().toUpperCase()));
			}
			if (hasText(actor)) {
				ps.add(cb.equal(root.get("actor"), actor.trim()));
			}
			if (from != null) {
				ps.add(cb.greaterThanOrEqualTo(root.<LocalDateTime>get("occurredAt"), from));
			}
			if (to != null) {
				ps.add(cb.lessThan(root.<LocalDateTime>get("occurredAt"), to));
			}
			return cb.and(ps.toArray(new Predicate[0]));
		};
		PageRequest pr = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 200),
				Sort.by(Sort.Direction.DESC, "occurredAt"));
		Page<AuditEvent> result = repository.findAll(spec, pr);
		return PageResponse.of(result.map(this::toResponse));
	}

	private static boolean hasText(String s) {
		return s != null && !s.isBlank();
	}

	private EventResponse toResponse(AuditEvent e) {
		return EventResponse.builder().id(e.getId()).source(e.getSource()).eventType(e.getEventType()).actor(e.getActor())
				.entityType(e.getEntityType()).entityId(e.getEntityId()).details(e.getDetails()).occurredAt(e.getOccurredAt()).build();
	}
}
