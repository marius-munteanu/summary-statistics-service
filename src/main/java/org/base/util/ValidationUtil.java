package org.base.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Class used for validating files
public class ValidationUtil {
    public static boolean isCSVFile(String filePath, String firstLine) {
        if (!filePath.toLowerCase().endsWith(".csv")) {
            return false;
        }
        return firstLine != null && (firstLine.contains(","));
    }

    public static List<String> readURLNames(MultipartFile multipartFile) {
        List<String> dataURLs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()))) {
            reader.lines().forEach(dataURLs::add);
        } catch (IOException e) {
            return Collections.emptyList();
        }
        return dataURLs;
    }

    public static boolean isURL(String pathOrUrl) {
        return pathOrUrl.toLowerCase().startsWith("http://") || pathOrUrl.toLowerCase().startsWith("https://");
    }
}
