package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.PokerFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokerFileRepository extends JpaRepository<PokerFile, Long> {
    boolean existsByFileName(String filename);
}
