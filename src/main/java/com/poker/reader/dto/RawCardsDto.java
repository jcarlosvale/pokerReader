package com.poker.reader.dto;

import lombok.Data;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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

    @Override
    public String toString() {
        return card1 + card2;
    }

}
