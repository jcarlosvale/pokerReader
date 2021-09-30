package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.HandPositionId;
import com.poker.reader.domain.model.PlayerPosition;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlayerPositionRepository extends JpaRepository<PlayerPosition, HandPositionId> {

    String CONT_HANDS_OF_PLAYER =
            "select count(*) " +
            "from player_position pp " +
            "where pp.nickname = :nickname";
    @Query(value = CONT_HANDS_OF_PLAYER, nativeQuery = true)
    int countHandsOfPlayer(@Param("nickname") String nickname);

    String FIND_BY_HAND_ID =
            "select * from player_position pp where pp.hand_id = :handId";
    @Query(value = FIND_BY_HAND_ID, nativeQuery = true)
    List<PlayerPosition> findByHandId(@Param("handId") Long handId);
}
