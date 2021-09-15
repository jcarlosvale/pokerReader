package com.poker.reader.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileProcessedDto {
    private String tournament;
    private int totalHands;
    private Set<String> players;
    private List<AnalysedPlayer> analysedPlayers;
}
