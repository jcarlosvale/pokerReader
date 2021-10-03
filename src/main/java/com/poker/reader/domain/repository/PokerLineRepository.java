package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.PokerLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    String SAVE_NEW_TOURNAMENTS_FROM_HAND =
            "INSERT INTO tournaments " +
                    "(tournament_id, file_name, created_at) " +
                    "(" +
                    "   select  " +
                    "       distinct tournament_id as tournamentId, " +
                    "       filename as fileName, " +
                    "       now() as createdAt " +
                    "   from pokerline " +
                    "   where hand_id = :handId    " +
                    ") " +
                    "ON CONFLICT (tournament_id) " +
                    "do nothing";
    @Transactional
    @Modifying
    @Query(value = SAVE_NEW_TOURNAMENTS_FROM_HAND, nativeQuery = true)
    void saveNewTournamentsFromHand(@Param("handId") long handId);

    String SAVE_NEW_PLAYERS =
            "INSERT INTO players " +
            "(nickname, created_at) " +
            "( " +
            "   select " +
            "       distinct trim(substring(line from 'Seat [0-9]*:(.*)\\([0-9]* in chips')) as nickname, " +
            "       now() " +
            "   from pokerline " +
            "   where " +
            "       section = 'HEADER' " +
            "       and line like '%Seat %:%in chips%'" +
            ") " +
            "ON CONFLICT (nickname) " +
            "do nothing";
    @Transactional
    @Modifying
    @Query(value = SAVE_NEW_PLAYERS, nativeQuery = true)
    void saveNewPlayers();

    String SAVE_NEW_PLAYERS_FROM_HAND =
            "INSERT INTO players " +
                    "(nickname, created_at) " +
                    "( " +
                    "   select " +
                    "       distinct trim(substring(line from 'Seat [0-9]*:(.*)\\([0-9]* in chips')) as nickname, " +
                    "       now() " +
                    "   from pokerline " +
                    "   where " +
                    "       section = 'HEADER' " +
                    "       and line like '%Seat %:%in chips%'" +
                    "       and hand_id = :handId    " +
                    ") " +
                    "ON CONFLICT (nickname) " +
                    "do nothing";
    @Transactional
    @Modifying
    @Query(value = SAVE_NEW_PLAYERS_FROM_HAND, nativeQuery = true)
    void saveNewPlayersFromHand(@Param("handId") long handId);

    String SAVE_NEW_HANDS = "INSERT INTO hands " +
            "(hand_id, level, small_blind, big_blind, created_at, played_at, tournament_id) " +
            "(" +
            "   select " +
            "       hand_id, " +
            "       trim(substring(line from 'Level(.*)\\(')), " +
            "       cast(trim(substring(line from '\\(([0-9]*)/')) as int8), " +
            "       cast(trim(substring(line from '/([0-9]*)\\)')) as int8), " +
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

    String SAVE_NEW_HANDS_FROM_HAND = "INSERT INTO hands " +
            "(hand_id, level, small_blind, big_blind, created_at, played_at, tournament_id) " +
            "(" +
            "   select " +
            "       hand_id, " +
            "       trim(substring(line from 'Level(.*)\\(')), " +
            "       cast(trim(substring(line from '\\(([0-9]*)/')) as int8), " +
            "       cast(trim(substring(line from '/([0-9]*)\\)')) as int8), " +
            "       now(), " +
            "       to_timestamp((regexp_matches(line, '[0-9]{4}/[0-9]{1,2}/[0-9]{1,2} [0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}'))[1], 'YYYY/MM/DD HH24:MI:SS'), " +
            "       tournament_id " +
            "   from pokerline " +
            "   where  " +
            "   line like '%PokerStars Hand #%' " +
            "   and section = 'HEADER'" +
            "   and hand_id = :handId    " +
            ") " +
            "on conflict (hand_id) " +
            "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_NEW_HANDS_FROM_HAND, nativeQuery = true)
    void saveNewHandsFromHand(@Param("handId") long handId);

    String SELECT_DISTINCT_HAND_ID =
            "select distinct hand_id from pokerline";
    @Query(value = SELECT_DISTINCT_HAND_ID, nativeQuery = true)
    Set<Long> getDistinctHandIds();

    long countByTournamentId(long tournamentId);

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

    String SAVE_PLAYER_POSITION_FROM_HAND =
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
                    "   and hand_id = :handId    " +
                    ") " +
                    "on conflict (hand_id, position) " +
                    "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_PLAYER_POSITION_FROM_HAND, nativeQuery = true)
    void savePlayerPositionFromHand(@Param("handId") long handId);

    String SAVE_CARDS_OF_PLAYER =
            "INSERT INTO cards_of_player " +
            "(position, description, hand) " +
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
            "on conflict (hand, position) " +
            "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_CARDS_OF_PLAYER, nativeQuery = true)
    void saveCardsOfPlayer();

    String SAVE_CARDS_OF_PLAYER_FROM_HAND =
            "INSERT INTO cards_of_player " +
                    "(position, description, hand) " +
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
                    "       and hand_id = :handId    " +
                    ")" +
                    "on conflict (hand, position) " +
                    "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_CARDS_OF_PLAYER_FROM_HAND, nativeQuery = true)
    void saveCardsOfPlayerFromHand(@Param("handId") long handId);

    String SAVE_BLIND_POSITIONS =
            "INSERT INTO blind_position " +
            "(hand, position, place) " +
            "( " +
            "   select " +
            "       hand_id, " +
            "       cast(substring(line from 'Seat ([0-9]*):') as int8) as position, " +
            "       case    " +
            "           when strpos(line, '(button)') > 0 then 'button'  " +
            "           when strpos(line, '(small blind)') > 0 then 'small blind'   " +
            "           when strpos(line, '(big blind)') > 0 then 'big blind'   " +
            "       end as place    " +
            "   from pokerline  " +
            "   where " +
            "       section = 'SUMMARY' " +
            "       and line like '%Seat %:%' " +
            "       and (line like '%(button)%' or line like '%(small blind)%' or line like '%(big blind)%') " +
            ")  " +
            "on conflict(hand, position) " +
            "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_BLIND_POSITIONS, nativeQuery = true)
    void saveBlindPositions();

    String SAVE_BLIND_POSITIONS_FROM_HAND =
            "INSERT INTO blind_position " +
                    "(hand, position, place) " +
                    "( " +
                    "   select " +
                    "       hand_id, " +
                    "       cast(substring(line from 'Seat ([0-9]*):') as int8) as position, " +
                    "       case    " +
                    "           when strpos(line, '(button)') > 0 then 'button'  " +
                    "           when strpos(line, '(small blind)') > 0 then 'small blind'   " +
                    "           when strpos(line, '(big blind)') > 0 then 'big blind'   " +
                    "       end as place    " +
                    "   from pokerline  " +
                    "   where " +
                    "       section = 'SUMMARY' " +
                    "       and line like '%Seat %:%' " +
                    "       and (line like '%(button)%' or line like '%(small blind)%' or line like '%(big blind)%') " +
                    "       and hand_id = :handId    " +
                    ")  " +
                    "on conflict(hand, position) " +
                    "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_BLIND_POSITIONS_FROM_HAND, nativeQuery = true)
    void saveBlindPositionsFromHand(@Param("handId") long handId);

    String GET_LAST_HAND_FROM_FILE =
            "select max(p.hand_id) from pokerline p where p.filename = :filename";
    @Query(value = GET_LAST_HAND_FROM_FILE, nativeQuery = true)
    long getLastHandFromFile(@Param("filename") String filename);

    List<PokerLine> getAllByHandIdOrderByLineNumber(long handId);
}
