# Patient Queue Management System API

A Spring Boot + MySQL backend for managing a daily patient token queue system in a clinic. It provides endpoints for token booking (mobile screen), counter display boards, and configuration management.

---

## How to Run the Project

### Requirements
- **Java JDK 21** or higher installed.
- **Maven** installed (or use the provided `mvnw` wrapper).
- **MySQL Server** running locally.

### Database Setup
1. Open your MySQL client and create a database named `queue-management-api`:
   ```sql
   CREATE DATABASE `queue-management-api`;
   ```
2. Configure database credentials in [application.properties](file:///d:/queue-management-api/src/main/resources/application.properties):
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/queue-management-api
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

### Execution

#### Run via IntelliJ IDEA
1. Open **IntelliJ IDEA**.
2. Click **Open** and choose the `queue-management-api` directory.
3. IntelliJ will automatically detect the `pom.xml` and import dependencies.
4. Locate the main class: [QueueManagementApiApplication.java](file:///d:/queue-management-api/src/main/java/com/queue/queue_management_api/QueueManagementApiApplication.java).
5. Click the green **Run** play icon next to the `main` method (or press `Shift + F10`).
6. The server will start on port `8081` (as configured in [application.properties](file:///d:/queue-management-api/src/main/resources/application.properties)).

---

## Design Decisions

### 1. Automatic Daily Token Reset
- **Decision:** Create a table named **DailyCounter** with a composite primary key based on the date to support automatic daily token resets. This approach eliminates the need for cron jobs or database scripts to reset token sequences at midnight.
- **Why:** The booking logic queries the row corresponding to `LocalDate.now()`. When a new day begins, the query yields no row, triggering the insertion of a new counter row initialized to token `1`. This design is stateless regarding server timezone transitions, resilient to restarts, and automatically partitions token historical tracking.

### 2. Token Number vs. Queue Position
- **Decision:** I separate the stable `tokenNumber` (the ticket ID given to the patient, e.g., Token 15) from the mutable `queuePosition` (the physical order in the line).
- **Why:** When a patient is **skipped**, they must go to the end of the line. Instead of reissuing a new token number (which would confuse the patient) or deleting records, we simply update their `queuePosition` to `max(queuePosition) + 1` for today and set their status back to `WAITING`. This keeps the patient's token number constant while allowing staff to flexibly manipulate their position.

### 3. Queue Close Guard & One-at-a-Time Servicing
- **Decision:** Validations are enforced in the service layer (`QueueSettingsService` and `QueueService`) with transaction scopes (`@Transactional`).
- **Why:**
  - Before closing a queue, the system verifies `queueRepository.existsByStatus(QueueStatus.SERVING)`. If `true`, the operation is aborted with a clean validation error.
  - When calling the next patient, the service checks if a patient is currently in `SERVING` status, preventing overlaps.

### 4. Dynamic Estimated Wait Time
- **Decision:** Estimated wait time is computed on-the-fly when checking token status by counting active patients ahead in the queue today:
  ```sql
  SELECT COUNT(q) FROM Queue q WHERE q.queueDate = ?1 AND q.status IN (0, 1) AND q.queuePosition < ?2
  ```
- **Why:** Counting only active patients (status `WAITING` or `SERVING`) with a lower `queuePosition` ensures that completed, skipped, or canceled patients do not distort the wait time estimation for patients currently waiting.

---

## Handling Concurrent Token Requests

In a busy clinic, multiple patients might request a token simultaneously. To prevent duplicate token numbers or gaps under high concurrency:

1. **Database-Level Pessimistic Locking:**
   In [DailyCounterRepository](file:///d:/queue-management-api/src/main/java/com/queue/queue_management_api/repository/DailyCounterRepository.java), we acquire a pessimistic write lock when retrieving the daily counter:
   ```java
   @Lock(LockModeType.PESSIMISTIC_WRITE)
   @Query("SELECT d FROM DailyCounter d WHERE d.queueDate = :date")
   Optional<DailyCounter> findByQueueDateForUpdate(@Param("date") LocalDate date);
   ```
2. **Transaction Isolation:**
   When booking a token in [TokenService.bookToken](file:///d:/queue-management-api/src/main/java/com/queue/queue_management_api/service/TokenService.java#L30-L98), the database row is locked for writes for the duration of the transaction. Any other threads requesting a token for the same date must wait until the current transaction commits and releases the lock.
3. **Double-Checked Concurrency Guard:**
   If a request is the first of the day, it attempts to insert a new row. If another thread inserts the row concurrently, the database unique key constraint is triggered. The catching block intercepts the collision, selects the row with the lock, increments the sequence, and continues safely.
4. **Database Constraint Safety Net:**
   The `queue` table has a unique constraint on `(queueDate, tokenNumber)`. Even in the event of an unexpected race condition, the database will reject duplicate tokens and fail safely instead of corrupting the queue integrity.

---

## Future Improvements & Recommendations

Given more time, the following improvements would enhance the system:

1. **Enhanced Security & Rate Limiting:**
   Implement API rate-limiting and basic captcha on token booking to prevent bot attacks.
2. **Predictive Wait Time Analytics:**
   Replace the constant `minutesPerPatient` formula with a historical moving average or a machine learning model based on the service/appointment type, time of day, and specific staff performance to make wait times significantly more accurate.
