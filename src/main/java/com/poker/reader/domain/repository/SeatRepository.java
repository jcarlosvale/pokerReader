package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.Hand;
import com.poker.reader.domain.model.Player;
import com.poker.reader.domain.model.Seat;
import com.poker.reader.domain.repository.dto.PlayerPositionAtHandDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByPlayer(Player player);
    List<Seat> findByHand(Hand hand);

    String GET_PLAYER_IN_POSITION_BY_HAND =
            "select " +
                    "trim(substring(line, position(':' in line) + 1, length(line) - position(':' in line) - position('(' in reverse(line)))) as player, " +
                    "trim(substring(line, 6,1)) as position, " +
                    "hand_id as hand " +
                    "from pokerline " +
                    "where " +
                    "   is_processed = false " +
                    "   and section = 'HEADER' " +
                    "   and line like '%Seat %:%in chips%'";
    @Query(value = GET_PLAYER_IN_POSITION_BY_HAND, nativeQuery = true)
    List<PlayerPositionAtHandDto> getPlayerInPositionByHand();
}
