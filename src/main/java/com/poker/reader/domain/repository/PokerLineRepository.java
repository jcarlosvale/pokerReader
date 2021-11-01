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
            "(hand_id, table_id, level, small_blind, big_blind, created_at, played_at, tournament_id) " +
            "(" +
            "   select " +
            "       hand_id,    " +
            "       table_id,   " +
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
            "(hand_id, table_id, level, small_blind, big_blind, created_at, played_at, tournament_id) " +
            "(" +
            "   select " +
            "       hand_id, " +
            "       table_id, " +
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

    String SAVE_BOARD =
            "INSERT INTO board_of_hand " +
            "(hand_id, board) " +
            "( " +
                "select " +
                "    hand_id, " +
                "    substring(line from 'Board \\[(.*)\\]') " +
                "from pokerline " +
                "where " +
                "   section = 'SUMMARY' " +
                "   and line like '%Board [%]%'" +
            ")  " +
            "on conflict(hand_id) " +
            "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_BOARD, nativeQuery = true)
    void saveBoard();

    String SAVE_BOARD_FROM_HAND =
            "INSERT INTO board_of_hand " +
            "(hand_id, board) " +
            "( " +
                "select " +
                "    hand_id, " +
                "    substring(line from 'Board \\[(.*)\\]') " +
                "from pokerline " +
                "where " +
                "   section = 'SUMMARY' " +
                "   and line like '%Board [%]%' " +
                "   and hand_id = :handId" +
            ")  " +
            "on conflict(hand_id) " +
            "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_BOARD_FROM_HAND, nativeQuery = true)
    void saveBoardFromHand(@Param("handId") long handId);

    String SAVE_POT =
            "INSERT INTO pot_of_hand " +
            "(hand_id, total_pot) " +
            "( " +
            "   select " +
            "       hand_id, " +
            "       cast(substring(line from 'Total pot ([0-9]*)')  as int8) " +
            "   from pokerline " +
            "   where " +
            "       section = 'SUMMARY' " +
            "       and line like '%Total pot%' " +
            ")  " +
            "on conflict(hand_id) " +
            "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_POT, nativeQuery = true)
    void savePot();

    String SAVE_POT_FROM_HAND =
            "INSERT INTO pot_of_hand " +
            "(hand_id, total_pot) " +
            "( " +
            "   select " +
            "       hand_id, " +
            "       cast(substring(line from 'Total pot ([0-9]*)') as int8) " +
            "   from pokerline " +
            "   where " +
            "       section = 'SUMMARY' " +
            "       and line like '%Total pot%' " +
            "       and hand_id = :handId" +
            ")  " +
            "on conflict(hand_id) " +
            "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_POT_FROM_HAND, nativeQuery = true)
    void savePotFromHand(@Param("handId") long handId);

    String SAVE_FOLD_POSITION =
            "INSERT INTO fold_position                                          " +
            "(hand, position, round, no_bet)                                    " +
            "(                                                                  " +
            "select                                                             " +
            "   hand_id,                                                        " +
            "   cast(substring(line from 'Seat ([0-9]*):') as int8) as position," +
            "   case                                                            " +
            "       when strpos(line, 'folded before Flop')  > 0 then 'PREFLOP' " +
            "       when strpos(line, 'folded on the Flop')  > 0 then 'FLOP'    " +
            "       when strpos(line, 'folded on the Turn')  > 0 then 'TURN'    " +
            "       when strpos(line, 'folded on the River') > 0 then 'RIVER'   " +
            "   end,                                                            " +
            "   case                                                            " +
            "       when strpos(line, 'didn''t bet')  > 0 then true             " +
            "       else false                                                  " +
            "   end                                                             " +
            "from pokerline p                                                   " +
            "where                                                              " +
            "   p.section = 'SUMMARY'                                           " +
            "   and p.line like 'Seat%:%'                                       " +
            "   and (p.line like '%folded before Flop%'                         " +
            "   or  p.line like '%folded on the Flop%'                          " +
            "   or  p.line like '%folded on the Turn%'                          " +
            "   or  p.line like '%folded on the River%')                        " +
            ")                                                                  " +
            "on conflict(hand, position)                                        " +
            "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_FOLD_POSITION, nativeQuery = true)
    void saveFoldPosition();

    String SAVE_FOLD_POSITION_FROM_HAND =
            "INSERT INTO fold_position                                                  " +
                    "(hand, position, round, no_bet)                                    " +
                    "(                                                                  " +
                    "select                                                             " +
                    "   hand_id,                                                        " +
                    "   cast(substring(line from 'Seat ([0-9]*):') as int8) as position," +
                    "   case                                                            " +
                    "       when strpos(line, 'folded before Flop')  > 0 then 'PREFLOP' " +
                    "       when strpos(line, 'folded on the Flop')  > 0 then 'FLOP'    " +
                    "       when strpos(line, 'folded on the Turn')  > 0 then 'TURN'    " +
                    "       when strpos(line, 'folded on the River') > 0 then 'RIVER'   " +
                    "   end,                                                            " +
                    "   case                                                            " +
                    "       when strpos(line, 'didn''t bet')  > 0 then true             " +
                    "       else false                                                  " +
                    "   end                                                             " +
                    "from pokerline p                                                   " +
                    "where                                                              " +
                    "   p.section = 'SUMMARY'                                           " +
                    "   and p.line like 'Seat%:%'                                       " +
                    "   and (p.line like '%folded before Flop%'                         " +
                    "   or  p.line like '%folded on the Flop%'                          " +
                    "   or  p.line like '%folded on the Turn%'                          " +
                    "   or  p.line like '%folded on the River%')                        " +
                    "   and hand_id = :handId                                           " +
                    ")                                                                  " +
                    "on conflict(hand, position)                                        " +
                    "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_FOLD_POSITION_FROM_HAND, nativeQuery = true)
    void saveFoldPosition(@Param("handId") long handId);

    String SAVE_WIN_POSITION =
            "INSERT INTO win_position                                                                                       " +
            "(hand, position, showdown, pot, hand_description)                                                              " +
            "(                                                                                                              " +
            "select                                                                                                         " +
            "   hand_id,                                                                                                    " +
            "   cast(substring(line from 'Seat ([0-9]*):') as int8) as position,                                            " +
            "   case                                                                                                        " +
            "       when strpos(line, 'collected') > 0 then false                                                           " +
            "       when strpos(line, 'and won ' ) > 0 then true                                                            " +
            "   end as showdown,                                                                                            " +
            "   case                                                                                                        " +
            "       when strpos(line, 'collected') > 0 then cast(substring(line from 'collected \\(([0-9]*)\\)') as int8)   " +
            "       when strpos(line, 'and won ' ) > 0 then cast(substring(line from 'and won \\(([0-9]*)\\)')   as int8)   " +
            "   end as pot,                                                                                                 " +
            "   case                                                                                                        " +
            "       when strpos(line, 'collected') > 0 then null                                                            " +
            "       when strpos(line, 'and won ' ) > 0 then trim(substring(line from '\\) with (.*)'))                      " +
            "   end as hand_description                                                                                     " +
            "from pokerline p                                                                                               " +
            "where                                                                                                          " +
            "   p.section = 'SUMMARY'                                                                                       " +
            "   and p.line like 'Seat%:%'                                                                                   " +
            "   and (p.line like '%collected%'                                                                              " +
            "   or  p.line like '%and won %')                                                                               " +
            ")                                                                                                              " +
            "on conflict(hand, position)                                                                                    " +
            "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_WIN_POSITION, nativeQuery = true)
    void saveWinPositions();

    String SAVE_WIN_POSITION_FROM_HAND =
            "INSERT INTO win_position                                                                                       " +
                    "(hand, position, showdown, pot, hand_description)                                                              " +
                    "(                                                                                                              " +
                    "select                                                                                                         " +
                    "   hand_id,                                                                                                    " +
                    "   cast(substring(line from 'Seat ([0-9]*):') as int8) as position,                                            " +
                    "   case                                                                                                        " +
                    "       when strpos(line, 'collected') > 0 then false                                                           " +
                    "       when strpos(line, 'and won ' ) > 0 then true                                                            " +
                    "   end as showdown,                                                                                            " +
                    "   case                                                                                                        " +
                    "       when strpos(line, 'collected') > 0 then cast(substring(line from 'collected \\(([0-9]*)\\)') as int8)   " +
                    "       when strpos(line, 'and won ' ) > 0 then cast(substring(line from 'and won \\(([0-9]*)\\)')   as int8)   " +
                    "   end as pot,                                                                                                 " +
                    "   case                                                                                                        " +
                    "       when strpos(line, 'collected') > 0 then null                                                            " +
                    "       when strpos(line, 'and won ' ) > 0 then trim(substring(line from '\\) with (.*)'))                      " +
                    "   end as hand_description                                                                                     " +
                    "from pokerline p                                                                                               " +
                    "where                                                                                                          " +
                    "   p.section = 'SUMMARY'                                                                                       " +
                    "   and p.line like 'Seat%:%'                                                                                   " +
                    "   and (p.line like '%collected%'                                                                              " +
                    "   or  p.line like '%and won %')                                                                               " +
                    "   and hand_id = :handId                                                                                       " +
                    ")                                                                                                              " +
                    "on conflict(hand, position)                                                                                    " +
                    "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_WIN_POSITION_FROM_HAND, nativeQuery = true)
    void saveWinPositions(@Param("handId") long handId);

    String SAVE_LOSE_POSITION =
            "INSERT INTO lose_position                                                                                              " +
                    "(hand, position, hand_description)                                                                             " +
                    "(                                                                                                              " +
                    "select                                                                                                         " +
                    "   hand_id,                                                                                                    " +
                    "   cast(substring(line from 'Seat ([0-9]*):') as int8) as position,                                            " +
                    "   trim(substring(line from 'and lost with (.*)')) as hand_description                                         " +
                    "from pokerline p                                                                                               " +
                    "where                                                                                                          " +
                    "   p.section = 'SUMMARY'                                                                                       " +
                    "   and p.line like 'Seat%:%'                                                                                   " +
                    "   and p.line like '%and lost %'                                                                               " +
                    ")                                                                                                              " +
                    "on conflict(hand, position)                                                                                    " +
                    "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_LOSE_POSITION, nativeQuery = true)
    void saveLosePositions();

    String SAVE_LOSE_POSITION_FROM_HAND =
            "INSERT INTO lose_position                                                                                               " +
                    "(hand, position, hand_description)                                                              " +
                    "(                                                                                                              " +
                    "select                                                                                                         " +
                    "   hand_id,                                                                                                    " +
                    "   cast(substring(line from 'Seat ([0-9]*):') as int8) as position,                                            " +
                    "   trim(substring(line from 'and lost with (.*)')) as hand_description                                         " +
                    "from pokerline p                                                                                               " +
                    "where                                                                                                          " +
                    "   p.section = 'SUMMARY'                                                                                       " +
                    "   and p.line like 'Seat%:%'                                                                                   " +
                    "   and p.line like '%and lost %'                                                                               " +
                    "   and hand_id = :handId                                                                                       " +
                    ")                                                                                                              " +
                    "on conflict(hand, position)                                                                                    " +
                    "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_LOSE_POSITION_FROM_HAND, nativeQuery = true)
    void saveLosePositions(@Param("handId") long handId);

    String SAVE_HAND_CONSOLIDATION =
            "INSERT INTO hand_consolidation                                                                                               " +
                    "(tournament_id, table_id, board, hand, level, small_blind, big_blind, total_pot, nickname, position, place, "
                    + "cards_description, card1, card2, chen, normalised, pair, suited, stack_of_player, fold_round, no_bet, lose_hand_description, "
                    + "win_hand_description, win_pot, win_showdown, played_at)                                                              " +
                    "(                                                                                                              " +
                    " select "
                            + "h.tournament_id, "
                            + "h.table_id,  "
                            + "boh.board,   "
                            + "h.hand_id,   "
                            + "h.level, "
                            + "h.small_blind,  "
                            + "h.big_blind, "
                            + "poh.total_pot,   "
                            + "pp.nickname, "
                            + "pp.position, "
                            + "bp.place,    "
                            + "cop.description, "
                            + "c.card1, "
                            + "c.card2, "
                            + "c.chen,  "
                            + "c.normalised,    "
                            + "c.pair,  "
                            + "c.suited,    "
                            + "pp.stack,    "
                            + "fp.round as fold,    "
                            + "fp.no_bet,   "
                            + "lp.hand_description as loseHand, "
                            + "wp.hand_description as winHand,  "
                            + "wp.pot as winPot,    "
                            + "wp.showdown, "
                            + "h.played_at     "
                            + "from hands h "
                            + "join player_position pp on pp.hand_id = h.hand_id    "
                            + "left join blind_position bp on bp.hand = pp.hand_id and bp.position = pp.position    "
                            + "left join board_of_hand boh on boh.hand_id = h.hand_id   "
                            + "left join cards_of_player cop on cop.hand = pp.hand_id and cop.position = pp.position    "
                            + "left join cards c on c.description = cop.description     "
                            + "left join fold_position fp on fp.hand = pp.hand_id and fp.position = pp.position "
                            + "left join lose_position lp on lp.hand = pp.hand_id and lp.position = pp.position "
                            + "left join win_position wp on wp.hand = pp.hand_id and wp.position = pp.position  "
                            + "left join pot_of_hand poh on poh.hand_id = pp.hand_id    "
                            + ")                                                                                                     " +
                            "on conflict(hand, position)                                                                                    " +
                            "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_HAND_CONSOLIDATION, nativeQuery = true)
    void saveHandConsolidation();

    String SAVE_HAND_CONSOLIDATION_FROM_HAND =
            "INSERT INTO hand_consolidation                                                                                               " +
                    "(tournament_id, table_id, board, hand, level, small_blind, big_blind, total_pot, nickname, position, place, "
                    + "cards_description, card1, card2, chen, normalised, pair, suited, stack_of_player, fold_round, no_bet, lose_hand_description, "
                    + "win_hand_description, win_pot, win_showdown, played_at)                                                              " +
                    "(                                                                                                              " +
                    " select "
                    + "h.tournament_id, "
                    + "h.table_id,  "
                    + "boh.board,   "
                    + "h.hand_id,   "
                    + "h.level, "
                    + "h.small_blind,  "
                    + "h.big_blind, "
                    + "poh.total_pot,   "
                    + "pp.nickname, "
                    + "pp.position, "
                    + "bp.place,    "
                    + "cop.description, "
                    + "c.card1, "
                    + "c.card2, "
                    + "c.chen,  "
                    + "c.normalised,    "
                    + "c.pair,  "
                    + "c.suited,    "
                    + "pp.stack,    "
                    + "fp.round as fold,    "
                    + "fp.no_bet,   "
                    + "lp.hand_description as loseHand, "
                    + "wp.hand_description as winHand,  "
                    + "wp.pot as winPot,    "
                    + "wp.showdown, "
                    + "h.played_at     "
                    + "from hands h "
                    + "join player_position pp on pp.hand_id = h.hand_id    "
                    + "left join blind_position bp on bp.hand = pp.hand_id and bp.position = pp.position    "
                    + "left join board_of_hand boh on boh.hand_id = h.hand_id   "
                    + "left join cards_of_player cop on cop.hand = pp.hand_id and cop.position = pp.position    "
                    + "left join cards c on c.description = cop.description     "
                    + "left join fold_position fp on fp.hand = pp.hand_id and fp.position = pp.position "
                    + "left join lose_position lp on lp.hand = pp.hand_id and lp.position = pp.position "
                    + "left join win_position wp on wp.hand = pp.hand_id and wp.position = pp.position  "
                    + "left join pot_of_hand poh on poh.hand_id = pp.hand_id    "
                    + "where h.hand_id = :handId                                                                               "
                    + ")                                                                                                     " +
                    "on conflict(hand, position)                                                                                    " +
                    "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_HAND_CONSOLIDATION_FROM_HAND, nativeQuery = true)
    void saveHandConsolidation(@Param("handId") long handId);

    String SAVE_HAND_POSITION =
            "INSERT INTO hand_position                                                                                              " +
                    "(hand, number_of_players, min_pos, max_pos, button, positions)                                                 " +
                    "(                                                                                                              " +
                        "select \n"
                                + "\thc.hand,\n"
                                + "\tcount(hc.*) as numberOfPlayers,\n"
                                + "\tmin(hc.position) as minPos,\n"
                                + "\tmax(hc.position) as maxPos,\n"
                                + "\tbp.position as button,\n"
                                + "\tstring_agg(distinct cast(hc.\"position\" as text) , ',') as positions\n"
                                + "from \n"
                                + "\thand_consolidation hc \n"
                                + "left join \n"
                                + "\tblind_position bp on hc.hand = bp.hand \n"
                                + "where \n"
                                + " \tbp.place = 'button'\n"
                                + "group by\n"
                                + "\thc.hand,\n"
                                + "\tbp.position "
                    + ")                                                                                                     " +
                    "on conflict(hand)                                                                                    " +
                    "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_HAND_POSITION, nativeQuery = true)
    void saveHandPosition();

    String SAVE_HAND_POSITION_FROM_HAND =
            "INSERT INTO hand_position                                                                                              " +
                    "(hand, number_of_players, min_pos, max_pos, button, positions)                                                 " +
                    "(                                                                                                              " +
                        "select \n"
                                + "\thc.hand,\n"
                                + "\tcount(hc.*) as numberOfPlayers,\n"
                                + "\tmin(hc.position) as minPos,\n"
                                + "\tmax(hc.position) as maxPos,\n"
                                + "\tbp.position as button,\n"
                                + "\tstring_agg(distinct cast(hc.\"position\" as text) , ',') as positions\n"
                                + "from \n"
                                + "\thand_consolidation hc \n"
                                + "left join \n"
                                + "\tblind_position bp on hc.hand = bp.hand \n"
                                + "where \n"
                                + " \tbp.place = 'button'\n"
                                + " \tand hc.hand = :handId\n"
                                + "group by\n"
                                + "\thc.hand,\n"
                                + "\tbp.position "
                    + ")                                                                                                     " +
                    "on conflict(hand)                                                                                    " +
                    "do nothing ";
    @Transactional
    @Modifying
    @Query(value = SAVE_HAND_POSITION_FROM_HAND, nativeQuery = true)
    void saveHandPosition(@Param("handId") long handId);

    String GET_LAST_HAND_FROM_FILE =
            "select max(p.hand_id) from pokerline p where p.filename = :filename";
    @Query(value = GET_LAST_HAND_FROM_FILE, nativeQuery = true)
    long getLastHandFromFile(@Param("filename") String filename);

    String GET_LAST_HAND_FROM_TOURNAMENT =
            "select max(hand) from hand_consolidation where tournament_id = :tournamentId";
    @Query(value = GET_LAST_HAND_FROM_TOURNAMENT, nativeQuery = true)
    long getLastHandFromTournament(@Param("tournamentId") long tournamentId);

    List<PokerLine> getAllByHandIdOrderByLineNumber(long handId);
}
