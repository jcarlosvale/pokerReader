package com.poker.reader.view.rs.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerDto {

    @NotNull
    private String nickname;

    @NotNull
    private Integer totalHands;

    @NotNull
    private Integer showdowns;

    @NotNull
    private Integer showdownStat;

    @NotNull
    private Integer avgChen;

    @NotNull
    private String createdAt;

    @NotNull
    private String cards;

    @NotNull
    private String rawCards;

    private String css;
}
