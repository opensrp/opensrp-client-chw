package org.smartregister.chw.hf.listener;

import android.view.View;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.activity.AncMemberProfileActivity;
import org.smartregister.chw.hf.activity.ChildProfileActivity;
import org.smartregister.chw.hf.activity.PncMemberProfileActivity;
import org.smartregister.chw.hf.activity.ReferralTaskViewActivity;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class ReferralsTaskViewClickListener implements View.OnClickListener {
    private ReferralTaskViewActivity referralTaskViewActivity;
    private String taskFocus;
    private CommonPersonObjectClient commonPersonObjectClient;
    private MemberObject memberObject;
    private String familyHeadName;
    private String familyHeadPhoneNumber;

    @Override
    public void onClick(@NotNull View view) {
        if (view.getId() == R.id.view_profile) {
            goToClientProfile();
        } else if (view.getId() == R.id.mark_ask_done) {
            getReferralTaskViewActivity().closeReferral();
        }
    }

    private void goToClientProfile() {
        switch (getTaskFocus()) {
            case CoreConstants.TASKS_FOCUS.SICK_CHILD:
                ChildProfileActivity.startMe(getReferralTaskViewActivity(), false,
                        new MemberObject(getCommonPersonObjectClient()), ChildProfileActivity.class);
                break;
            case CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS:
                AncMemberProfileActivity.startMe(getReferralTaskViewActivity(), getMemberObject().getBaseEntityId(), getCommonPersonObjectClient());
                break;
            case CoreConstants.TASKS_FOCUS.PNC_DANGER_SIGNS:
                PncMemberProfileActivity.startMe(getReferralTaskViewActivity(), getMemberObject().getBaseEntityId(), getCommonPersonObjectClient());
                break;
            default:
                break;
        }
    }

    @Contract(pure = true)
    private ReferralTaskViewActivity getReferralTaskViewActivity() {
        return referralTaskViewActivity;
    }

    public void setReferralTaskViewActivity(ReferralTaskViewActivity referralTaskViewActivity) {
        this.referralTaskViewActivity = referralTaskViewActivity;
    }

    @Contract(pure = true)
    private String getTaskFocus() {
        return taskFocus;
    }

    public void setTaskFocus(String iSFromReferral) {
        this.taskFocus = iSFromReferral;
    }

    @Contract(pure = true)
    private CommonPersonObjectClient getCommonPersonObjectClient() {
        return commonPersonObjectClient;
    }

    public void setCommonPersonObjectClient(CommonPersonObjectClient commonPersonObjectClient) {
        this.commonPersonObjectClient = commonPersonObjectClient;
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
}