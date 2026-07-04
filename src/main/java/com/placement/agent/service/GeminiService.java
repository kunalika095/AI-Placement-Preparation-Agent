package com.placement.agent.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.placement.agent.config.GeminiConfig;
import com.placement.agent.dto.PreparationRequest;
import com.placement.agent.exception.GeminiApiException;
import com.placement.agent.model.AiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Service integrating with the Google Gemini API using RestClient.
 * Builds structured payloads including prompt context and schema guidelines,
 * calls the endpoint using gemini-2.5-flash, and parses the response.
 */
@Service
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    private final RestClient restClient;
    private final GeminiConfig geminiConfig;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    @Autowired
    public GeminiService(RestClient restClient, GeminiConfig geminiConfig, PromptBuilder promptBuilder, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.geminiConfig = geminiConfig;
        this.promptBuilder = promptBuilder;
        this.objectMapper = objectMapper;
    }

    /**
     * Generates placement preparation content from Gemini based on the student's request.
     *
     * @param request the parameters supplied by the student
     * @return the structured AiResponse content
     */
    public AiResponse generatePreparationContent(PreparationRequest request) {
        String apiKey = geminiConfig.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.equals("YOUR_GEMINI_API_KEY")) {
            logger.error("Gemini API key is not configured");
            throw new GeminiApiException("Google Gemini API key is missing or unconfigured. Please check your configurations.");
        }

        String prompt = promptBuilder.buildPrompt(request);
        logger.info("Engineered prompt for student: {}", request.getStudentName());

        // Define response schema for Gemini Structured Output
        Map<String, Object> textType = Map.of("type", "STRING");
        Map<String, Object> stringArray = Map.of("type", "ARRAY", "items", textType);

        Map<String, Object> codingQuestionSchema = Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                        "title", textType,
                        "problemStatement", textType,
                        "constraints", textType,
                        "sampleInput", textType,
                        "sampleOutput", textType,
                        "explanation", textType
                ),
                "required", List.of("title", "problemStatement", "constraints", "sampleInput", "sampleOutput", "explanation")
        );

        Map<String, Object> aptitudeQuestionSchema = Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                        "question", textType,
                        "options", stringArray,
                        "correctAnswer", textType,
                        "explanation", textType
                ),
                "required", List.of("question", "options", "correctAnswer", "explanation")
        );

        Map<String, Object> roadmapPhaseSchema = Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                        "phase", textType,
                        "duration", textType,
                        "topics", stringArray,
                        "resourcesOrTips", textType
                ),
                "required", List.of("phase", "duration", "topics", "resourcesOrTips")
        );

        Map<String, Object> responseSchema = Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                        "hrQuestions", stringArray,
                        "technicalQuestions", stringArray,
                        "codingQuestions", Map.of("type", "ARRAY", "items", codingQuestionSchema),
                        "aptitudeQuestions", Map.of("type", "ARRAY", "items", aptitudeQuestionSchema),
                        "preparationTips", stringArray,
                        "learningRoadmap", Map.of("type", "ARRAY", "items", roadmapPhaseSchema)
                ),
                "required", List.of("hrQuestions", "technicalQuestions", "codingQuestions", "aptitudeQuestions", "preparationTips", "learningRoadmap")
        );

        // Build the request body Map
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "responseSchema", responseSchema
                )
        );

        try {
            logger.info("Sending request to Gemini API...");
            String rawResponse = restClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                    .body(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, resp) -> {
                        logger.error("Gemini API call failed with status: {}", resp.getStatusCode());
                        throw new GeminiApiException("Gemini API responded with error status: " + resp.getStatusCode());
                    })
                    .body(String.class);

            logger.debug("Raw Response from Gemini: {}", rawResponse);

            // Parse response structure
            Map<String, Object> responseMap = objectMapper.readValue(rawResponse, Map.class);
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseMap.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new GeminiApiException("No content generated. The Gemini API returned an empty candidate list.");
            }

            Map<String, Object> candidate = candidates.get(0);
            Map<String, Object> content = (Map<String, Object>) candidate.get("content");
            if (content == null) {
                throw new GeminiApiException("Missing content field in response candidate.");
            }

            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts == null || parts.isEmpty()) {
                throw new GeminiApiException("No parts returned in response content.");
            }

            String generatedJson = (String) parts.get(0).get("text");
            logger.info("Gemini response JSON successfully fetched");

            // Map standard Gemini text back to structured response objects
            return objectMapper.readValue(generatedJson, AiResponse.class);

        } catch (GeminiApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error communicating with Gemini API", e);
            throw new GeminiApiException("Unexpected error occurred while requesting preparation plan from Gemini API: " + e.getMessage(), e);
        }
    }
}
