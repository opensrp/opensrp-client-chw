package org.smartregister.chw.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.family.util.DBConstants;

public class ChildDBConstants {
    public static final String OTHER_PHONE_NUMBER = "other_phone_number";
    public static final String PHONE_NUMBER = "phone_number";
    private static final int FIVE_YEAR = 5;

    public static String childAgeLimitFilter() {
        return childAgeLimitFilter(DBConstants.KEY.DOB, FIVE_YEAR, ChildDBConstants.KEY.ENTRY_POINT);
    }

    private static String childAgeLimitFilter(String dateColumn, int age, String entryPoint) {
        return " ((( julianday('now') - julianday(" + CoreConstants.TABLE_NAME.CHILD + "." + dateColumn + "))/365.25) <" + age + ")  " +
                " and (( ifnull(" + CoreConstants.TABLE_NAME.CHILD + "." + entryPoint + ",'') <> 'PNC' ) or (ifnull(" + CoreConstants.TABLE_NAME.CHILD + "." + entryPoint + ",'') = 'PNC' and date(" + CoreConstants.TABLE_NAME.CHILD + "." + dateColumn + ", '+28 days') < date())) " +
                " and ((( julianday('now') - julianday(" + CoreConstants.TABLE_NAME.CHILD + "." + dateColumn + "))/365.25) < 5) ";
    }

    public static String childAgeLimitFilter(String tableName) {
        return childAgeLimitFilter(tableColConcat(tableName, DBConstants.KEY.DOB), FIVE_YEAR, tableColConcat(tableName, ChildDBConstants.KEY.ENTRY_POINT));
    }

    private static String tableColConcat(String tableName, String columnName) {
        if (StringUtils.isBlank(tableName) || StringUtils.isBlank(columnName)) {
            return "";
        }
        return tableName.concat(".").concat(columnName);
    }

    public static String childDueFilter() {
        return "(( " +
                "IFNULL(STRFTIME('%Y%m%d%H%M%S', datetime((" + KEY.LAST_HOME_VISIT + ")/1000,'unixepoch')),0) " +
                "< STRFTIME('%Y%m%d%H%M%S', datetime('now','start of month')) " +
                "AND IFNULL(STRFTIME('%Y%m%d%H%M%S', datetime((" + KEY.VISIT_NOT_DONE + ")/1000,'unixepoch')),0) " +
                "< STRFTIME('%Y%m%d%H%M%S', datetime('now','start of month')) " +
                " ))";
    }

    public static String childMainFilter(String mainCondition, String mainMemberCondition, String filters, String sort, int limit, int offset) {
        return "SELECT " + CommonFtsObject.idColumn + " FROM " + CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.CHILD) + " WHERE " + CommonFtsObject.idColumn + " IN " +
                " ( SELECT " + CommonFtsObject.idColumn + " FROM " + CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.CHILD) + " WHERE  " + mainCondition + "  AND " + CommonFtsObject.phraseColumn + matchPhrase(filters) +
                " UNION " +
                " SELECT " + tableColConcat(CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.CHILD), CommonFtsObject.idColumn) + " FROM " + CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.CHILD) +
                " JOIN " + CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.FAMILY) + " on " + tableColConcat(CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.CHILD), CommonFtsObject.relationalIdColumn) + " = " + tableColConcat(CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.FAMILY), CommonFtsObject.idColumn) +
                " JOIN " + CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.FAMILY_MEMBER) + " on " + tableColConcat(CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.FAMILY_MEMBER), CommonFtsObject.idColumn) + " = " + tableColConcat(CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.FAMILY), DBConstants.KEY.PRIMARY_CAREGIVER) +
                " WHERE  " + mainMemberCondition.trim() + " AND " + tableColConcat(CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.FAMILY_MEMBER), CommonFtsObject.phraseColumn + matchPhrase(filters)) +
                ")  " + orderByClause(sort) + limitClause(limit, offset);
    }

    private static String matchPhrase(String phrase) {
        String stringPhrase = phrase;
        if (stringPhrase == null) {
            stringPhrase = "";
        }

        // Underscore does not work well in fts search
        if (stringPhrase.contains("_")) {
            stringPhrase = stringPhrase.replace("_", "");
        }
        return " MATCH '" + stringPhrase + "*' ";

    }

    private static String orderByClause(String sort) {
        if (StringUtils.isNotBlank(sort)) {
            return " ORDER BY " + sort;
        }
        return "";
    }

    private static String limitClause(int limit, int offset) {
        return " LIMIT " + offset + "," + limit;
    }

    public static final class KEY {
        //public static final String VISIT_STATUS = "visit_status";
        public static final String VISIT_NOT_DONE = "visit_not_done";
        public static final String LAST_HOME_VISIT = "last_home_visit";
        public static final String DATE_CREATED = "date_created";
        public static final String RELATIONAL_ID = "relationalid";
        public static final String FAMILY_FIRST_NAME = "family_first_name";
        public static final String FAMILY_MIDDLE_NAME = "family_middle_name";
        public static final String FAMILY_MEMBER_PHONENUMBER = "family_member_phone_number";
        public static final String FAMILY_MEMBER_PHONENUMBER_OTHER = "family_member_phone_number_other";
        public static final String FAMILY_LAST_NAME = "family_last_name";
        public static final String FAMILY_HOME_ADDRESS = "family_home_address";
        public static final String ENTITY_TYPE = "entity_type";
        public static final String ENTRY_POINT = "entry_point";
        public static final String CHILD_BF_HR = "early_bf_1hr";
        public static final String CHILD_PHYSICAL_CHANGE = "physically_challenged";
        public static final String BIRTH_CERT = "birth_cert";
        public static final String BIRTH_CERT_ISSUE_DATE = "birth_cert_issue_date";
        public static final String BIRTH_CERT_NUMBER = "birth_cert_num";
        public static final String BIRTH_CERT_NOTIFIICATION = "birth_notification";
        public static final String ILLNESS_DATE = "date_of_illness";
        public static final String ILLNESS_DESCRIPTION = "illness_description";
        public static final String ILLNESS_ACTION = "action_taken";
        public static final String ILLNESS_ACTION_BA = "action_taken_1m5yr";
        public static final String OTHER_ACTION = "other_treat_1m5yr";
        public static final String EVENT_DATE = "event_date";
        public static final String EVENT_TYPE = "event_type";
        public static final String INSURANCE_PROVIDER = "insurance_provider";
        public static final String INSURANCE_PROVIDER_NUMBER = "insurance_provider_number";
        public static final String INSURANCE_PROVIDER_OTHER = "insurance_provider_other";
        public static final String TYPE_OF_DISABILITY = "type_of_disability";
        public static final String RHC_CARD = "rhc_card";
        public static final String NUTRITION_STATUS = "nutrition_status";
        public static final String VACCINE_CARD = "vaccine_card";
        public static final String MOTHER_ENTITY_ID = "mother_entity_id";
        public static final String PRIMARY_CAREGIVER = "primary_caregiver";
        public static final String FAMILY_PRIMARY_CAREGIVER = "family_primary_caregiver";
        public static final String LAST_MENSTRUAL_PERIOD = "last_menstrual_period";
        public static final String SCHEDULE_GROUP_NAME = "schedule_group_name";
        public static final String SCHEDULE_NAME = "schedule_name";
        public static final String DUE_DATE = "due_date";
        public static final String NOT_DONE_DATE = "not_done_date";
        public static final String OVER_DUE_DATE = "over_due_date";
        public static final String EXPIRY_DATE = "expiry_date";
        public static final String COMPLETION_DATE = "completion_date";
        public static final String VISIT_TYPE = "visit_type";
        public static final String VISIT_DATE = "visit_date";

        // Family child visit status
        //public static final String CHILD_VISIT_STATUS = "child_visit_status";
    }
}
