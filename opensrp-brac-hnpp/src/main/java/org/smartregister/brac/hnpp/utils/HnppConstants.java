package org.smartregister.brac.hnpp.utils;

import org.smartregister.chw.core.utils.CoreConstants;

public class HnppConstants extends CoreConstants {
    public static final String TEST_GU_ID = "test";

    public static final class DrawerMenu {
        public static final String ELCO_CLIENT = "Elco Clients";
        public static final String ALL_MEMBER = "All member";
    }
    public static final class FORM_KEY {
        public static final String SS_INDEX = "ss_index";
        public static final String VILLAGE_INDEX = "village_index";
    }
    public static final class KEY {
        public static final String TOTAL_MEMBER = "member_count";
        public static final String VILLAGE_NAME = "village_name";
        public static final String CLASTER = "claster";
        public static final String MODULE_ID = "module_id";
        public static final String RELATION_WITH_HOUSEHOLD = "relation_with_household_head";
        public static final String GU_ID = "gu_id";
        public static final String HOUSE_HOLD_ID = "house_hold_id";
        public static final String HOUSE_HOLD_NAME = "house_hold_name";
        public static final String SS_NAME = "ss_name";
        public static final String CHILD_MOTHER_NAME_REGISTERED = "mother_name";
        public static final String CHILD_MOTHER_NAME = "Mother_Guardian_First_Name_english";
        public static final String ID_AVAIL = "id_avail";
        public static final String NATIONAL_ID = "national_id";
        public static final String BIRTH_ID = "birth_id";
        public static final String IS_BITHDAY_KNOWN = "is_birthday_known";
        public static final String BLOOD_GROUP = "blood_group";
    }
    public static class IDENTIFIER {
        public static final String FAMILY_TEXT = "Family";

        public IDENTIFIER() {
        }
    }

    public static String relationshipObject = "{" +
            "  \"খানা প্রধান\": \"Household Head\"," +
            "  \"মা\": \"Mother\"," +
            "  \"বাবা\": \"Father\"," +
            "  \"ছেলে\": \"Son\"," +
            "  \"মেয়ে\": \"Daughter\"," +
            "  \"স্ত্রী\": \"Wife\"," +
            "  \"স্বামী\": \"Husband\"," +
            "  \"নাতি\": \"Grandson\"," +
            "  \"নাতনী\": \"GrandDaughter\"," +
            "  \"ছেলের বউ\": \"SonsWife\"," +
            "  \"মেয়ের স্বামী\": \"DaughtersHusband\"," +
            "  \"শ্বশুর\": \"FatherInLaw\"," +
            "  \"শাশুড়ি\": \"MotherInLaw\"," +
            "  \"দাদা\": \"GrandfatherPaternal\"," +
            "  \"দাদি\": \"GrandmotherPaternal\"," +
            "  \"নানা\": \"GrandfatherPaternal\"," +
            "  \"নানী\": \"GrandmotherMaternal\"," +
            "  \"অন্যান্য\": \"Others\"" +
            "}";
}
