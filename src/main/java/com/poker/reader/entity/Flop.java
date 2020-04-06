package com.poker.reader.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Flop {
    private String card1;
    private String card2;
    private String card3;
}
