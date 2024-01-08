package org.base.dto;

import java.util.List;
import java.util.Objects;

public class RecordErrors {
    private String url;
    private List<String> lineWithError;

    public RecordErrors() {
    }

    public RecordErrors(String url, List<String> lineWithError) {
        this.url = url;
        this.lineWithError = lineWithError;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getLineWithError() {
        return lineWithError;
    }

    public void setLineWithError(List<String> lineWithError) {
        this.lineWithError = lineWithError;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordErrors that = (RecordErrors) o;
        return Objects.equals(url, that.url) && Objects.equals(lineWithError, that.lineWithError);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, lineWithError);
    }
}
