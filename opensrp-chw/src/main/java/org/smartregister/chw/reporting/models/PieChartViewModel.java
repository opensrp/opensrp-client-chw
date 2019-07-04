package org.smartregister.chw.reporting.models;

import org.smartregister.reporting.domain.IndicatorTally;

import java.util.Map;

public class PieChartViewModel {

    private int labelStringResource;
    private String yesIndicatorKey;
    private String noIndicatorKey;
    private String indicatorCode;
    private Map<String, IndicatorTally> indicatorTallyMap;

    public PieChartViewModel(String yesIndicatorKey, String noIndicatorKey, String indicatorCode, int labelStringResource) {
        this.yesIndicatorKey = yesIndicatorKey;
        this.noIndicatorKey = noIndicatorKey;
        this.indicatorCode = indicatorCode;
        this.labelStringResource = labelStringResource;
    }

    public String getYesIndicatorKey() {
        return yesIndicatorKey;
    }

    public String getNoIndicatorKey() {
        return noIndicatorKey;
    }

    public String getIndicatorCode() {
        return indicatorCode;
    }

    public int getLabelStringResource() {
        return labelStringResource;
    }

    public Map<String, IndicatorTally> getIndicatorTallyMap() {
        return indicatorTallyMap;
    }

    public void setIndicatorTallyMap(Map<String, IndicatorTally> indicatorTallyMap) {
        this.indicatorTallyMap = indicatorTallyMap;
    }
}
