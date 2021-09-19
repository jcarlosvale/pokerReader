package com.poker.reader.domain.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Util {

    public static final String DATE_TIME_FORMAT = "yyyy/MM/dd";

    private Util() {}

    /**
     * Retrieves the list of file from a given directory
     * @param directory
     * @param extensionFiles
     * @return list of files
     */
    public static List<File> readFilesFromDirectory(String directory, String ... extensionFiles) {
        return (List<File>) FileUtils.listFiles(new File(directory), extensionFiles, false);
    }

    /**
     * Returns the lines from a give filepath
     * @param filePath
     * @return list of line
     * @throws IOException
     */
    public static List<String> readLinesFromFile(String filePath) throws IOException {
        return readLinesFromFile(new File(filePath));
    }

    public static List<String> readLinesFromFile(File file) throws IOException {
        return FileUtils.readLines(file, "utf-8");
    }

    public static LocalDate toLocalDate(String strDateTime) {
        return LocalDate.parse(strDateTime, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));

    }
}
