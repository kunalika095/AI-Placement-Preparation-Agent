package com.placement.agent.controller;

import com.placement.agent.dto.HistoryItemResponse;
import com.placement.agent.dto.PreparationRequest;
import com.placement.agent.model.PlacementPreparation;
import com.placement.agent.service.PlacementPreparationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing end-points for creating, listing, viewing, and deleting
 * campus placement preparation configurations and results.
 */
@RestController
@RequestMapping("/api/preparation")
public class PlacementPreparationController {

    private static final Logger logger = LoggerFactory.getLogger(PlacementPreparationController.class);

    private final PlacementPreparationService service;

    @Autowired
    public PlacementPreparationController(PlacementPreparationService service) {
        this.service = service;
    }

    /**
     * POST /api/preparation/generate
     * Validates inputs, makes requests to Google Gemini, stores the output, and returns it.
     */
    @PostMapping("/generate")
    public ResponseEntity<PlacementPreparation> generatePlan(@Valid @RequestBody PreparationRequest request) {
        logger.info("REST request to generate preparation plan for student: {}", request.getStudentName());
        PlacementPreparation plan = service.generateAndSavePlan(request);
        return new ResponseEntity<>(plan, HttpStatus.CREATED);
    }

    /**
     * GET /api/preparation/history
     * Returns a summary listing of all historically generated plans.
     */
    @GetMapping("/history")
    public ResponseEntity<List<HistoryItemResponse>> getHistory() {
        logger.info("REST request to get preparation plan history list");
        List<HistoryItemResponse> historyList = service.getPreparationHistory();
        return ResponseEntity.ok(historyList);
    }

    /**
     * GET /api/preparation/history/{id}
     * Returns the full details including AI response contents for a given historical plan.
     */
    @GetMapping("/history/{id}")
    public ResponseEntity<PlacementPreparation> getPlanDetails(@PathVariable String id) {
        logger.info("REST request to get details for plan ID: {}", id);
        PlacementPreparation plan = service.getPlanById(id);
        return ResponseEntity.ok(plan);
    }

    /**
     * DELETE /api/preparation/history/{id}
     * Deletes a plan from history.
     */
    @DeleteMapping("/history/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable String id) {
        logger.info("REST request to delete plan ID: {}", id);
        service.deletePlanById(id);
        return ResponseEntity.noContent().build();
    }
}
