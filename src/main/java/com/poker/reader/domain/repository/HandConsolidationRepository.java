package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.HandConsolidation;
import com.poker.reader.domain.model.HandPositionId;
import com.poker.reader.domain.repository.projection.HandDtoProjection;
import com.poker.reader.domain.repository.projection.PlayerDtoProjection;
import com.poker.reader.domain.repository.projection.TournamentDtoProjection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HandConsolidationRepository extends JpaRepository<HandConsolidation, HandPositionId> {
    String GET_PLAYER_DTO =
            "select \n"
                    + "\tdistinct hc.nickname as nickname,\n"
                    + "\tcount(hc.hand) as totalHands,\n"
                    + "\tsum(case when cards_description is null then 0 else 1 end) as showdowns,\n"
                    + "\tround(sum(case when cards_description is null then 0 else 1 end) * 100.0/ count(hc.hand)) as showdownStat,\n"
                    + "\tround(avg(hc.chen)) as avgChen,\n"
                    + "\tto_char(min(hc.played_at), 'dd-mm-yy HH24:MI:SS') as createdAt,\n"
                    + "\tstring_agg(distinct hc.normalised , ', ') as cards,\n"
                    + "\tstring_agg(hc.cards_description, ', ') as rawcards,\n"
                    + "\t'd-none' as css\n"
                    + "from hand_consolidation hc\n"
                    + "group by \n"
                    + "\thc.nickname";
    @Query(value = GET_PLAYER_DTO, nativeQuery = true )
    Page<PlayerDtoProjection> getAllPlayerDto(Pageable pageable);

    String GET_PLAYER_DTO_BY_NICKNAME =
            "select \n"
                    + "\tdistinct hc.nickname as nickname,\n"
                    + "\tcount(hc.hand) as totalHands,\n"
                    + "\tsum(case when cards_description is null then 0 else 1 end) as showdowns,\n"
                    + "\tround(sum(case when cards_description is null then 0 else 1 end) * 100.0/ count(hc.hand)) as showdownStat,\n"
                    + "\tround(avg(hc.chen)) as avgChen,\n"
                    + "\tto_char(min(hc.played_at), 'dd-mm-yy HH24:MI:SS') as createdAt,\n"
                    + "\tstring_agg(distinct hc.normalised , ', ') as cards,\n"
                    + "\tstring_agg(hc.cards_description, ', ') as rawcards,\n"
                    + "\t'd-none' as css\n"
                    + "\tfrom hand_consolidation hc\n"
                    + "\t where hc.nickname = :nickname\n"
                    + "\tgroup by \n"
                    + "\thc.nickname";
    @Query(value = GET_PLAYER_DTO_BY_NICKNAME, nativeQuery = true )
    Optional<PlayerDtoProjection> getPlayerDtoByNickname(@Param("nickname") String nickname);

    String GET_TOURNAMENTS_DTO =
            "select \n"
                    + "\tt.tournament_id as tournamentId,\n"
                    + "\tt.file_name as fileName,\n"
                    + "\tto_char(min(hc.played_at), 'dd-mm-yy HH24:MI:SS') as playedAt,\n"
                    + "\tcount(distinct hc.hand) as hands,\n"
                    + "\tcount(distinct hc.nickname) as players,\n"
                    + "\tsum(case when hc.cards_description is null then 0 else 1 end) as showdowns\n"
                    + "from tournaments t\n"
                    + "join hand_consolidation hc on t.tournament_id = hc.tournament_id\n"
                    + "group by \n"
                    + "\tt.tournament_id,\n"
                    + "\tt.file_name\n";
    @Query(value = GET_TOURNAMENTS_DTO, nativeQuery = true )
    Page<TournamentDtoProjection> getAllTournamentsDto(Pageable pageable);

    String GET_HANDS_FROM_TOURNAMENT =
            "select \n"
                    + "\thc.tournament_id as tournamentId,\n"
                    + "\thc.hand as handId,\n"
                    + "\thc.level as level,\n"
                    + "\tconcat(cast(hc.small_blind as text) || '/', cast(hc.big_blind as text)) as blinds,\n"
                    + "\tcount(distinct hc.nickname) as players,\n"
                    + "\tsum(case when hc.cards_description is null then 0 else 1 end) as showdowns,\n"
                    + "\t\tto_char(hc.played_at, 'dd-mm-yy HH24:MI:SS') as playedAt,\n"
                    + "\thc.total_pot as pot,\n"
                    + "\thc.board as board,\n"
                    + "\tcase \n"
                    + "\t\twhen length(hc.board) = 8 then 'FLOP'\n"
                    + "\t\twhen length(hc.board) = 11 then 'TURN'\n"
                    + "\t\twhen length(hc.board) = 14 then 'RIVER'\n"
                    + "\t\telse null\n"
                    + "\tend as boardShowdown\n"
                    + "from \n"
                    + "\thand_consolidation hc\n"
                    + "where \n"
                    + "\thc.tournament_id = :tournamentId\n"
                    + "group by\n"
                    + "\thc.tournament_id,\n"
                    + "\thc.hand,\n"
                    + "\thc.level,\n"
                    + "\thc.small_blind,\n"
                    + "\thc.big_blind,\n"
                    + "\thc.played_at,\n"
                    + "\thc.total_pot,\n"
                    + "\thc.board\n"
                    + "order by \n"
                    + "\thc.played_at";
    @Query(value = GET_HANDS_FROM_TOURNAMENT, nativeQuery = true )
    List<HandDtoProjection> getHandsFromTournament(@Param("tournamentId") Long tournamentId);
}
