package org.smartregister.chw.util;

public class Constants {

    public static String EC_CLIENT_FIELDS = "ec_client_fields.json";

    public static class CONFIGURATION {
        public static final String LOGIN = "login";
        public static final String FAMILY_REGISTER = "family_register";
        public static final String FAMILY_MEMBER_REGISTER = "family_member_register";
        public static final String ANC_REGISTER = "anc_register";
        public static final String MALARIA_REGISTER = "anc_malaria_confirmation";

    }

    public static final class EventType {
        public static final String BIRTH_CERTIFICATION = "Birth Certification";
        public static final String OBS_ILLNESS = "Observations Illness";
        public static final String COUNSELING = "Counseling";
        public static final String FAMILY_REGISTRATION = "Family Registration";
        public static final String FAMILY_MEMBER_REGISTRATION = "Family Member Registration";

        public static final String CHILD_REGISTRATION = "Child Registration";
        public static final String UPDATE_CHILD_REGISTRATION = "Update Child Registration";
        public static final String CHILD_HOME_VISIT = "Child Home Visit";
        public static final String CHILD_VISIT_NOT_DONE = "Visit not done";
        public static final String VACCINE_CARD_RECEIVED = "Child vaccine card received";
        public static final String MINIMUM_DIETARY_DIVERSITY = "Minimum dietary diversity";
        public static final String MUAC = "Mid-upper arm circumference (MUAC)";
        public static final String UPDATE_FAMILY_RELATIONS = "Update Family Relations";
        public static final String UPDATE_FAMILY_MEMBER_RELATIONS = "Update Family Member Relations";

        public static final String UPDATE_FAMILY_REGISTRATION = "Update Family Registration";
        public static final String UPDATE_FAMILY_MEMBER_REGISTRATION = "Update Family Member Registration";

        public static final String REMOVE_MEMBER = "Remove Family Member";
        public static final String REMOVE_CHILD = "Remove Child Under 5";
        public static final String REMOVE_FAMILY = "Remove Family";

        public static final String ANC_REGISTRATION = "ANC Registration";
        public static final String ANC_HOME_VISIT = "ANC Home Visit";
        public static final String UPDATE_ANC_REGISTRATION = "Update ANC Registration";
        public static final String PREGNANCY_OUTCOME = "Pregnancy Outcome";
    }

    /**
     * Only access form constants via the getter
     */
    public static class JSON_FORM {
        private static final String BIRTH_CERTIFICATION = "birth_certification";

        private static final String OBS_ILLNESS = "observation_illness";
        private static final String FAMILY_REGISTER = "family_register";
        private static final String FAMILY_MEMBER_REGISTER = "family_member_register";
        private static final String CHILD_REGISTER = "child_enrollment";
        private static final String FAMILY_DETAILS_REGISTER = "family_details_register";
        private static final String FAMILY_DETAILS_REMOVE_MEMBER = "family_details_remove_member";

        private static final String FAMILY_DETAILS_REMOVE_CHILD = "family_details_remove_child";
        private static final String FAMILY_DETAILS_REMOVE_FAMILY = "family_details_remove_family";
        private static final String HOME_VISIT_COUNSELLING = "routine_home_visit";

        private static final String ANC_REGISTRATION = "anc_member_registration";
        private static final String PREGNANCY_OUTCOME = "anc_pregnancy_outcome";
        private static final String MALARIA_CONFIRMATION = "malaria_confirmation";

        public static String getBirthCertification() {
            return Utils.getLocalForm(BIRTH_CERTIFICATION);
        }

        public static String getObsIllness() {
            return Utils.getLocalForm(OBS_ILLNESS);
        }

        public static String getFamilyRegister() {
            return Utils.getLocalForm(FAMILY_REGISTER);
        }

        public static String getFamilyMemberRegister() {
            return Utils.getLocalForm(FAMILY_MEMBER_REGISTER);
        }

        public static String getChildRegister() {
            return Utils.getLocalForm(CHILD_REGISTER);
        }

        public static String getFamilyDetailsRegister() {
            return Utils.getLocalForm(FAMILY_DETAILS_REGISTER);
        }

        public static String getFamilyDetailsRemoveMember() {
            return Utils.getLocalForm(FAMILY_DETAILS_REMOVE_MEMBER);
        }

        public static String getFamilyDetailsRemoveChild() {
            return Utils.getLocalForm(FAMILY_DETAILS_REMOVE_CHILD);
        }

        public static String getFamilyDetailsRemoveFamily() {
            return Utils.getLocalForm(FAMILY_DETAILS_REMOVE_FAMILY);
        }

        public static String getHomeVisitCounselling() {
            return Utils.getLocalForm(HOME_VISIT_COUNSELLING);
        }

        public static String getAncRegistration() {
            return Utils.getLocalForm(ANC_REGISTRATION);
        }

        public static String getPregnancyOutcome() {
            return Utils.getLocalForm(PREGNANCY_OUTCOME);
        }

        public static String getMalariaConfirmation() {
            return Utils.getLocalForm(MALARIA_CONFIRMATION);
        }

        public static class ANC_HOME_VISIT {
            private static final String DANGER_SIGNS = "anc_hv_danger_signs";
            private static final String ANC_COUNSELING = "anc_hv_counseling";
            private static final String SLEEPING_UNDER_LLITN = "anc_hv_sleeping_under_llitn";
            private static final String ANC_CARD_RECEIVED = "anc_hv_anc_card_received";
            private static final String TT_IMMUNIZATION = "anc_hv_tt_immunization";
            private static final String IPTP_SP = "anc_hv_anc_iptp_sp";

            private static final String HEALTH_FACILITY_VISIT = "anc_hv_health_facility_visit";
            private static final String FAMILY_PLANNING = "anc_hv_family_planning";
            private static final String NUTRITION_STATUS = "anc_hv_nutrition_status";
            private static final String COUNSELLING = "anc_hv_counselling";
            private static final String MALARIA = "anc_hv_malaria";
            private static final String OBSERVATION_AND_ILLNESS = "anc_hv_observations";
            private static final String REMARKS_AND_COMMENTS = "anc_hv_remarks_and_comments";
            private static final String EARLY_CHILDHOOD_DEVELOPMENT = "early_childhood_development";

            public static String getDangerSigns() {
                return Utils.getLocalForm(DANGER_SIGNS);
            }

            public static String getAncCounseling() {
                return Utils.getLocalForm(ANC_COUNSELING);
            }

            public static String getSleepingUnderLlitn() {
                return Utils.getLocalForm(SLEEPING_UNDER_LLITN);
            }

            public static String getAncCardReceived() {
                return Utils.getLocalForm(ANC_CARD_RECEIVED);
            }

            public static String getTtImmunization() {
                return Utils.getLocalForm(TT_IMMUNIZATION);
            }

            public static String getIptpSp() {
                return Utils.getLocalForm(IPTP_SP);
            }

            public static String getHealthFacilityVisit() {
                return Utils.getLocalForm(HEALTH_FACILITY_VISIT);
            }

            public static String getFamilyPlanning() {
                return Utils.getLocalForm(FAMILY_PLANNING);
            }

            public static String getNutritionStatus() {
                return Utils.getLocalForm(NUTRITION_STATUS);
            }

            public static String getCOUNSELLING() {
                return Utils.getLocalForm(COUNSELLING);
            }

            public static String getMALARIA() {
                return Utils.getLocalForm(MALARIA);
            }

            public static String getObservationAndIllness() {
                return Utils.getLocalForm(OBSERVATION_AND_ILLNESS);
            }

            public static String getRemarksAndComments() {
                return Utils.getLocalForm(REMARKS_AND_COMMENTS);
            }

            public static String getEarlyChildhoodDevelopment() {
                return Utils.getLocalForm(EARLY_CHILDHOOD_DEVELOPMENT);
            }
        }
    }

    public static class RELATIONSHIP {
        public static final String FAMILY = "family";
        public static final String FAMILY_HEAD = "family_head";
        public static final String PRIMARY_CAREGIVER = "primary_caregiver";
    }

    public static class TABLE_NAME {
        public static final String FAMILY = "ec_family";
        public static final String FAMILY_MEMBER = "ec_family_member";
        public static final String CHILD = "ec_child";
        public static final String CHILD_ACTIVITY = "ec_child_activity";
        public static final String ANC_MEMBER = "ec_anc_register";
        public static final String ANC_MEMBER_LOG = "ec_anc_log";
        public static final String MALARIA_CONFIRMATION = "ec_malaria_confirmation";
        public static final String ANC_PREGNANCY_OUTCOME = "ec_pregnancy_outcome";
    }

    public static final class INTENT_KEY {
        public static final String SERVICE_DUE = "service_due";
        public static final String CHILD_BASE_ID = "child_base_id";
        public static final String CHILD_NAME = "child_name";
        public static final String CHILD_DATE_OF_BIRTH = "child_dob";
        public static final String CHILD_LAST_VISIT_DAYS = "child_visit_days";
        public static final String CHILD_VACCINE_LIST = "child_vaccine_list";
        public static final String GROWTH_TITLE = "growth_title";
        public static final String GROWTH_QUESTION = "growth_ques";
        public static final String GROWTH_IMMUNIZATION_TYPE = "growth_type";
        public static final String CHILD_COMMON_PERSON = "child_common_peron";
        public static final String IS_COMES_FROM_FAMILY = "is_comes_from";
    }

    public static final class IMMUNIZATION_CONSTANT {
        public static final String DATE = "date";
        public static final String VACCINE = "vaccine";
    }

    public static final class DrawerMenu {
        public static final String ALL_FAMILIES = "All Families";
        public static final String ANC_CLIENTS = "ANC Clients";
        public static final String CHILD_CLIENTS = "Child Clients";
        public static final String HIV_CLIENTS = "Hiv Clients";
        public static final String ANC = "ANC";
        public static final String LD = "L&D";
        public static final String PNC = "PNC";
        public static final String FAMILY_PLANNING = "Family Planning";
        public static final String MALARIA = "Malaria";
    }

    public static final class RULE_FILE {
        public static final String HOME_VISIT = "home-visit-rules.yml";
        public static final String ANC_HOME_VISIT = "anc-home-visit-rules.yml";
        public static final String BIRTH_CERT = "birth-cert-rules.yml";
        public static final String SERVICE = "service-rules.yml";
        public static final String IMMUNIZATION_EXPIRED = "immunization-expire-rules.yml";
        public static final String CONTACT_RULES = "contact-rules.yml";
    }

    public static class PROFILE_CHANGE_ACTION {
        public static final String ACTION_TYPE = "change_action_type";
        public static final String PRIMARY_CARE_GIVER = "change_primary_cg";
        public static final String HEAD_OF_FAMILY = "change_head";
    }

    public static class JsonAssets {
        public static final String DETAILS = "details";
        public static final String FAM_NAME = "fam_name";
        public static final String SURNAME = "surname";
        public static final String PREGNANT_1_YR = "preg_1yr";
        public static final String SEX = "sex";
        public static final String PRIMARY_CARE_GIVER = "primary_caregiver";
        public static final String IS_PRIMARY_CARE_GIVER = "is_primary_caregiver";
        public static final String AGE = "age";
        public static final String ID_AVAIL = "id_avail";
        public static final String NATIONAL_ID = "national_id";
        public static final String VOTER_ID = "voter_id";
        public static final String DRIVER_LICENSE = "driver_license";
        public static final String PASSPORT = "passport";
        public static final String INSURANCE_PROVIDER = "insurance_provider";
        public static final String INSURANCE_PROVIDER_OTHER = "insurance_provider_other";
        public static final String INSURANCE_PROVIDER_NUMBER = "insurance_provider_number";
        public static final String DISABILITIES = "disabilities";
        public static final String DISABILITY_TYPE = "type_of_disability";
        public static final String SERVICE_PROVIDER = "service_provider";
        public static final String LEADER = "leader";
        public static final String OTHER_LEADER = "leader_other";
        public static final String BIRTH_CERT_AVAILABLE = "birth_cert_available";
        public static final String BIRTH_REGIST_NUMBER = "birth_regist_number";
        public static final String RHC_CARD = "rhc_card";
        public static final String NUTRITION_STATUS = "nutrition_status";

        public static class FAMILY_MEMBER {
            public static final String HIGHEST_EDUCATION_LEVEL = "highest_edu_level";
            public static final String PHONE_NUMBER = "phone_number";
            public static final String OTHER_PHONE_NUMBER = "other_phone_number";
        }
    }

    public static class ProfileActivityResults {
        public static final int CHANGE_COMPLETED = 9090;
    }

    public static class FORM_CONSTANTS {

        public static class REMOVE_MEMBER_FORM {
            public static final String REASON = "remove_reason";
            public static final String DATE_DIED = "date_died";
            public static final String DATE_MOVED = "date_moved";
        }

        public static class CHANGE_CARE_GIVER {
            public static class PHONE_NUMBER {
                public static final String CODE = "159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            }

            public static class OTHER_PHONE_NUMBER {
                public static final String CODE = "5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
                public static final String PARENT_CODE = "159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            }

            public static class HIGHEST_EDU_LEVEL {
                public static final String CODE = "1712AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            }
        }

        public static class ILLNESS_ACTION_TAKEN_LEVEL {
            public static final String CODE = "164378AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        }

        public static class VACCINE_CARD {
            public static final String CODE = "164147AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        }
        public static class MINIMUM_DIETARY {
            public static final String CODE = "";
        }
        public static class MUAC {
            public static final String CODE = "160908AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        }
        public static class FORM_SUBMISSION_FIELD {
            public static final String TASK_MINIMUM_DIETARY = "task_minimum_dietary";
            public static final String TASK_MUAC = "task_muac";
            public static final String HOME_VISIT_ID= "home_visit_id";
            public static final String HOME_VISIT_DATE_LONG= "home_visit_date";
            public static final String LAST_HOME_VISIT = "last_home_visit";
            public static final String HOME_VISIT_SINGLE_VACCINE= "singleVaccine";
            public static final String HOME_VISIT_GROUP_VACCINE= "groupVaccine";
            public static final String HOME_VISIT_VACCINE_NOT_GIVEN= "vaccineNotGiven";
            public static final String HOME_VISIT_SERVICE= "service";
            public static final String HOME_VISIT_SERVICE_NOT_GIVEN= "serviceNotGiven";
            public static final String HOME_VISIT_BIRTH_CERT= "birth_certificate";
            public static final String HOME_VISIT_ILLNESS= "illness_information";

        }
    }

    public static class GLOBAL {
        public static final String NAME = "name";
        public static final String MESSAGE = "message";
    }

    public static class MenuType {
        public static final String ChangeHead = "ChangeHead";
        public static final String ChangePrimaryCare = "ChangePrimaryCare";
    }

    public static class IDENTIFIER {
        public static final String UNIQUE_IDENTIFIER_KEY = "opensrp_id";
    }

    public static class ECClientConfig {
        public static String LIBERIA_EC_CLIENT_FIELDS = "ec_client_fields.json";
        public static String TANZANIA_EC_CLIENT_FIELDS = "tz_ec_client_fields.json";
    }

    public static final class RQ_CODE {
        public static final int STORAGE_PERMISIONS = 1;
    }

    public static final class PEER_TO_PEER {

        public static final String LOCATION_ID = "location-id";
    }
}
