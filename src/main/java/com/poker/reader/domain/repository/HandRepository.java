package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.Hand;
import com.poker.reader.domain.model.Tournament;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HandRepository extends JpaRepository<Hand, Long> {

    String FIND_MOST_RECENT_HAND = "select * from hands h where h.tournament_id = :tournamentId order by played_at " +
            "desc limit 1";
    @Query(value = FIND_MOST_RECENT_HAND, nativeQuery = true)
    Hand findMostRecent(@Param("tournamentId") Long tournamentId);

    List<Hand> findAllByTournamentOrderByPlayedAt(Tournament tournament);

    long countAllByTournament(Tournament tournament);

    String COUNT_PLAYERS =
            "select count(distinct pp.nickname) " +
                    "   from hands h " +
                    "   join player_position pp on pp.hand_id = h.hand_id " +
                    "where h.hand_id = :handId ";
    @Query(nativeQuery = true, value = COUNT_PLAYERS)
    int countPlayers(@Param("handId") Long handId);

    String COUNT_SHOWDOWNS =
            "select count(cop.description) " +
                    "   from hands h " +
                    "   join player_position pp on pp.hand_id = h.hand_id " +
                    "   join cards_of_player cop on pp.hand_id = cop.hand and pp.position = cop.position " +
                    "where h.hand_id = :handId ";
    @Query(nativeQuery = true, value = COUNT_SHOWDOWNS)
    int countShowdowns(@Param("handId") Long handId);

    String FIND_HAND_ID_BY_TOURNAMENT_ID =
            "select h.hand_id "
                    + "from hands h "
                    + "where h.tournament_id = :tournamentId "
                    + "order by h.hand_id asc";
    @Query(nativeQuery = true, value = FIND_HAND_ID_BY_TOURNAMENT_ID)
    List<Long> findAllHandIdByTournamentId(@Param("tournamentId") Long tournamentId);

    List<Hand> findAllByTournamentAndHandIdIsLessThanEqualOrderByHandId(Tournament tournament, Long handId);
}
