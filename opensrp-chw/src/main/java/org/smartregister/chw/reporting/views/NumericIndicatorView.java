package org.smartregister.chw.reporting.views;

import android.content.Context;
import android.view.View;

import org.smartregister.chw.reporting.models.NumericViewModel;
import org.smartregister.reporting.domain.NumericIndicatorVisualization;
import org.smartregister.reporting.view.NumericDisplayFactory;

import static org.smartregister.chw.reporting.ReportingUtil.getIndicatorView;

public class NumericIndicatorView implements IndicatorView {

    private Context context;
    private NumericViewModel numericViewModel;
    private NumericDisplayFactory numericDisplayFactory;

    public NumericIndicatorView(Context context, NumericViewModel numericViewModel) {
        this.context = context;
        this.numericViewModel = numericViewModel;
        numericDisplayFactory = new NumericDisplayFactory();
    }

    @Override
    public View createView() {
        return getIndicatorView(getNumericVisualization(), numericDisplayFactory, context);
    }

    private NumericIndicatorVisualization getNumericVisualization() {
        return new NumericIndicatorVisualization(context.getResources().getString(
                numericViewModel.getLabelStringResource()), (int) numericViewModel.getTotalCount());
    }
}
