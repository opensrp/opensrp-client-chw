package org.smartregister.chw.core.listener;


import android.view.View;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.core.utils.Utils;

import timber.log.Timber;

public class CallWidgetDialogListener implements View.OnClickListener {

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
                Utils.launchDialer(callDialogFragment.getActivity(), callDialogFragment, phoneNumber);
                callDialogFragment.dismiss();
            } catch (Exception e) {
                Timber.e(e);
            }
        } else if (i == R.id.call_caregiver_phone) {
            try {
                String phoneNumber = (String) v.getTag();
                Utils.launchDialer(callDialogFragment.getActivity(), callDialogFragment, phoneNumber);
                callDialogFragment.dismiss();
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

}
