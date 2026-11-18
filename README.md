# ReportingService

Collects the business events of the other services (audit trail) in its own PostgreSQL database (`report_db`)
and builds statistics. Port **8086**.

## Endpoints (JWT issued by IdentityService)
| Method | URL | Permission |
|---|---|---|
| POST | /api/events | AUDIT_WRITE (role SERVICE, ADMIN) |
| GET | /api/events?source=&eventType=&actor=&from=&to=&page=&size= | REPORT_READ |
| GET | /api/reports/summary?days=30 | REPORT_READ |

`POST /api/events` body: `{"source":"document-service","eventType":"FILE_UPLOADED","actor":"a@b.de","entityType":"TENDER","entityId":"12","details":"offer.pdf"}`
(`actor` defaults to the caller, `occurredAt` (ISO date-time) defaults to now, `eventType` is stored in upper case).

`GET /api/reports/summary` returns, for the last N days (1 to 365): total, events per type, per source, top 10 actors and events per day.
`from` / `to` use ISO format, e.g. `2026-11-01T00:00:00`.

Producers: DocumentService (FILE_UPLOADED, FILE_DELETED) and SchedulerService (JOB_SUCCESS, JOB_FAILED) send their events here
with the technical account. TenderFlow does not publish events yet.

## Run
Env vars: `SPRING_DATASOURCE_PASSWORD`, `JWT_SECRET` (same as IdentityService).
Docker: `docker network create platform-net` (once), `copy .env.example .env`, `docker compose up --build`.
