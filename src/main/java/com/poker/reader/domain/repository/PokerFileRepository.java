package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.PokerFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PokerFileRepository extends JpaRepository<PokerFile, Long> {

    String FIND_POKERFILE_ID = "select poker_file_id from pokerfile where is_processed = false";
    String SAVE_ALL_AS_PROCESSED =
            "UPDATE public.pokerfile " +
            "   SET is_processed=true ";

    boolean existsByFileName(String filename);

    @Query(value = FIND_POKERFILE_ID, nativeQuery = true)
    List<Long> getPokerFileNotProcessedIds();

    @Transactional
    @Modifying
    @Query(value = SAVE_ALL_AS_PROCESSED, nativeQuery = true)
    void saveAllAsProcessed();
}
