package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.presenter.BaseAncMedicalHistoryPresenter;
import org.smartregister.chw.core.activity.CoreAncMedicalHistoryActivity;
import org.smartregister.chw.hiv.domain.HivMemberObject;
import org.smartregister.chw.interactor.CbhsMedicalHistoryInteractor;

import java.util.List;

public class CbhsMedicalHistoryActivity extends CoreAncMedicalHistoryActivity {
    private static HivMemberObject cbhsMemberObject;
    private final Flavor flavor = new CbhsMedicalHistoryActivityFlv();
    private ProgressBar progressBar;

    public static void startMe(Activity activity, HivMemberObject memberObject) {
        Intent intent = new Intent(activity, CbhsMedicalHistoryActivity.class);
        cbhsMemberObject = memberObject;
        activity.startActivity(intent);
    }

    @Override
    public void initializePresenter() {
        presenter = new BaseAncMedicalHistoryPresenter(new CbhsMedicalHistoryInteractor(), this, cbhsMemberObject.getBaseEntityId());
    }

    @Override
    public void setUpView() {
        linearLayout = findViewById(org.smartregister.chw.opensrp_chw_anc.R.id.linearLayoutMedicalHistory);
        progressBar = findViewById(org.smartregister.chw.opensrp_chw_anc.R.id.progressBarMedicalHistory);

        TextView tvTitle = findViewById(org.smartregister.chw.opensrp_chw_anc.R.id.tvTitle);
        tvTitle.setText(getString(org.smartregister.chw.opensrp_chw_anc.R.string.back_to, cbhsMemberObject.getFirstName() + " " + cbhsMemberObject.getMiddleName() + " " + cbhsMemberObject.getLastName()));

        ((TextView) findViewById(R.id.medical_history)).setText(getString(R.string.visits_history));
    }

    @Override
    public View renderView(List<Visit> visits) {
        super.renderView(visits);
        View view = flavor.bindViews(this);
        displayLoadingState(true);
        flavor.processViewData(visits, this);
        displayLoadingState(false);
        TextView heiVisitTitle = view.findViewById(org.smartregister.chw.core.R.id.customFontTextViewHealthFacilityVisitTitle);
        heiVisitTitle.setText(R.string.cbhs_visit);
        return view;
    }

    @Override
    public void displayLoadingState(boolean state) {
        progressBar.setVisibility(state ? View.VISIBLE : View.GONE);
    }
}
