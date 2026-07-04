# AI Placement Preparation Agent

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg?style=flat-square)](https://www.oracle.com/java/technologies/downloads/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen.svg?style=flat-square)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-green.svg?style=flat-square)](https://www.mongodb.com/atlas)
[![Gemini](https://img.shields.io/badge/Gemini%20API-2.5%20Flash-blue.svg?style=flat-square)](https://aistudio.google.com/)
[![Docker](https://img.shields.io/badge/Docker-Supported-cyan.svg?style=flat-square)](https://www.docker.com/)

A production-ready full-stack AI web application designed to help graduating students prepare for campus placements. The agent leverages Google's state-of-the-art **Gemini 2.5 Flash** model with strict schema-constrained outputs to generate personalized behavioral HR questions, technical conceptual questions, coding tests, and quantitative aptitude quizzes, alongside structured phase-based learning roadmaps and preparation tips.

---

## Table of Contents
1. [Key Features](#key-features)
2. [System Architecture](#system-architecture)
3. [API Documentation](#api-documentation)
4. [Prompt Engineering](#prompt-engineering)
5. [Prerequisites](#prerequisites)
6. [Local Installation & Setup](#local-installation--setup)
7. [Running with Docker](#running-with-docker)
8. [Testing & Verification](#testing--verification)

---

## Key Features

*   **Responsive Dark-Mode-First UI:** Premium glassmorphic interface built using vanilla HTML5, CSS3, and JavaScript, fully responsive across mobile, tablet, and desktop viewports.
*   **Behavioral HR Questions:** 5 custom questions assessing behavioral fit for the specific target role.
*   **Technical Domain Questions:** 5 conceptual questions assessing deep understanding of chosen topics.
*   **Structured Coding Sandboxes:** 3 programming problems matching the selected difficulty level, complete with constraints, inputs, outputs, and solution code.
*   **Interactive Aptitude Quiz:** 5 quantitative and logical reasoning questions styled as interactive quizzes with immediate score counts, solution reveals, and detailed explanations.
*   **Step-by-step Timeline Roadmap:** Chronological phases containing study durations, topic subsets, and learning resources.
*   **Plan History Management:** Fully integrated CRUD controls allowing students to save, list, reload, and delete preparation histories from MongoDB Atlas.

---

## System Architecture

The application is structured as a single-unit monolithic architecture where the Spring Boot backend serves both the REST APIs and the static frontend SPA directly.

```
┌────────────────────────────────────────────────────────┐
│                      Web Browser                       │
│  (Home Page, Inputs Form, Interactive Quiz, History)   │
└───────────▲────────────────────────────────┬───────────┘
            │                                │
      Static Assets                      REST Queries
     (HTML/CSS/JS)                        (JSON DTOs)
            │                                │
┌───────────┴────────────────────────────────▼───────────┐
│                   Spring Boot Backend                  │
│                                                        │
│  ┌──────────────┐   ┌──────────────┐   ┌────────────┐  │
│  │ Controllers  ├──►│   Services   ├──►│Repository  │  │
│  └──────────────┘   └──────┬───────┘   └─────┬──────┘  │
└────────────────────────────┼─────────────────┼─────────┘
                             │                 │
                             ▼                 ▼
                     ┌───────────────┐ ┌───────────────┐
                     │  Gemini API   │ │ MongoDB Atlas │
                     │ (gemini-2.5)  │ │ (Cloud / Local)
                     └───────────────┘ └───────────────┘
```

### File Hierarchy & Packages

*   **`config`**:
    *   [CorsConfig.java](file:///C:/Users/HP/.gemini/antigravity/scratch/ai-placement-preparation-agent/src/main/java/com/placement/agent/config/CorsConfig.java) - Global CORS mappings enabling flexible deployment.
    *   [GeminiConfig.java](file:///C:/Users/HP/.gemini/antigravity/scratch/ai-placement-preparation-agent/src/main/java/com/placement/agent/config/GeminiConfig.java) - RestClient bean configuration with 60-second read timeouts for LLM calls.
*   **`controller`**:
    *   [PlacementPreparationController.java](file:///C:/Users/HP/.gemini/antigravity/scratch/ai-placement-preparation-agent/src/main/java/com/placement/agent/controller/PlacementPreparationController.java) - Exposes REST endpoints for the client.
*   **`service`**:
    *   [PromptBuilder.java](file:///C:/Users/HP/.gemini/antigravity/scratch/ai-placement-preparation-agent/src/main/java/com/placement/agent/service/PromptBuilder.java) - Contains prompt template construction logic.
    *   [GeminiService.java](file:///C:/Users/HP/.gemini/antigravity/scratch/ai-placement-preparation-agent/src/main/java/com/placement/agent/service/GeminiService.java) - Formulates requests, integrates the REST schema, and queries Gemini.
    *   [PlacementPreparationService.java](file:///C:/Users/HP/.gemini/antigravity/scratch/ai-placement-preparation-agent/src/main/java/com/placement/agent/service/PlacementPreparationService.java) - Handles coordination, mapping, and database saves.
*   **`repository`**:
    *   [PlacementPreparationRepository.java](file:///C:/Users/HP/.gemini/antigravity/scratch/ai-placement-preparation-agent/src/main/java/com/placement/agent/repository/PlacementPreparationRepository.java) - Spring Data CRUD mappings for MongoDB.
*   **`model`**:
    *   [PlacementPreparation.java](file:///C:/Users/HP/.gemini/antigravity/scratch/ai-placement-preparation-agent/src/main/java/com/placement/agent/model/PlacementPreparation.java) - MongoDB Entity representing saved plans.
    *   [AiResponse.java](file:///C:/Users/HP/.gemini/antigravity/scratch/ai-placement-preparation-agent/src/main/java/com/placement/agent/model/AiResponse.java) - Encapsulates coding, aptitude, HR, roadmap models.
*   **`dto`**:
    *   [PreparationRequest.java](file:///C:/Users/HP/.gemini/antigravity/scratch/ai-placement-preparation-agent/src/main/java/com/placement/agent/dto/PreparationRequest.java) - Captures user parameters with validation checks.
    *   [HistoryItemResponse.java](file:///C:/Users/HP/.gemini/antigravity/scratch/ai-placement-preparation-agent/src/main/java/com/placement/agent/dto/HistoryItemResponse.java) - Lightweight structure for listing history elements.
*   **`exception`**:
    *   [GlobalExceptionHandler.java](file:///C:/Users/HP/.gemini/antigravity/scratch/ai-placement-preparation-agent/src/main/java/com/placement/agent/exception/GlobalExceptionHandler.java) - Standardizes errors into structured JSON.

---

## API Documentation

All APIs are prefixed with `/api/preparation`.

### 1. Generate Preparation Plan
*   **URL:** `/api/preparation/generate`
*   **Method:** `POST`
*   **Request Body (JSON):**
    ```json
    {
      "studentName": "John Doe",
      "jobRole": "Software Engineer",
      "programmingLanguage": "Java",
      "topic": "Concurrency and Threads",
      "difficultyLevel": "Intermediate"
    }
    ```
*   **Response Status:** `201 Created`
*   **Response Body:** Full `PlacementPreparation` document containing the unique ID, inputs, created date, and nested structured `aiResponse` elements.

### 2. Get History List
*   **URL:** `/api/preparation/history`
*   **Method:** `GET`
*   **Response Status:** `200 OK`
*   **Response Body (JSON):** Array of lightweight history summary items (excludes the heavy AI response body).
    ```json
    [
      {
        "id": "64a4b8df1a9d5a864c23ba78",
        "studentName": "John Doe",
        "jobRole": "Software Engineer",
        "programmingLanguage": "Java",
        "topic": "Concurrency and Threads",
        "difficulty": "Intermediate",
        "createdDate": "2026-07-03T23:30:15"
      }
    ]
    ```

### 3. Get Plan Details
*   **URL:** `/api/preparation/history/{id}`
*   **Method:** `GET`
*   **Response Status:** `200 OK`
*   **Response Body:** Full `PlacementPreparation` document matched by ID. Returns `404 Not Found` if ID does not exist.

### 4. Delete Plan
*   **URL:** `/api/preparation/history/{id}`
*   **Method:** `DELETE`
*   **Response Status:** `204 No Content`

---

## Prompt Engineering

To enforce strict, syntactically correct responses and avoid runtime parsing failures, the application uses **Structured Output JSON Schemas** with the Google Gemini API.

The configuration requests the Gemini model `gemini-2.5-flash` with the following configuration:
1.  `responseMimeType` set to `application/json`.
2.  `responseSchema` defining the exact object hierarchy of [AiResponse](file:///C:/Users/HP/.gemini/antigravity/scratch/ai-placement-preparation-agent/src/main/java/com/placement/agent/model/AiResponse.java).

The prompt built in [PromptBuilder.java](file:///C:/Users/HP/.gemini/antigravity/scratch/ai-placement-preparation-agent/src/main/java/com/placement/agent/service/PromptBuilder.java) incorporates parameters and restricts output:

```
You are an AI Placement Preparation Agent and expert placement trainer.

Generate a highly personalized campus placement preparation package for the student:
- Student Name: {studentName}
- Target Job Role: {jobRole}
- Target Programming Language: {programmingLanguage}
- Key Topic/Domain: {topic}
- Difficulty Level: {difficultyLevel}

You must generate exactly the following content:
1. 5 HR Questions
2. 5 Technical Questions
3. 3 Coding Questions (each with title, problemStatement, constraints, sampleInput, sampleOutput, explanation)
4. 5 Aptitude Questions (each with question, options, correctAnswer, explanation)
5. Preparation Tips (list of 4-6 tips)
6. Learning Roadmap (step-by-step roadmap phases)

Return only valid JSON matching the requested schema.
```

---

## Prerequisites

*   **Java:** Java 21 (LTS)
*   **Maven:** Maven 3.8+ (or Maven Wrapper `mvnw` included in workspace)
*   **MongoDB:** MongoDB Instance (Local or MongoDB Atlas Cloud instance)
*   **Gemini API:** A Google Gemini API Key. You can get one from [Google AI Studio](https://aistudio.google.com/).

---

## Local Installation & Setup

1.  **Configure Application Properties:**
    Copy the template file [application.properties.template](file:///C:/Users/HP/.gemini/antigravity/scratch/ai-placement-preparation-agent/src/main/resources/application.properties.template) to `application.properties`:
    ```cmd
    copy src\main\resources\application.properties.template src\main\resources\application.properties
    ```

2.  **Edit configurations in `application.properties`:**
    *   Set `spring.data.mongodb.uri` to your MongoDB Atlas cloud URI or leave as `mongodb://localhost:27017/placement_prep` for local databases.
    *   Set `gemini.api.key` to your Google Gemini API key.

3.  **Build and Run the Application:**
    Compile the Java files and run using the Maven wrapper:
    ```cmd
    .\mvnw.cmd spring-boot:run
    ```

4.  **Access the Application:**
    Open your web browser and navigate to:
    ```
    http://localhost:8080/
    ```

---

## Running with Docker

You can spin up the application along with a MongoDB container using Docker Compose.

1.  **Set Environment Variables:**
    Set your Gemini API Key in your terminal shell:
    *   **PowerShell:**
        ```powershell
        $env:GEMINI_API_KEY="your_api_key_here"
        ```
    *   **CMD:**
        ```cmd
        set GEMINI_API_KEY=your_api_key_here
        ```

2.  **Run Docker Compose:**
    Launch the services in detached mode:
    ```cmd
    docker compose up --build -d
    ```
    This command will:
    *   Compile the code using a multi-stage Alpine Maven builder.
    *   Package the Spring Boot application.
    *   Spin up a MongoDB database container with data volumes.
    *   Start the Spring Boot container on port `8080`.

3.  **Shutdown Services:**
    To stop and clean up containers:
    ```cmd
    docker compose down -v
    ```

---

## Testing & Verification

Automated unit tests mock the database operations and the external Gemini HTTP calls. To run the test suite:

```cmd
.\mvnw.cmd test
```
