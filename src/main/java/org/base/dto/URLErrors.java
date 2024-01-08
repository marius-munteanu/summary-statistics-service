package org.base.dto;

import java.util.Objects;

public class URLErrors {
    private String url;
    private String errorName;

    public URLErrors(String url, String errorName) {
        this.url = url;
        this.errorName = errorName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        URLErrors urlErrors = (URLErrors) o;
        return Objects.equals(url, urlErrors.url) && Objects.equals(errorName, urlErrors.errorName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, errorName);
    }
}
