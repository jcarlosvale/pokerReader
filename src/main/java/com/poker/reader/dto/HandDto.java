package com.poker.reader.dto;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public class HandDto {

    @Getter
    private final String handId;

    @Getter
    @Setter
    private final String tournamentId;

    public HandDto(String handId, String tournamentId) {
        checkNotNull(handId, "hand must be not null");
        checkNotNull(tournamentId, "tournament must be not null");

        this.handId = handId;
        this.tournamentId = tournamentId;
    }

    private final Map<FileSection, List<String>> sectionLinesMap = new HashMap<>();

    public void addLine(FileSection fileSection, String line) {
        sectionLinesMap.computeIfAbsent(fileSection, s -> new ArrayList<>()).add(line);
    }

    public List<String> getLinesFromSection(FileSection fileSection) {
        return sectionLinesMap.get(fileSection);
    }

}
