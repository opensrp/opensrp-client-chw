package org.smartregister.chw.reporting.views;

import android.view.View;

public interface IndicatorView {
    View createView();

    enum CountType {
        STATIC_COUNT,
        LATEST_COUNT
    }
}
