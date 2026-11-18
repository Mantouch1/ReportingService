package com.report.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.report.dto.SummaryResponse;
import com.report.repository.AuditEventRepository;

class ReportServiceTest {

	private final AuditEventRepository repository = mock(AuditEventRepository.class);
	private final ReportService service = new ReportService(repository);

	@Test
	void summaryKeepsQueryOrderAndLimitsTopActors() {
		when(repository.countByOccurredAtGreaterThanEqual(any())).thenReturn(42L);
		when(repository.countByEventType(any())).thenReturn(List.of(new Object[] { "FILE_UPLOADED", 30L }, new Object[] { "JOB_SUCCESS", 12L }));
		when(repository.countBySource(any())).thenReturn(List.of(new Object[] { "document-service", 30L }));
		List<Object[]> actors = new ArrayList<>();
		for (int i = 0; i < 15; i++) {
			actors.add(new Object[] { "user" + i, (long) (100 - i) });
		}
		when(repository.countByActor(any())).thenReturn(actors);
		when(repository.countPerDay(any())).thenReturn(List.<Object[]>of(new Object[] { Date.valueOf("2026-11-10"), 5L }));

		SummaryResponse s = service.summary(7);

		assertEquals(42L, s.totalEvents());
		assertEquals(7, s.days());
		assertEquals("FILE_UPLOADED", s.byEventType().keySet().iterator().next());
		assertEquals(10, s.topActors().size());
		assertEquals(Map.of("2026-11-10", 5L), s.perDay());
	}

	@Test
	void daysAreClamped() {
		when(repository.countByOccurredAtGreaterThanEqual(any())).thenReturn(0L);
		when(repository.countByEventType(any())).thenReturn(List.of());
		when(repository.countBySource(any())).thenReturn(List.of());
		when(repository.countByActor(any())).thenReturn(List.of());
		when(repository.countPerDay(any())).thenReturn(List.of());
		assertEquals(365, service.summary(100000).days());
		assertEquals(1, service.summary(0).days());
		assertTrue(service.summary(5).byEventType().isEmpty());
	}
}
