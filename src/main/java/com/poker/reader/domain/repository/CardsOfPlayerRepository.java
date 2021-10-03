package com.poker.reader.domain.repository;

import com.poker.reader.domain.model.CardsOfPlayer;
import com.poker.reader.domain.model.HandPositionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardsOfPlayerRepository extends JpaRepository<CardsOfPlayer, HandPositionId> {

    String GET_ALL_BY_NICKNAME =
            "select * " +
            "from cards_of_player cop " +
            "join player_position pp on cop.hand = pp.hand_id and cop.position = pp.position " +
            "where " +
            "   pp.nickname = :nickname";
    @Query(value = GET_ALL_BY_NICKNAME, nativeQuery = true)
    List<CardsOfPlayer> getAllByNickname(@Param("nickname") String nickname);

    List<CardsOfPlayer> getAllByHand(long hand);

}
