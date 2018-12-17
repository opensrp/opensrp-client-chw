package org.smartgresiter.wcaro.listener;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import org.smartgresiter.wcaro.R;

public class CallWidgetDialogListener implements View.OnClickListener {

    DialogFragment myDialogFragment = null;
    public CallWidgetDialogListener(DialogFragment dialogFragment){
        myDialogFragment = dialogFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close:
                myDialogFragment.dismiss();
                break;
            case R.id.call_head_phone:
                Intent intent1 = new Intent(Intent.ACTION_CALL);
                intent1.setData(Uri.parse("tel:123456789"));
                myDialogFragment.getActivity().startActivity(intent1);
                break;
            case R.id.call_caregiver_phone:
                Intent intent2 = new Intent(Intent.ACTION_CALL);
                intent2.setData(Uri.parse("tel:123456789"));
                myDialogFragment.getActivity().startActivity(intent2);
                break;
            default:
                break;
        }
    }
}
