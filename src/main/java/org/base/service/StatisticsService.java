package org.base.service;

import org.base.dto.RecordData;
import org.base.dto.RecordErrors;
import org.base.dto.SummaryData;
import org.base.dto.URLErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.base.fetcher.DataFetcher.*;
import static org.base.util.StatisticsUtil.findPeronByMedianAgeRoundUp;
import static org.base.util.ValidationUtil.readURLNames;

@Service
public class StatisticsService {

    public ResponseEntity<SummaryData> processDataFromPathsOrURLs(MultipartFile multipartFile) {
        List<String> pathsOrURLs = readURLNames(multipartFile);

        if (pathsOrURLs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SummaryData());
        }

        SummaryData summaryStatisticsData = calculateStatisticsPathsOrUrls(pathsOrURLs);
        return ResponseEntity.status(HttpStatus.OK)
                .body(summaryStatisticsData);
    }

    public ResponseEntity<SummaryData> processDataFromCSVFiles(List<MultipartFile> multipartFileList) {

        if (multipartFileList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SummaryData());
        }
        SummaryData summaryStatisticsData = calculateStatisticsUploadFiles(multipartFileList);
        return ResponseEntity.status(HttpStatus.OK)
                .body(summaryStatisticsData);
    }

    /**
     * This method accepts n+1 CSV files, uploaded over HTTP
     */
    private SummaryData calculateStatisticsUploadFiles(List<MultipartFile> multipartFileList) {
        Set<URLErrors> urlErrors = new HashSet<>();
        Set<RecordErrors> lineErrors = new HashSet<>();

        long startTime = System.currentTimeMillis();

        // Use AtomicInteger to share thread count between methods
        AtomicInteger threadCount = new AtomicInteger(Thread.activeCount());

        // Fetch the record data concurrently
        List<RecordData> allData = readCSVRecordDataFiles(multipartFileList, urlErrors, lineErrors, threadCount);

        System.out.println("Number of records : " + allData.size());

        return calculateStatistics(urlErrors, lineErrors, startTime, threadCount, allData);
    }

    /**
     * This method fetches @RecordData from CSVs that are stored somewhere on the cloud over HTTP,
     * or CSV files that are stored locally on the machine
     */
    private SummaryData calculateStatisticsPathsOrUrls(List<String> pathsOrURLs) {
        Set<URLErrors> urlErrors = new HashSet<>();
        Set<RecordErrors> lineErrors = new HashSet<>();

        long startTime = System.currentTimeMillis();

        // Use AtomicInteger to share thread count between methods
        AtomicInteger threadCount = new AtomicInteger(Thread.activeCount());
        // Fetch the record data concurrently
        List<RecordData> allData = fetchRecordData(pathsOrURLs, urlErrors, lineErrors, threadCount);

        System.out.println("Number of records : " + allData.size());

        return calculateStatistics(urlErrors, lineErrors, startTime, threadCount, allData);
    }

    private static SummaryData calculateStatistics(Set<URLErrors> urlErrors, Set<RecordErrors> lineErrors, long startTime, AtomicInteger threadCount, List<RecordData> allData) {
        SummaryData summaryData = new SummaryData();

        int size = allData.size();
        if (size != 0) {
            // Calculate the average age
            double averageAge = allData.stream().collect(Collectors.averagingDouble(RecordData::getAge));
            BigDecimal averageAgeDecimal = BigDecimal.valueOf(averageAge).setScale(2, RoundingMode.HALF_UP); // Set decimal points to 2 and round up
            summaryData.setAverageAge(averageAgeDecimal.doubleValue());

            // Check if the list is even
            if (size % 2 == 0) {
                double middleAge1 = allData.get(size / 2 - 1).getAge();
                double middleAge2 = allData.get(size / 2).getAge();
                double medianEvenAge = (middleAge1 + middleAge2) / 2;
                summaryData.setMedianAge(medianEvenAge);

                // If the ages are the same, no need to iterate over the list to find a person with the median age
                if (middleAge1 == middleAge2) {
                    summaryData.setPersonWithMedianAge(allData.get(size / 2).getFullName());
                } else {
                    // Get the person closest to the medianEvenAge by incrementing the medianEvenAge
                    int indexOfPersonWithMedianAge = findPeronByMedianAgeRoundUp(allData, medianEvenAge);
                    if (indexOfPersonWithMedianAge != -1) {
                        RecordData personWithMedianAgeRecord = allData.get(indexOfPersonWithMedianAge);
                        summaryData.setPersonWithMedianAge(personWithMedianAgeRecord.getFullName());
                    }
                }
            } else {
                //If size in odd, set the middle person name
                summaryData.setMedianAge(allData.get(size / 2).getAge());
                summaryData.setPersonWithMedianAge(allData.get(size / 2).getFullName());
            }
        }
        long endTime = System.currentTimeMillis();

        summaryData.setUrlErrors(urlErrors);
        summaryData.setLineErrors(lineErrors);
        summaryData.setTimeInMillis((endTime - startTime));
        // Retrieve the thread count from the AtomicInteger
        summaryData.setThreadsUsed(threadCount.get());

        return summaryData;
    }
}
