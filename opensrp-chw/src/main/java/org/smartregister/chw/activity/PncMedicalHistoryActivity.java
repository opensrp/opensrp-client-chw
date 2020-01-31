package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import org.smartregister.chw.anc.domain.GroupedVisit;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.core.activity.CorePncMedicalHistoryActivity;
import org.smartregister.chw.interactor.PncMedicalHistoryActivityInteractor;
import org.smartregister.chw.pnc.contract.BasePncMedicalHistoryContract;

import java.util.List;

public class PncMedicalHistoryActivity extends CorePncMedicalHistoryActivity {

    private Flavor flavor = new PncMedicalHistoryActivityFlv();

    public static void startMe(Activity activity, MemberObject memberObject) {
        Intent intent = new Intent(activity, PncMedicalHistoryActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    @Override
    protected BasePncMedicalHistoryContract.Interactor getPncMedicalHistoryInteractor() {
        return new PncMedicalHistoryActivityInteractor();
    }

    @Override
    public View renderMedicalHistoryView(List<GroupedVisit> groupedVisits) {
        View view = flavor.bindViews(this);
        displayLoadingState(true);
        flavor.processViewData(groupedVisits, this, memberObject);
        displayLoadingState(false);
        return view;
    }

}
