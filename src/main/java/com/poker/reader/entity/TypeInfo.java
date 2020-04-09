package com.poker.reader.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.poker.reader.parser.util.Tokens.*;

@AllArgsConstructor
@Getter
public enum TypeInfo {
    UNCALLED_BET(UNCALLED_BET_TYPE_INFO),
    COLLECTED(START_COLLECTED_FROM_POT),
    FOLDED_BEFORE_FLOP(FOLDED_BEFORE_FLOP_TYPE_INFO),
    FOLDED_ON_THE_RIVER(FOLDED_ON_THE_RIVER_TYPE_INFO),
    DID_NOT_BET(DID_NOT_BET_TYPE_INFO),
    BUTTON(BUTTON_TYPE_INFO),
    BIG_BLIND(BIG_BLIND_TYPE_INFO),
    SMALL_BLIND(SMALL_BLIND_TYPE_INFO)
    ;
    private String token;
}
