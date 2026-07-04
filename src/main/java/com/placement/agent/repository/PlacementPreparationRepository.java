package com.placement.agent.repository;

import com.placement.agent.model.PlacementPreparation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link PlacementPreparation} documents in MongoDB Atlas.
 * Inherits standard CRUD operations from MongoRepository.
 */
@Repository
public interface PlacementPreparationRepository extends MongoRepository<PlacementPreparation, String> {
}
