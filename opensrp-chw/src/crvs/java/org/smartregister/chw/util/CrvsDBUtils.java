package org.smartregister.chw.util;

import static org.smartregister.AllConstants.CLIENT_TYPE;
import static org.smartregister.chw.anc.util.Constants.TABLES.EC_CHILD;
import static org.smartregister.chw.core.utils.CoreConstants.TABLE_NAME.EC_OUT_OF_AREA_CHILD;

import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.family.util.DBConstants;

import java.util.ArrayList;

public class CrvsDBUtils {

    public static String[] mainColumns(String tableName, String familyTable, String familyMemberTable) {
        ArrayList<String> columnList = new ArrayList<>();
        columnList.add(tableName + "." + DBConstants.KEY.RELATIONAL_ID + " as " + ChildDBConstants.KEY.RELATIONAL_ID);
        columnList.add(tableName + "." + DBConstants.KEY.LAST_INTERACTED_WITH);
        columnList.add(tableName + "." + DBConstants.KEY.BASE_ENTITY_ID);
        columnList.add(tableName + "." + DBConstants.KEY.FIRST_NAME);
        columnList.add(tableName + "." + DBConstants.KEY.MIDDLE_NAME);
        columnList.add(familyMemberTable + "." + DBConstants.KEY.FIRST_NAME + " as " + ChildDBConstants.KEY.FAMILY_FIRST_NAME);
        columnList.add(familyMemberTable + "." + DBConstants.KEY.LAST_NAME + " as " + ChildDBConstants.KEY.FAMILY_LAST_NAME);
        columnList.add(familyMemberTable + "." + DBConstants.KEY.MIDDLE_NAME + " as " + ChildDBConstants.KEY.FAMILY_MIDDLE_NAME);
        columnList.add(familyMemberTable + "." + ChildDBConstants.PHONE_NUMBER + " as " + ChildDBConstants.KEY.FAMILY_MEMBER_PHONENUMBER);
        columnList.add(familyMemberTable + "." + ChildDBConstants.OTHER_PHONE_NUMBER + " as " + ChildDBConstants.KEY.FAMILY_MEMBER_PHONENUMBER_OTHER);
        columnList.add(familyTable + "." + DBConstants.KEY.VILLAGE_TOWN + " as " + ChildDBConstants.KEY.FAMILY_HOME_ADDRESS);
        columnList.add(tableName + "." + DBConstants.KEY.LAST_NAME);
        columnList.add(tableName + "." + DBConstants.KEY.UNIQUE_ID);
        columnList.add(tableName + "." + DBConstants.KEY.GENDER);
        columnList.add(tableName + "." + DBConstants.KEY.DOB);
        columnList.add(tableName + "." + org.smartregister.family.util.Constants.JSON_FORM_KEY.DOB_UNKNOWN);
        columnList.add(tableName + "." + ChildDBConstants.KEY.LAST_HOME_VISIT);
        columnList.add(tableName + "." + ChildDBConstants.KEY.VISIT_NOT_DONE);
        columnList.add(tableName + "." + ChildDBConstants.KEY.CHILD_BF_HR);
        columnList.add(tableName + "." + ChildDBConstants.KEY.CHILD_PHYSICAL_CHANGE);
        columnList.add(tableName + "." + ChildDBConstants.KEY.BIRTH_CERT);
        columnList.add(tableName + "." + ChildDBConstants.KEY.BIRTH_CERT_ISSUE_DATE);
        columnList.add(tableName + "." + ChildDBConstants.KEY.BIRTH_CERT_NUMBER);
        columnList.add(tableName + "." + ChildDBConstants.KEY.BIRTH_CERT_NOTIFIICATION);
        columnList.add(tableName + "." + ChildDBConstants.KEY.BIRTH_REG_TYPE);
        columnList.add(tableName + "." + ChildDBConstants.KEY.SYSTEM_BIRTH_NOTIFICATION);
        columnList.add(tableName + "." + ChildDBConstants.KEY.INFORMANT_REASON);
        columnList.add(tableName + "." + ChildDBConstants.KEY.ILLNESS_DATE);
        columnList.add(tableName + "." + ChildDBConstants.KEY.ILLNESS_DESCRIPTION);
        columnList.add(tableName + "." + ChildDBConstants.KEY.DATE_CREATED);
        columnList.add(tableName + "." + ChildDBConstants.KEY.ILLNESS_ACTION);
        columnList.add(tableName + "." + ChildDBConstants.KEY.VACCINE_CARD);
        columnList.add("'" + EC_CHILD + "'" + " as " + CLIENT_TYPE);

        return columnList.toArray(new String[columnList.size()]);
    }

    // Union select columns for out of area children
    public static String[] outOfAreaMainColumns(String tableName) {
        ArrayList<String> columnList = new ArrayList<>();
        columnList.add("'' as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.RELATIONAL_ID);
        columnList.add(tableName + "." + DBConstants.KEY.LAST_INTERACTED_WITH);
        columnList.add(tableName + "." + DBConstants.KEY.BASE_ENTITY_ID);
        columnList.add(tableName + "." + DBConstants.KEY.FIRST_NAME);
        columnList.add(tableName + "." + DBConstants.KEY.MIDDLE_NAME);
        columnList.add(tableName + "." + ChildDBConstants.KEY.MOTHER_NAME + " as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.FAMILY_FIRST_NAME);
        columnList.add("'' as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.FAMILY_LAST_NAME);
        columnList.add("'' as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.FAMILY_MIDDLE_NAME);
        columnList.add("'' as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.FAMILY_MEMBER_PHONENUMBER);
        columnList.add("'' as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.FAMILY_MEMBER_PHONENUMBER_OTHER);
        columnList.add("'' as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.FAMILY_HOME_ADDRESS);
        columnList.add(tableName + "." + DBConstants.KEY.MIDDLE_NAME + " as " + DBConstants.KEY.LAST_NAME);
        columnList.add(tableName + "." + DBConstants.KEY.UNIQUE_ID);
        columnList.add(tableName + "." + DBConstants.KEY.GENDER);
        columnList.add(tableName + "." + DBConstants.KEY.DOB);
        columnList.add(tableName + "." + org.smartregister.family.util.Constants.JSON_FORM_KEY.DOB_UNKNOWN);
        columnList.add("'' as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.LAST_HOME_VISIT);
        columnList.add("'' as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.VISIT_NOT_DONE);
        columnList.add("'' as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.CHILD_BF_HR);
        columnList.add("'' as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.CHILD_PHYSICAL_CHANGE);
        columnList.add(tableName + "." + org.smartregister.chw.core.utils.ChildDBConstants.KEY.BIRTH_CERT);
        columnList.add(tableName + "." + org.smartregister.chw.core.utils.ChildDBConstants.KEY.BIRTH_CERT_ISSUE_DATE);
        columnList.add(tableName + "." + org.smartregister.chw.core.utils.ChildDBConstants.KEY.BIRTH_CERT_NUMBER);
        columnList.add(tableName + "." + org.smartregister.chw.core.utils.ChildDBConstants.KEY.BIRTH_CERT_NOTIFIICATION);
        columnList.add(tableName + "." + ChildDBConstants.KEY.BIRTH_REG_TYPE);
        columnList.add(tableName + "." + ChildDBConstants.KEY.SYSTEM_BIRTH_NOTIFICATION);
        columnList.add(tableName + "." + ChildDBConstants.KEY.INFORMANT_REASON);
        columnList.add("'' as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.ILLNESS_DATE);
        columnList.add("'' as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.ILLNESS_DESCRIPTION);
        columnList.add(tableName + "." + org.smartregister.chw.core.utils.ChildDBConstants.KEY.DATE_CREATED);
        columnList.add("'' as " + org.smartregister.chw.core.utils.ChildDBConstants.KEY.ILLNESS_ACTION);
        columnList.add("'' as " + ChildDBConstants.KEY.VACCINE_CARD);
        columnList.add("'" + EC_OUT_OF_AREA_CHILD + "'" + " as " + CLIENT_TYPE);

        return columnList.toArray(new String[columnList.size()]);
    }
}
