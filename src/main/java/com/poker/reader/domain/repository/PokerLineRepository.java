package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.PokerLine;
import com.poker.reader.domain.repository.dto.ShowCardDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PokerLineRepository extends JpaRepository<PokerLine, Long> {
/*
    String QUERY_PLAYER_LINES_BY_FILE_ID = "select line from pokerline where \n"
            + "line like '%Seat %:%in chips)'\n"
            + "and section = 'HEADER' \n"
            + "and poker_file_id = :fileId";
    List<String>


 */

    String GET_NEW_SEATS =
            "select " +
                    "\ttrim(substring(line, 6,1)) as position, " +
                    "case " +
                    "   when position('mucked [' in line) > 0 then trim(substring(line, position('mucked [' in line)+8, 5))" +
                    "   when position('showed [' in line) > 0 then trim(substring(line, position('showed [' in line)+8, 5))" +
                    "   else null " +
                    "end as cards, " +
                    "   hand_id as hand " +
                    "from pokerline pl " +
                    "join pokerfile pf on (pl.poker_file_id = pf.poker_file_id) " +
                    "where " +
                    "   pf.is_processed = false " +
                    "   and section = 'SUMMARY' " +
                    "   and line like '%Seat %:%'";

    long countByPokerFileId(long fileId);

    @Query(value = GET_NEW_SEATS, nativeQuery = true)
    List<ShowCardDto> getNewSeats();

}
