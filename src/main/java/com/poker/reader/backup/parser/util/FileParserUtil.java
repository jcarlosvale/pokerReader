package com.poker.reader.backup.parser.util;

import com.poker.reader.backup.entity.TypeAction;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileParserUtil {

    public static final String DATE_TIME_FORMAT = "yyyy/MM/dd";

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

    public static LocalDate extractLocalDate(String line, String startDate, String endDate) {
        String value = extract(line, startDate, endDate);
        if (value == null) {return null;}
        value = value.substring(0,DATE_TIME_FORMAT.length()).trim();  //removing locale and ]
        return LocalDate.parse(value, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }

    public static Long extractValueFromAction(String line, TypeAction typeAction) {
        if (null == typeAction)                         {return null;}
        if (typeAction.equals(TypeAction.ANTE))         {return extractLongAfter(line, Tokens.ANTE_ACTION);}
        if (typeAction.equals(TypeAction.SMALL_BLIND))  {return extractLongAfter(line, Tokens.SMALL_BLIND_ACTION);}
        if (typeAction.equals(TypeAction.BIG_BLIND))    {return extractLongAfter(line, Tokens.BIG_BLIND_ACTION);}
        if (typeAction.equals(TypeAction.FOLD))         {return 0L;}
        //if (typeAction.equals(TypeAction.CALL_ALL_IN))  {return extractLong(line, Tokens.CALL_ACTION, Tokens.ALL_IN_ACTION);}
        if (typeAction.equals(TypeAction.CALL))         {return extractLongAfter(line, Tokens.CALL_ACTION);}
        if (typeAction.equals(TypeAction.BETS))         {return extractLongAfter(line, Tokens.BETS_ACTION);}
        if (typeAction.equals(TypeAction.CHECK))        {return 0L;}
        if (typeAction.equals(TypeAction.ALL_IN))       {return extractLong(line, " to ", Tokens.ALL_IN_ACTION);}
        if (typeAction.equals(TypeAction.RAISE))        {return extractLongAfter(line, " to ");}
        if (typeAction.equals(TypeAction.NO_SHOW_HAND)) {return 0L;}
        return null;
    }

    public static String extractCard(String line, String startToken, String endToken, int cardNumber) {
        String cards = extract(line, startToken, endToken);
        if (null == cards || cards.isEmpty()) return null;
        if (cards.split(" ").length < cardNumber) return null;
        return cards.split(" ")[cardNumber-1];
    }

    public static List<String> extractList(String line, String startToken, String endToken, String splitToken) {
        line = extract(line, startToken, endToken);
        String[] splited = line.split(splitToken);
        ArrayList<String> list = new ArrayList<>();
        for(String s : splited) {
            list.add(s);
        }
        return list;
    }
}
