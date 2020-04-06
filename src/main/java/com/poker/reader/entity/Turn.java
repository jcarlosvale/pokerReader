package com.poker.reader.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Turn {
    private String card;
}
