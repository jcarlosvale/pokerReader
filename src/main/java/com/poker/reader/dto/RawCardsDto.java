package com.poker.reader.dto;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import lombok.Data;

@Data
public class RawCardsDto {
    private String rawData;
    private String card1;
    private String card2;

    public RawCardsDto(String rawData) {
        checkNotNull(rawData, "cards must be not null");
        checkArgument(rawData.length() > 4, "invalid format of rawdata " + rawData);
        this.rawData = rawData;
        this.card1 = rawData.substring(0,2);
        this.card2 = rawData.substring(3,5);
    }

    public static void main(String[] args) {
        String test = "Seat 2: tEddy-KBG 77 (334 in chips) is sitting out";
        System.out.println();

    }
}
