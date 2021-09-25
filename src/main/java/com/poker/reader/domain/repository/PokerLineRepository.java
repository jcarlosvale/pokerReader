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

    String GET_SHOW_CARDS_FROM_SHOWDOWN =
            "select " +
                    "trim(substring(line, 1, position(': shows [' in line)-1)) as player, " +
                    "trim(substring(line, position(': shows [' in line)+9, 5)) as cards, " +
                    "trim(hand_id) as hand " +
                    "from pokerline pl " +
                    "where line like ('%: shows [%') and section = 'SHOWDOWN' ";

    String GET_SHOW_CARDS_FROM_SUMMARY =
            "select " +
            "case " +
            "when position(' (button) (small blind)' in line) > 0 then trim(substring(line, 9, position('mucked' in line) - 33)) " +
            "when position(' (small blind)' in line) > 0 then trim(substring(line, 9, position('mucked' in line) - 24)) " +
            "when position(' (big blind)' in line) > 0 then trim(substring(line, 9, position('mucked' in line) - 22)) " +
            "when position(' (button)' in line) > 0 then trim(substring(line, 9, position('mucked' in line) - 19)) " +
            "else trim(substring(line, position(': ' in line)+2, position('mucked' in line) - position(':' in line) - 3)) " +
            "end as player, " +
            "trim(substring(line, position('mucked [' in line)+8, 5)) as cards, " +
            "trim(hand_id) as hand " +
            "from pokerline pl " +
            "where line like ('%mucked [%') and section = 'SUMMARY' ";

    long countByPokerFileId(long fileId);

    @Query(value = GET_SHOW_CARDS_FROM_SHOWDOWN, nativeQuery = true)
    List<ShowCardDto> getShowedCardsFromShowDown();

    @Query(value = GET_SHOW_CARDS_FROM_SUMMARY, nativeQuery = true)
    List<ShowCardDto> getShowedCardsFromSummary();

}
