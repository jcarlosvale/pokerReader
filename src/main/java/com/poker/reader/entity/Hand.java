package com.poker.reader.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private final Set<Seat> seats = new HashSet<>();
    private final List<Action> actions = new ArrayList<>();
}
