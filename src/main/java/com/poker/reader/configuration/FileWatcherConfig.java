package com.poker.reader.configuration;

import com.poker.reader.listener.MyFileChangeListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.File;
import java.time.Duration;

@Configuration
@Log4j2
public class FileWatcherConfig {

    @Value("${monitoring-folder}")
    private String folderPath;

    @Value("${monitoring-interval}")
    private long monitoringInterval;

    @Autowired
    MyFileChangeListener myFileChangeListener;

    @Bean
    public FileSystemWatcher fileSystemWatcher() {
        File folder = new File(folderPath);

        if(!folder.isDirectory()) {
            throw new RuntimeException("DIRECTORY TO MONITOR NOT FOUND: " + folderPath);
        } else {
            FileSystemWatcher fileSystemWatcher = new FileSystemWatcher(true, Duration.ofMillis(monitoringInterval), Duration.ofMillis(2000L));
            log.info("Starting fileSystemWatcher: " + folderPath);
            fileSystemWatcher.addSourceDirectory(folder);
            fileSystemWatcher.addListener(myFileChangeListener);
            fileSystemWatcher.start();
            log.info("started fileSystemWatcher");
            return fileSystemWatcher;
        }
    }

    @PreDestroy
    public void onDestroy() {
        fileSystemWatcher().stop();
    }
}