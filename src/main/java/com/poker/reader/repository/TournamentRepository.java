package com.poker.reader.repository;

import com.poker.reader.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository  extends JpaRepository<Tournament, Long> {
}

