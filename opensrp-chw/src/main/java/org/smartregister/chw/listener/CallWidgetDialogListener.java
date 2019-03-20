package org.smartregister.chw.listener;

import android.util.Log;
import android.view.View;

import org.smartregister.chw.R;
import org.smartregister.chw.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.util.Utils;

public class CallWidgetDialogListener implements View.OnClickListener {

    static String TAG = CallWidgetDialogListener.class.getCanonicalName();

    FamilyCallDialogFragment callDialogFragment = null;

    public CallWidgetDialogListener(FamilyCallDialogFragment dialogFragment) {
        callDialogFragment = dialogFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                callDialogFragment.dismiss();
                break;
            case R.id.call_head_phone:
                try {
                    String phoneNumber = (String) v.getTag();
                    Utils.launchDialer(callDialogFragment.getActivity(), callDialogFragment, phoneNumber);
                    callDialogFragment.dismiss();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                break;
            case R.id.call_caregiver_phone:
                try {
                    String phoneNumber = (String) v.getTag();
                    Utils.launchDialer(callDialogFragment.getActivity(), callDialogFragment, phoneNumber);
                    callDialogFragment.dismiss();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            default:
                break;
        }
    }

}
