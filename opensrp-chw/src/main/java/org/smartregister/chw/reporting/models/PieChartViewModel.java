package org.smartregister.chw.reporting.models;

public class PieChartViewModel {
    private IndicatorModel yesSlice;
    private IndicatorModel noSlice;

    public PieChartViewModel(IndicatorModel yesSlice, IndicatorModel noSlice) {
        this.yesSlice = yesSlice;
        this.noSlice = noSlice;
    }

    public IndicatorModel getYesSlice() {
        return yesSlice;
    }

    public IndicatorModel getNoSlice() {
        return noSlice;
    }
}
