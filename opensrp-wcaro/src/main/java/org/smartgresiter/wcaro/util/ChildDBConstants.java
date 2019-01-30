package org.smartgresiter.wcaro.util;

public class ChildDBConstants {
    public static final class KEY {
        //public static final String VISIT_STATUS = "visit_status";
        public static final String VISIT_NOT_DONE = "visit_not_done";
        public static final String LAST_HOME_VISIT = "last_home_visit";
        public static final String RELATIONAL_ID = "relationalid";
        public static final String FAMILY_FIRST_NAME = "family_first_name";
        public static final String FAMILY_LAST_NAME = "family_last_name";
        public static final String FAMILY_HOME_ADDRESS = "family_home_address";
        public static final String ENTITY_TYPE = "entity_type";
        public static final String EVENT_DATE = "event_date";
        public static final String EVENT_TYPE = "event_type";

        // Family child visit status
        //public static final String CHILD_VISIT_STATUS = "child_visit_status";
    }

    public static String childDueFilter() {
        return " ((" + ChildDBConstants.KEY.LAST_HOME_VISIT + " is null OR ((" + ChildDBConstants.KEY.LAST_HOME_VISIT + "/1000) > strftime('%s',datetime('now','start of month')))) AND (" + ChildDBConstants.KEY.VISIT_NOT_DONE + " is null OR ((" + ChildDBConstants.KEY.VISIT_NOT_DONE + "/1000) > strftime('%s',datetime('now','start of month'))))) ";
    }
}
