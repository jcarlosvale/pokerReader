package com.poker.reader.domain.repository.projection;

public interface PlayerDtoProjection {
    String getNickname();

    Integer getTotalHands();

    Integer getShowdowns();

    Integer getShowdownStat();

    Integer getAvgChen();

    String getCreatedAt();

    String getCards();

    String getRawCards();
}
