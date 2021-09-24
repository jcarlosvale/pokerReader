package com.poker.reader.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class PokerReaderProperties {

    @Value("${monitoring-folder}")
    private String folderPokerFiles;

    @Value("${monitoring-interval}")
    private long monitoringInterval;

    @Value("${page-size}")
    private int pageSize;

    @Value("${batch-size}")
    private int batchSize;
}
