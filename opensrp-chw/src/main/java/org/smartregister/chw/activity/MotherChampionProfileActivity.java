package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import org.smartregister.chw.R;
import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.activity.CorePmtctProfileActivity;
import org.smartregister.chw.core.interactor.CorePmtctProfileInteractor;
import org.smartregister.chw.core.presenter.CoreFamilyOtherMemberActivityPresenter;
import org.smartregister.chw.core.presenter.CorePmtctMemberProfilePresenter;
import org.smartregister.chw.dao.MotherChampionDao;
import org.smartregister.chw.pmtct.util.Constants;
import org.smartregister.domain.AlertStatus;

import androidx.annotation.NonNull;

public class MotherChampionProfileActivity extends CorePmtctProfileActivity {
    private static String baseEntityId;

    public static void startProfile(Activity activity, String baseEntityId) {
        MotherChampionProfileActivity.baseEntityId = baseEntityId;
        Intent intent = new Intent(activity, MotherChampionProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        activity.startActivity(intent);
    }

    @Override
    protected void initializePresenter() {
        showProgressBar(true);
        String baseEntityId = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID);
        memberObject = MotherChampionDao.getMember(baseEntityId);
        profilePresenter = new CorePmtctMemberProfilePresenter(this, new CorePmtctProfileInteractor(), memberObject);
        fetchProfileData();
        profilePresenter.refreshProfileBottom();
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.return_to_mother_champion_clients);

        textViewRecordPmtct.setText(R.string.record_followup);
    }

    @Override
    public void refreshFamilyStatus(AlertStatus status) {
        super.refreshFamilyStatus(status);
        rlFamilyServicesDue.setVisibility(View.GONE);
    }

    @Override
    protected Class<? extends CoreFamilyProfileActivity> getFamilyProfileActivityClass() {
        return null;
    }

    @Override
    protected void removeMember() {

    }

    @NonNull
    @Override
    public CoreFamilyOtherMemberActivityPresenter presenter() {
        return null;
    }

    @Override
    public void setProfileImage(String s, String s1) {

    }

    @Override
    public void setProfileDetailThree(String s) {

    }

    @Override
    public void toggleFamilyHead(boolean b) {

    }

    @Override
    public void togglePrimaryCaregiver(boolean b) {

    }

    @Override
    public void refreshList() {

    }

    @Override
    public void updateHasPhone(boolean b) {

    }

    @Override
    public void setFamilyServiceStatus(String s) {

    }

    @Override
    public void verifyHasPhone() {

    }

    @Override
    public void notifyHasPhone(boolean b) {

    }
}
