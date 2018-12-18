package org.smartgresiter.wcaro.listener;

import android.app.DialogFragment;
import android.util.Log;
import android.view.View;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.util.Utils;

public class CallWidgetDialogListener implements View.OnClickListener {

    static String TAG = CallWidgetDialogListener.class.getCanonicalName();

    DialogFragment myDialogFragment = null;

    public CallWidgetDialogListener(DialogFragment dialogFragment) {
        myDialogFragment = dialogFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                break;
            case R.id.call_head_phone:
                try {
                    String phoneNumber = (String) v.getTag();
                    Utils.launchDialer(myDialogFragment.getActivity(), phoneNumber);
                } catch (Exception e) {
                    Log.e(TAG,e.toString());
                }
                break;
            case R.id.call_caregiver_phone:
                try {
                    String phoneNumber = (String) v.getTag();
                    Utils.launchDialer(myDialogFragment.getActivity(), phoneNumber);
                } catch (Exception e) {
                    Log.e(TAG,e.toString());
                }
            default:
                break;
        }
        myDialogFragment.dismiss();
    }

}
