package org.base.dto;

import java.util.Set;

public class SummaryData {
    private double medianAge;
    private double averageAge;
    private String personWithMedianAge;
    private long timeInMillis;
    private int threadsUsed;
    private Set<URLErrors> urlErrors;
    private Set<RecordErrors> lineErrors;

    public Set<URLErrors> getUrlErrors() {
        return urlErrors;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public int getThreadsUsed() {
        return threadsUsed;
    }

    public void setThreadsUsed(int threadsUsed) {
        this.threadsUsed = threadsUsed;
    }

    public void setUrlErrors(Set<URLErrors> urlErrors) {
        this.urlErrors = urlErrors;
    }

    public Set<RecordErrors> getLineErrors() {
        return lineErrors;
    }

    public void setLineErrors(Set<RecordErrors> lineErrors) {
        this.lineErrors = lineErrors;
    }

    public double getMedianAge() {
        return medianAge;
    }

    public void setMedianAge(double medianAge) {
        this.medianAge = medianAge;
    }

    public double getAverageAge() {
        return averageAge;
    }

    public void setAverageAge(double averageAge) {
        this.averageAge = averageAge;
    }

    public String getPersonWithMedianAge() {
        return personWithMedianAge;
    }

    public void setPersonWithMedianAge(String personWithMedianAge) {
        this.personWithMedianAge = personWithMedianAge;
    }
}
