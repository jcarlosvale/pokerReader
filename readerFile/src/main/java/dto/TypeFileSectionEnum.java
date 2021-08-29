package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import parser.Tokens;

@AllArgsConstructor
@Getter
public enum TypeFileSectionEnum {
    CHAT_MESSAGE(Tokens.SECTION_CHAT_MESSAGE),
    HEADER(Tokens.SECTION_HEADER),
    PRE_FLOP(Tokens.SECTION_PRE_FLOP),
    FLOP(Tokens.SECTION_FLOP),
    TURN(Tokens.SECTION_TURN),
    RIVER(Tokens.SECTION_RIVER),
    SHOWDOWN(Tokens.SECTION_SHOWDOWN),
    SUMMARY(Tokens.SECTION_SUMMARY),
    END_OF_HAND(""),
    ;
    private String token;
}
