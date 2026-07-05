package com.placement.agent.controller;

import com.placement.agent.dto.PreparationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/preparation")
@Slf4j
public class PreparationController {

    @Value("${GEMINI_API_KEY}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/generate")
    public ResponseEntity<?> generatePlan(@RequestBody PreparationRequest request) {
        try {
            String prompt = buildPrompt(request);
            // Gemini API endpoint (pro model)
            String geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + geminiApiKey;

            Map<String, Object> body = new HashMap<>();
            body.put("contents", new Object[]{
                    Map.of("role", "user", "parts", new Object[]{
                            Map.of("text", prompt)
                    })
            });

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            Map<String, Object> geminiResponse = restTemplate.postForObject(geminiUrl, entity, Map.class);
            return ResponseEntity.ok(geminiResponse);
        } catch (Exception e) {
            log.error("Error generating preparation plan", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    private String buildPrompt(PreparationRequest req) {
        return String.format(
                "Generate an interview preparation plan for a student named %s applying for a %s role. " +
                        "The student knows %s programming language and wants to focus on %s with difficulty level %s. " +
                        "Provide: 5 HR interview questions, 5 technical interview questions, 3 coding questions, 5 aptitude questions, preparation tips, and a learning roadmap.",
                req.getStudentName(),
                req.getJobRole(),
                req.getProgrammingLanguage(),
                req.getTopic(),
                req.getDifficultyLevel()
        );
    }
}
