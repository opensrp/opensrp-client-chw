package org.smartregister.chw.hf.listener;

import android.app.Activity;
import android.view.View;

import org.jetbrains.annotations.Contract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.hf.activity.AncMemberProfileActivity;
import org.smartregister.chw.hf.activity.ChildProfileActivity;
import org.smartregister.chw.hf.activity.PncMemberProfileActivity;
import org.smartregister.chw.hf.activity.ReferralTaskViewActivity;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Task;

/**
 * Created by wizard on 06/08/19.
 */
public class ReferralRecyclerClickListener implements View.OnClickListener {
    private Task task;
    private CommonPersonObjectClient commonPersonObjectClient;
    private Activity activity;
    private String startingActivity;
    private MemberObject memberObject;
    private String familyHeadName;
    private String familyHeadPhoneNumber;

    @Override
    public void onClick(View view) {
        if (getActivity() instanceof AncMemberProfileActivity || getActivity() instanceof PncMemberProfileActivity) {
            ReferralTaskViewActivity.startReferralTaskViewActivity(getActivity(), getMemberObject(), getFamilyHeadName(), getFamilyHeadPhoneNumber(), getCommonPersonObjectClient(), getTask(), getStartingActivity());
        } else if (getActivity() instanceof ChildProfileActivity) {
            ReferralTaskViewActivity.startReferralTaskViewActivity(getActivity(), getCommonPersonObjectClient(), getTask(), getStartingActivity());
        }
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Contract(pure = true)
    private MemberObject getMemberObject() {
        return memberObject;
    }

    public void setMemberObject(MemberObject memberObject) {
        this.memberObject = memberObject;
    }

    @Contract(pure = true)
    private String getFamilyHeadName() {
        return familyHeadName;
    }

    public void setFamilyHeadName(String familyHeadName) {
        this.familyHeadName = familyHeadName;
    }

    @Contract(pure = true)
    private String getFamilyHeadPhoneNumber() {
        return familyHeadPhoneNumber;
    }

    public void setFamilyHeadPhoneNumber(String familyHeadPhoneNumber) {
        this.familyHeadPhoneNumber = familyHeadPhoneNumber;
    }

    @Contract(pure = true)
    private CommonPersonObjectClient getCommonPersonObjectClient() {
        return commonPersonObjectClient;
    }

    public void setCommonPersonObjectClient(CommonPersonObjectClient commonPersonObjectClient) {
        this.commonPersonObjectClient = commonPersonObjectClient;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Contract(pure = true)
    private String getStartingActivity() {
        return startingActivity;
    }

    public void setStartingActivity(String startingActivity) {
        this.startingActivity = startingActivity;
    }
}
