package org.smartregister.chw.core.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Preconditions;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Period;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.FamilyCallDialogContract;
import org.smartregister.chw.core.custom_views.CoreFamilyMemberFloatingMenu;
import org.smartregister.chw.core.fragment.CopyToClipboardDialog;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.family.util.DBConstants;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.util.PermissionUtils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public abstract class Utils extends org.smartregister.family.util.Utils {
    public static final SimpleDateFormat dd_MMM_yyyy = new SimpleDateFormat("dd MMM yyyy");
    public static final SimpleDateFormat yyyy_mm_dd = new SimpleDateFormat("yyyy-mm-dd");
    private static List<String> assets;

    public static String getImmunizationHeaderLanguageSpecific(Context context, String value) {
        if(TextUtils.isEmpty(value)) return "";
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
        if(TextUtils.isEmpty(value)) return "";
        if (value.equalsIgnoreCase("yes")) {
            return context.getString(R.string.yes);
        } else if (value.equalsIgnoreCase("no")) {
            return context.getString(R.string.no);
        }
        return value;
    }

    public static String getGenderLanguageSpecific(Context context, String value) {
        if(TextUtils.isEmpty(value)) return "";
        if (value.equalsIgnoreCase("male")) {
            return context.getString(org.smartregister.family.R.string.male);
        } else if (value.equalsIgnoreCase("female")) {
            return context.getString(org.smartregister.family.R.string.female);
        }
        return value;
    }

    public static String getServiceTypeLanguageSpecific(Context context, String value) {
        if (value.equalsIgnoreCase(CoreConstants.GROWTH_TYPE.EXCLUSIVE.getValue())) {
            return context.getString(R.string.exclusive_breastfeeding);
        } else if (value.equalsIgnoreCase(CoreConstants.GROWTH_TYPE.VITAMIN.getValue())) {
            return context.getString(R.string.vitamin_a);
        } else if (value.equalsIgnoreCase(CoreConstants.GROWTH_TYPE.DEWORMING.getValue())) {
            return context.getString(R.string.deworming);
        }
        return value;
    }

    public static String firstCharacterUppercase(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static String convertToDateFormateString(String timeAsDDMMYYYY, SimpleDateFormat dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy", Locale.getDefault());//12-08-2018
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
                callView.setPendingCallRequest(() -> Utils.launchDialer(activity, callView, phoneNumber));
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

    public static String actualDaysBetweenDateAndNow(Context context, String date) {
        Integer days = daysBetweenDateAndNow(date);
        if (days != null) {
            if (days <= 1) {
                return days + getStringSpacePrefix(context, R.string.day);
            } else {
                return days + getStringSpacePrefix(context, R.string.days);
            }
        }
        return "";
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

    private static String getStringSpacePrefix(Context context, int resId) {
        return " " + context.getString(resId);
    }

    @Nullable
    public static String getDayOfMonthWithSuffix(int day, Context context) {
        Preconditions.checkArgument(day >= 1 && day <= 31, "illegal day of month: " + day);
        switch (day) {
            case 1:
                return context.getString(R.string.abv_first);
            case 2:
                return context.getString(R.string.abv_second);
            case 3:
                return context.getString(R.string.abv_third);
            case 4:
                return context.getString(R.string.abv_fourth);
            case 5:
                return context.getString(R.string.abv_fifth);
            case 6:
                return context.getString(R.string.abv_sixth);
            case 7:
                return context.getString(R.string.abv_seventh);
            case 8:
                return context.getString(R.string.abv_eigth);
            case 9:
                return context.getString(R.string.abv_nineth);
            case 10:
                return context.getString(R.string.abv_tenth);
            case 11:
                return context.getString(R.string.abv_eleventh);
            case 12:
                return context.getString(R.string.abv_twelfth);
            default:
                return null;
        }
    }

    /**
     * use  translated equivalent  {@link #getDayOfMonthWithSuffix(int, Context)}
     *
     * @param n
     * @return
     */
    @Deprecated
    public static String getDayOfMonthSuffix(String n) {
        return getDayOfMonthSuffix(Integer.parseInt(n));
    }

    /**
     * use  translated equivalent  {@link #getDayOfMonthWithSuffix(int, Context)}
     *
     * @param n
     * @return
     */
    @Deprecated
    public static String getDayOfMonthSuffix(final int n) {
        Preconditions.checkArgument(n >= 1 && n <= 31, "illegal day of month: " + n);
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
        CommonRepository commonRepository = Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName);
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

    /**
     * Check is the file exists
     *
     * @param form_name
     * @return
     */
    public static String getLocalForm(String form_name) {
        Locale current = CoreChwApplication.getCurrentLocale();

        String formIdentity = MessageFormat.format("{0}_{1}", form_name, current.getLanguage());
        // validate variant exists
        try {
            if (assets == null) {
                assets = new ArrayList<>();
            }

            if (assets.size() == 0) {
                String[] local_assets = CoreChwApplication.getInstance().getApplicationContext().getAssets().list("json.form");
                if (local_assets != null && local_assets.length > 0) {
                    for (String s : local_assets) {
                        assets.add(s.substring(0, s.length() - 5));
                    }
                }
            }

            if (assets.contains(formIdentity)) {
                return formIdentity;
            }
        } catch (Exception e) {
            // return default
            return form_name;
        }
        return form_name;
    }

    public static String getLocalForm(String form_name, Locale locale, AssetManager assetManager) {
        return getFileName(form_name, locale, assetManager);
    }

    public static String getFileName(String form_name, Locale current, AssetManager assetManager) {
        String formIdentity = MessageFormat.format("{0}_{1}", form_name, current.getLanguage());
        try {
            if (assets == null) {
                assets = new ArrayList<>();
            }


            if (assets.size() == 0) {
                String[] local_assets = assetManager.list("json.form");
                if (local_assets != null && local_assets.length > 0) {
                    for (String s : local_assets) {
                        assets.add(s.substring(0, s.length() - 5));
                    }
                }
            }

            if (assets.contains(formIdentity)) {
                return formIdentity;
            }
        } catch (Exception e) {
            Timber.v(e);
        }
        return form_name;
    }

    public static String getFamilyMembersSql(String familyID) {

        return DBConstants.KEY.RELATIONAL_ID + " , " +
                DBConstants.KEY.BASE_ENTITY_ID + " , " +
                DBConstants.KEY.FIRST_NAME + " , " +
                DBConstants.KEY.MIDDLE_NAME + " , " +
                DBConstants.KEY.LAST_NAME + " , " +
                DBConstants.KEY.PHONE_NUMBER + " , " +
                DBConstants.KEY.OTHER_PHONE_NUMBER + " , " +
                DBConstants.KEY.DOB + " , " +
                DBConstants.KEY.DOD + " , " +
                DBConstants.KEY.GENDER;
    }

    public static String formatReferralDuration(DateTime referralTime, Context context) {
        DateTime now = new DateTime();
        int days = Days.daysBetween(referralTime, now).getDays();
        if (days == 0) {
            return context.getString(R.string.hours_ago, Hours.hoursBetween(referralTime, now).getHours());
        } else if (days == 1) {
            return context.getString(R.string.yesterday);
        } else {
            return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(referralTime.toDate());
        }
    }

    public static String getSyncFilterValue() {
        String providerId = org.smartregister.Context.getInstance().allSharedPreferences().fetchRegisteredANM();
        String userLocationId = org.smartregister.Context.getInstance().allSharedPreferences().fetchUserLocalityId(providerId);
        List<String> locationIds = LocationHelper.getInstance().locationsFromHierarchy(true, null);
        if (!isEmptyCollection(locationIds)) {
            int index = locationIds.indexOf(userLocationId);
            List<String> subLocationIds = locationIds.subList(index, locationIds.size());
            return StringUtils.join(subLocationIds, ",");
        }
        return "";
    }

    public static void redrawWithOption(CoreFamilyMemberFloatingMenu menu, boolean has_phone) {
        TextView callTextView = menu.findViewById(R.id.CallTextView);
        TextView callTextViewHint = menu.findViewById(R.id.CallTextViewHint);

        if (has_phone) {

            callTextViewHint.setVisibility(GONE);
            menu.getCallLayout().setOnClickListener(menu);
            callTextView.setTypeface(null, Typeface.NORMAL);
            callTextView.setTextColor(menu.getResources().getColor(android.R.color.black));
            ((FloatingActionButton) menu.findViewById(R.id.callFab)).getDrawable().setAlpha(255);

        } else {

            callTextViewHint.setVisibility(VISIBLE);
            menu.getCallLayout().setOnClickListener(null);
            callTextView.setTypeface(null, Typeface.ITALIC);
            callTextView.setTextColor(menu.getResources().getColor(R.color.grey));
            ((FloatingActionButton) menu.findViewById(R.id.callFab)).getDrawable().setAlpha(122);
        }
    }

    public static boolean isWomanOfReproductiveAge(CommonPersonObjectClient commonPersonObject) {
        if (commonPersonObject == null) {
            return false;
        }

        // check age and gender
        String dobString = org.smartregister.util.Utils.getValue(commonPersonObject.getColumnmaps(), "dob", false);
        String gender = org.smartregister.util.Utils.getValue(commonPersonObject.getColumnmaps(), "gender", false);
        if (!TextUtils.isEmpty(dobString) && gender.trim().equalsIgnoreCase("Female")) {
            Period period = new Period(new DateTime(dobString), new DateTime());
            int age = period.getYears();
            return age >= 15 && age <= 49;
        }

        return false;
    }

    /**
     * This is a compatibility class to process the old child home visit events to
     * the new visits structure
     *
     * @param eventClient
     * @return
     */
    public static List<EventClient> processOldEvents(EventClient eventClient) {

        // remove all nested events and add them to this object
        List<EventClient> events = new ArrayList<>();

        if (eventClient.getEvent() == null) {
            return new ArrayList<>();
        }

        Event event = eventClient.getEvent();
        List<org.smartregister.domain.db.Obs> observations = new ArrayList<>();
        for (org.smartregister.domain.db.Obs obs : event.getObs()) {
            switch (obs.getFieldCode()) {
                case "illness_information":
                    try {
                        JSONObject jsonObject = new JSONObject(obs.getValues().get(0).toString());
                        if (jsonObject.has("obsIllness")) {
                            Event obsEvent = convert(jsonObject.getString("obsIllness"), Event.class);
                            events.add(new EventClient(obsEvent, eventClient.getClient()));
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                    break;
                case "birth_certificate":
                    try {
                        String val = obs.getValues().get(0).toString();
                        if (val.equalsIgnoreCase("NOT_GIVEN") || val.equalsIgnoreCase("GIVEN")) {
                            observations.add(obs);
                            continue;
                        }

                        JSONObject jsonObject = new JSONObject(val);
                        if (jsonObject.has("birtCert")) {
                            Event obsEvent = convert(jsonObject.getString("birtCert"), Event.class);
                            events.add(new EventClient(obsEvent, eventClient.getClient()));
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                    break;
                case "service":
                    try {
                        JSONArray jsonArray = new JSONArray(obs.getValues());
                        int length = jsonArray.length();
                        int x = 0;
                        while (x < length) {
                            String value_raw = jsonArray.getString(x);
                            String values = value_raw.substring(1, value_raw.length() - 1);
                            String[] services = values.split(",");
                            for (String service_str : services) {
                                String[] service = service_str.split(":");
                                if (service.length == 2) {
                                    String key = service[0].substring(1, service[0].length() - 1);
                                    String val = service[1].substring(1, service[1].length() - 1);

                                    org.smartregister.domain.db.Obs obs1 = new org.smartregister.domain.db.Obs();
                                    obs1.setFieldType("formsubmissionField");
                                    obs1.setFieldDataType("text");
                                    obs1.setFieldCode(key);
                                    obs1.setFormSubmissionField(key);
                                    obs1.setParentCode("");
                                    obs1.setValues(new ArrayList<>(Arrays.asList(val)));
                                    obs1.setHumanReadableValues(new ArrayList<>(Arrays.asList(val)));
                                    observations.add(obs1);
                                }
                            }
                            x++;
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                    break;
                default:
                    observations.add(obs);
                    break;
            }

        }

        // exclude these events
        // fieldCode : singleVaccine , service ,  vaccineNotGiven , groupVaccine , serviceNotGiven


        // convert the json in these events
        // fieldCode : birth_certificate

        event.setObs(null);
        event.setObs(observations);

        events.add(new EventClient(event, eventClient.getClient()));

        return events;
    }

    private static <T> T convert(String jsonString, Class<T> t) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        try {
            return JsonFormUtils.gson.fromJson(jsonString, t);
        } catch (Exception e) {
            Timber.e(e);
            Timber.e(jsonString);
            return null;
        }
    }

    public static String getStringResourceByName(String name, Context context) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(name, "string", packageName);
        if (resId == 0) {
            return name;
        } else {
            return context.getString(resId);
        }
    }
}
