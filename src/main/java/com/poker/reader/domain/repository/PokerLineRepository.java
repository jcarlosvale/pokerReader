package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.PokerLine;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokerLineRepository extends JpaRepository<PokerLine, Long> {

    String QUERY_PLAYER_LINES_BY_FILE_ID = "select line from pokerline where \n"
            + "line like '%Seat %:%in chips)'\n"
            + "and section = 'HEADER' \n"
            + "and poker_file_id = :fileId";
    List<String>

    long countByPokerFileId(long fileId);
}
