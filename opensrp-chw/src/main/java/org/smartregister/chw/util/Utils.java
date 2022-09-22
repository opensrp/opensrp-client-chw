package org.smartregister.chw.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.ClientReferralActivity;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.util.DBConstants;
import org.smartregister.growthmonitoring.domain.ZScore;
import org.smartregister.growthmonitoring.repository.WeightForHeightRepository;
import org.smartregister.helper.BottomNavigationHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public static List<ReferralTypeModel> getCommonReferralTypes(Activity activity, String baseEntityId) {
        CommonPersonObjectClient client = Utils.getCommonPersonObjectClient(baseEntityId);
        String gender = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.GENDER, false);

        List<ReferralTypeModel> referralTypeModels = new ArrayList<>();
        if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH) {
            referralTypeModels.add(new ReferralTypeModel(activity.getString(R.string.gbv_referral),
                    Constants.JSON_FORM.getGbvReferralForm(), CoreConstants.TASKS_FOCUS.SUSPECTED_GBV));
            referralTypeModels.add(new ReferralTypeModel(activity.getString(R.string.hts_referral),
                    CoreConstants.JSON_FORM.getHtsReferralForm(), CoreConstants.TASKS_FOCUS.SUSPECTED_HIV));
            if(gender.equalsIgnoreCase("Female") && isMemberOfReproductiveAge(client, 10, 49)){
                referralTypeModels.add(new ReferralTypeModel(activity.getString(R.string.pregnancy_confirmation_referral),
                        CoreConstants.JSON_FORM.getPregnancyConfirmationReferralForm(), CoreConstants.TASKS_FOCUS.PREGNANCY_CONFIRMATION));
            }
        }
        return referralTypeModels;
    }

    public static String toCSV(List<String> list) {
        String result = "";
        if (list.size() > 0) {
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


    public static  String getClientGender(String baseEntityId){
        CommonRepository commonRepository = org.smartregister.family.util.Utils.context().commonrepository(org.smartregister.family.util.Utils.metadata().familyMemberRegister.tableName);

        final CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(baseEntityId);
        final CommonPersonObjectClient client = new CommonPersonObjectClient(commonPersonObject.getCaseId(), commonPersonObject.getDetails(), "");
        client.setColumnmaps(commonPersonObject.getColumnmaps());
        return org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), org.smartregister.family.util.DBConstants.KEY.GENDER, false);
    }

    /*
    * For CBHS Registration
    */
    public static void updateAgeAndGender(JSONArray fields, int age, String gender) throws Exception {
        boolean foundAge = false;
        boolean foundGender = false;
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            if (field.getString("name").equals("age")) {
                field.getJSONObject("properties").put("text", String.valueOf(age));
                foundAge = true;
            }
            if (field.getString("name").equals("gender")) {
                field.getJSONObject("properties").put("text", gender);
                foundGender = true;
            }
            if (foundAge && foundGender) {
                return;
            }
        }
    }


}
