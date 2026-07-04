package com.placement.agent.service;

import com.placement.agent.dto.PreparationRequest;
import org.springframework.stereotype.Component;

/**
 * Service class dedicated to prompt engineering for Google Gemini API.
 * Builds descriptive prompts using target parameters to guide the AI to generate high-quality placement prep material.
 */
@Component
public class PromptBuilder {

    /**
     * Builds a detailed text prompt for the Gemini AI.
     * Instructs the model to assume the role of an expert placement trainer and generate
     * customized, structured preparation contents for the specific student and role.
     *
     * @param request the student's input parameters
     * @return the engineered prompt string
     */
    public String buildPrompt(PreparationRequest request) {
        return String.format(
            "You are an AI Placement Preparation Agent and expert placement trainer.\n\n" +
            "Generate a highly personalized campus placement preparation package for the student:\n" +
            "- Student Name: %s\n" +
            "- Target Job Role: %s\n" +
            "- Target Programming Language: %s\n" +
            "- Key Topic/Domain: %s\n" +
            "- Difficulty Level: %s\n\n" +
            "You must generate exactly the following content:\n" +
            "1. 5 HR Questions: Tailored to the '%s' job role, evaluating behavioral fit, communication, and basic alignment.\n" +
            "2. 5 Technical Questions: Assessing deep conceptual understanding of '%s' using '%s' at '%s' difficulty.\n" +
            "3. 3 Coding Questions: Algorithms, data structures, or code snippets focusing on '%s' in '%s', appropriate for '%s' level. " +
            "Each coding question must include: title, problemStatement, constraints, sampleInput, sampleOutput, and a detailed explanation of the solution logic.\n" +
            "4. 5 Aptitude Questions: Commonly asked quantitative, analytical, or logical reasoning questions for placement tests. " +
            "Each must have: question, 4 options (array of strings), correctAnswer (exactly matching one of the options), and explanation.\n" +
            "5. Preparation Tips: A list of 4-6 highly practical preparation tips tailored for this specific profile and topic.\n" +
            "6. Learning Roadmap: A step-by-step roadmap split into sequential phases (e.g. Phase 1: Fundamentals, Phase 2: Implementation, Phase 3: Mock Testing) containing duration, list of topics to cover, and resources or tips.\n\n" +
            "Return only valid JSON matching the requested schema. Ensure correct JSON format without trailing commas or syntax errors.",
            request.getStudentName(),
            request.getJobRole(),
            request.getProgrammingLanguage(),
            request.getTopic(),
            request.getDifficultyLevel(),
            request.getJobRole(),
            request.getTopic(),
            request.getProgrammingLanguage(),
            request.getDifficultyLevel(),
            request.getTopic(),
            request.getProgrammingLanguage(),
            request.getDifficultyLevel()
        );
    }
}
