package com.poker.reader.view.rs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsDto {

    //how many hands without playing, excluding BB, SB
    private int noActionCount;

    private String noActionPerc;

    private int sbCount;

    private int bbCount;

    private int btnCount;

    private int utgCount;

    private int mpCount;

    private int ljCount;

    private int hjCount;

    private int coCount;

}
