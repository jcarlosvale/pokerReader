package com.poker.reader.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.poker.reader.parser.util.Tokens.*;

@AllArgsConstructor
@Getter
public enum TypeInfo {
    COLLECTED(START_COLLECTED_FROM_POT_INFO),
    FOLDED_BEFORE_FLOP(FOLDED_BEFORE_FLOP_TYPE_INFO),
    FOLDED_ON_THE_RIVER(FOLDED_ON_THE_RIVER_TYPE_INFO),
    BUTTON(BUTTON_TYPE_INFO),
    BIG_BLIND(BIG_BLIND_TYPE_INFO),
    SMALL_BLIND(SMALL_BLIND_TYPE_INFO),
    SHOWED_HAND(SHOWED_HAND_INFO),
    WON(WON_INFO),
    LOST(LOST_INFO),
    ;
    private String token;
}
