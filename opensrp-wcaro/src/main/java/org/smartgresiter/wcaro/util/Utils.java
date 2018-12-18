package org.smartgresiter.wcaro.util;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.smartgresiter.wcaro.R;
import org.smartregister.util.PermissionUtils;

public class Utils extends org.smartregister.family.util.Utils {

    static String TAG = Utils.class.getCanonicalName();

    public static boolean launchDialer(Activity activity, String phoneNumber) {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE);

            // request permission

            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.CALL_PHONE},
                    PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE);
            return false;
        } else {

            if (((TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number()
                    == null) {

                Log.i(TAG, "No dial application so we launch copy to clipboard...");

                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(activity.getText(R.string.copied_phone_number), phoneNumber);
                clipboard.setPrimaryClip(clip);

                // no phone
                Toast.makeText(activity, activity.getText(R.string.copied_phone_number), Toast.LENGTH_SHORT).show();

            } else {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                activity.startActivity(intent);
            }
            return true;
        }
    }

}
