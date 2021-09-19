package com.poker.reader.view.rs;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PokerReaderController {

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
