package org.base.rest;

import org.base.dto.SummaryData;
import org.base.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/rest")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping(path = "/summary", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity<SummaryData> computeStatistics(
            @RequestPart MultipartFile multipartFile) {

        return statisticsService.processDataFromPathsOrURLs(multipartFile);
    }

    @PostMapping(path = "/summary/csv")
    @ResponseStatus(OK)
    public ResponseEntity<SummaryData> computeStatisticsCSV(
            @RequestPart("csvfiles") List<MultipartFile> CSVs) {

        return statisticsService.processDataFromCSVFiles(CSVs);
    }
}
