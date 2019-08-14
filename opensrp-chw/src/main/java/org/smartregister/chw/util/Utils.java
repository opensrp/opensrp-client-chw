package org.smartregister.chw.util;

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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.FamilyCallDialogContract;
import org.smartregister.chw.fragment.CopyToClipboardDialog;
import org.smartregister.chw.fragment.GrowthNutritionInputFragment;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.util.PermissionUtils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static com.google.android.gms.common.internal.Preconditions.checkArgument;


public class Utils extends org.smartregister.family.util.Utils {

    public static final SimpleDateFormat dd_MMM_yyyy = new SimpleDateFormat("dd MMM yyyy");
    public static final SimpleDateFormat yyyy_mm_dd = new SimpleDateFormat("yyyy-mm-dd");
    private static List<String> assets;

    public static String getImmunizationHeaderLanguageSpecific(Context context, String value) {
        if (value.equalsIgnoreCase("at birth")) {
            return context.getString(R.string.at_birth);
        } else if (value.contains("weeks")) {
            return context.getString(R.string.week_full);
        } else if (value.contains("months")) {
            return context.getString(R.string.month_full);
        }
        return value;
    }

    public static String getYesNoAsLanguageSpecific(Context context, String value) {
        if (value.equalsIgnoreCase("yes")) {
            return context.getString(R.string.yes);
        } else if (value.equalsIgnoreCase("no")) {
            return context.getString(R.string.no);
        }
        return value;
    }

    public static String getGenderLanguageSpecific(Context context, String value) {
        if (value.equalsIgnoreCase("male")) {
            return context.getString(org.smartregister.family.R.string.male);
        } else if (value.equalsIgnoreCase("female")) {
            return context.getString(org.smartregister.family.R.string.female);
        }
        return value;
    }

    public static String getServiceTypeLanguageSpecific(Context context, String value) {
        if (value.equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue())) {
            return context.getString(R.string.exclusive_breastfeeding);
        } else if (value.equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.VITAMIN.getValue())) {
            return context.getString(R.string.vitamin_a);
        } else if (value.equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.DEWORMING.getValue())) {
            return context.getString(R.string.deworming);
        }
        return value;
    }

    public static String firstCharacterUppercase(String str) {
        if (TextUtils.isEmpty(str)) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static String convertToDateFormateString(String timeAsDDMMYYYY, SimpleDateFormat dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");//12-08-2018
        try {
            Date date = sdf.parse(timeAsDDMMYYYY);
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
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

                Timber.i("No dial application so we launch copy to clipboard...");

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
        return R.color.due_profile_blue;
    }

    public static Integer daysBetweenDateAndNow(String date) {
        DateTime duration;
        if (StringUtils.isNotBlank(date)) {
            try {
                duration = new DateTime(new Date(Long.valueOf(date)));
                Days days = Days.daysBetween(duration.withTimeAtStartOfDay(), DateTime.now().withTimeAtStartOfDay());
                return days.getDays();
            } catch (Exception e) {
                Timber.e(e);
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

    private static String getStringSpacePrefix(Context context, int resId) {
        return " " + context.getString(resId);
    }

    /**
     * Check is the file exists
     *
     * @param form_name
     * @return
     */
    public static String getLocalForm(String form_name) {
        Locale current = ChwApplication.getCurrentLocale();

        String formIdentity = MessageFormat.format("{0}_{1}", form_name, current.getLanguage());
        // validate variant exists
        try {
            if (assets == null)
                assets = new ArrayList<>();

            if (assets.size() == 0) {
                String[] local_assets = ChwApplication.getInstance().getApplicationContext().getAssets().list("json.form");
                if (local_assets != null && local_assets.length > 0) {
                    for (String s : local_assets) {
                        assets.add(s.substring(0, s.length() - 5));
                    }
                }
            }

            if (assets.contains(formIdentity))
                return formIdentity;
        } catch (Exception e) {
            // return default
            return form_name;
        }
        return form_name;
    }

    public static String getDayOfMonthSuffix(String n) {
        return getDayOfMonthSuffix(Integer.parseInt(n));
    }

    public static String getDayOfMonthSuffix(final int n) {
        checkArgument(n >= 1 && n <= 31, "illegal day of month: " + n);
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    @NonNull
    public static CommonPersonObjectClient clientForEdit(@NonNull String baseEntityId) {
        CommonRepository commonRepository = org.smartregister.chw.util.Utils.context().commonrepository(org.smartregister.chw.util.Utils.metadata().familyMemberRegister.tableName);
        CommonPersonObject personObject = commonRepository.findByBaseEntityId(baseEntityId);
        CommonPersonObjectClient client = new CommonPersonObjectClient(personObject.getCaseId(), personObject.getDetails(), "");
        client.setColumnmaps(personObject.getColumnmaps());
        return client;
    }

    @NonNull
    public static Intent formActivityIntent(@NonNull Activity activity, @NonNull String jsonForm) {
        Intent intent = new Intent(activity, org.smartregister.family.util.Utils.metadata().familyMemberFormActivity);
        intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm);

        Form form = new Form();
        form.setActionBarBackground(R.color.family_actionbar);
        form.setWizard(false);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

        return intent;
    }

    /**
     * @param obs
     * @return
     */
    public static Map<String, List<Obs>> groupObsByFieldObservations(List<Obs> obs) {
        Map<String, List<Obs>> map = new HashMap<>();
        for (Obs o : obs) {
            List<Obs> cur_vals = map.get(o.getFormSubmissionField());
            if (cur_vals == null) {
                cur_vals = new ArrayList<>();
            }
            cur_vals.add(o);

            map.put(o.getFormSubmissionField(), cur_vals);
        }
        return map;
    }
}
