package org.smartregister.chw.util;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.util.DBConstants;

public class ChildDBConstants extends org.smartregister.chw.core.utils.ChildDBConstants {

    private static final int FIVE_YEAR = 5;
    private static final int TWO_YEAR = 2;

    public static String childDueVaccinesFilterForChildrenBelowTwoAndGirlsAgeNineToEleven() {
        return childDueVaccinesFilterForChildrenBelowTwoAndGirlsAgeNineToEleven(DBConstants.KEY.DOB, TWO_YEAR, org.smartregister.chw.core.utils.ChildDBConstants.KEY.ENTRY_POINT, org.smartregister.chw.core.utils.ChildDBConstants.KEY.MOTHER_ENTITY_ID);
    }

    public static String childDueVaccinesFilterForChildrenBelowTwoAndGirlsAgeNineToEleven(String tableName) {
        return childDueVaccinesFilterForChildrenBelowTwoAndGirlsAgeNineToEleven(tableColConcat(tableName, DBConstants.KEY.DOB), TWO_YEAR, tableColConcat(tableName, org.smartregister.chw.core.utils.ChildDBConstants.KEY.ENTRY_POINT), tableColConcat(tableName, org.smartregister.chw.core.utils.ChildDBConstants.KEY.MOTHER_ENTITY_ID));
    }

    private static String childDueVaccinesFilterForChildrenBelowTwoAndGirlsAgeNineToEleven(String dateColumn, int age, String entryPoint, String motherEntityId) {
        return " (( ifnull(" + CoreConstants.TABLE_NAME.CHILD + "." + entryPoint + ",'') <> 'PNC' ) " +
                " or (ifnull(" + CoreConstants.TABLE_NAME.CHILD + "." + entryPoint + ",'') = 'PNC'" +
                " and ( date(" + CoreConstants.TABLE_NAME.CHILD + "." + dateColumn + ", '+28 days') <= date() " +
                " and ((SELECT is_closed FROM ec_family_member WHERE base_entity_id = " + CoreConstants.TABLE_NAME.CHILD + "." + motherEntityId + " ) = 0))) " +
                " or (ifnull(ec_child.entry_point,'') = 'PNC' " +
                " and (SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 1))" +
                " and ec_family_member.is_closed = 0 " +
                "and (((julianday('now') - julianday(ec_child.dob))/365.25) < 2 or (ec_child.gender = 'Female' and (((julianday('now') - julianday(ec_child.dob))/365.25) BETWEEN 9 AND 11)))\n";
    }

    private static String tableColConcat(String tableName, String columnName) {
        if (StringUtils.isBlank(tableName) || StringUtils.isBlank(columnName)) {
            return "";
        }
        return tableName.concat(".").concat(columnName);
    }
}