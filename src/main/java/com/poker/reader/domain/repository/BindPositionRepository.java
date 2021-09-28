package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.BlindPosition;
import com.poker.reader.domain.model.HandPositionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BindPositionRepository extends JpaRepository<BlindPosition, HandPositionId> {
}
