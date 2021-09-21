package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.Cards;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardsRepository extends JpaRepository<Cards, String> {

}
