package com.poker.reader.view.rs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HandDto {

    private long handId;
    private String level;
    private String blinds;
    private int players;
    private int showdowns;
    private String playedAt;
    private int pot;
    private String board;
    private String boardShowdown;
}
