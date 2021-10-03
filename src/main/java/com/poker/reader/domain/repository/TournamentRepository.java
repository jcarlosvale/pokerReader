package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    String COUNT_PLAYERS =
            "select count(distinct pp.nickname) " +
            "   from tournaments t " +
            "   join hands h on h.tournament_id = t.tournament_id " +
            "   join player_position pp on pp.hand_id = h.hand_id " +
            "where t.tournament_id = :tournamentId ";
    @Query(nativeQuery = true, value = COUNT_PLAYERS)
    int countPlayers(@Param("tournamentId") Long tournamentId);

    String COUNT_SHOWDOWNS =
            "select count(cop.description) " +
            "   from tournaments t " +
            "   join hands h on h.tournament_id = t.tournament_id " +
            "   join player_position pp on pp.hand_id = h.hand_id " +
            "   join cards_of_player cop on pp.hand_id = cop.hand and pp.position = cop.position " +
            "where t.tournament_id = :tournamentId ";
    @Query(nativeQuery = true, value = COUNT_SHOWDOWNS)
    int countShowdowns(@Param("tournamentId") Long tournamentId);
}
