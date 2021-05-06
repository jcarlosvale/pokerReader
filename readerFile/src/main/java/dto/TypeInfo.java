package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import parser.Tokens;

@AllArgsConstructor
@Getter
public enum TypeInfo {
    COLLECTED(Tokens.START_COLLECTED_FROM_POT_INFO),
    FOLDED_BEFORE_FLOP(Tokens.FOLDED_BEFORE_FLOP_TYPE_INFO),
    FOLDED_ON_THE_RIVER(Tokens.FOLDED_ON_THE_RIVER_TYPE_INFO),
    BUTTON(Tokens.BUTTON_TYPE_INFO),
    BIG_BLIND(Tokens.BIG_BLIND_TYPE_INFO),
    SMALL_BLIND(Tokens.SMALL_BLIND_TYPE_INFO),
    SHOWED_HAND(Tokens.SHOWED_HAND_INFO),
    WON(Tokens.WON_INFO),
    LOST(Tokens.LOST_INFO),
    ;
    private String token;
}
