package org.base.fetcher;

import org.base.dto.RecordData;
import org.base.dto.RecordErrors;
import org.base.dto.URLErrors;
import org.base.exectutor.ExecutorServiceManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.base.exectutor.ExecutorServiceManager.MAX_THREADS;
import static org.base.util.ValidationUtil.isCSVFile;
import static org.base.util.ValidationUtil.isURL;

public class DataFetcher {

    // This method fetches record data from multiple paths/URLs concurrently
    public static List<RecordData> fetchRecordData(List<String> pathsOrURLs, Set<URLErrors> urlErrors, Set<RecordErrors> lineErrors, AtomicInteger threadCount) {
        ExecutorServiceManager executorServiceManager = new ExecutorServiceManager(Math.min(pathsOrURLs.size(), MAX_THREADS));

        // Submit tasks for each path or URL using CompletableFuture and collect the futures
        List<CompletableFuture<List<RecordData>>> futures = pathsOrURLs.stream()
                .map(pathOrUrl -> executorServiceManager.submitTask(() -> fetchData(pathOrUrl, urlErrors, lineErrors)))
                .collect(Collectors.toList());

        // Wait for all tasks to complete and then shut down the ExecutorService
        executorServiceManager.waitForCompletion(futures);
        executorServiceManager.shutdown();

        // Calculate the active thread count after completion
        threadCount.set(Thread.activeCount() - threadCount.get());

        // Combine results and sort by age
        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .sorted(Comparator.comparing(RecordData::getAge))
                .collect(Collectors.toList());
    }

    // This method reads record data from multiple multipart CSV files concurrently
    public static List<RecordData> readCSVRecordDataFiles(List<MultipartFile> csvFiles, Set<URLErrors> urlErrors, Set<RecordErrors> lineErrors, AtomicInteger threadCount) {
        ExecutorServiceManager executorServiceManager = new ExecutorServiceManager(Math.min(csvFiles.size(), MAX_THREADS));

        List<CompletableFuture<List<RecordData>>> futures = csvFiles.stream()
                .map(file -> executorServiceManager.submitTask(() -> processSingleCSVFile(file, urlErrors, lineErrors)))
                .collect(Collectors.toList());

        // Wait for all tasks to complete and then shut down the ExecutorService
        executorServiceManager.waitForCompletion(futures);
        executorServiceManager.shutdown();

        // Calculate the active thread count after completion
        threadCount.set(Thread.activeCount() - threadCount.get());

        // Combine results and sort by age
        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .sorted(Comparator.comparing(RecordData::getAge))
                .collect(Collectors.toList());
    }

    private static List<RecordData> fetchData(String pathOrUrl, Set<URLErrors> urlErrors, Set<RecordErrors> lineErrors) {
        try {
            List<String> recordLineErrors = new ArrayList<>();

            if (isURL(pathOrUrl)) {
                // Handle URL
                URL url = new URL(pathOrUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    return processCSVFile(pathOrUrl, reader, recordLineErrors, urlErrors, lineErrors);
                } finally {
                    // Add record line errors if any and close the connection
                    if (!recordLineErrors.isEmpty()) {
                        lineErrors.add(new RecordErrors(pathOrUrl, recordLineErrors));
                    }
                    connection.disconnect();
                }
            } else {
                // Handle local file
                try (BufferedReader reader = new BufferedReader(new FileReader(pathOrUrl))) {
                    return processCSVFile(pathOrUrl, reader, recordLineErrors, urlErrors, lineErrors);
                }
            }
        } catch (IOException e) {
            urlErrors.add(new URLErrors(pathOrUrl, "Error accessing file or URL"));
            return Collections.emptyList();
        }
    }

    public static List<RecordData> processSingleCSVFile(MultipartFile file, Set<URLErrors> urlErrors, Set<RecordErrors> lineErrors) throws IOException {
        // Read the first line to check if its a valid CSV file
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<String> recordLineErrors = new ArrayList<>();

            // Process each line of the CSV file and extract the specified columns
            return processCSVFile(file.getOriginalFilename(), reader, recordLineErrors, urlErrors, lineErrors);
        }
    }

    public static List<RecordData> processCSVFile(String pathOrUrl, BufferedReader reader, List<String> recordLineErrors,
                                                  Set<URLErrors> urlErrors, Set<RecordErrors> lineErrors) throws IOException {
        // Read the first line to check if its a valid CSV file
        String firstLine = reader.readLine();
        if (!isCSVFile(pathOrUrl, firstLine)) {
            urlErrors.add(new URLErrors(pathOrUrl, "File is not a valid CSV"));
            return Collections.emptyList();
        }

        List<RecordData> allData = reader.lines()
                .map(line -> parseRecordLine(pathOrUrl, line, recordLineErrors))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!recordLineErrors.isEmpty()) {
            lineErrors.add(new RecordErrors(pathOrUrl, recordLineErrors));
        }

        return allData;
    }

    public static RecordData parseRecordLine(String url, String line, List<String> lineErrors) {
        String[] parts = line.split(",");
        if (parts.length == 3) {
            try {
                String fname = parts[0].trim();
                String lname = parts[1].trim();
                int age = Integer.parseInt(parts[2].trim());

                RecordData recordData = new RecordData();
                recordData.setFname(fname);
                recordData.setLname(lname);
                recordData.setAge(age);

                return recordData;
            } catch (NumberFormatException e) {
                lineErrors.add("Invalid age format in line: " + line + " for url " + url);
                return null;
            }
        } else {
            lineErrors.add("RecordError on line: " + line + " for url " + url);
            return null;
        }
    }
}
