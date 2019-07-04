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

    private Context context;
    private PieChartFactory pieChartFactory;
    private PieChartViewModel pieChartViewModel;

    public PieChartIndicatorView(Context context, PieChartViewModel pieChartViewModel) {
        pieChartFactory = new PieChartFactory();
        this.pieChartViewModel = pieChartViewModel;
        this.context = context;
    }

    @Override
    public View createView() {
        PieChartIndicatorVisualization pieChartIndicatorVisualization = getPieChartVisualization();

        if (pieChartViewModel.getIndicatorLabel() != null) {
            pieChartIndicatorVisualization.setIndicatorLabel(pieChartViewModel.getIndicatorLabel());
        }

        if (pieChartViewModel.getIndicatorNote() != null) {
            pieChartIndicatorVisualization.setIndicatorNote(pieChartViewModel.getIndicatorNote());
        }

        return getIndicatorView(pieChartIndicatorVisualization, pieChartFactory, context);
    }

    private PieChartIndicatorVisualization getPieChartVisualization() {
        // Define pie chart chartSlices
        List<PieChartSlice> chartSlices = new ArrayList<>();
        int yesCount = (int) pieChartViewModel.getYesSlice().getTotalCount();
        int noCount = (int) pieChartViewModel.getNoSlice().getTotalCount();
        chartSlices.add(new PieChartSlice(yesCount, DashboardUtil.YES_GREEN_SLICE_COLOR));
        chartSlices.add(new PieChartSlice(noCount, DashboardUtil.NO_RED_SLICE_COLOR));
        // Build the chart
        return new PieChartIndicatorVisualization.PieChartIndicatorVisualizationBuilder()
                .indicatorLabel(context.getResources().getString(pieChartViewModel.getYesSlice().getLabelStringResource()))
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
