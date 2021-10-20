package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.HandConsolidation;
import com.poker.reader.domain.model.HandPositionId;
import com.poker.reader.domain.repository.projection.HandDtoProjection;
import com.poker.reader.domain.repository.projection.PlayerDetailsDtoProjection;
import com.poker.reader.domain.repository.projection.PlayerDtoProjection;
import com.poker.reader.domain.repository.projection.StackDtoProjection;
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

    String CALCULATE_AVG_STACK_FROM_LAST_HAND_OF_TOURNAMENT =
            "select \n"
                    + "\tround(avg(hc.stack_of_player)) as avgStack\n"
                    + "from \n"
                    + "\thand_consolidation hc \n"
                    + "where \n"
                    + "\thc.tournament_id = :tournamentId\n"
                    + "\tand hc.hand = (select max(hand) from hand_consolidation)\n"
                    + "group by\n"
                    + "\thc.tournament_id,\n"
                    + "\thc.hand";
    @Query(value = CALCULATE_AVG_STACK_FROM_LAST_HAND_OF_TOURNAMENT, nativeQuery = true )
    int calculateAvgStackFromLastHandOfTournament(@Param("tournamentId") Long tournamentId);

    String GET_PLAYERS_STACKS_FROM_LAST_HAND_OF_TOURNAMENT =
            "select \n"
                    + "\thc.tournament_id as tournamentId,\n"
                    + "\thc.hand as handId,\n"
                    + "\thc.nickname as nickname,\n"
                    + "\thc.stack_of_player as stackOfPlayer,\n"
                    + "\thc.big_blind as bigBlind,\n"
                    + "\tround(hc.stack_of_player / hc.big_blind) as blinds,\n"
                    + "\thc.total_pot as pot\n"
                    + "from \n"
                    + "\thand_consolidation hc \n"
                    + "where \n"
                    + "\thc.tournament_id = :tournamentId\n"
                    + "\tand hc.hand = (select max(hand) from hand_consolidation)\n";
    @Query(value = GET_PLAYERS_STACKS_FROM_LAST_HAND_OF_TOURNAMENT, nativeQuery = true )
    List<StackDtoProjection> getPlayersStacksFromLastHandOfTournament(@Param("tournamentId") Long tournamentId);

    String GET_PLAYERS_DETAILS_FROM_HAND =
            "select\n"
                    + "\thc.tournament_id as tournamentId,\n"
                    + "\thc.hand as handId,\n"
                    + "\thc.level as level,\n"
                    + "\tto_char(hc.played_at, 'dd-mm-yy HH24:MI:SS') as playedAt,\n"
                    + "\tcase \n"
                    + "\t\twhen length(hc.board) = 8 then 'FLOP'\n"
                    + "\t\twhen length(hc.board) = 11 then 'TURN'\n"
                    + "\t\twhen length(hc.board) = 14 then 'RIVER'\n"
                    + "\t\telse null\n"
                    + "\tend as boardShowdown,\n"
                    + "\tconcat(cast(hc.small_blind as text) || '/', cast(hc.big_blind as text)) as blinds,\n"
                    + "\thc.board as board,\n"
                    + "\thc.total_pot as pot,\n"
                    + "\thc.nickname as nickname,\n"
                    + "\thc.chen as chen,\n"
                    + "\tconcat(cast(hc.normalised as text) || ' / ', cast(hc.cards_description as text)) as cards,\n"
                    + "\tcase \n"
                    + "\t\twhen hc.place = 'button' then true\n"
                    + "\t\telse false\n"
                    + "\tend as isButton,\n"
                    + "\tcase \n"
                    + "\t\twhen hc.place = 'small blind' then true\n"
                    + "\t\telse false\n"
                    + "\tend as isSmallBlind,\n"
                    + "\tcase \n"
                    + "\t\twhen hc.place = 'big blind' then true\n"
                    + "\t\telse false\n"
                    + "\tend as isBigBlind,\n"
                    + "\thc.stack_of_player as stackOfPlayer,\n"
                    + "\tround(hc.stack_of_player / hc.big_blind) as blindsCount,\n"
                    + "\tcase \n"
                    + "\t\twhen hc.win_pot is null then false\n"
                    + "\t\telse true\n"
                    + "\tend as isWinner,\n"
                    + "\tcase \n"
                    + "\t\twhen hc.lose_hand_description is not null then true\n"
                    + "\t\telse false\n"
                    + "\tend as isLose,\n"
                    + "\tcase\n"
                    + "\t\twhen hc.win_hand_description is not null then hc.win_hand_description\n"
                    + "\t\telse hc.lose_hand_description \n"
                    + "\tend as handDescription,\n"
                    + "\thc.place as place,\n"
                    + "\thc.poker_position as pokerPosition,\n"
                    + "\thc.position as position\n"
                    + "from hand_consolidation hc \n"
                    + "where \n"
                    + "hc.hand = :handId";
    @Query(value = GET_PLAYERS_DETAILS_FROM_HAND, nativeQuery = true )
    List<PlayerDetailsDtoProjection> getPlayersDetailsFromHand(@Param("handId") Long handId);

    String GET_HANDS_FROM_PLAYERS_UNTIL_HAND_ID_BY_TOURNAMENT_ORDER_BY_NICKNAME =
            "select \n"
                    + "row_number() over(), \n"
                    + "*\n"
                    + "from \n"
                    + "\thand_consolidation hc \n"
                    + "where \n"
                    + "\thc.hand <= :handId\n"
                    + "and hc.tournament_id = :tournamentId\n"
                    + "and hc.nickname in (select nickname from hand_consolidation where hand = :handId)\n"
                    + "order by \n"
                    + "\thc.nickname,\n"
                    + "\thc.hand\n";
    @Query(value = GET_HANDS_FROM_PLAYERS_UNTIL_HAND_ID_BY_TOURNAMENT_ORDER_BY_NICKNAME, nativeQuery = true )
    List<HandConsolidation> getHandsFromPlayersUntilHandIdByTournamentOrderByNickname(@Param("handId") Long handId, @Param("tournamentId") Long tournamentId);
}
