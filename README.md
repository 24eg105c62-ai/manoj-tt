# Pocket Skill Track 🎓⚡

> **A Complete, Responsive, Secure, Full-Stack Technical Learning Management Platform**  
> Built with **React 18**, **Vite**, **Vanilla CSS**, **Java 21**, **Spring Boot 3.3.4**, **Spring Security**, **JWT**, **Spring Data JPA**, **MySQL 8**, and **Recharts**.

---

## 🏛️ System Architecture

```mermaid
graph TD
    Client["React 18 SPA (Vite + Vanilla CSS + Recharts)"] -->|REST /api/v1 (JSON)| Security["Spring Security 6 (JWT Filter & RBAC)"]
    Security --> Controllers["REST Controllers (/api/v1/*)"]
    Controllers --> Services["Transactional Services (Spring Data JPA)"]
    Services --> DB[("MySQL 8 Normalized Store (22 Tables)")]
    Services --> PDFBox["Apache PDFBox (Text Extractor & Heuristics)"]
    Services --> LLM["Optional LLM Service (Configurable via ENV)"]
```

Pocket Skill Track is structured cleanly into layered separation of concerns:
- **Frontend SPA**: React 18 with Vite, Vanilla CSS design system (dark default with responsive light theme toggle), Lucide icons, Recharts interactive data visualization, dedicated API service client, and React Router 6.
- **Backend API**: Java 21 LTS with Spring Boot 3.3.4, layered into `controller`, `service`, `repository`, `dto`, `entity`, `config`, and `security` packages.
- **Relational Store**: Normalized MySQL 8 relational schema with strict foreign keys, unique indexes, cascading rules, and timestamp auditing.

---

## 🚀 Key Modules & Capabilities

| Module | Features & Implementation Highlights |
| :--- | :--- |
| **1. Authentication & Security** | BCrypt 12-round hashing, stateless HMAC-SHA256 JWT tokens, backend-enforced `ROLE_STUDENT` on public registration, per-user data isolation, and narrow CORS whitelisting. |
| **2. Student Dashboard** | Real-time database metrics: enrolled courses, completed topics, streak counters, quiz attempts, historical progress charts via Recharts, unlocked badges, and prioritized next actions. |
| **3. Technical Courses** | Structured curriculum: categories, difficulty levels, modules, reading topics, syllabus progress calculation, bookmarks, and enrollment states. |
| **4. Anti-Cheating Quizzes** | Topic-level MCQ banks, difficulty filters, timers, and server-side evaluation. **Correct answers and explanations are never sent to the client before submission.** |
| **5. Score Predictions** | Transparent statistical baselines derived from empirical quiz performance, momentum deltas, variance analysis, predicted ranges, confidence ratings, and clear diagnostic limitations. |
| **6. PDF Summarizer** | Secure PDF upload (magic-byte `%PDF` validation, 10MB limit), Apache PDFBox text extraction, key technical concepts, chapter breakdowns, revision notes, and page citations. Fully functional offline with zero fabricated summaries. |
| **7. Achievement Badges** | Criteria-based milestones (first topic, course completion, quiz score ≥90%, streak days), shareable cards, community celebration feed, comments, and **strictly enforced one-reaction-per-user** rule. |
| **8. Admin Panel** | Protected by `ROLE_ADMIN`: student roster directory with account suspension toggle, course/module/topic CRUD, question bank authoring with verified answers, and immutable security audit logs. |

---

## 🗄️ Normalized MySQL 8 Database Schema

The database consists of **22 normalized relational tables**:

```
users (id, student_id, email, password_hash, full_name, college, department, study_year, bio, enabled, created_at, updated_at)
roles (id, name: ROLE_STUDENT, ROLE_ADMIN)
user_roles (user_id, role_id)
courses (id, title, slug, description, category, difficulty_level, thumbnail_url, is_published, created_at)
course_modules (id, course_id, title, module_order)
topics (id, module_id, title, content, reading_time_minutes, topic_order)
enrollments (id, user_id, course_id, enrolled_at, progress_percent, completed_at)
topic_completions (id, user_id, topic_id, completed_at)
bookmarks (id, user_id, course_id, created_at)
questions (id, topic_id, question_text, explanation, difficulty, points, active, created_at)
answer_options (id, question_id, option_text, is_correct, option_order)
quiz_attempts (id, user_id, topic_id, score, total_points, percentage, passed, time_spent_seconds, completed_time)
quiz_responses (id, attempt_id, question_id, selected_option_id, is_correct, points_awarded)
score_predictions (id, user_id, category_or_topic, predicted_min, predicted_max, predicted_mid, confidence_level, sample_size, primary_factors_json, calculated_at)
pdf_documents (id, user_id, original_filename, stored_filename, file_size_bytes, page_count, checksum_sha256, uploaded_at)
summaries (id, pdf_id, summary_type, overview, key_concepts_json, chapter_breakdown_json, revision_notes_json, source_references_json, created_at)
achievements (id, badge_code, title, description, icon_name, criteria_type, threshold_value, category)
student_achievements (id, user_id, achievement_id, unlocked_at)
achievement_reactions (id, student_achievement_id, user_id, reaction_type: LIKE, CELEBRATE, INSIGHTFUL, created_at)
achievement_comments (id, student_achievement_id, user_id, comment_text, status, created_at)
notifications (id, user_id, title, message, notification_type, is_read, created_at)
audit_logs (id, actor_id, actor_email, action, resource_type, resource_id, ip_address, details_json, created_at)
```

---

## 🔑 Default Provisioned Credentials

The database is pre-seeded with controlled administrative and demo student accounts:

| Role | Email / Student ID | Password | Access Scope |
| :--- | :--- | :--- | :--- |
| **Platform Administrator** | `admin@pocketskilltrack.edu` (ID: `ADMIN001`) | `AdminPassword123!` | Complete Admin Panel (`/admin/*`) & Student Views |
| **Demo Student** | `student@pocketskilltrack.edu` (ID: `STU2026001`) | `StudentPass123!` | Enrolled Courses, Quizzes, Predictions, PDF Summarizer |

> *Note: There is **no public registration** for administrative accounts. Administrators must be provisioned through secure configuration or by existing administrators.*

---

## 🛠️ Local Setup & Startup Guide

### Prerequisites
- **Java 21 LTS**
- **Apache Maven 3.8+**
- **Node.js 20+** & **npm**
- **MySQL 8.0 Server** (running on port 3306)

### 1. Database Initialization
Ensure MySQL 8 is running, and create the database if not already present:
```sql
CREATE DATABASE IF NOT EXISTS pocketskilltrack CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Backend Startup (Spring Boot)
From the project root:
```powershell
cd backend
mvn spring-boot:run
```
The backend initializes database tables, seed courses, questions, and achievements, and listens on `http://localhost:8080`.

### 3. Frontend Startup (React Vite)
From the project root:
```powershell
cd frontend
npm install
npm run dev
```
The frontend dev server starts on `http://localhost:5173`. Requests to `/api/*` are reverse-proxied to `http://localhost:8080`.

---

## 🧪 Testing Commands

### Backend Tests
Execute unit and slice tests:
```powershell
cd backend
mvn test
```
*Current test suite: 12 tests across Auth, Courses, Quizzes, Predictions, and Achievements with 0 failures.*

### Frontend Production Build
Validate React compilation, routing, and asset minification:
```powershell
cd frontend
npm run build
```

---

## 🌐 Application Routes

| Path | Description | Access |
| :--- | :--- | :--- |
| `/login` | Secure JWT Login Form | Public |
| `/register` | Student Registration (with College, Dept, Year) | Public |
| `/dashboard` | Student Learning Dashboard & Analytics | Student / Admin |
| `/courses` | Browse Technical Courses with Search/Filter | Student / Admin |
| `/courses/:id` | Course Detail, Modules & Syllabus Topics | Student / Admin |
| `/quizzes` | Topic-based Quiz Assessments | Student / Admin |
| `/quiz/:id` | Interactive Quiz Assessment Player | Student / Admin |
| `/results` | Assessment Results & Attempt History | Student / Admin |
| `/predictions` | Empirical Score Prediction Model & Baselines | Student / Admin |
| `/pdf-summarizer` | Apache PDFBox Summarizer & Citation Viewer | Student / Admin |
| `/achievements` | Milestone Badges & Social Celebration Feed | Student / Admin |
| `/profile` | Student Profile Management & Security Overview | Student / Admin |
| `/admin` | Administrator Overview & System KPIs | Admin Only |
| `/admin/students` | Student Roster & Account Access Controls | Admin Only |
| `/admin/courses` | Course, Module & Topic CRUD Authoring | Admin Only |
| `/admin/quizzes` | Question Bank Authoring with Verified Options | Admin Only |
| `/admin/analytics` | Tamper-Evident Security Audit Logs | Admin Only |
#   m a n o j - t t  
 