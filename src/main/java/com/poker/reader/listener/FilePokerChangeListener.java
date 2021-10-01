package com.poker.reader.listener;

import com.poker.reader.domain.service.FileImportService;
import com.poker.reader.domain.service.FileProcessorService;
import com.poker.reader.domain.util.Util;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.devtools.filewatch.ChangedFile;
import org.springframework.boot.devtools.filewatch.ChangedFile.Type;
import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class FilePokerChangeListener implements FileChangeListener {

    private final FileImportService fileImportService;
    private final FileProcessorService fileProcessorService;

    @SneakyThrows
    @Override
    public void onChange(Set<ChangedFiles> changeSet) {
        for(ChangedFiles cfiles : changeSet) {
            for(ChangedFile cfile: cfiles.getFiles()) {
                if(  (cfile.getType().equals(Type.MODIFY)
                     || cfile.getType().equals(Type.ADD))) {
                     //|| cfile.getType().equals(Type.DELETE) )) {

                    log.info("Operation: " + cfile.getType() + " On file: "+ cfile.getFile().getName() + " is done");
                    fileImportService.importFile(cfile.getFile().getName(), Util.readLinesFromFile(cfile.getFile()));
                    fileProcessorService.processLastHandFromPokerFile(cfile.getFile().getName());
                }
            }
        }
    }
}