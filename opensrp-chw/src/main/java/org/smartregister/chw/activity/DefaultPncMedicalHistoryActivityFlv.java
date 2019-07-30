package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import org.smartregister.chw.anc.domain.Visit;

import java.util.List;

import timber.log.Timber;

public class DefaultPncMedicalHistoryActivityFlv implements PncMedicalHistoryActivity.Flavor {
    @Override
    public View bindViews(Activity activity) {
        return null;
    }

    @Override
    public void processViewData(List<Visit> visits, Context context) {
        Timber.v("processViewData");
    }
}
