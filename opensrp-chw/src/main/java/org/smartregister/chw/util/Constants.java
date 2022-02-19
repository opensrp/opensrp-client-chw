package org.smartregister.chw.util;

import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.Utils;

public class Constants extends CoreConstants {
    public static final String REFERRAL_TASK_FOCUS = "referral_task_focus";
    public static final String REFERRAL_TYPES = "ReferralTypes";
    public static final String APP_VERSION = "app_version";
    public static final String DB_VERSION = "db_version";
    public static final String MALARIA_REFERRAL_FORM = "malaria_referral_form";
    public static final String ALL_CLIENT_REGISTRATION_FORM = "all_clients_registration_form";
    public static String PRENANCY_OUTCOME = "preg_outcome";
    public static String FAMILY_MEMBER_LOCATION_TABLE = "ec_family_member_location";
    public static String CHILD_OVER_5 = "child_over_5";
    public static String HAS_DEATH_CERTIFICATE = "received_death_certificate";
    public static String STILL_BORN_DEATH = "still_born_death";
    public static String DEATH_PLACE = "death_place";
    public static String FATHER_NAME = "father_name";
    public static String FATHER_BIRTH_PLACE = "father_birth_place";
    public static String MOTHER_MARITAL_STATUS = "mother_marital_status";
    public static String BIRTH_PLACE_TYPE = "birth_place_type";
    public static String DEL_ATTENDANT = "del_attendant";
    public static String DEL_MODE = "del_mode";
    public static String TYPE_OF_PREGNANCY = "type_of_pregnancy";
    public static String MOTHER_HIGHEST_EDU_LEVEL = "mother_highest_edu_level";
    public static String FATHER_MARITAL_STATUS = "father_marital_status";
    public static String FATHER_HIGHEST_EDU_LEVEL = "father_highest_edu_level";
    public static String UNIQUE_ID = "unique_id";
    public static String BASE_ENTITY_ID = "base_entity_id";
    public static String EC_CLIENT_CLASSIFICATION = "ec_client_classification.json";
    public static String NATIONALITY = "nationality";
    public static String REMOVE_REASON = "remove_reason";
    public static String NATIONAL_ID = "national_id";
    public static String MARITAL_STATUS = "marital_status";
    public static String DATE_DIED = "date_died";

    public enum FamilyRegisterOptionsUtil {Miscarriage, Other}

    public enum FamilyMemberType {ANC, PNC, Other}

    public static class FORM_SUBMISSION_FIELD {
        public static String pncHfNextVisitDateFieldType = "pnc_hf_next_visit_date";

    }

    public static class Postfixes {
        public static String OUT_OF_AREA_BIRTH = "_outOfAreaBirth";
        public static String OUT_OF_AREA_DEATH = "_outOfAreaDeath";
    }

    public static class EncounterType {
        public static final String SICK_CHILD = "Sick Child Referral";
        public static final String PNC_REFERRAL = "PNC Referral";
        public static final String ANC_REFERRAL = "ANC Referral";
        public static final String PNC_CHILD_REGISTRATION = "PNC Child Registration";
        public static final String OUT_OF_AREA_CHILD_REGISTRATION = "Out Of Area Child Registration";
        public static final String OUT_OF_AREA_DEATH_REGISTRATION = "Out Of Area Death Registration";
    }

    public static class ChildIllnessViewType {
        public static final int RADIO_BUTTON = 0;
        public static final int EDIT_TEXT = 1;
        public static final int CHECK_BOX = 2;
    }

    public static class ReportParameters {
        public static String COMMUNITY = "COMMUNITY";
        public static String COMMUNITY_ID = "COMMUNITY_ID";
        public static String REPORT_DATE = "REPORT_DATE";
        public static String INDICATOR_CODE = "INDICATOR_CODE";
    }

    public static class PeerToPeerUtil {
        public static String COUNTRY_ID = "COUNTRY_ID";
    }

    public static class AncHomeVisitUtil {
        private static final String DELIVERY_KIT_RECEIVED = "anc_woman_delivery_kit_received";

        public static String getDeliveryKitReceived() {
            return Utils.getLocalForm(DELIVERY_KIT_RECEIVED, JSON_FORM.locale, JSON_FORM.assetManager);
        }

    }
}
