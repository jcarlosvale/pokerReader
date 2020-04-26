package com.poker.reader.entity;

import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Seat {
    private Integer seatId;
    private Player player;
    private Long stack;
    private HoldCards holdCards;
    private final Set<InfoPlayerAtHand> infoPlayerAtHandList = new HashSet<>();
}
