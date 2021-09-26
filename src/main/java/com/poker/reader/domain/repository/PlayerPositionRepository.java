package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.PlayerPosition;
import com.poker.reader.domain.model.PlayerPositionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerPositionRepository extends JpaRepository<PlayerPosition, PlayerPositionId> {

    String CONT_HANDS_OF_PLAYER =
            "select count(*) " +
            "from player_position pp " +
            "where pp.nickname = :nickname";
    @Query(value = CONT_HANDS_OF_PLAYER, nativeQuery = true)
    int countHandsOfPlayer(@Param("nickname") String nickname);

    List<PlayerPosition> findByHandId(Long handId);
}
