package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.Hand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HandRepository extends JpaRepository<Hand, String> {
    @Query(
            value = "select * from hands h where h.tournament_id = :tournamentId order by played_at desc limit 1",
            nativeQuery = true)
    Hand findMostRecent(@Param("tournamentId") String tournamentId);
}
