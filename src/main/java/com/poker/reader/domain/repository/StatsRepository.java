package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.HandPositionId;
import com.poker.reader.domain.model.Stats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatsRepository extends JpaRepository<Stats, HandPositionId> {

}
