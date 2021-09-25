package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, String> {

    String SAVE_NEW_TOURNAMENTS = "INSERT INTO tournaments " +
            "(select  " +
            "distinct (regexp_matches(line, 'Tournament #([0-9]+)'))[1], " +
            "now(), " +
            "pf.file_name  " +
            "from pokerline pl " +
            "join pokerfile pf on (pl.poker_file_id = pf.poker_file_id) " +
            "where  " +
            "pf.is_processed = false " +
            "and line like '%Tournament #%' " +
            "and section = 'HEADER') " +
            "ON CONFLICT (tournament_id) " +
            "do nothing";
    @Transactional
    @Modifying
    @Query(value = SAVE_NEW_TOURNAMENTS, nativeQuery = true)
    void saveNewTournaments();


    boolean existsTournamentByFileNameEquals(String filename);

}
