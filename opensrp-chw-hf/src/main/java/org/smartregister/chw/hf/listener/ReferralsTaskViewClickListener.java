package org.smartregister.chw.hf.listener;

import android.view.View;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.activity.ChildProfileActivity;
import org.smartregister.chw.hf.activity.ReferralTaskViewActivity;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class ReferralsTaskViewClickListener implements View.OnClickListener {
    private ReferralTaskViewActivity referralTaskViewActivity;
    private String taskFocus;
    private CommonPersonObjectClient commonPersonObjectClient;

    @Override
    public void onClick(@NotNull View view) {
        if (view.getId() == R.id.view_profile) {
            goToClientProfile();
        } else if (view.getId() == R.id.mark_ask_done) {
            getReferralTaskViewActivity().closeReferral();
        }
    }

    private void goToClientProfile() {
        if (getTaskFocus().equals(CoreConstants.TASKS_FOCUS.SICK_CHILD)) {
            ChildProfileActivity.startMe(getReferralTaskViewActivity(), false, new MemberObject(getCommonPersonObjectClient()), ChildProfileActivity.class);
        }
    }

    @Contract(pure = true)
    private ReferralTaskViewActivity getReferralTaskViewActivity() {
        return referralTaskViewActivity;
    }

    @Contract(pure = true)
    private String getTaskFocus() {
        return taskFocus;
    }

    @Contract(pure = true)
    private CommonPersonObjectClient getCommonPersonObjectClient() {
        return commonPersonObjectClient;
    }

    public void setCommonPersonObjectClient(CommonPersonObjectClient commonPersonObjectClient) {
        this.commonPersonObjectClient = commonPersonObjectClient;
    }

    public void setTaskFocus(String iSFromReferral) {
        this.taskFocus = iSFromReferral;
    }

    public void setReferralTaskViewActivity(ReferralTaskViewActivity referralTaskViewActivity) {
        this.referralTaskViewActivity = referralTaskViewActivity;
    }
}
