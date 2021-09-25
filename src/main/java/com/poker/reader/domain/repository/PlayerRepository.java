package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, String> {
    String SAVE_NEW_PLAYERS = "INSERT INTO players " +
            "(select   " +
            "distinct trim(substring(line, position(':' in line) + 1, length(line) - position(':' in line) " +
            "- position('(' in reverse(line)))), " +
            "now() " +
            "from pokerline pl " +
            "join pokerfile pf on (pl.poker_file_id = pf.poker_file_id) " +
            "where  " +
            "pf.is_processed = false  " +
            "and line like '%Seat %:%in chips%' " +
            "and section = 'HEADER') " +
            "ON CONFLICT (nickname) " +
            "do nothing";

    @Transactional
    @Modifying
    @Query(value = SAVE_NEW_PLAYERS, nativeQuery = true)
    void saveNewPlayers();
    /*
    select line,
trim(substring(line, position(':' in line) + 1, length(line) - position(':' in line) - position('(' in reverse(line)))) --last position
from pokerline where
line like '%Seat %:%in chips)'
and section = 'HEADER'
and poker_file_id = 11019;
     */
}
