package com.poker.reader.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileProcessedDto {
    private String tournament;
    private int totalHands;
    private int totalPlayers;
    private Set<String> players;
    private List<AnalysedPlayer> analysedPlayers;

    @JsonCreator
    public FileProcessedDto(
            @JsonProperty("tournament") String tournament,
            @JsonProperty("totalHands") int totalHands,
            @JsonProperty("totalPlayers") int totalPlayers,
            @JsonProperty("players") Set<String> players,
            @JsonProperty("analysedPlayers") List<AnalysedPlayer> analysedPlayers) {
        this.tournament = tournament;
        this.totalHands = totalHands;
        this.totalPlayers = totalPlayers;
        this.players = players;
        this.analysedPlayers = analysedPlayers;
    }
}
