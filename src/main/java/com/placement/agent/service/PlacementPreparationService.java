package com.placement.agent.service;

import com.placement.agent.dto.HistoryItemResponse;
import com.placement.agent.dto.PreparationRequest;
import com.placement.agent.exception.ResourceNotFoundException;
import com.placement.agent.model.AiResponse;
import com.placement.agent.model.PlacementPreparation;
import com.placement.agent.repository.PlacementPreparationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service orchestrating preparation plan generation, storage, listing, and deletion.
 */
@Service
public class PlacementPreparationService {

    private static final Logger logger = LoggerFactory.getLogger(PlacementPreparationService.class);

    private final GeminiService geminiService;
    private final PlacementPreparationRepository repository;

    @Autowired
    public PlacementPreparationService(GeminiService geminiService, PlacementPreparationRepository repository) {
        this.geminiService = geminiService;
        this.repository = repository;
    }

    /**
     * Triggers Gemini API content generation, persists the record to MongoDB Atlas, and returns the result.
     *
     * @param request the student parameter inputs
     * @return the fully generated and saved PlacementPreparation document
     */
    public PlacementPreparation generateAndSavePlan(PreparationRequest request) {
        logger.info("Initiating plan generation for student: {}", request.getStudentName());
        
        // Generate content from Gemini
        AiResponse response = geminiService.generatePreparationContent(request);

        // Map and save to repository database
        PlacementPreparation preparation = new PlacementPreparation(
                request.getStudentName(),
                request.getJobRole(),
                request.getProgrammingLanguage(),
                request.getTopic(),
                request.getDifficultyLevel(),
                response
        );

        PlacementPreparation savedPrep = repository.save(preparation);
        logger.info("Successfully saved placement plan with ID: {}", savedPrep.getId());
        return savedPrep;
    }

    /**
     * Retrieves lightweight metadata of all saved plans, sorted by creation date descending.
     *
     * @return a list of lightweight HistoryItemResponses
     */
    public List<HistoryItemResponse> getPreparationHistory() {
        logger.info("Fetching preparation history list");
        List<PlacementPreparation> preps = repository.findAll(Sort.by(Sort.Direction.DESC, "createdDate"));
        
        return preps.stream()
                .map(p -> new HistoryItemResponse(
                        p.getId(),
                        p.getStudentName(),
                        p.getJobRole(),
                        p.getProgrammingLanguage(),
                        p.getTopic(),
                        p.getDifficulty(),
                        p.getCreatedDate()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a detailed preparation plan by its unique ID.
     *
     * @param id the document ID
     * @return the full PlacementPreparation document
     * @throws ResourceNotFoundException if the ID does not exist
     */
    public PlacementPreparation getPlanById(String id) {
        logger.info("Retrieving plan by ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Preparation history item not found with ID: " + id));
    }

    /**
     * Deletes a preparation plan from history.
     *
     * @param id the document ID
     * @throws ResourceNotFoundException if the ID does not exist
     */
    public void deletePlanById(String id) {
        logger.info("Deleting plan by ID: {}", id);
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Preparation history item not found with ID: " + id);
        }
        repository.deleteById(id);
        logger.info("Successfully deleted plan with ID: {}", id);
    }
}
