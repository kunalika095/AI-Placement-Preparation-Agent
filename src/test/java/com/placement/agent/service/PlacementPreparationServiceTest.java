package com.placement.agent.service;

import com.placement.agent.dto.HistoryItemResponse;
import com.placement.agent.dto.PreparationRequest;
import com.placement.agent.model.AiResponse;
import com.placement.agent.model.PlacementPreparation;
import com.placement.agent.repository.PlacementPreparationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlacementPreparationServiceTest {

    @Mock
    private GeminiService geminiService;

    @Mock
    private PlacementPreparationRepository repository;

    @InjectMocks
    private PlacementPreparationService service;

    private PreparationRequest request;
    private AiResponse mockAiResponse;
    private PlacementPreparation mockPlan;

    @BeforeEach
    void setUp() {
        request = new PreparationRequest("John Doe", "Software Engineer", "Java", "OOP", "Medium");
        mockAiResponse = new AiResponse();
        mockAiResponse.setHrQuestions(List.of("HR Q1"));
        mockAiResponse.setTechnicalQuestions(List.of("Tech Q1"));

        mockPlan = new PlacementPreparation("John Doe", "Software Engineer", "Java", "OOP", "Medium", mockAiResponse);
        mockPlan.setId("mock-id-123");
    }

    @Test
    void generateAndSavePlan_Success() {
        when(geminiService.generatePreparationContent(any(PreparationRequest.class))).thenReturn(mockAiResponse);
        when(repository.save(any(PlacementPreparation.class))).thenReturn(mockPlan);

        PlacementPreparation result = service.generateAndSavePlan(request);

        assertNotNull(result);
        assertEquals("mock-id-123", result.getId());
        assertEquals("John Doe", result.getStudentName());
        assertEquals(mockAiResponse, result.getAiResponse());
        verify(geminiService, times(1)).generatePreparationContent(request);
        verify(repository, times(1)).save(any(PlacementPreparation.class));
    }

    @Test
    void getPreparationHistory_Success() {
        when(repository.findAll(any(Sort.class))).thenReturn(List.of(mockPlan));

        List<HistoryItemResponse> history = service.getPreparationHistory();

        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals("mock-id-123", history.get(0).getId());
        assertEquals("John Doe", history.get(0).getStudentName());
    }

    @Test
    void getPlanById_Success() {
        when(repository.findById("mock-id-123")).thenReturn(Optional.of(mockPlan));

        PlacementPreparation result = service.getPlanById("mock-id-123");

        assertNotNull(result);
        assertEquals("mock-id-123", result.getId());
        assertEquals("John Doe", result.getStudentName());
    }

    @Test
    void deletePlanById_Success() {
        when(repository.existsById("mock-id-123")).thenReturn(true);
        doNothing().when(repository).deleteById("mock-id-123");

        assertDoesNotThrow(() -> service.deletePlanById("mock-id-123"));
        verify(repository, times(1)).deleteById("mock-id-123");
    }
}
