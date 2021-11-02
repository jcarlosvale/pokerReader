package com.poker.reader.view.rs;

import com.poker.reader.configuration.PokerReaderProperties;
import com.poker.reader.domain.repository.projection.HandDtoProjection;
import com.poker.reader.domain.repository.projection.PlayerDtoProjection;
import com.poker.reader.domain.repository.projection.TournamentDtoProjection;
import com.poker.reader.domain.service.FileHtmlProcessorService;
import com.poker.reader.domain.service.FileProcessorService;
import com.poker.reader.domain.service.FileReaderService;
import com.poker.reader.domain.service.StatsService;
import com.poker.reader.view.rs.dto.PageDto;
import com.poker.reader.view.rs.dto.PlayerMonitoredDto;
import com.poker.reader.view.rs.model.ModelTournamentMonitored;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class PokerReaderController {

    private final FileHtmlProcessorService fileHtmlProcessorService;
    private final FileReaderService fileReaderService;
    private final FileProcessorService fileProcessorService;
    private final StatsService statsService;
    private final PokerReaderProperties pokerReaderProperties;

    @GetMapping("/players")
    public String listPlayers(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(pokerReaderProperties.getPageSize());

        Page<PlayerDtoProjection> playerPage =
                fileHtmlProcessorService
                        .findPaginatedPlayers(PageRequest.of(currentPage - 1, pageSize, Sort.by("nickname").ascending()));

        model.addAttribute("playerPage", playerPage);

        addPaginationToModel(model, playerPage.getTotalPages());

        return "players";
    }

    @GetMapping("/tournaments")
    public String listTournaments(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(pokerReaderProperties.getPageSize());

        Page<TournamentDtoProjection> tournamentPage =
                fileHtmlProcessorService.findPaginatedTournaments
                        (PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("tournamentPage", tournamentPage);

        addPaginationToModel(model, tournamentPage.getTotalPages());

        return "tournaments";
    }

    @GetMapping("/monitoring/{tournamentId}")
    public String monitorTournament(
            Model model,
            @PathVariable("tournamentId") Long tournamentId) {

        ModelTournamentMonitored modelTournamentMonitored = fileHtmlProcessorService.getTournamentMonitoredModel(tournamentId);

        model.addAttribute("modelTournamentMonitored", modelTournamentMonitored);

        return "monitoring";
    }

    @GetMapping("/tournament/{tournamentId}")
    public String detailsFromTournament(
            Model model,
            @PathVariable("tournamentId") Long tournamentId) {

        List<HandDtoProjection> handsDto = fileHtmlProcessorService.getHandsFromTournament(tournamentId);
        model.addAttribute("handsDto", handsDto);
        model.addAttribute("tournamentId", tournamentId);

        return "tournament";
    }

    @GetMapping("/hand/{handId}")
    public String detailsFromHand(
            Model model,
            @PathVariable("handId") Long handId) {

        HandDtoProjection handDto = fileHtmlProcessorService.getHand(handId);
        List<PlayerMonitoredDto> playerMonitoredDtoList = fileHtmlProcessorService.getPlayerMonitoredListFromTournamentAndHand(handId);
        String rawData = fileHtmlProcessorService.getRawDataFrom(handId);
        PageDto pageDto = fileHtmlProcessorService.createHandPaginationFromTournament(handId, handDto.getTournamentId());

        model.addAttribute("pageDto", pageDto);
        model.addAttribute("handDto", handDto);
        model.addAttribute("playerMonitoredDtoList", playerMonitoredDtoList);
        model.addAttribute("rawData", rawData);

        return "hand";
    }

    @GetMapping("/player/{nickname}")
    public String detailsFromHand(
            Model model,
            @PathVariable("nickname") String nickname) {

        PlayerDtoProjection playerDtoProjection = fileHtmlProcessorService.findPlayer(nickname);
        model.addAttribute("player", playerDtoProjection);
        return "player";
    }

    @GetMapping("/")
    public String main() {
        return "main";
    }

    @GetMapping("/importFiles")
    public String importFiles(Model model) {
        String message;
        try {
            message = fileReaderService.importPokerHistoryFiles();
        } catch (IOException e) {
            message = e.getMessage();
        }

        model.addAttribute("messageHeader", "Importing Files:");
        model.addAttribute("message", message);
        return "process";
    }

    @GetMapping("/processFiles")
    public String processFiles(Model model) {
        String message;
        try {
            message = fileProcessorService.processFilesFromDatabase();
        } catch (Exception e) {
            message = e.getMessage();
        }

        model.addAttribute("messageHeader", "Processing Files:");
        model.addAttribute("message", message);
        return "process";
    }

    @GetMapping("/bootstrap")
    public String bootstrap() {
        return "bootstrap-add";
    }

    private void addPaginationToModel(Model model, int totalPages) {
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
    }
}
