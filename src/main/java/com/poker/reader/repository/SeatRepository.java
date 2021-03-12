package com.poker.reader.repository;

import com.poker.reader.entity.Hand;
import com.poker.reader.entity.Player;
import com.poker.reader.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
//    @Query(value = "SELECT * FROM Seat s WHERE s.seat_id_hand = :handId and s.seat_player = :playerName and s.seat_id = :seatId",
//            nativeQuery = true)
//    Optional<Seat> findBySeatIdAndHandIdAndPlayerName(@Param("seatId") Integer seatId,
//                                                      @Param("handId") Long handId,
//                                                      @Param("playerName") String playerName);
    Optional<Seat> findFirstBySeatIdAndHandAndPlayer(Integer seatId, Hand hand, Player player);
}
