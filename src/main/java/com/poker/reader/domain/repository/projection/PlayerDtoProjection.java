package com.poker.reader.domain.repository.projection;

import java.time.LocalDateTime;

public interface PlayerDtoProjection {
    String getNickname();

    Integer getTotalHands();

    Integer getShowdowns();

    Integer getShowdownStat();

    Integer getAvgChen();

    LocalDateTime getCreatedAt();

    String getCards();

    String getRawCards();

    String getCss();
}
