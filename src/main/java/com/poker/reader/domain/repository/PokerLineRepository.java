package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.PokerLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokerLineRepository extends JpaRepository<PokerLine, Long> {

    long countByPokerFileId(long fileId);
}
