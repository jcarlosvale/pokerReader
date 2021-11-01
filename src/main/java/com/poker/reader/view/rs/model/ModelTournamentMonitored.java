package com.poker.reader.view.rs.model;

import com.poker.reader.view.rs.dto.PlayerMonitoredDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelTournamentMonitored {

    private Long tournamentId;
    private Long handId;

    private Integer stackOfHero;

    private Integer minBlindsRecommendation;
    private String recommendation;
    private String cssRecommendation;

    private Integer avgStack;
    private Integer blindsCount;

    private List<PlayerMonitoredDto> playerMonitoredDtoList;


}
