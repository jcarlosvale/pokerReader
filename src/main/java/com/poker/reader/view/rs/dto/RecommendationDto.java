package com.poker.reader.view.rs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationDto {

    String nickname;
    Long tournamentId;
    Long handId;
    Integer minBlinds;
    Integer avgStack;
    Integer stack;
    Integer blinds;
    String recommendation;
    String css;


}
