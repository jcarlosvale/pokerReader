package com.poker.reader.controller;

import com.poker.reader.service.FileReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PokerController {

    private final FileReaderService fileReaderService;

    @GetMapping("/load")
    public ResponseEntity<String> load() throws IOException {
        List<String> filesProcessed = fileReaderService.processFilesFromFolder();
        return ResponseEntity.ok(filesProcessed.toString());
    }
}
