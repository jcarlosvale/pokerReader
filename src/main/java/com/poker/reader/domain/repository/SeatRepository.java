package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.Player;
import com.poker.reader.domain.model.Seat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByPlayer(Player player);
}
