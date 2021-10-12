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
public class PlayerPositionDto {
    @NotNull
    private String nickname;

    @NotNull
    private Integer chen;

    @NotNull
    private String cards;

    @NotNull
    private String position;

    @NotNull
    private long stack;

    @NotNull
    private int blinds;

    private boolean isWinner;

    private boolean isLose;

    private String handDescription;

    private String css;
}
