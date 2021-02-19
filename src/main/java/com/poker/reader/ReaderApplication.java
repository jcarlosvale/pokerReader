package com.poker.reader;

import com.poker.reader.configuration.PokerReaderProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PokerReaderProperties.class)
@SuppressWarnings("checkstyle:hideutilityclassconstructor")
public class ReaderApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ReaderApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
