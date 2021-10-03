package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.Hand;
import com.poker.reader.domain.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HandRepository extends JpaRepository<Hand, Long> {

    String FIND_MOST_RECENT_HAND = "select * from hands h where h.tournament_id = :tournamentId order by played_at " +
            "desc limit 1";
    @Query(value = FIND_MOST_RECENT_HAND, nativeQuery = true)
    Hand findMostRecent(@Param("tournamentId") Long tournamentId);

    List<Hand> findAllByTournamentOrderByPlayedAt(Tournament tournament);

    long countAllByTournament(Tournament tournament);
}
