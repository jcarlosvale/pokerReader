package com.poker.reader.configuration;

import com.poker.reader.listener.MyFileChangeListener;
import java.io.File;
import java.time.Duration;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class FileWatcherConfig extends FileSystemWatcher{

    private final MyFileChangeListener myFileChangeListener;
    private final PokerReaderProperties pokerReaderProperties;

    @PostConstruct
    public void listenFolder() {

        String folderPath = pokerReaderProperties.getFolderPokerFiles();
        long monitoringInterval = pokerReaderProperties.getMonitoringInterval();

        File folder = new File(folderPath);

        if(!folder.isDirectory()) {
            throw new RuntimeException("DIRECTORY TO MONITOR NOT FOUND: " + folderPath);
        } else {
            FileSystemWatcher fileSystemWatcher =
                    new FileSystemWatcher(true, Duration.ofMillis(monitoringInterval), Duration.ofMillis(2000L));
            log.info("Starting fileSystemWatcher: " + folderPath);
            fileSystemWatcher.addSourceDirectory(folder);
            fileSystemWatcher.addListener(myFileChangeListener);
            fileSystemWatcher.start();
            log.info("started fileSystemWatcher");
        }
    }

    @PreDestroy
    public void onDestroy() {
        stop();
    }
}