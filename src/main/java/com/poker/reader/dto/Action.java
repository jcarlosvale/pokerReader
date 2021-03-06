package com.poker.reader.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Action {
    private PlayerDTO playerDTO;
    private TypeAction typeAction;
    private Long value;
    private TypeStreet typeStreet;
    private HoldCards holdCards;
    private String scoring;
}
