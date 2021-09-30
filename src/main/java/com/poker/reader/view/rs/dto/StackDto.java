package com.poker.reader.view.rs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StackDto {

    private long avgStack;

    private long stackFromHero;

    private int blinds;

    private String recommendation;

    private long minBlinds;
}
