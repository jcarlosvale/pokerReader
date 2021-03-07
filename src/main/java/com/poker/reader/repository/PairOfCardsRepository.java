package com.poker.reader.repository;

import com.poker.reader.entity.PairOfCards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PairOfCardsRepository extends JpaRepository<PairOfCards, Long> {
}
