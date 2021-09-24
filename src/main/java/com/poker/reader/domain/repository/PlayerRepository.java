package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, String> {
    /*
    select line,
trim(substring(line, position(':' in line) + 1, length(line) - position(':' in line) - position('(' in reverse(line)))) --last position
from pokerline where
line like '%Seat %:%in chips)'
and section = 'HEADER'
and poker_file_id = 11019;
     */
}
