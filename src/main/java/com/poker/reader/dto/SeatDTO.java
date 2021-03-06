package com.poker.reader.dto;

import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class SeatDTO {
    private Integer seatId;
    private PlayerDTO playerDTO;
    private Long stack;
    private HoldCards holdCards;
    private final Set<InfoPlayerAtHand> infoPlayerAtHandList = new HashSet<>();
}
