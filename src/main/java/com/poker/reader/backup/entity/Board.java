package com.poker.reader.backup.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Board {
    private String card1;
    private String card2;
    private String card3;
    private String card4;
    private String card5;
}
