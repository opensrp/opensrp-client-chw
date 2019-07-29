package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.presenter.BaseAncMemberProfilePresenter;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.interactor.PncMemberProfileInteractor;
import org.smartregister.chw.pnc.activity.BasePncMemberProfileActivity;

public class PncMemberProfileActivity extends BasePncMemberProfileActivity {

    public static void startMe(Activity activity, MemberObject memberObject, String familyHeadName, String familyHeadPhoneNumber) {
        Intent intent = new Intent(activity, PncMemberProfileActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_NAME, familyHeadName);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_PHONE, familyHeadPhoneNumber);
        activity.startActivity(intent);
    }

    //TODO
    @Override
    protected void setupViews() {
        super.setupViews();
        textViewAncVisitNot.setOnClickListener(null);
    }

    @Override
    protected void registerPresenter() {
        presenter = new BaseAncMemberProfilePresenter(this, new PncMemberProfileInteractor(this), MEMBER_OBJECT);
    }

    @Override
    public void openMedicalHistory() {
        PncMedicalHistoryActivity.startMe(this, MEMBER_OBJECT);
    }

    @Override
    public void openUpcomingService() {
        PncUpcomingServicesActivity.startMe(this, MEMBER_OBJECT);
    }
}
