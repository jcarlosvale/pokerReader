package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.Player;
import com.poker.reader.domain.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByPlayer(Player player);
    //List<Seat> findByHand(Hand hand);
}
