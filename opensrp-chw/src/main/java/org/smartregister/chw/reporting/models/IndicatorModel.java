package org.smartregister.chw.reporting.models;

import org.smartregister.chw.reporting.views.IndicatorView;

public class IndicatorModel {

    private String indicatorCode;
    private int labelStringResource;
    private IndicatorView.CountType countType;
    private long totalCount;


    public IndicatorModel(IndicatorView.CountType countType, String indicatorCode, int labelStringResource, long count) {
        this.countType = countType;
        this.indicatorCode = indicatorCode;
        this.labelStringResource = labelStringResource;
        this.totalCount = count;
    }

    public IndicatorView.CountType getCountType() {
        return countType;
    }

    public String getIndicatorCode() {
        return indicatorCode;
    }

    public int getLabelStringResource() {
        return labelStringResource;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
