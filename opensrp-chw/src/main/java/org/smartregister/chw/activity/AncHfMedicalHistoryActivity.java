package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.presenter.BaseAncMedicalHistoryPresenter;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.core.activity.CoreAncMedicalHistoryActivity;
import org.smartregister.chw.interactor.AncHfMedicalHistoryInteractor;

import java.util.List;

public class AncHfMedicalHistoryActivity extends CoreAncMedicalHistoryActivity {
    private Flavor flavor = new AncHfMedicalHistoryActivityFlv();

    public static void startMe(Activity activity, MemberObject memberObject) {
        Intent intent = new Intent(activity, AncHfMedicalHistoryActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    @Override
    public void initializePresenter() {
        presenter = new BaseAncMedicalHistoryPresenter(new AncHfMedicalHistoryInteractor(), this, memberObject.getBaseEntityId());
    }
    @Override
    public View renderView(List<Visit> visits) {
        super.renderView(visits);
        View view = flavor.bindViews(this);
        displayLoadingState(true);
        flavor.processViewData(visits, this);
        displayLoadingState(false);
        return view;
    }
}
