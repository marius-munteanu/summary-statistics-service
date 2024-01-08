package org.base.service;

import org.base.dto.SummaryData;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticsServiceTest {

    private final StatisticsService statisticsService = new StatisticsService();

    @Test
    void should_return_ok_with_summary_data_without_line_url_errors_from_URL_list() {
        // Given
        URL urlPath = ClassLoader.getSystemResource("csvfiles/file1.csv");
        MultipartFile multipartFile = new MockMultipartFile("file1", urlPath.getPath().getBytes());

        // When
        ResponseEntity<SummaryData> responseEntity = statisticsService.processDataFromPathsOrURLs(multipartFile);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(Objects.requireNonNull(responseEntity.getBody()).getPersonWithMedianAge());
        assertTrue(0 != responseEntity.getBody().getAverageAge());
        assertTrue(0 != responseEntity.getBody().getMedianAge());
        assertTrue(0 != responseEntity.getBody().getTimeInMillis());
        assertTrue(responseEntity.getBody().getLineErrors().isEmpty());
        assertTrue(responseEntity.getBody().getUrlErrors().isEmpty());
    }

    @Test
    void should_return_ok_with_summary_data_with_line_url_errors_from_URL_list() {
        // Given
        MultipartFile multipartFile = new MockMultipartFile("files", getAllPaths().getBytes());

        // When
        ResponseEntity<SummaryData> responseEntity = statisticsService.processDataFromPathsOrURLs(multipartFile);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertFalse(responseEntity.getBody().getLineErrors().isEmpty());
        assertFalse(responseEntity.getBody().getUrlErrors().isEmpty());
    }

    @Test
    void should_calculate_median_age_of_even_dataset_and_return_closest_to_median_age_person_from_URL_list() {
        // Given

        // file10_even contains 6 entries, calculating the median age will give us 73.5, there is no person in the dataset with this
        // age. In our case, we will always return the bigger aged person because we first increment the median age
        // for searching the dataset. This can be changed according to the business rules decided
        URL urlPath = ClassLoader.getSystemResource("csvfiles/file10_even.csv");
        MultipartFile multipartFile = new MockMultipartFile("file10_even", urlPath.getPath().getBytes());

        // When
        ResponseEntity<SummaryData> responseEntity = statisticsService.processDataFromPathsOrURLs(multipartFile);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Cory FINLEY", responseEntity.getBody().getPersonWithMedianAge());
        assertTrue(0 != responseEntity.getBody().getAverageAge());
        assertTrue(0 != responseEntity.getBody().getMedianAge());
        assertTrue(0 != responseEntity.getBody().getTimeInMillis());
        assertTrue(responseEntity.getBody().getLineErrors().isEmpty());
        assertTrue(responseEntity.getBody().getUrlErrors().isEmpty());
    }

    @Test
    void should_return_badRequest_if_multipart_file_has_no_urls() {
        // Given
        byte[] fileContent = "".getBytes();
        MultipartFile mockMultipartFile = new MockMultipartFile("test.csv", fileContent);

        // When
        ResponseEntity<SummaryData> responseEntity = statisticsService.processDataFromPathsOrURLs(mockMultipartFile);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void should_return_ok_with_summary_data_without_line_url_errors_from_flat_csv_file() throws IOException {
        // Given
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("csvfiles/file1.csv");
        List<MultipartFile> multipartFileList = Collections.singletonList(new MockMultipartFile("file1", "file1.csv", "text/csv", inputStream));

        // When
        ResponseEntity<SummaryData> responseEntity = statisticsService.processDataFromCSVFiles(multipartFileList);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(Objects.requireNonNull(responseEntity.getBody()).getPersonWithMedianAge());
        assertTrue(0 != responseEntity.getBody().getAverageAge());
        assertTrue(0 != responseEntity.getBody().getMedianAge());
        assertTrue(0 != responseEntity.getBody().getTimeInMillis());
        assertTrue(responseEntity.getBody().getLineErrors().isEmpty());
        assertTrue(responseEntity.getBody().getUrlErrors().isEmpty());
    }

    @Test
    void should_return_ok_with_summary_data_with_line_url_errors_from_flat_csv_files() throws IOException {
        // Given
        List<MultipartFile> multipartFiles = getAllCSVFiles();

        // When
        ResponseEntity<SummaryData> responseEntity = statisticsService.processDataFromCSVFiles(multipartFiles);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertFalse(responseEntity.getBody().getLineErrors().isEmpty());
        assertFalse(responseEntity.getBody().getUrlErrors().isEmpty());
    }

    @Test
    void should_calculate_median_age_of_even_dataset_and_return_closest_to_median_age_person_from_flat_csv_file() throws IOException {
        // Given

        // file10_even contains 6 entries, calculating the median age will give us 73.5, there is no person in the dataset with this
        // age. In our case, we will always return the bigger aged person because we first increment the median age
        // for searching the dataset. This can be changed according to the business rules decided
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("csvfiles/file10_even.csv");
        List<MultipartFile> multipartFileList = Collections.singletonList(new MockMultipartFile("file10_even", "file10_even.csv", "text/csv", inputStream));

        // When
        ResponseEntity<SummaryData> responseEntity = statisticsService.processDataFromCSVFiles(multipartFileList);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Cory FINLEY", responseEntity.getBody().getPersonWithMedianAge());
        assertTrue(0 != responseEntity.getBody().getAverageAge());
        assertTrue(0 != responseEntity.getBody().getMedianAge());
        assertTrue(0 != responseEntity.getBody().getTimeInMillis());
        assertTrue(responseEntity.getBody().getLineErrors().isEmpty());
        assertTrue(responseEntity.getBody().getUrlErrors().isEmpty());
    }

    @Test
    void should_return_badRequest_if_multipart_file_has_no_csvs() {
        // When
        ResponseEntity<SummaryData> responseEntity = statisticsService.processDataFromCSVFiles(Collections.emptyList());

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    private List<MultipartFile> getAllCSVFiles() throws IOException {
        List<MultipartFile> multipartFileList = new ArrayList<>();

        InputStream inputStream1 = ClassLoader.getSystemResourceAsStream("csvfiles/file1.csv");
        MockMultipartFile mockMultipartFile1 = new MockMultipartFile("file1", "file1.csv", "text/csv", inputStream1);
        multipartFileList.add(mockMultipartFile1);

        InputStream inputStream2 = ClassLoader.getSystemResourceAsStream("csvfiles/file2.csv");
        MockMultipartFile mockMultipartFile2 = new MockMultipartFile("file2", "file2.csv", "text/csv", inputStream2);
        multipartFileList.add(mockMultipartFile2);

        InputStream inputStream3 = ClassLoader.getSystemResourceAsStream("csvfiles/file3.csv");
        MockMultipartFile mockMultipartFile3 = new MockMultipartFile("file3", "file3.csv", "text/csv", inputStream3);
        multipartFileList.add(mockMultipartFile3);

        InputStream inputStream4 = ClassLoader.getSystemResourceAsStream("csvfiles/file4.csv");
        MockMultipartFile mockMultipartFile4 = new MockMultipartFile("file4", "file4.csv", "text/csv", inputStream4);
        multipartFileList.add(mockMultipartFile4);

        InputStream inputStream5 = ClassLoader.getSystemResourceAsStream("csvfiles/file5.csv");
        MockMultipartFile mockMultipartFile5 = new MockMultipartFile("file5", "file5.csv", "text/csv", inputStream5);
        multipartFileList.add(mockMultipartFile5);

        InputStream inputStream6 = ClassLoader.getSystemResourceAsStream("csvfiles/file6_bad.csv");
        MockMultipartFile mockMultipartFile6 = new MockMultipartFile("file6", "file6_bad.csv", "text/csv", inputStream6);
        multipartFileList.add(mockMultipartFile6);

        InputStream inputStream7 = ClassLoader.getSystemResourceAsStream("csvfiles/file9_bad.csv");
        MockMultipartFile mockMultipartFile7 = new MockMultipartFile("file7", "file9_bad.csv", "text/csv", inputStream7);
        multipartFileList.add(mockMultipartFile7);

        return multipartFileList;
    }

    private String getAllPaths() {
        URL urlPath1 = ClassLoader.getSystemResource("csvfiles/file1.csv");
        URL urlPath2 = ClassLoader.getSystemResource("csvfiles/file2.csv");
        URL urlPath3 = ClassLoader.getSystemResource("csvfiles/file3.csv");
        URL urlPath4 = ClassLoader.getSystemResource("csvfiles/file4.csv");
        URL urlPath5 = ClassLoader.getSystemResource("csvfiles/file5.csv");
        URL urlPath6 = ClassLoader.getSystemResource("csvfiles/file6_bad.csv");
        URL urlPath7 = ClassLoader.getSystemResource("csvfiles/file9_bad.csv");

        return urlPath1.getPath() +
                "\n" +
                urlPath2.getPath() +
                "\n" +
                urlPath3.getPath() +
                "\n" +
                urlPath4.getPath() +
                "\n" +
                urlPath5.getPath() +
                "\n" +
                urlPath6.getPath() +
                "\n" +
                urlPath7.getPath();
    }
}
