package org.smartregister.chw.activity;

import android.view.View;

import org.smartregister.chw.R;

public class AboveFiveChildProfileActivity extends ChildProfileActivity {

    @Override
    protected void onCreation() {
        super.onCreation();
        recordVisitPanel.setVisibility(View.GONE);
    }

    @Override
    protected void updateTopbar() {
        //no need to do anything
    }

    @Override
    public void setParentName(String parentName) {
        textViewParentName.setVisibility(View.GONE);
    }

    @Override
    public void setLastVisitRowView(String days) {
        super.setLastVisitRowView(days);
        textViewLastVisit.setVisibility(View.GONE);
        textViewMedicalHistory.setTextColor(getResources().getColor(R.color.black));
    }

    @Override
    public void setProfileImage(String baseEntityId) {
        imageViewProfile.setBorderWidth(2);
        imageRenderHelper.refreshProfileImage(baseEntityId, imageViewProfile, R.mipmap.ic_member);
        imageViewProfile.setBorderColor(getResources().getColor(R.color.white));
    }

    @Override
    public void setServiceNameDue(String serviceName, String dueDate) {
        //no need to do anything
    }

    @Override
    public void setServiceNameUpcoming(String serviceName, String dueDate) {
        //no need to do anything
    }

    @Override
    public void setServiceNameOverDue(String serviceName, String dueDate) {
        //no need to do anything
    }
}
