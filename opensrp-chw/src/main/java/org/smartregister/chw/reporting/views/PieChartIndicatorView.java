package org.smartregister.chw.reporting.views;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import org.smartregister.chw.reporting.models.PieChartViewModel;
import org.smartregister.chw.util.DashboardUtil;
import org.smartregister.reporting.domain.PieChartIndicatorVisualization;
import org.smartregister.reporting.domain.PieChartSlice;
import org.smartregister.reporting.listener.PieChartSelectListener;
import org.smartregister.reporting.view.PieChartFactory;

import java.util.ArrayList;
import java.util.List;

import static org.smartregister.chw.reporting.ReportingUtil.getIndicatorView;

public class PieChartIndicatorView implements IndicatorView {

    private PieChartFactory pieChartFactory;
    private PieChartViewModel pieChartYes;
    private PieChartViewModel pieChartNo;
    private Context context;

    public PieChartIndicatorView(PieChartViewModel pieChartYes, PieChartViewModel pieChartNo, Context context) {
        pieChartFactory = new PieChartFactory();
        this.pieChartYes = pieChartYes;
        this.pieChartNo = pieChartNo;
        this.context = context;
    }

    @Override
    public View createView() {
        PieChartIndicatorVisualization pieChartIndicatorVisualization = getPieChartVisualization();
        return getIndicatorView(pieChartIndicatorVisualization, pieChartFactory, context);
    }

    private PieChartIndicatorVisualization getPieChartVisualization() {
        // Define pie chart chartSlices
        List<PieChartSlice> chartSlices = new ArrayList<>();
        int yesCount = 0;
        int noCount = 0;
        if (pieChartYes.getIndicatorTallyMap().get(pieChartYes.getYesIndicatorKey()) != null) {
            yesCount = pieChartYes.getIndicatorTallyMap().get(pieChartYes.getYesIndicatorKey()).getCount();
        }
        if (pieChartNo.getIndicatorTallyMap().get(pieChartYes.getNoIndicatorKey()) != null) {
            noCount = pieChartNo.getIndicatorTallyMap().get(pieChartYes.getNoIndicatorKey()).getCount();
        }

        PieChartSlice yesSlice = new PieChartSlice(yesCount, DashboardUtil.YES_GREEN_SLICE_COLOR);
        PieChartSlice noSlice = new PieChartSlice(noCount, DashboardUtil.NO_RED_SLICE_COLOR);
        chartSlices.add(yesSlice);
        chartSlices.add(noSlice);

        // Build the chart
        return new PieChartIndicatorVisualization.PieChartIndicatorVisualizationBuilder()
                .indicatorLabel(context.getResources().getString(pieChartYes.getLabelStringResource()))
                .chartHasLabels(true)
                .chartHasLabelsOutside(true)
                .chartHasCenterCircle(false)
                .chartSlices(chartSlices)
                .chartListener(new ChartListener()).build();
    }

    public class ChartListener implements PieChartSelectListener {
        @Override
        public void handleOnSelectEvent(PieChartSlice sliceValue) {
            Toast.makeText(context, DashboardUtil.getPieSelectionValue(sliceValue, context),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
