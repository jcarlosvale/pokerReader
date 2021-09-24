package com.poker.reader.view.rs;

import com.poker.reader.configuration.PokerReaderProperties;
import com.poker.reader.domain.service.FileHtmlProcessorService;
import com.poker.reader.domain.service.FileProcessorService;
import com.poker.reader.domain.service.FileReaderService;
import com.poker.reader.view.rs.dto.PlayerDto;
import com.poker.reader.view.rs.dto.TournamentDto;
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
    private final PokerReaderProperties pokerReaderProperties;

    @GetMapping("/players")
    public String listPlayers(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(pokerReaderProperties.getPageSize());

        Page<PlayerDto> playerPage =
                fileHtmlProcessorService.findPaginatedPlayers
                        (PageRequest.of(currentPage - 1, pageSize, Sort.by("nickname").ascending()));

        model.addAttribute("playerPage", playerPage);

        int totalPages = playerPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "players";
    }

    @GetMapping("/tournaments")
    public String listTournaments(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(pokerReaderProperties.getPageSize());

        Page<TournamentDto> tournamentPage =
                fileHtmlProcessorService.findPaginatedTournaments
                        (PageRequest.of(currentPage - 1, pageSize, Sort.by("createdAt").descending()));

        model.addAttribute("tournamentPage", tournamentPage);

        int totalPages = tournamentPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "tournaments";
    }

    @GetMapping("/monitoring/{tournamentId}")
    public String listLastPlayersFromTournament(
            Model model,
            @PathVariable("tournamentId") String tournamentId) {

        List<PlayerDto> playerDtoList = fileHtmlProcessorService.getLastPlayersFromTournament(tournamentId);

        model.addAttribute("playersMonitoredList", playerDtoList);
        model.addAttribute("tournamentId", tournamentId);

        return "monitoring";
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
}
