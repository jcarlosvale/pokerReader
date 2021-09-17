package com.poker.reader.processor;

import static com.poker.reader.parser.TestUtil.mockAnalysedPlayer;
import static com.poker.reader.processor.FileHtmlProcessor.generatePlayersTable;
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

        String expectedTable =
                "<tbody><tr class=\"bg-primary\"><td>player 1</td><td>10</td><td>2</td><td>1xTT, 1xAQo</td></tr><tr class=\"bg-danger\"><td>player 2</td><td>3</td><td>2</td><td>1xQJo, 1x72o</td></tr></tbody>";
        //when
        String actualTable = generatePlayersTable(analysedPlayerList);

        //then
        assertThat(actualTable.split("\n")[1]).isEqualTo(expectedTable);
    }

}