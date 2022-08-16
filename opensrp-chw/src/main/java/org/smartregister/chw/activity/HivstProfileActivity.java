package org.smartregister.chw.activity;

import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.activity.CoreHivstProfileActivity;
import org.smartregister.chw.core.presenter.CoreFamilyOtherMemberActivityPresenter;

import androidx.annotation.NonNull;


public class HivstProfileActivity extends CoreHivstProfileActivity {
    @Override
    protected Class<? extends CoreFamilyProfileActivity> getFamilyProfileActivityClass() {
        return null;
    }

    @Override
    protected void removeMember() {
        //implement
    }

    @NonNull
    @Override
    public CoreFamilyOtherMemberActivityPresenter presenter() {
        return null;
    }

    @Override
    public void setProfileImage(String s, String s1) {
        //implement
    }

    @Override
    public void setProfileDetailThree(String s) {
        //implement
    }

    @Override
    public void toggleFamilyHead(boolean b) {
        //implement
    }

    @Override
    public void togglePrimaryCaregiver(boolean b) {
        //implement
    }

    @Override
    public void refreshList() {
        //implement
    }

    @Override
    public void updateHasPhone(boolean b) {
        //implement
    }

    @Override
    public void setFamilyServiceStatus(String s) {
        //implement
    }

    @Override
    public void verifyHasPhone() {
        //implement
    }

    @Override
    public void notifyHasPhone(boolean b) {
        //implement
    }
}
