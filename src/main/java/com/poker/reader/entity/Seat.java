package com.poker.reader.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Seat {
    private Integer absolutePosition;
    private Player player;
    private Long stack;
}
