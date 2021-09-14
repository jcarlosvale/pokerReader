package com.poker.reader.parser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class LinesOfHand {

    private final Map<FileSection, List<String>> sectionLinesMap = new HashMap<>();

    @Getter
    private final String handId;

    @Getter
    @Setter
    private final String tournamentId;

    @JsonCreator
    public LinesOfHand(@NonNull @JsonProperty("handId") String handId,
                   @NonNull @JsonProperty("tournamentId") String tournamentId) {
        this.handId = handId;
        this.tournamentId = tournamentId;
    }

    public void addLine(FileSection fileSection, String line) {
        sectionLinesMap.computeIfAbsent(fileSection, s -> new ArrayList<>()).add(line);
    }

    public List<String> getLinesFromSection(FileSection fileSection) {
        return sectionLinesMap.get(fileSection);
    }

}
