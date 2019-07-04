package org.smartregister.chw.reporting.models;

public class PieChartViewModel {
    private IndicatorModel yesSlice;
    private IndicatorModel noSlice;
    private String indicatorLabel;
    private String indicatorNote;

    public PieChartViewModel(IndicatorModel yesSlice, IndicatorModel noSlice, String indicatorLabel, String indicatorNote) {
        this.yesSlice = yesSlice;
        this.noSlice = noSlice;
        this.indicatorLabel = indicatorLabel;
        this.indicatorNote = indicatorNote;
    }

    public IndicatorModel getYesSlice() {
        return yesSlice;
    }

    public IndicatorModel getNoSlice() {
        return noSlice;
    }

    public String getIndicatorNote() {
        return indicatorNote;
    }

    public String getIndicatorLabel() {
        return indicatorLabel;
    }
}
