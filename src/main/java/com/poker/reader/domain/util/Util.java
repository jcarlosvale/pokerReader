package com.poker.reader.domain.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public static LocalDateTime toLocalDateTime(String strDateTime) {
        String[] fields = strDateTime.split(" ");
        String[] date = fields[0].split("/");
        String[] time = fields[1].split(":");
        int year = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);
        int day = Integer.parseInt(date[2]);
        int hour = Integer.parseInt(time[0]);
        int min = Integer.parseInt(time[1]);
        int sec = Integer.parseInt(time[2]);
        return LocalDateTime.of(year,month,day, hour, min, sec);
    }

    public static String toLocalDatetime(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }
}
