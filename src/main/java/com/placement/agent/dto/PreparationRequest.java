package com.placement.agent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for validating and holding student requests for placement preparation.
 */
public class PreparationRequest {

    @NotBlank(message = "Student Name is required")
    @Size(min = 2, max = 100, message = "Student Name must be between 2 and 100 characters")
    private String studentName;

    @NotBlank(message = "Job Role is required")
    private String jobRole;

    @NotBlank(message = "Programming Language is required")
    private String programmingLanguage;

    @NotBlank(message = "Topic is required")
    private String topic;

    @NotBlank(message = "Difficulty Level is required")
    private String difficultyLevel;

    public PreparationRequest() {
    }

    public PreparationRequest(String studentName, String jobRole, String programmingLanguage, String topic, String difficultyLevel) {
        this.studentName = studentName;
        this.jobRole = jobRole;
        this.programmingLanguage = programmingLanguage;
        this.topic = topic;
        this.difficultyLevel = difficultyLevel;
    }

    // Getters and Setters
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

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
}
