package com.opensrp.chw.core.listener;


import android.view.View;

import com.opensrp.chw.core.R;
import com.opensrp.chw.core.fragment.FamilyCallDialogFragment;

import timber.log.Timber;

import static com.opensrp.chw.core.utils.Utils.launchDialer;

public class CallWidgetDialogListener implements View.OnClickListener {

    private static String TAG = CallWidgetDialogListener.class.getCanonicalName();

    private FamilyCallDialogFragment callDialogFragment;

    public CallWidgetDialogListener(FamilyCallDialogFragment dialogFragment) {
        callDialogFragment = dialogFragment;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.close) {
            callDialogFragment.dismiss();
        } else if (i == R.id.call_head_phone) {
            try {
                String phoneNumber = (String) v.getTag();
                launchDialer(callDialogFragment.getActivity(), callDialogFragment, phoneNumber);
                callDialogFragment.dismiss();
            } catch (Exception e) {
                Timber.e(e);
            }
        } else if (i == R.id.call_caregiver_phone) {
            try {
                String phoneNumber = (String) v.getTag();
                launchDialer(callDialogFragment.getActivity(), callDialogFragment, phoneNumber);
                callDialogFragment.dismiss();
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

}
