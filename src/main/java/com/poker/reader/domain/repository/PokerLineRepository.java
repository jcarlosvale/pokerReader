package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.PokerLine;
import com.poker.reader.domain.repository.dto.ShowCardDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface PokerLineRepository extends JpaRepository<PokerLine, Long> {

    String SAVE_NEW_TOURNAMENTS =
            "INSERT INTO tournaments " +
            "(tournament_id, file_name, created_at) " +
            "(" +
            "   select  " +
            "       distinct tournament_id as tournamentId, " +
            "       filename as fileName, " +
            "       now() as createdAt " +
            "   from pokerline " +
            ") " +
            "ON CONFLICT (tournament_id) " +
            "do nothing";
    @Transactional
    @Modifying
    @Query(value = SAVE_NEW_TOURNAMENTS, nativeQuery = true)
    void saveNewTournaments();

    String SAVE_NEW_PLAYERS =
            "INSERT INTO players " +
            "(nickname, created_at) " +
            "( " +
            "   select   " +
            "       distinct nickname, " +
            "       now()  " +
            "   from player_position " +
            ") " +
            "ON CONFLICT (nickname) " +
            "do nothing";
    @Transactional
    @Modifying
    @Query(value = SAVE_NEW_PLAYERS, nativeQuery = true)
    void saveNewPlayers();

    String SAVE_NEW_HANDS = "INSERT INTO hands " +
            "(hand_id, created_at, played_at, tournament_id) " +
            "(" +
            "   select " +
            "       hand_id, " +
            "       now(), " +
            "       to_timestamp((regexp_matches(line, '[0-9]{4}/[0-9]{1,2}/[0-9]{1,2} [0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}'))[1], 'YYYY/MM/DD HH24:MI:SS'), " +
            "       tournament_id " +
            "   from pokerline " +
            "   where  " +
            "   line like '%PokerStars Hand #%' " +
            "   and section = 'HEADER'" +
            ") " +
            "on conflict (hand_id) " +
            "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_NEW_HANDS, nativeQuery = true)
    void saveNewHands();

    String GET_NEW_SEATS =
            "select " +
                    "trim(substring(line, 5, position(':' in line) - 5)) as position, " +
                    "case " +
                    "   when position('mucked [' in line) > 0 then trim(substring(line, position('mucked [' in line)+8, 5))" +
                    "   when position('showed [' in line) > 0 then trim(substring(line, position('showed [' in line)+8, 5))" +
                    "   else null " +
                    "end as cards, " +
                    "   hand_id as hand " +
                    "from pokerline " +
                    "where " +
                    "   is_processed = false " +
                    "   and section = 'SUMMARY' " +
                    "   and line like '%Seat %:%'";
    @Query(value = GET_NEW_SEATS, nativeQuery = true)
    List<ShowCardDto> getNewSeats();

    String SELECT_DISTINCT_HAND_ID =
            "select distinct hand_id from pokerline";
    @Query(value = SELECT_DISTINCT_HAND_ID, nativeQuery = true)
    Set<Long> getDistinctHandIds();

    String COUNT_NOT_PROCESSED_LINES =
            "select count(line) from pokerline where is_processed = false";
    @Query(value = COUNT_NOT_PROCESSED_LINES, nativeQuery = true)
    Long countNotProcessedLines();


    long countByTournamentId(long tournamentId);

    String UPDATE_TO_PROCESSED_LINES =
            "UPDATE pokerline " +
                    "SET is_processed = true " +
                    "WHERE is_processed = false ";
    @Transactional
    @Modifying
    @Query(value = UPDATE_TO_PROCESSED_LINES, nativeQuery = true)
    void updateToProcessedLines();

    String SAVE_PLAYER_POSITION =
            "INSERT INTO player_position " +
            "(hand_id, nickname, position, stack) " +
            "( " +
                "select hand_id, " +
                        "trim(substring(line from 'Seat [0-9]*:(.*)\\([0-9]* in chips')) as nickname, " +
                        "cast(trim(substring(line from 'Seat ([0-9]*):')) as int8) as position, " +
                        "cast(trim(substring(line from '\\(([0-9]*) in chips')) as int8) as stack  " +
                "from pokerline " +
                "where " +
                "   section = 'HEADER' " +
                "   and line like '%Seat %:%in chips%'" +
            ") " +
            "on conflict (hand_id, position) " +
            "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_PLAYER_POSITION, nativeQuery = true)
    void savePlayerPosition();

    String SAVE_CARDS_OF_PLAYER =
            "INSERT INTO cards_of_player " +
            "(position, cards, hand_id) " +
            "(" +
            "   select " +
            "       cast(substring(line from 'Seat ([0-9]*):') as int8) as position, " +
            "       case " +
            "           when position('mucked [' in line) > 0 then substring(line from 'mucked \\[(.{5})\\]') " +
            "           when position('showed [' in line) > 0 then substring(line from 'showed \\[(.{5})\\]') " +
            "           else null " +
            "       end as cards, " +
            "       hand_id as hand " +
            "   from pokerline " +
            "   where " +
            "       section = 'SUMMARY' " +
            "       and line like '%Seat %:%' " +
            "       and (line like '%mucked [%' or line like '%showed [%')" +
            ")" +
            "on conflict (hand_id, cards, position) " +
            "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_CARDS_OF_PLAYER, nativeQuery = true)
    void saveCardsOfPlayer();
}
