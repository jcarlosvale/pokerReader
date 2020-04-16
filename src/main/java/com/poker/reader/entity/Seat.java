package com.poker.reader.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Seat {
    private Integer seatId;
    private Player player;
    private Long stack;
    private HoldCards holdCards;
    private Summary summary;
}
