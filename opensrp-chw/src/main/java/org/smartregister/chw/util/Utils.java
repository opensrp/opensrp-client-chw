package org.smartregister.chw.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Pair;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.ClientReferralActivity;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.family.util.DBConstants;
import org.smartregister.growthmonitoring.domain.ZScore;
import org.smartregister.growthmonitoring.repository.WeightForHeightRepository;
import org.smartregister.helper.BottomNavigationHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static org.smartregister.chw.core.utils.CoreReferralUtils.getCommonRepository;

public class Utils extends org.smartregister.chw.core.utils.Utils {

    public static final String dd_MMM_yyyy = "dd MMM yyyy";

    public static void launchClientReferralActivity(Activity activity, List<ReferralTypeModel> referralTypeModels, String baseEntityId) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ENTITY_ID, baseEntityId);
        bundle.setClassLoader(ReferralTypeModel.class.getClassLoader());
        bundle.putParcelableArrayList(Constants.REFERRAL_TYPES, (ArrayList<ReferralTypeModel>) referralTypeModels);
        activity.startActivity(new Intent(activity, ClientReferralActivity.class).putExtras(bundle));
    }

    @NotNull
    public static List<ReferralTypeModel> getCommonReferralTypes(Activity activity) {
        List<ReferralTypeModel> referralTypeModels = new ArrayList<>();
        if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH) {

            referralTypeModels.add(new ReferralTypeModel(activity.getString(R.string.suspected_malaria),
                    Constants.JSON_FORM.getMalariaReferralForm(), CoreConstants.TASKS_FOCUS.SUSPECTED_MALARIA));

            referralTypeModels.add(new ReferralTypeModel(activity.getString(R.string.hiv_referral),
                    Constants.JSON_FORM.getHivReferralForm(), CoreConstants.TASKS_FOCUS.SUSPECTED_HIV));

            referralTypeModels.add(new ReferralTypeModel(activity.getString(R.string.tb_referral),
                    Constants.JSON_FORM.getTbReferralForm(), CoreConstants.TASKS_FOCUS.SUSPECTED_TB));

            referralTypeModels.add(new ReferralTypeModel(activity.getString(R.string.gbv_referral),
                    Constants.JSON_FORM.getGbvReferralForm(), CoreConstants.TASKS_FOCUS.SUSPECTED_GBV));
        }
        return referralTypeModels;
    }

    public static String toCSV(String[] list) {
        String result = "";
        if (list.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : list) {
                sb.append(s).append(", ");
            }
            result = sb.deleteCharAt(sb.length() - 2).toString();
        }
        return result;
    }

    public static void setupBottomNavigation(BottomNavigationHelper bottomNavigationHelper,
                                             BottomNavigationView bottomNavigationView,
                                             BottomNavigationView.OnNavigationItemSelectedListener listener) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu);
            bottomNavigationHelper.disableShiftMode(bottomNavigationView);
            bottomNavigationView.setOnNavigationItemSelectedListener(listener);
        }

        if (bottomNavigationView != null && !ChwApplication.getApplicationFlavor().hasQR())
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_scan_qr);

        if (bottomNavigationView != null && !ChwApplication.getApplicationFlavor().hasJobAids())
            bottomNavigationView.getMenu().removeItem(org.smartregister.family.R.id.action_job_aids);

        if (bottomNavigationView != null && !ChwApplication.getApplicationFlavor().hasReports())
            bottomNavigationView.getMenu().removeItem(R.id.action_report);
    }

    public static double getWFHZScore(String gender, String height, String weight) {
        double zScore = 0.0;
        List<ZScore> zScoreValues = new WeightForHeightRepository().findZScoreVariables(gender, Double.parseDouble(height));
        if (zScoreValues.size() > 0) {
            zScore = zScoreValues.get(0).getZ(Double.parseDouble(weight));
        }
        return zScore;
    }

    public static String formatDateForVisual(String date, String inputFormat) {
        if (StringUtils.isEmpty(date)) return "";
        SimpleDateFormat format = new SimpleDateFormat(inputFormat);
        Date newDate = null;
        try {
            newDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat(dd_MMM_yyyy);
        return format.format(newDate);
    }

    public static String getClientName(String firstName, String middleName, String lastName) {
        String trimFirstName = firstName.trim();
        String trimMiddleName = middleName.trim();
        String trimLastName = lastName.trim();
        if (ChwApplication.getApplicationFlavor().hasSurname()) {
            return getName(trimFirstName, trimMiddleName, trimLastName);
        } else {
            return Utils.getName(trimFirstName, trimMiddleName);
        }
    }


    public static void updateToolbarTitle(Activity activity, int toolbarTextViewId) {
        int titleResource = -1;
        if (activity.getIntent().getExtras() != null)
            titleResource = activity.getIntent().getExtras().getInt(CoreConstants.INTENT_KEY.TOOLBAR_TITLE, -1);
        if (titleResource != -1) {
            TextView toolbarTitleTextView = activity.findViewById(toolbarTextViewId);
            if (titleResource == org.smartregister.chw.core.R.string.return_to_family_members) {
                toolbarTitleTextView.setText(activity.getString(org.smartregister.chw.core.R.string.return_to_family_members));
            } else {
                toolbarTitleTextView.setText(titleResource);
            }
        }
    }

    public static String getDisplayLanguage(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        return locale.getDisplayLanguage();
    }

    public static String getAppLanguage(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        return locale.getLanguage();
    }

    private static long getDateDifferenceInDays(Date endDate, Date startDate) {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
    }

    public static String getRandomGeneratedId() {
        return UUID.randomUUID().toString();
    }

    public static Triple<String, String, String> fetchUserProfileFromDB(String childBaseEntityId) {
        String query = CoreChildUtils.mainSelect(CoreConstants.TABLE_NAME.CHILD, CoreConstants.TABLE_NAME.FAMILY, CoreConstants.TABLE_NAME.FAMILY_MEMBER, childBaseEntityId);
        try (Cursor cursor = getCommonRepository(CoreConstants.TABLE_NAME.CHILD).rawCustomQueryForAdapter(query)) {
            if (cursor != null && cursor.moveToFirst()) {
                CommonPersonObject personObject = getCommonRepository(CoreConstants.TABLE_NAME.CHILD).readAllcommonforCursorAdapter(cursor);
                String gender = org.smartregister.family.util.Utils.getValue(personObject.getColumnmaps(), DBConstants.KEY.GENDER, false);
                String dob = org.smartregister.family.util.Utils.getValue(personObject.getColumnmaps(), DBConstants.KEY.DOB, false);
                long ageInDays = getDateDifferenceInDays(new Date(), DateTime.parse(dob).toDate());

                return Triple.of(String.valueOf(ageInDays), dob, gender);
            }
        } catch (Exception ex) {
            Timber.e(ex, "queryDBFromUserProfile");
        }
        return null;
    }

    public static Pair<String, String> fetchMUACFromDB(String childBaseEntityId) {
        String muacValue = org.smartregister.chw.dao.VisitDao.getMUACValue(childBaseEntityId);
        String muacCode = muacValue.substring(4);
        String muacDiaplay = muacCode.substring(0, 1).toUpperCase() + muacCode.substring(1);
        return Pair.create(muacCode, muacDiaplay);
    }


}
