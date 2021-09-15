package com.poker.reader.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    public int getTotalPlayers() {
        return players.size();
    }

    @JsonCreator
    public FileProcessedDto(
            @JsonProperty("tournament") String tournament,
            @JsonProperty("totalHands") int totalHands,
            @JsonProperty("players") Set<String> players,
            @JsonProperty("analysedPlayers") List<AnalysedPlayer> analysedPlayers) {
        this.tournament = tournament;
        this.totalHands = totalHands;
        this.players = players;
        this.analysedPlayers = analysedPlayers;
    }
}
