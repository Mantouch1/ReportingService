package com.report.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.report.entity.AuditEvent;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long>, JpaSpecificationExecutor<AuditEvent> {

	long countByOccurredAtGreaterThanEqual(LocalDateTime since);

	@Query("select e.eventType, count(e) from AuditEvent e where e.occurredAt >= :since group by e.eventType order by count(e) desc")
	List<Object[]> countByEventType(@Param("since") LocalDateTime since);

	@Query("select e.source, count(e) from AuditEvent e where e.occurredAt >= :since group by e.source order by count(e) desc")
	List<Object[]> countBySource(@Param("since") LocalDateTime since);

	@Query("select e.actor, count(e) from AuditEvent e where e.occurredAt >= :since and e.actor is not null group by e.actor order by count(e) desc")
	List<Object[]> countByActor(@Param("since") LocalDateTime since);

	@Query(value = "select cast(occurred_at as date) as d, count(*) from audit_events where occurred_at >= :since "
			+ "group by cast(occurred_at as date) order by d", nativeQuery = true)
	List<Object[]> countPerDay(@Param("since") LocalDateTime since);
}
