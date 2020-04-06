package com.poker.reader.parser.util;

import com.poker.reader.entity.TypeAction;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileParserUtil {

    public static final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

    public static Integer extractInteger(String line, String startToken, String endToken) {
        String value = extract(line, startToken, endToken);
        return (StringUtils.isNotBlank(value)) ? Integer.valueOf(value) : null;
    }

    public static Long extractLong(String line, String startToken, String endToken) {
        String value = extract(line, startToken, endToken);
        return (StringUtils.isNotBlank(value)) ? Long.valueOf(value) : null;
    }

    public static Long extractLongAfter(String line, String afterToken) {
        String value = StringUtils.substringAfter(line, afterToken);
        return (StringUtils.isNotBlank(value)) ? Long.valueOf(value.trim()) : null;
    }

    public static BigDecimal extractBigDecimal(String line, String startToken, String endToken) {
        String value = extract(line, startToken, endToken);
        return (StringUtils.isNotBlank(value)) ? new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN) :
                BigDecimal.ZERO;
    }

    public static String extract(String line, String startToken, String endToken) {
        String value = StringUtils.substringBetween(line, startToken, endToken);
        return (StringUtils.isNotBlank(value)) ? value.trim() : null;
    }

    public static LocalDateTime extractLocalDateTime(String line, String startDate, String endDate) {
        String value = extract(line, startDate, endDate);
        if (value == null) return null;
        value = value.substring(0,value.length()-3);  //removing locale and ]
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }

    public static TypeAction selectTypeAction(String line) {
        if (line.contains(Tokens.ANTE))         return TypeAction.ANTE;
        if (line.contains(Tokens.SMALL_BLIND))  return TypeAction.SMALL_BLIND;
        if (line.contains(Tokens.BIG_BLIND))    return TypeAction.BIG_BLIND;
        if (line.contains(Tokens.FOLD))         return TypeAction.FOLD;
        if (line.contains(Tokens.CALL)) {
            if (line.contains(Tokens.ALL_IN))   return TypeAction.CALL_ALL_IN;
            else                                return TypeAction.CALL;
        }
        if (line.contains(Tokens.CHECK))        return TypeAction.CHECK;
        if (line.contains(Tokens.BETS))         return TypeAction.BETS;

        if (line.contains(Tokens.RAISE)) {
            if (line.contains(Tokens.ALL_IN))   return TypeAction.ALL_IN;
            else                                return TypeAction.RAISE;
        }
        return null;
    }

    public static Long extractValueFromAction(String line, TypeAction typeAction) {
        if (null == typeAction) return null;
        if (typeAction.equals(TypeAction.ANTE))         return extractLongAfter(line, Tokens.ANTE);
        if (typeAction.equals(TypeAction.SMALL_BLIND))  return extractLongAfter(line, Tokens.SMALL_BLIND);
        if (typeAction.equals(TypeAction.BIG_BLIND))    return extractLongAfter(line, Tokens.BIG_BLIND);
        if (typeAction.equals(TypeAction.FOLD))         return 0L;
        if (typeAction.equals(TypeAction.CALL_ALL_IN))  return extractLong(line, Tokens.CALL, Tokens.ALL_IN);
        if (typeAction.equals(TypeAction.CALL))         return extractLongAfter(line, Tokens.CALL);
        if (typeAction.equals(TypeAction.BETS))         return extractLongAfter(line, Tokens.BETS);
        if (typeAction.equals(TypeAction.CHECK))        return 0L;
        if (typeAction.equals(TypeAction.ALL_IN))       return extractLong(line, " to ", Tokens.ALL_IN);
        if (typeAction.equals(TypeAction.RAISE))        return extractLongAfter(line, " to ");
        return null;
    }

    public static String extractCard(String line, String startToken, String endToken, int cardNumber) {
        String cards = extract(line, startToken, endToken);
        if (null == cards || cards.isEmpty()) return null;
        return cards.split(" ")[cardNumber-1];
    }
}
