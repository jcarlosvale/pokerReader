package com.poker.reader.parser;

import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class TestUtil {
/*
    public static AnalysedPlayer mockAnalysedPlayer(String player, String ... cards) {
        Map<NormalisedCardsDto, Integer> map = new HashMap<>();
        List<String> rawCardsList = new ArrayList<>();
        for (String card : cards) {
            map.put(DtoOperationsUtil.toNormalisedCardsDto(card), 1);
            rawCardsList.add(card);
        }
        return new AnalysedPlayer(player, map, rawCardsList);
    }

 */

    @Test
    void verifyDate() {
        String strDate = "2020/11/27 17:08:56 ET";
        String[] fields = strDate.split(" ");
        String[] date = fields[0].split("//");
        String[] time = fields[1].split(":");
        int year = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);
        int day = Integer.parseInt(date[2]);
        int hour = Integer.parseInt(time[0]);
        int min = Integer.parseInt(time[1]);
        int sec = Integer.parseInt(time[2]);
        var actualTime = LocalDateTime.of(year,month,day, hour, min, sec);
        Assertions
                .assertThat(actualTime)
                .isEqualTo(LocalDateTime.parse("2020-11-27T17:08:56"));
    }
}
