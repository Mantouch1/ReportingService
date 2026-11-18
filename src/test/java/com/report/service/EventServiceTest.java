package com.report.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.report.dto.EventRequest;
import com.report.dto.EventResponse;
import com.report.entity.AuditEvent;
import com.report.exception.BadRequestException;
import com.report.repository.AuditEventRepository;

class EventServiceTest {

	private final AuditEventRepository repository = mock(AuditEventRepository.class);
	private final EventService service = new EventService(repository);

	private void saveReturnsArgument() {
		when(repository.save(any(AuditEvent.class))).thenAnswer(i -> i.getArgument(0));
	}

	@Test
	void actorDefaultsToCallerAndEventTypeIsUppercased() {
		saveReturnsArgument();
		EventRequest r = EventRequest.builder().source("document-service").eventType("file_uploaded").build();
		EventResponse out = service.record(r, "service@tenderflow.local");
		assertEquals("service@tenderflow.local", out.getActor());
		assertEquals("FILE_UPLOADED", out.getEventType());
	}

	@Test
	void explicitActorIsKept() {
		saveReturnsArgument();
		EventRequest r = EventRequest.builder().source("s").eventType("T").actor("alice@x.de").build();
		assertEquals("alice@x.de", service.record(r, "caller").getActor());
	}

	@Test
	void futureEventsAreRejected() {
		EventRequest r = EventRequest.builder().source("s").eventType("T").occurredAt(LocalDateTime.now().plusDays(1)).build();
		assertThrows(BadRequestException.class, () -> service.record(r, "caller"));
	}

	@Test
	void searchRejectsInvertedRange() {
		LocalDateTime now = LocalDateTime.now();
		assertThrows(BadRequestException.class, () -> service.search(null, null, null, now, now.minusDays(1), 0, 10));
	}
}
