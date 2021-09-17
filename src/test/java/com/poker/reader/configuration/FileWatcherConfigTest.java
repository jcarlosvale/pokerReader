package com.poker.reader.configuration;

import java.io.File;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


class FileWatcherConfigTest {

    @Test
    void testFolder() {
        File file = new File("c:\\temp");
        Assertions.assertThat(file.isDirectory()).isTrue();
    }
}