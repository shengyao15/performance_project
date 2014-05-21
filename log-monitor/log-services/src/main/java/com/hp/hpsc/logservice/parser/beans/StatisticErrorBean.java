package com.hp.hpsc.logservice.parser.beans;

import java.util.Map;

public class StatisticErrorBean {

    private String collectDate;
    private String featureName;
    // Key:error name, value: error amount
    private Map<String, Integer> errorDetails;

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public Map<String, Integer> getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(Map<String, Integer> errorDetails) {
        this.errorDetails = errorDetails;
    }

    public String getCollectDate() {
        return collectDate;
    }

    public void setCollectDate(String collectDate) {
        this.collectDate = collectDate;
    }
}
