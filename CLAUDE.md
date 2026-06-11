# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Java Exam Paper Generation System ("Java 程序设计基础 -- 出题组卷系统") -- a full-stack application for managing a question bank and generating exam papers for a Java programming fundamentals course. Pre-loaded with ~150 questions across 10 chapters and 6 question types, with 20 pre-built exam papers initialized on first startup.

## Tech Stack

- **Backend**: Spring Boot 3.4.2, Java 17, Spring Data JPA, H2 in-memory database, Lombok, Apache POI (Word export)
- **Frontend**: Vue 3, Vite, Element Plus (Chinese locale), Vue Router, Axios
- **Build**: Maven (backend), npm (frontend)
- **Database**: H2 in-memory (`jdbc:h2:mem:examdb`), JPA ddl-auto=update, H2 console at `/h2-console`

## Common Commands

```bash
# One-click start/stop (recommended)
./server.sh start       # Linux/macOS -- starts both backend and frontend
./server.sh stop
./server.sh restart
./server.sh status

# Windows
server.bat start
server.bat stop
server.bat restart
server.bat status

# Manual backend
cd backend
mvn spring-boot:run          # starts on http://localhost:8080

# Manual frontend
cd frontend
npm install                   # first time only
npm run dev                   # starts on http://localhost:5173

# Frontend build for production
cd frontend
npm run build                 # outputs to frontend/dist/
```

## Architecture

### Monorepo Layout

```
backend/              Spring Boot application (com.exam package)
  src/main/java/com/exam/
    entity/           JPA entities: Question, ExamPaper, PaperQuestion
    repository/       Spring Data JPA interfaces
    service/          Business logic (auto-generation algorithm in ExamPaperService)
    controller/       REST controllers (/api/questions, /api/papers)
    dto/              Request/Response DTOs
    config/           WebConfig (CORS), DataInitializer (seed data), GlobalExceptionHandler
    enums/            QuestionType (6 types), Difficulty (3 levels)
  src/main/resources/
    application.yml   H2 datasource, JPA config, Jackson settings
    questions.json    Seed data -- ~150 questions loaded on first startup
frontend/             Vue 3 SPA
  src/
    api/index.js      Axios wrapper with questionApi and paperApi objects
    router.js         4 routes: papers list, paper preview, question bank, auto-generate
    views/            Page components (QuestionBank, PaperList, PaperPreview, AutoGenerate)
    App.vue           Shell with nav header and router-view
  vite.config.js      Dev server on :5173, proxies /api to backend :8080
server.sh/.bat        Unified start/stop/status scripts
```

### Data Model (3 entities)

- **Question** -- The question bank. Fields: type (enum), chapter, difficulty (enum), content, options (JSON string for choice types), answer, explanation, defaultScore, source
- **ExamPaper** -- An exam paper. Fields: title, totalScore, durationMinutes, description, createdAt. Has `@OneToMany` to PaperQuestion.
- **PaperQuestion** -- Join table linking papers to questions with `questionOrder` and per-paper `score`. Uses `@ManyToOne` lazy fetching to both ExamPaper and Question.

The `PaperQuestion.score` allows different papers to assign different point values to the same question. `Question.options` is stored as a JSON string (array of `{label, text}` objects) and parsed on both frontend and Word export.

### Auto-Generation Algorithm (ExamPaperService)

The core algorithm in `ExamPaperService.pickQuestions()` works in stages:
1. **By question type** -- iterates through all 6 QuestionTypes with configurable counts and per-type score values
2. **By source ratio** -- splits candidates into "课后习题原题" (textbook) vs network sources based on `textbookPercent`/`networkPercent`
3. **By difficulty ratio** -- within each source pool, applies `easyPercent`/`mediumPercent`/`hardPercent`
4. **Fallback** -- if a source or difficulty pool runs dry, fills remaining slots from the full candidate list

### Data Initialization (DataInitializer)

Implements `CommandLineRunner`. On first startup (when question count is 0):
1. Loads all questions from `classpath:questions.json`
2. Creates 20 pre-built exam papers, each with 28 questions totaling 100 points (10 single-choice x2, 5 multiple-choice x4, 5 true/false x2, 5 fill-blank x4, 2 short-answer x10, 1 programming x10)

The JSON file can be replaced with an exported file from the "export questions" API to update the seed bank.

### API Surface

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/questions?type=&chapter=&difficulty=&source=&page=0&size=20` | Paginated question query with filters |
| GET | `/api/questions/{id}` | Single question |
| POST | `/api/questions` | Create question |
| PUT | `/api/questions/{id}` | Update question |
| DELETE | `/api/questions/{id}` | Delete question (also removes from all papers) |
| GET | `/api/questions/chapters` | All distinct chapters |
| GET | `/api/questions/sources` | All distinct sources |
| GET | `/api/questions/stats` | Counts grouped by type/chapter/difficulty/source |
| GET | `/api/questions/export` | Export all questions as JSON file |
| POST | `/api/questions/import` | Import JSON file (replaces entire bank) |
| GET | `/api/papers` | List all papers |
| GET | `/api/papers/{id}` | Paper with full question details |
| POST | `/api/papers` | Manually create paper |
| POST | `/api/papers/auto-generate` | Auto-generate paper |
| DELETE | `/api/papers/{id}` | Delete paper |
| GET | `/api/papers/{id}/export` | Export paper as .docx (Word) |

### Frontend Routing

- `/papers` -- Paper list (default route, redirected from `/`)
- `/questions` -- Question bank management with filtering, pagination, CRUD, import/export
- `/auto-generate` -- Auto-generation form (type counts, difficulty sliders, source ratio sliders, chapter multi-select)
- `/papers/:id` -- Paper preview with grouped display and show/hide answers

### Key Patterns and Conventions

- **No test directory** -- There is no `src/test` with actual tests. The project has no automated test suite.
- **H2 in-memory** -- Data is lost on restart. The DataInitializer re-seeds from `questions.json` each time.
- **Lombok everywhere** -- All entities and DTOs use `@Data`, `@Getter/@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`.
- **Lazy loading workaround** -- `ExamPaperService` explicitly sets `paper.setPaperQuestions(savedPqs)` after save to avoid triggering lazy loading (which can cause H2 deadlocks). When modifying paper-question logic, maintain this pattern.
- **Options as JSON string** -- Question options are stored as a serialized JSON array string in the database, not as a separate table. Parsed on read in frontend (`JSON.parse`) and Word export (regex extraction).
- **Word export uses Apache POI** -- `ExamPaperService.exportToWord()` builds an `XWPFDocument` with sections grouped by question type, Chinese numbering (一/二/三...).
- **CORS configured for dev** -- `WebConfig` allows `localhost:5173` and `localhost:3000`. Vite dev server proxies `/api` to `localhost:8080`.
- **Chinese UI** -- Element Plus configured with `zh-cn` locale. All user-facing strings are in Chinese.

### Potential Pitfalls

- **H2 deadlock risk** -- The code has specific workarounds for H2 lazy-loading deadlocks (see explicit collection assignment in service). If you add new service methods that traverse `ExamPaper.paperQuestions`, copy this pattern.
- **Import replaces everything** -- The question import API (`POST /api/questions/import`) calls `questionRepository.deleteAll()` before inserting. This is destructive.
- **BAT script hardcodes JDK path** -- `server.bat` has `JAVA_HOME=C:\Program Files\Java\jdk-17.0.2`. May need adjustment per machine.
- **No authentication** -- All API endpoints are open with no auth layer.
