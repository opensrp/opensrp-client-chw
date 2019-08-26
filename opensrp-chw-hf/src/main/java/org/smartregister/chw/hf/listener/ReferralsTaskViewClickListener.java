package org.smartregister.chw.hf.listener;

import android.content.Intent;
import android.view.View;

import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.activity.ReferralRegisterActivity;
import org.smartregister.chw.hf.activity.ReferralTaskViewActivity;

public class ReferralsTaskViewClickListener implements View.OnClickListener {
    private ReferralTaskViewActivity referralTaskViewActivity;
    private boolean iSFromReferral;

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.view_profile) {
            if (isiSFromReferral()) {
                startReferralsRegisterActivity();
            } else {
                getReferralTaskViewActivity().finish();
            }
        } else if (view.getId() == R.id.mark_ask_done) {
            getReferralTaskViewActivity().closeReferral();
        }
    }

    public boolean isiSFromReferral() {
        return iSFromReferral;
    }

    public void setiSFromReferral(boolean iSFromReferral) {
        this.iSFromReferral = iSFromReferral;
    }

    public void startReferralsRegisterActivity() {
        Intent intent = new Intent(getReferralTaskViewActivity(), ReferralRegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getReferralTaskViewActivity().startActivity(intent);
        getReferralTaskViewActivity().overridePendingTransition(org.smartregister.chw.core.R.anim.slide_in_up, org.smartregister.chw.core.R.anim.slide_out_up);
        getReferralTaskViewActivity().finish();
    }

    public ReferralTaskViewActivity getReferralTaskViewActivity() {
        return referralTaskViewActivity;
    }

    public void setReferralTaskViewActivity(ReferralTaskViewActivity referralTaskViewActivity) {
        this.referralTaskViewActivity = referralTaskViewActivity;
    }
}
