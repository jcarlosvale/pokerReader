package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.Cards;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardsRepository extends JpaRepository<Cards, Long> {

    Optional<Cards> findByPlayerAndDescription(String player, String description);

    List<Cards> findByPlayer(String player);
}
