package org.smartgresiter.wcaro.util;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import org.smartgresiter.wcaro.interactor.ChildProfileInteractor;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.DBConstants;

import java.util.Calendar;

public class ChildUtils {
    private static final long MILLI_SEC=24 * 60 * 60 * 1000;
    //need to add primary caregiver filter at query
    //ec_family_member.is_primary_caregiver" is true
    public static String mainSelectRegisterWithoutGroupby(String tableName,String familyTableName,String familyMemberTableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, mainColumns(tableName,familyTableName,familyMemberTableName));
        queryBUilder.customJoin("LEFT JOIN " + familyTableName + " ON  " + tableName + ".relational_id =  " + familyTableName + ".id");
        queryBUilder.customJoin("LEFT JOIN " + familyMemberTableName + " ON  " + tableName + ".relational_id =  " + familyTableName + ".id");


        String query=queryBUilder.mainCondition(mainCondition);
//      String query=" Select ec_child.id as _id , ec_child.relational_id as relationalid , \n" +
//              "ec_child.last_interacted_with , ec_child.base_entity_id , ec_child.first_name , ec_family_member.first_name as family_first_name , ec_family_member.last_name as family_last_name , \n" +
//              "ec_family.village_town as family_home_address , \n" +
//              "ec_child.last_name , ec_child.unique_id , ec_child.gender , ec_child.dob FROM ec_child,ec_family_member,ec_family \n" +
//              "where  ec_child.relational_id =  ec_family.id group by ec_child.base_entity_id ORDER BY ec_child.last_interacted_with DESC   LIMIT 0,20";

        return query;
    }
    public static String mainSelect(String tableName,String familyTableName,String familyMemberTableName, String mainCondition) {

       String  query=mainSelectRegisterWithoutGroupby(tableName,familyTableName,familyMemberTableName,tableName+"."+DBConstants.KEY.BASE_ENTITY_ID+" = '"+mainCondition+"'");


        return query;
    }

    private static String[] mainColumns(String tableName,String familyTable,String familyMemberTable) {

        String[] columns = new String[]{
                tableName + "." + DBConstants.KEY.RELATIONAL_ID +" as " +ChildDBConstants.KEY.RELATIONAL_ID,
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
    public static ChildVisit getChildVisitStatus(CommonRepository commonRepository,String baseEntityId){
        //TODO need to get the childvisit from database
        ChildVisit childVisit=new ChildVisit();
        //testing data
        childVisit.setLastVisitTime(1545867630000L);
        childVisit.setServiceName("Penta1");
        childVisit.setServiceDate("3 oct");
        childVisit.setServiceStatus(ChildProfileInteractor.ServiceType.UPCOMING.name());

        long diff=System.currentTimeMillis()-childVisit.getLastVisitTime();
        if(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1){
            childVisit.setVisitStatus(ChildProfileInteractor.VisitType.DUE.name());
            if(childVisit.getLastVisitTime()!=0){
                if(diff<MILLI_SEC){
                    childVisit.setLastVisitDays("less 24 hrs");
                }else{
                    childVisit.setLastVisitDays(diff/MILLI_SEC+" days");
                }
            }
            return childVisit;
        }
        if(childVisit.getLastVisitTime()==0){
            childVisit.setVisitStatus(ChildProfileInteractor.VisitType.OVERDUE.name());
            return childVisit;
        }

        if(diff<MILLI_SEC){
            childVisit.setLastVisitDays("less 24 hrs");
            childVisit.setVisitStatus(ChildProfileInteractor.VisitType.LESS_TWENTY_FOUR.name());
            childVisit.setLastVisitMonth(theMonth(Calendar.getInstance().get(Calendar.MONTH)));
            return childVisit;
        }
        else {
            childVisit.setLastVisitDays(diff/MILLI_SEC+" days");
            childVisit.setVisitStatus(ChildProfileInteractor.VisitType.OVER_TWENTY_FOUR.name());
            return childVisit;
        }
    }
    public static String theMonth(int month){
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return monthNames[month];
    }
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(text);
        }
    }
}
