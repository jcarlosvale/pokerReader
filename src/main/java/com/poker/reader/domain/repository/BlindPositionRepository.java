package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.BlindPosition;
import com.poker.reader.domain.model.HandPositionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlindPositionRepository extends JpaRepository<BlindPosition, HandPositionId> {
    BlindPosition findBlindPositionByHandAndPlace(Long hand, String place);
}
