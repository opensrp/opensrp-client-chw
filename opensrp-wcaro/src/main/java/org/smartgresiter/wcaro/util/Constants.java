package org.smartgresiter.wcaro.util;

public class Constants {

    public static class CONFIGURATION {
        public static final String LOGIN = "login";
        public static final String FAMILY_REGISTER = "family_register";
        public static final String FAMILY_MEMBER_REGISTER = "family_member_register";

    }

    public static final class EventType {
        public static final String FAMILY_REGISTRATION = "Family Registration";
        public static final String FAMILY_MEMBER_REGISTRATION = "Family Member Registration";

        public static final String UPDATE_FAMILY_REGISTRATION = "Update Family Registration";
        public static final String UPDATE_FAMILY_MEMBER_REGISTRATION = "Update Family Member Registration";
    }

    public static class JSON_FORM {
        public static final String FAMILY_REGISTER = "family_register";
        public static final String FAMILY_MEMBER_REGISTER = "family_member_register";
        public static final String CHILD_REGISTER = "child_enrollment";
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
    }
    public static final class INTENT_KEY {
        public static final String SERVICE_DUE = "service_due";
    }
    public static final class IMMUNIZATION_CONSTANT{
        public static final String DATE = "date";
        public static final String VACCINE = "vaccine";
    }

    public static final class DrawerMenu {
        public static final String ALL_FAMILIES = "All Families";
        public static final String ANC_CLIENTS = "ANC Clients";
        public static final String CHILD_CLIENTS = "Child Clients";
        public static final String HIV_CLIENTS = "Hiv Clients";
    }

}
