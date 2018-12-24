package org.smartgresiter.wcaro.util;

import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.DBConstants;

public class ChildUtils {
    public static String mainSelectRegister(String tableName,String familyTableName,String familyMemberTableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, mainColumns(tableName,familyTableName,familyMemberTableName));
        queryBUilder.customJoin("LEFT JOIN " + familyTableName + " ON  " + tableName + ".relational_id =  " + familyTableName + ".id");
        queryBUilder.customJoin("LEFT JOIN " + familyMemberTableName + " ON  " + tableName + ".relational_id =  " + familyTableName + ".id");


        String query=queryBUilder.mainCondition(mainCondition)+" group by "+tableName+"."+DBConstants.KEY.BASE_ENTITY_ID;
//      String query=" Select ec_child.id as _id , ec_child.relational_id as relationalid , \n" +
//              "ec_child.last_interacted_with , ec_child.base_entity_id , ec_child.first_name , ec_family_member.first_name as family_first_name , ec_family_member.last_name as family_last_name , \n" +
//              "ec_family.village_town as family_home_address , \n" +
//              "ec_child.last_name , ec_child.unique_id , ec_child.gender , ec_child.dob FROM ec_child,ec_family_member,ec_family \n" +
//              "where  ec_child.relational_id =  ec_family.id group by ec_child.base_entity_id ORDER BY ec_child.last_interacted_with DESC   LIMIT 0,20";

        return query;
    }
    public static String mainSelect(String tableName,String familyTableName,String familyMemberTableName, String mainCondition) {

       String  query=mainSelectRegister(tableName,familyTableName,familyMemberTableName,tableName+"."+DBConstants.KEY.BASE_ENTITY_ID+" = '"+mainCondition+"'");


        return query;
    }

    private static String[] mainColumns(String tableName,String familyTable,String familyMemberTable) {

        String[] columns = new String[]{
                tableName + "." + DBConstants.KEY.RELATIONAL_ID +" as " +"relationalid",
                tableName + "." + DBConstants.KEY.LAST_INTERACTED_WITH,
                tableName + "." + DBConstants.KEY.BASE_ENTITY_ID,
                tableName + "." + DBConstants.KEY.FIRST_NAME,
                familyMemberTable + "."+DBConstants.KEY.FIRST_NAME+" as "+ChildDBConstants.KEY.FAMILY_FIRST_NAME,
                familyMemberTable + "."+DBConstants.KEY.LAST_NAME+" as "+ChildDBConstants.KEY.FAMILY_LAST_NAME,
                familyTable + "."+DBConstants.KEY.VILLAGE_TOWN+" as "+ChildDBConstants.KEY.FAMILY_HOME_ADDRESS,
                tableName + "." + DBConstants.KEY.LAST_NAME,
                tableName + "." + DBConstants.KEY.UNIQUE_ID,
                tableName + "." + DBConstants.KEY.GENDER,
                tableName + "." + DBConstants.KEY.DOB};
        return columns;
    }
}
