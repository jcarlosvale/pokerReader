package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.Hand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HandRepository extends JpaRepository<Hand, String> {

}
