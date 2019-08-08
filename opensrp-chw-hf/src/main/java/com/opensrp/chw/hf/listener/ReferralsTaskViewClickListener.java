package com.opensrp.chw.hf.listener;

import android.view.View;

import com.opensrp.chw.hf.activity.ReferralTaskViewActivity;
import com.opensrp.hf.R;

public class ReferralsTaskViewClickListener implements View.OnClickListener {
    private ReferralTaskViewActivity referralTaskViewActivity;

    public ReferralTaskViewActivity getReferralTaskViewActivity() {
        return referralTaskViewActivity;
    }

    public void setReferralTaskViewActivity(ReferralTaskViewActivity referralTaskViewActivity) {
        this.referralTaskViewActivity = referralTaskViewActivity;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.view_profile) {
            getReferralTaskViewActivity().finish();
        } else if (view.getId() == R.id.mark_ask_done) {
            getReferralTaskViewActivity().closeTask();
        }
    }
}
