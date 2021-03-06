package com.poker.reader.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HoldCards {
    private PlayerDTO playerDTO;
    private String card1;
    private String card2;
}
