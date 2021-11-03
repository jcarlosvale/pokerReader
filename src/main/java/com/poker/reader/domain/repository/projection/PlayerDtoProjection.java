package com.poker.reader.domain.repository.projection;

import com.poker.reader.domain.service.Analyse;

public interface PlayerDtoProjection {
    String getNickname();

    Integer getTotalHands();

    Integer getShowdowns();

    Integer getShowdownStat();

    Integer getAvgChen();

    String getCreatedAt();

    String getCards();

    String getRawCards();

    default String getCss() {
        return Analyse.analyseChen(getAvgChen()).getCss();
    }
}
