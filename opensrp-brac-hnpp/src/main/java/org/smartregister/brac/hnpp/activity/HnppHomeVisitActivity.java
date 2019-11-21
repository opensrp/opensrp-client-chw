package org.smartregister.brac.hnpp.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.brac.hnpp.interactor.HnppAncHomeVisitInteractor;
import org.smartregister.chw.anc.activity.BaseAncHomeVisitActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.presenter.BaseAncHomeVisitPresenter;
import org.smartregister.chw.anc.util.Constants;

import static org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT;

public class HnppHomeVisitActivity extends BaseAncHomeVisitActivity {

    public static void startMe(Activity activity, MemberObject memberObject, Boolean isEditMode) {
        Intent intent = new Intent(activity, HnppHomeVisitActivity.class);
        intent.putExtra(MEMBER_PROFILE_OBJECT, memberObject);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.EDIT_MODE, isEditMode);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_HOME_VISIT);
    }

    @Override
    protected void registerPresenter() {
        presenter = new BaseAncHomeVisitPresenter(memberObject, this, new HnppAncHomeVisitInteractor());
    }

}