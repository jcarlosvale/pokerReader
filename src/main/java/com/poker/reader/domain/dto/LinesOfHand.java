package com.poker.reader.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class LinesOfHand {

    private final Map<FileSection, List<String>> sectionLinesMap = new HashMap<>();


    private String handId;

    private String tournamentId;

    private LocalDate playedAt;


    public void addLine(FileSection fileSection, String line) {
        sectionLinesMap.computeIfAbsent(fileSection, s -> new ArrayList<>()).add(line);
    }

    public List<String> getLinesFromSection(FileSection fileSection) {
        return sectionLinesMap.get(fileSection);
    }

}
