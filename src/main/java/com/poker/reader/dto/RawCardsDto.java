package com.poker.reader.dto;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import lombok.Getter;

@Getter
public class RawCardsDto {
    private String rawData;
    private String card1;
    private String card2;

    public RawCardsDto(String rawData) {
        checkNotNull(rawData, "cards must be not null");
        checkArgument(rawData.length() > 5, "invalid format of rawdata");
        this.rawData = rawData;
        this.card1 = rawData.substring(0,2);
        this.card2 = rawData.substring(3,5);
    }
}
