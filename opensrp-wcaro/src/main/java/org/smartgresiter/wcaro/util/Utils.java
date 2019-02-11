package org.smartgresiter.wcaro.util;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.contract.FamilyCallDialogContract;
import org.smartgresiter.wcaro.fragment.CopyToClipboardDialog;
import org.smartregister.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

public class Utils extends org.smartregister.family.util.Utils {

    private static String TAG = Utils.class.getCanonicalName();


    public static final ArrayList<String> ALLOWED_LEVELS;
    public static final String CHA = "CHA";
    public static final String CHSS = "CHSS";
    public static final String CLINIC = "Clinic";

    static {
        ALLOWED_LEVELS = new ArrayList<>();
        ALLOWED_LEVELS.add(CLINIC);
        ALLOWED_LEVELS.add(CHSS);
        ALLOWED_LEVELS.add(CHA);
    }

    public static boolean launchDialer(final Activity activity, final FamilyCallDialogContract.View callView, final String phoneNumber) {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            // set a pending call execution request
            if (callView != null) {
                callView.setPendingCallRequest(new FamilyCallDialogContract.Dialer() {
                    @Override
                    public void callMe() {
                        Utils.launchDialer(activity, callView, phoneNumber);
                    }
                });
            }

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE);

            return false;
        } else {

            if (((TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number()
                    == null) {

                Log.i(TAG, "No dial application so we launch copy to clipboard...");

                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(activity.getText(R.string.copied_phone_number), phoneNumber);
                clipboard.setPrimaryClip(clip);

                CopyToClipboardDialog copyToClipboardDialog = new CopyToClipboardDialog(activity, R.style.copy_clipboard_dialog);
                copyToClipboardDialog.setContent(phoneNumber);
                copyToClipboardDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                copyToClipboardDialog.show();
                // no phone
                Toast.makeText(activity, activity.getText(R.string.copied_phone_number), Toast.LENGTH_SHORT).show();

            } else {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                activity.startActivity(intent);
            }
            return true;
        }
    }

    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static boolean areDrawablesIdentical(Drawable drawableA, Drawable drawableB) {
        Drawable.ConstantState stateA = drawableA.getConstantState();
        Drawable.ConstantState stateB = drawableB.getConstantState();
        // If the constant state is identical, they are using the same drawable resource.
        // However, the opposite is not necessarily true.
        return (stateA != null && stateB != null && stateA.equals(stateB))
                || getBitmap(drawableA).sameAs(getBitmap(drawableB));
    }

    public static Bitmap getBitmap(Drawable drawable) {
        Bitmap result;
        if (drawable instanceof BitmapDrawable) {
            result = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            // Some drawables have no intrinsic width - e.g. solid colours.
            if (width <= 0) {
                width = 1;
            }
            if (height <= 0) {
                height = 1;
            }

            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return result;
    }

    public static int getOverDueProfileImageResourceIDentifier() {
        return R.color.visit_status_over_due;
    }

    public static int getDueProfileImageResourceIDentifier() {
        return R.color.visit_status_ok;
    }

    public static Integer daysBetweenDateAndNow(String date) {
        DateTime duration;
        if (StringUtils.isNotBlank(date)) {
            try {
                duration = new DateTime(date);
                Days days = Days.daysBetween(duration.withTimeAtStartOfDay(), DateTime.now().withTimeAtStartOfDay());
                return days.getDays();
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
        return null;
    }

    public static String actualDaysBetweenDateAndNow(Context context, String date) {
        Integer days = daysBetweenDateAndNow(date);
        if (days != null) {
            if (days <= 1) {
                return days + getStringSpacePrefix(context, R.string.day);
            } else return days + getStringSpacePrefix(context, R.string.days);
        }
        return "";
    }

    public static String actualDuration(Context context, String duration) {
        List<String> printList = new ArrayList<>();
        String[] splits = duration.split("\\s+");
        for (String s : splits) {
            if (s.contains("d")) {
                printList.add(replaceSingularPlural(s, "d", getStringSpacePrefix(context, R.string.day), getStringSpacePrefix(context, R.string.days)));
            } else if (s.contains("w")) {
                printList.add(replaceSingularPlural(s, "w", getStringSpacePrefix(context, R.string.week), getStringSpacePrefix(context, R.string.weeks)));
            } else if (s.contains("m")) {
                printList.add(replaceSingularPlural(s, "m", getStringSpacePrefix(context, R.string.month), getStringSpacePrefix(context, R.string.months)));
            } else if (s.contains("y")) {
                printList.add(replaceSingularPlural(s, "y", getStringSpacePrefix(context, R.string.year), getStringSpacePrefix(context, R.string.years)));
            }
        }

        return StringUtils.join(printList, " ");
    }


    private static String replaceSingularPlural(String string, String dwmyString, String singular, String plural) {
        int dwmy = Integer.valueOf(string.substring(0, string.indexOf(dwmyString)));
        return " " + string.replace(dwmyString, dwmy > 1 ? plural : singular);
    }

    private static String getStringSpacePrefix(Context context, int resId) {
        return " " + context.getString(resId);
    }
}
