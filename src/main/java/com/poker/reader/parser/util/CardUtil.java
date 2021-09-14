package com.poker.reader.parser.util;

public final class CardUtil {

    private CardUtil() {}

    public static int valueOf(char faceCard) {
        if (faceCard == 'T') return 10;
        if (faceCard == 'J') return 11;
        if (faceCard == 'Q') return 12;
        if (faceCard == 'K') return 13;
        if (faceCard == 'A') return 14;
        return faceCard - '0';
    }
}
