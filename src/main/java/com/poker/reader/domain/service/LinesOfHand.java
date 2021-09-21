package com.poker.reader.domain.service;

import com.poker.reader.domain.model.FileSection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LinesOfHand {

    private final Map<FileSection, List<String>> sectionLinesMap = new HashMap<>();


    private String handId;

    private String tournamentId;

    private String filename;

    private LocalDateTime playedAt;


    public void addLine(FileSection fileSection, String line) {
        sectionLinesMap.computeIfAbsent(fileSection, s -> new ArrayList<>()).add(line);
    }

    public List<String> getLinesFromSection(FileSection fileSection) {
        return sectionLinesMap.get(fileSection);
    }

}
