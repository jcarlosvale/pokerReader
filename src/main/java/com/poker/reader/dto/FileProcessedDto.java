package com.poker.reader.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileProcessedDto {
    private String tournament;
    private int totalHands;
    private int totalPlayers;
    private List<AnalysedPlayer> analysedPlayers;
}
