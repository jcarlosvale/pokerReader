package com.poker.reader.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HoldCards {
    private Player player;
    private String card1;
    private String card2;
}
