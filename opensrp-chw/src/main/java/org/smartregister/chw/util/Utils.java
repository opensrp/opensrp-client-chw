package org.smartregister.chw.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.ClientReferralActivity;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.growthmonitoring.domain.ZScore;
import org.smartregister.growthmonitoring.repository.WeightForHeightRepository;
import org.smartregister.helper.BottomNavigationHelper;

import java.util.ArrayList;
import java.util.List;

public class Utils extends org.smartregister.chw.core.utils.Utils {

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

    public static String getClientName(String firstName, String middleName, String lastName) {
        String trimFirstName = firstName.trim();
        String trimMiddleName = middleName.trim();
        String trimLastName = lastName.trim();
        if (ChwApplication.getApplicationFlavor().hasSurname()) {
            if (StringUtils.isNotBlank(trimFirstName) && StringUtils.isNotBlank(trimLastName)) {
                if (StringUtils.isNotBlank(trimMiddleName)) {
                    return trimFirstName + " " + trimMiddleName + " " + trimLastName;
                }
                return trimFirstName + " " + trimLastName;

            } else {
                if (StringUtils.isNotBlank(trimFirstName)) {
                    if (StringUtils.isNotBlank(trimMiddleName)) {
                        return trimFirstName + " " + trimMiddleName;
                    }
                    return trimFirstName;

                } else if (StringUtils.isNotBlank(trimLastName)) {
                    if (StringUtils.isNotBlank(trimMiddleName)) {
                        return trimMiddleName + " " + trimLastName;
                    }
                    return trimLastName;
                }
            }
        } else {
            if (StringUtils.isNotBlank(trimFirstName)) {
                if (StringUtils.isNotBlank(trimMiddleName)) {
                    return trimFirstName + " " + trimMiddleName;
                }
                return trimFirstName;
            }
        }
        return "";
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

}
