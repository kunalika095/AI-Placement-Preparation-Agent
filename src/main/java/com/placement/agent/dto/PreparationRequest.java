package com.placement.agent.dto;

import lombok.Data;

@Data
public class PreparationRequest {
    private String studentName;
    private String jobRole;
    private String programmingLanguage;
    private String topic;
    private String difficultyLevel;
}
