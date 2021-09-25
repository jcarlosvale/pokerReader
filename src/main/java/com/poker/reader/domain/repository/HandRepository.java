package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.Hand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Repository
public interface HandRepository extends JpaRepository<Hand, String> {

    String FIND_ALL_HAND_ID_BY_TOURNAMENT_ID = "select h.hand_id from hands h";
    String FIND_MOST_RECENT_HAND = "select * from hands h where h.tournament_id = :tournamentId order by played_at " +
            "desc limit 1";
    String SAVE_NEW_HANDS = "INSERT INTO hands " +
            "(hand_id, created_at, played_at, tournament_id) " +
            "(select " +
            "(regexp_matches(line, 'PokerStars Hand #([0-9]+)'))[1], " +
            "now(), " +
            "to_timestamp((regexp_matches(line, '[0-9]{4}/[0-9]{1,2}/[0-9]{1,2} [0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}'))[1], 'YYYY/MM/DD HH24:MI:SS'), " +
            "(regexp_matches(line, 'Tournament #([0-9]+)'))[1] " +
            "from pokerline pl " +
            "join pokerfile pf on (pl.poker_file_id = pf.poker_file_id) " +
            "where  " +
            "pf.is_processed = false  " +
            "and line like '%PokerStars Hand #%'" +
            "and section = 'HEADER') " +
            "on conflict (hand_id) " +
            "do nothing ";

    @Query(value = FIND_MOST_RECENT_HAND, nativeQuery = true)
    Hand findMostRecent(@Param("tournamentId") String tournamentId);

    @Query(value = FIND_ALL_HAND_ID_BY_TOURNAMENT_ID, nativeQuery = true)
    Set<String> findAllHandIds();

    @Transactional
    @Modifying
    @Query(value = SAVE_NEW_HANDS, nativeQuery = true)
    void saveNewHands();
}
