package com.placement.agent.dto;

import java.time.LocalDateTime;

/**
 * Lightweight DTO representing a simplified history item.
 * Excludes the detailed AI response to reduce payload size in summary lists.
 */
public class HistoryItemResponse {

    private String id;
    private String studentName;
    private String jobRole;
    private String programmingLanguage;
    private String topic;
    private String difficulty;
    private LocalDateTime createdDate;

    public HistoryItemResponse() {
    }

    public HistoryItemResponse(String id, String studentName, String jobRole, String programmingLanguage, String topic, String difficulty, LocalDateTime createdDate) {
        this.id = id;
        this.studentName = studentName;
        this.jobRole = jobRole;
        this.programmingLanguage = programmingLanguage;
        this.topic = topic;
        this.difficulty = difficulty;
        this.createdDate = createdDate;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
