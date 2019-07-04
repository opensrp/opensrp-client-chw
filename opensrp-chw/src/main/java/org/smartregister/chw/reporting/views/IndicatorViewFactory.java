package org.smartregister.chw.reporting.views;

import android.view.View;

public class IndicatorViewFactory {
    public static View createView(IndicatorView indicatorView){
        return indicatorView.createView();
    }
}
