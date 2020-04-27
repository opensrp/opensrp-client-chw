package org.smartregister.chw.util;

import android.content.res.AssetManager;

import org.json.JSONObject;
import org.opensrp.api.constants.Gender;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.chw.malaria.util.Constants;

import java.util.Locale;

/**
 * Created by cozej4 on 2020-04-25.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class PathfinderFamilyPlanningConstants {
    /**
     * Only access form constants via the getter
     */
    public static class JSON_FORM {

        public static final String FEMALE_FAMILY_PLANNING_REGISTRATION_FORM = "pathfinder_female_family_planning_registration";
        public static final String MALE_FAMILY_PLANNING_REGISTRATION_FORM = "pathfinder_male_family_planning_registration";
        public static final String FEMALE_FAMILY_PLANNING_CHANGE_METHOD_FORM = "pathfinder_female_family_planning_change_method";
        public static final String MALE_FAMILY_PLANNING_CHANGE_METHOD_FORM = "pathfinder_male_family_planning_change_method";
        public static final String FAMILY_PLANNING_INTRODUCTION = "pathfinder_family_planning_introduction";
        public static final String PREGNANCY_SCREENING = "pathfinder_pregnancy_screening";
        public static final String CHOOSE_FAMILY_PLANNING_METHOD = "pathfinder_choose_family_planning_method";
        public static final String GIVE_FAMILY_PLANNING_METHOD = "pathfinder_give_family_planning_method";


        public static String getFamilyPlanningRegistrationForm(String gender, Locale locale, AssetManager assetManager) {
            String formName = gender.equalsIgnoreCase(Gender.MALE.toString()) ? MALE_FAMILY_PLANNING_REGISTRATION_FORM : FEMALE_FAMILY_PLANNING_REGISTRATION_FORM;
            return org.smartregister.chw.core.utils.Utils.getLocalForm(formName, locale, assetManager);
        }

        public static String getFamilyPlanningChangeMethodForm(String gender, Locale locale, AssetManager assetManager) {
            String formName = gender.equalsIgnoreCase(Gender.MALE.toString()) ? MALE_FAMILY_PLANNING_CHANGE_METHOD_FORM : FEMALE_FAMILY_PLANNING_CHANGE_METHOD_FORM;
            return org.smartregister.chw.core.utils.Utils.getLocalForm(formName, locale, assetManager);
        }

        public static String getFamilyPlanningIntroduction(Locale locale, AssetManager assetManager) {
            return org.smartregister.chw.core.utils.Utils.getLocalForm(FAMILY_PLANNING_INTRODUCTION, locale, assetManager);
        }

        public static String getPregnancyScreening(Locale locale, AssetManager assetManager) {
            return org.smartregister.chw.core.utils.Utils.getLocalForm(PREGNANCY_SCREENING, locale, assetManager);
        }

        public static String getChooseFamilyPlanningMethod(Locale locale, AssetManager assetManager) {
            return org.smartregister.chw.core.utils.Utils.getLocalForm(CHOOSE_FAMILY_PLANNING_METHOD, locale, assetManager);
        }

        public static String getGiveFamilyPlanningMethod(Locale locale, AssetManager assetManager) {
            return org.smartregister.chw.core.utils.Utils.getLocalForm(GIVE_FAMILY_PLANNING_METHOD, locale, assetManager);
        }

        public static boolean isMultiPartForm(JSONObject jsonForm) {
            String encounterType = jsonForm.optString(CoreJsonFormUtils.ENCOUNTER_TYPE);
            return !encounterType.equals(Constants.EVENT_TYPE.MALARIA_FOLLOW_UP_VISIT);
        }


        public static class FamilyPlanningFollowUpVisitUtils {
            private static final String FAMILY_PLANNING_FOLLOWUP_COUNSEL = "fp_followup_counsel";
            private static final String FAMILY_PLANNING_FOLLOWUP_RESUPPLY = "fp_followup_resupply";
            private static final String FAMILY_PLANNING_FOLLOWUP_SIDE_EFFECTS = "fp_followup_side_effects";

            public static String getFamilyPlanningFollowupCounsel(Locale locale, AssetManager assetManager) {
                return org.smartregister.chw.core.utils.Utils.getLocalForm(FAMILY_PLANNING_FOLLOWUP_COUNSEL, locale, assetManager);
            }

            public static String getFamilyPlanningFollowupResupply(Locale locale, AssetManager assetManager) {
                return org.smartregister.chw.core.utils.Utils.getLocalForm(FAMILY_PLANNING_FOLLOWUP_RESUPPLY, locale, assetManager);
            }

            public static String getFamilyPlanningFollowupSideEffects(Locale locale, AssetManager assetManager) {
                return Utils.getLocalForm(FAMILY_PLANNING_FOLLOWUP_SIDE_EFFECTS, locale, assetManager);
            }
        }
    }
}
