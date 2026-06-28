# JournalInsight

Android app for digital journaling with automatic emotion classification, built as a senior thesis project at the University of Puerto Rico, Bayamón Campus.

JournalInsight lets users write free-text journal entries that are automatically classified into one of five emotional categories using a custom Multinomial Naive Bayes model. Mental health professionals can be linked to a patient's account and review their emotional history over time, enabling data-informed, lower-effort clinical monitoring.

**Advisor:** Prof. Nelliud D. Torres, Department of Computer Science, UPR Bayamón

## Overview

- Users write journal entries from a mobile app.
- Each entry is sent to a REST backend, classified by emotion, and stored.
- A linked health professional can browse a patient's entries by date and see the detected emotion for each day.

Emotion categories: `happiness`, `sadness`, `anxiety`, `anger`, `neutral` (and `mixed` when the model isn't confident about a single dominant emotion).

## Architecture

Three-tier client-server architecture:

```
Android Client  --HTTPS/REST-->  Spring Boot API  --SQL-->  PostgreSQL
(Kotlin, Jetpack Compose)        (Java, JPA/Hibernate)      (relational DB)
```

The backend hosts the classifier service, exposes REST endpoints, and persists results. The Android app handles entry creation, professional linking, and history browsing — it never talks to the database directly.

## Tech Stack

**Frontend (Android)**
- Kotlin + Jetpack Compose
- Retrofit2 (HTTP client)
- Android Studio

**Backend**
- Java 17/21 + Spring Boot 3
- Maven
- JPA / Hibernate
- PostgreSQL 16
- IntelliJ IDEA

## Emotion Classifier

The core of the project is `EmotionClassifierService`, a **Multinomial Naive Bayes classifier implemented from scratch in Java** — no external machine learning libraries — to keep the backend lightweight and dependency-free.

Key details:

- **Training data:** ~250 manually labeled journal entries, balanced across the five emotion categories (50 each). Loaded from a CSV at server startup, so the classifier is ready before the first request.
- **Preprocessing:** lowercasing, punctuation removal, stop-word filtering, and a **Bag of Words** representation.
- **Negation handling:** a custom negation-marking step prefixes words following triggers like `not` or `never` with `NOT_`, so the model can distinguish `"I am happy"` from `"I am not happy"` without increasing model complexity.
- **Laplace smoothing:** prevents unseen vocabulary from zeroing out a category's probability.
- **Softmax normalization:** converts log-probabilities into comparable confidence scores.
- **Mixed classification:** if the top emotion scores below 55% confidence and the top two categories aren't both neutral, the entry is labeled `mixed` instead of forcing a single category.

This approach keeps inference computationally cheap, making it suitable for resource-constrained servers.

## REST API

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/entries` | Create a journal entry and return its emotion classification |
| `GET` | `/api/entries/{patientId}` | Retrieve a linked patient's entry history with classifications |
| `DELETE` | `/api/entries/{id}` | Delete a specific entry |

Patients link a professional using the professional's phone number; either party can unlink at any time. The relationship is many-to-many — a professional can monitor multiple patients, and a patient can be linked to multiple professionals.

## Data Model

Core entities: `Account`, `Patient`, `Professional`, `PatientProfessional` (link table), `JournalEntry`, `AnalysisResult`, `EmotionCategory`, `ResultEmotion`.

The schema separates raw entry content from its emotional analysis, so the classification pipeline can evolve independently of how entries are stored.

## Getting Started

### Backend

1. Make sure PostgreSQL 16 is running and a `journalinsight` database exists.
2. Set the required environment variables (do not hardcode credentials in `application.properties`):
   ```
   DB_USERNAME=postgres
   DB_PASSWORD=your_password_here
   ```
3. From `BackEnd/backend`, run:
   ```
   ./mvnw spring-boot:run
   ```
4. The API will be available at `http://localhost:8080`.

### Android App

1. Open the `FrontEnd` folder in Android Studio.
2. Make sure the Retrofit base URL points to your running backend instance.
3. Run on an emulator or physical device.

## Future Work

- Multilingual support (Spanish dataset and classifier).
- Trend dashboard with weekly/monthly emotional timelines for professionals.
- Alerting system for recurring negative emotional patterns.
- Upgrading from Naive Bayes to transformer-based models (e.g., BERT) for better contextual understanding.
- PDF export of a patient's emotional history for formal clinical use.
- iOS support via Kotlin Multiplatform.

## Author

**Ian C. Hernández Vélez**
Department of Computer Science, University of Puerto Rico at Bayamón
ianhndz14@gmail.com

---

© 2026 Ian C. Hernández Vélez. All rights reserved.