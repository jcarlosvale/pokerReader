package com.poker.reader.view.rs.model;

import com.poker.reader.view.rs.dto.PlayerMonitoredDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private Integer avgStack;

    private List<PlayerMonitoredDto> playerMonitoredDto;


}
