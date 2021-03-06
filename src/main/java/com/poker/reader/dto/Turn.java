package com.poker.reader.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Turn {
    private String card;
}
