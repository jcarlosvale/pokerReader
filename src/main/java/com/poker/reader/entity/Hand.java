package com.poker.reader.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class Hand {
    private Long id;
    private String level;
    private Integer smallBlind;
    private Integer bigBlind;
    private LocalDateTime dateTime;
    private String tableId;
    private Integer button;
}
