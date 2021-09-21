package com.poker.reader.view.rs;

import com.poker.reader.configuration.PokerReaderProperties;
import com.poker.reader.domain.service.FileHtmlProcessorService;
import com.poker.reader.view.rs.dto.PlayerDto;
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
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class PokerReaderController {

    private final FileHtmlProcessorService fileHtmlProcessorService;
    private final PokerReaderProperties pokerReaderProperties;

    @GetMapping("/players")
    public String listBooks(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(pokerReaderProperties.getPageSize());

        Page<PlayerDto> playerPage = fileHtmlProcessorService.findPaginated(PageRequest.of(currentPage - 1, pageSize,
                Sort.by("nickname").ascending()));

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

    @GetMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("message", "some message 2");
        return "hello";
    }

    @GetMapping("/bootstrap")
    public String bootstrap() {
        return "bootstrap-add";
    }
}
