package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.PokerFile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PokerFileRepository extends JpaRepository<PokerFile, Long> {

    String FIND_POKERFILE_ID = "select poker_file_id from pokerfile where is_processed = false";

    boolean existsByFileName(String filename);

    @Query(value = FIND_POKERFILE_ID, nativeQuery = true)
    List<Long> getPokerFileNotProcessedIds();
}
