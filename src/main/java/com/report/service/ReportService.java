package com.report.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.report.dto.SummaryResponse;
import com.report.repository.AuditEventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

	private static final int TOP_ACTORS = 10;

	private final AuditEventRepository repository;

	/** Statistics over the last N days (1..365). */
	public SummaryResponse summary(int days) {
		int d = Math.min(Math.max(days, 1), 365);
		LocalDateTime since = LocalDateTime.now().minusDays(d);
		return new SummaryResponse(since, d,
				repository.countByOccurredAtGreaterThanEqual(since),
				toMap(repository.countByEventType(since), Integer.MAX_VALUE),
				toMap(repository.countBySource(since), Integer.MAX_VALUE),
				toMap(repository.countByActor(since), TOP_ACTORS),
				toMap(repository.countPerDay(since), Integer.MAX_VALUE));
	}

	/** Rows are [key, count]; the order of the query is kept. */
	static Map<String, Long> toMap(List<Object[]> rows, int limit) {
		Map<String, Long> result = new LinkedHashMap<>();
		for (Object[] row : rows) {
			if (result.size() >= limit) {
				break;
			}
			result.put(String.valueOf(row[0]), ((Number) row[1]).longValue());
		}
		return result;
	}
}
