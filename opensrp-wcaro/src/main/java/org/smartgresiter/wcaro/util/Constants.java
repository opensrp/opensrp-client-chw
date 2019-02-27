package org.smartgresiter.wcaro.util;

public class Constants {

    public static class CONFIGURATION {
        public static final String LOGIN = "login";
        public static final String FAMILY_REGISTER = "family_register";
        public static final String FAMILY_MEMBER_REGISTER = "family_member_register";

    }

    public static final class EventType {
        public static final String BIRTH_CERTIFICATION = "Birth Certification";
        public static final String OBS_ILLNESS = "Observations Illness";
        public static final String FAMILY_REGISTRATION = "Family Registration";
        public static final String FAMILY_MEMBER_REGISTRATION = "Family Member Registration";

        public static final String CHILD_REGISTRATION = "Child Registration";
        public static final String UPDATE_CHILD_REGISTRATION = "Update Child Registration";
        public static final String CHILD_HOME_VISIT = "Child Home Visit";
        public static final String CHILD_VISIT_NOT_DONE = "Visit not done";

        public static final String UPDATE_FAMILY_REGISTRATION = "Update Family Registration";
        public static final String UPDATE_FAMILY_MEMBER_REGISTRATION = "Update Family Member Registration";

        public static final String REMOVE_MEMBER = "Remove Family Member";
        public static final String REMOVE_CHILD = "Remove Child Under 5";
        public static final String REMOVE_FAMILY = "Remove Family";
    }

    public static class JSON_FORM {
        public static final String BIRTH_CERTIFICATION = "birth_certification";
        public static final String OBS_ILLNESS = "observation_illness";
        public static final String FAMILY_REGISTER = "family_register";
        public static final String FAMILY_MEMBER_REGISTER = "family_member_register";
        public static final String CHILD_REGISTER = "child_enrollment";
        public static final String FAMILY_DETAILS_REGISTER = "family_details_register";
        public static final String FAMILY_DETAILS_REMOVE_MEMBER = "family_details_remove_member";
        public static final String FAMILY_DETAILS_REMOVE_CHILD = "family_details_remove_child";
        public static final String FAMILY_DETAILS_REMOVE_FAMILY = "family_details_remove_family";
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
    }

    public static final class RULE_FILE {
        public static final String HOME_VISIT = "home-visit-rules.yml";
    }

    public static class PROFILE_CHANGE_ACTION {
        public static final String ACTION_TYPE = "change_action_type";
        public static final String PRIMARY_CARE_GIVER = "change_primary_cg";
        public static final String HEAD_OF_FAMILY = "change_head";
    }

    public static class JsonAssets {
        public static class FAMILY_MEMBER {
            public static final String HIGHEST_EDUCATION_LEVEL = "highest_edu_level";
            public static final String PHONE_NUMBER = "phone_number";
            public static final String OTHER_PHONE_NUMBER = "other_phone_number";
        }
        public static String DETAILS = "details";
        public static String FAM_NAME = "fam_name";
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
}
