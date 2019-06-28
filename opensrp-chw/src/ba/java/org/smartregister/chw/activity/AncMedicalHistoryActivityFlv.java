package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import org.smartregister.chw.anc.domain.Visit;

import java.util.List;

public class AncMedicalHistoryActivityFlv implements AncMedicalHistoryActivity.Flavor {
    @Override
    public View bindViews(Activity activity) {
        LayoutInflater inflater = activity.getLayoutInflater();
        return inflater.inflate(org.smartregister.chw.opensrp_chw_anc.R.layout.medical_history_details, null);
    }

    @Override
    public void processViewData(List<Visit> visits, Context context) {
//TODO
    }
}
