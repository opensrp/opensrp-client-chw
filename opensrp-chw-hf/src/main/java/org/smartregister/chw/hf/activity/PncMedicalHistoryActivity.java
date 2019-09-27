package org.smartregister.chw.hf.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import org.smartregister.chw.anc.contract.BaseAncMedicalHistoryContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.core.activity.CorePncMedicalHistoryActivity;
import org.smartregister.chw.core.helper.BaMedicalHistoryActivityHelper;
import org.smartregister.chw.hf.interactor.PncMedicalHistoryActivityInteractor;

import java.util.List;

public class PncMedicalHistoryActivity extends CorePncMedicalHistoryActivity {

    private HfMedicalHistoryFlavor flavor = new HfMedicalHistoryFlavor();

    public static void startMe(Activity activity, MemberObject memberObject) {
        Intent intent = new Intent(activity, PncMedicalHistoryActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    @Override
    public View renderView(List<Visit> visits) {
        View view = flavor.bindViews(this);
        displayLoadingState(true);
        flavor.processViewData(visits, this);
        displayLoadingState(false);
        return view;
    }

    @Override
    protected BaseAncMedicalHistoryContract.Interactor getPncMedicalHistoryInteractor() {
        return new PncMedicalHistoryActivityInteractor();
    }

    private class HfMedicalHistoryFlavor extends BaMedicalHistoryActivityHelper {
    }
}
