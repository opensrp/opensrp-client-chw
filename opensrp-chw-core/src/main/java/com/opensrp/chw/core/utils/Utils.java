package com.opensrp.chw.core.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.opensrp.chw.core.R;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;
import org.smartregister.location.helper.LocationHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

import static com.google.android.gms.common.internal.Preconditions.checkArgument;
import static org.smartregister.util.Utils.isEmptyCollection;

public class Utils {

    public static final SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd MMM yyyy");
    public static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-mm-dd");

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

    private static String replaceSingularPlural(String string, String dwmyString, String singular, String plural) {
        int dwmy = Integer.valueOf(string.substring(0, string.indexOf(dwmyString)));
        return " " + string.replace(dwmyString, dwmy > 1 ? plural : singular);
    }

    private static String getStringSpacePrefix(Context context, int resId) {
        return " " + context.getString(resId);
    }

    public static String getAncMemberNameAndAge(String firstName, String middleName, String surName, String age) {
        int integerAge = new Period(new DateTime(age), new DateTime()).getYears();
        String first_name = firstName.trim();
        String middle_name = middleName.trim();
        String sur_name = surName != null ? surName.trim() : "";

        if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(middleName) && StringUtils.isNotBlank(age)) {
            return (first_name + " " + middle_name + " " + sur_name).trim() + ", " + integerAge;
        }
        return "";
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

    public static String readFile(String form_name, Locale current, AssetManager assetManager) {
        String formIdentity = MessageFormat.format("{0}_{1}", form_name, current.getLanguage());
        // validate variant exists
        try {
            InputStream inputStream = assetManager.open("json.form/" + formIdentity + ".json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,
                    "UTF-8"));
            String jsonString;
            StringBuilder stringBuilder = new StringBuilder();
            while ((jsonString = reader.readLine()) != null) {
                stringBuilder.append(jsonString);
            }
            inputStream.close();

            return formIdentity;
        } catch (Exception e) {
            // return default
            return form_name;
        }
    }


    public static String getBuildDate(Boolean isShortMonth, long buildTimeStamp) {
        String simpleDateFormat;
        if (isShortMonth) {
            simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(new Date(buildTimeStamp));
        } else {
            simpleDateFormat =
                    new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date(buildTimeStamp));
        }
        return simpleDateFormat;
    }


    public static String firstCharacterUppercase(String str) {
        if (TextUtils.isEmpty(str)) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static String getLocalForm(String form_name, Locale locale, AssetManager assetManager) {
        return readFile(form_name, locale, assetManager);
    }
}
