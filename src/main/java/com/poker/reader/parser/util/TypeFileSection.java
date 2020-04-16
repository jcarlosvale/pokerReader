package com.poker.reader.parser.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TypeFileSection {
    HEADER(Tokens.SECTION_HEADER),
    PRE_FLOP(Tokens.SECTION_PRE_FLOP),
    FLOP(Tokens.SECTION_FLOP),
    TURN(Tokens.SECTION_TURN),
    RIVER(Tokens.SECTION_RIVER),
    SUMMARY(Tokens.SECTION_SUMMARY),
    END_OF_HAND(""),
    ;
    private String token;
}
