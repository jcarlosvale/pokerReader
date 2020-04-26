package com.poker.reader.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Action {
    private Player player;
    private TypeAction typeAction;
    private Long value;
    private TypeStreet typeStreet;
    private HoldCards holdCards;
    private String scoring;
}
