package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.presenter.BaseAncUpcomingServicesPresenter;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.core.activity.CoreHivUpcomingServicesActivity;
import org.smartregister.chw.interactor.CbhsUpcomingServicesInteractor;

public class CbhsUpcomingServiceActivity extends CoreHivUpcomingServicesActivity {
    public static void startMe(Activity activity, MemberObject memberObject) {
        Intent intent = new Intent(activity, CbhsUpcomingServiceActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    @Override
    public void initializePresenter() {
        presenter = new BaseAncUpcomingServicesPresenter(memberObject, new CbhsUpcomingServicesInteractor(), this);
    }
}
