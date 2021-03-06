package com.poker.reader.mapper;

import com.poker.reader.dto.PlayerDTO;
import com.poker.reader.entity.Player;

public class PlayerMapper {
    public static Player toEntity(PlayerDTO playerDTO) {
        return Player
                .builder()
                .nickname(playerDTO.getNickname())
                .build();
    }
}
