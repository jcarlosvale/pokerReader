package com.poker.reader.parser.util;

import static com.poker.reader.parser.util.CardUtil.valueOf;

public class Chen {

    public static final long MIN = -100;


    /**
     *
     * @param card1
     * @param card2
     * @param pair
     * @param suited
     * @return
     */
    public static int calculateChenFormulaFrom(char card1, char card2, boolean pair, boolean suited) {
        double chenValue = chenValueOf(card1);
        int gap = 0;
        if (pair) {
            if (card1 == '2') {
                chenValue = 5;
            } else {
                chenValue *= 2;
            }
        } else {
            gap = valueOf(card1) - valueOf(card2) - 1;
            if (gap == 3) {
                gap = 4;
            } else {
                if (gap >= 4) {
                    gap = 5;
                }
            }
            chenValue -= gap;

            if ((gap <= 1) && (valueOf(card1) < valueOf('Q'))) {
                chenValue += 1;
            }
        }
        if (suited) {
            chenValue += 2;
        }
        return (int) Math.round(chenValue);
    }

    public static int calculateChenFormulaFrom(String card) {
        return calculateChenFormulaFrom(
                card.charAt(0), card.charAt(1),
                card.charAt(0) == card.charAt(1),
                card.length() > 2 && card.charAt(2) == 's');
    }


    private static double chenValueOf(char card) {
        if (card == 'T') return 5;
        if (card == 'J') return 6;
        if (card == 'Q') return 7;
        if (card == 'K') return 8;
        if (card == 'A') return 10;
        return (card - '0')/2.0;
    }
}
