package com.poker.reader.parser.processor;

import static com.poker.reader.parser.TestUtil.mockAnalysedPlayer;
import static com.poker.reader.parser.processor.FileHtmlProcessor.generatePlayersTable;
import static org.assertj.core.api.Assertions.assertThat;

import com.poker.reader.dto.AnalysedPlayer;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class FileHtmlProcessorTest {

    @Test
    void generateTable() {
        //given
        List<AnalysedPlayer> analysedPlayerList = new ArrayList<>();
        analysedPlayerList.add(mockAnalysedPlayer("player 1", "Ah Qt", "Ts Th"));
        analysedPlayerList.add(mockAnalysedPlayer("player 2", "2h 7t", "Qs Jh"));

        String expectedTable = "<thead><tr><th>player</th><th>cards</th></tr></thead>\n"
                + "<tbody><tr class=\"table-active\"><td>player 1</td><td>TT, AQo</td></tr><tr class=\"table-active\"><td>player 2</td><td>QJo, 72o</td></tr></tbody>";
        //when
        String actualTable = generatePlayersTable(analysedPlayerList);

        //then
        assertThat(actualTable).isEqualTo(expectedTable);
    }

}