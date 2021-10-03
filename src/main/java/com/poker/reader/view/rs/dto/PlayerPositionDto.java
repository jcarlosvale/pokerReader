package com.poker.reader.view.rs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

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

    private String css;
}
