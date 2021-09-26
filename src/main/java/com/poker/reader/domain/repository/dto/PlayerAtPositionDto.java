package com.poker.reader.domain.repository.dto;

import java.util.HashMap;
import java.util.Map;

public class PlayerAtPositionDto {

    Map<Integer, String> mapOfPosition = new HashMap<>();

    public void putPlayerAtPosition(int position, String nickname) {
        mapOfPosition.put(position, nickname);
    }

    public String getPlayerAtPosition(int position) {
        return mapOfPosition.get(position);
    }
}
