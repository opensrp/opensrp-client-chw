package org.smartregister.brac.hnpp.utils;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.common.collect.ImmutableMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.brac.hnpp.BuildConfig;
import org.smartregister.brac.hnpp.R;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HnppConstants extends CoreConstants {
    public static final String TEST_GU_ID = "test";
    public static final String MODULE_ID_TRAINING = "TRAINING";
    public static final int MEMBER_ID_SUFFIX = 11;
    public static final int HOUSE_HOLD_ID_SUFFIX = 9;
    public static SimpleDateFormat DDMMYY = new SimpleDateFormat("dd-MM-yyyy");
    public class HOME_VISIT_FORMS {
        public static final String  ANC_CARD_FORM = "anc_card_form";
        public static final String  IMMUNIZATION = "hv_immunization";
        public static final String  DANGER_SIGNS = "anc_hv_danger_signs";

        public static final String  ANC1_FORM = "hnpp_anc1_registration";
        public static final String  ANC2_FORM = "hnpp_anc2_registration";
        public static final String  ANC3_FORM = "hnpp_anc3_registration";
        public static final String  GENERAL_DISEASE = "hnpp_anc_general_disease";
        public static final String  PREGNANCY_HISTORY = "hnpp_anc_pregnancy_history";
        public static final String  MEMBER_REFERRAL = "hnpp_member_referral";

    }
    public class EVENT_TYPE{

        public static final String MEMBER_REFERRAL = "Member Referral";
    }
    public class OTHER_SERVICE_TYPE{
        public static final int TYPE_WOMEN_PACKAGE = 1;
        public static final int TYPE_GIRL_PACKAGE = 2;
        public static final int TYPE_NCD = 3;
        public static final int TYPE_IYCF = 4;
    }

    public static boolean isExistSpecialCharacter(String filters){
        if(!TextUtils.isEmpty(filters) && filters.contains("/")){
            return true;
        }
        return false;
    }

    public static void updateAppBackground(View view){
        if(!isReleaseBuild()){
            view.setBackgroundColor(Color.parseColor("#B53737"));
        }
    }
    public static void updateAppBackgroundOnResume(View view){
        if(!isReleaseBuild()){
            view.setBackgroundColor(Color.parseColor("#B53737"));
        }else{
            view.setBackgroundColor(Color.parseColor("#F6F6F6"));
        }
    }

    public static ArrayList<String> getClasterSpinnerArray() {

        return new ArrayList<>(getClasterNames().keySet());
    }
    public static String getClusterNameFromValue(String value){
        HashMap<String, String> keys = getClasterNames();
        for (String key: keys.keySet()){
            if(keys.get(key).equalsIgnoreCase(value)){
                return key;
            }
        }
        return "";
    }

    public static HashMap<String, String> getClasterNames() {
        LinkedHashMap<String,String> clusterArray = new LinkedHashMap<>();
        clusterArray.put("ক্লাস্টার ১", "1st_Cluster");
        clusterArray.put("ক্লাস্টার ২", "2nd_Cluster");
        clusterArray.put("ক্লাস্টার ৩", "3rd_Cluster");
        clusterArray.put("ক্লাস্টার ৪", "4th_Cluster");
        return clusterArray;
    }

    public static final class DrawerMenu {
        public static final String ELCO_CLIENT = "Elco Clients";
        public static final String ALL_MEMBER = "All member";
    }

    public static final class FORM_KEY {
        public static final String SS_INDEX = "ss_index";
        public static final String VILLAGE_INDEX = "village_index";
    }
    public static String getGender(String value){
        if(value.equalsIgnoreCase("F")){
            return "মহিলা";
        }
        if(value.equalsIgnoreCase("M")){
            return "পুরুষ";
        }
        return value;
    }

    public static String getTotalCountBn(int count){
        char[] bn_numbers = "০১২৩৪৫৬৭৮৯".toCharArray();
        String c = String.valueOf(count);
        String number_to_return = "";
        for(char ch: c.toCharArray()){

            number_to_return+=bn_numbers[Integer.valueOf(ch)%Integer.valueOf('0')];
        }
        return number_to_return;
    }

    public static boolean isReleaseBuild(){
        AllSharedPreferences preferences = Utils.getAllSharedPreferences();
        String usingUrl = preferences.getPreference(AllConstants.DRISHTI_BASE_URL);
        if(!TextUtils.isEmpty(usingUrl) && (usingUrl.equalsIgnoreCase(BuildConfig.opensrp_url_release)
                || usingUrl.equalsIgnoreCase(BuildConfig.opensrp_url_release_without_slash)
        || usingUrl.equalsIgnoreCase(BuildConfig.opensrp_url_release_with_https))){
            return true;
        }
        return false;
    }
    public static String getSimPrintsProjectId(){

        return isReleaseBuild()?BuildConfig.SIMPRINT_PROJECT_ID_RELEASE:BuildConfig.SIMPRINT_PROJECT_ID_TRAINING;
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
        public static final String SERIAL_NO = "serial_no";
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

    public static String getRelationWithHouseholdHead(String value){
        String relationshipObject = "{" +
                "  \"খানা প্রধান\": \"Household Head\"," +
                "  \"মা/আম্মা\": \"Mother\"," +
                "  \"বাবা/আব্বা\": \"Father\"," +
                "  \"ছেলে\": \"Son\"," +
                "  \"মেয়ে\": \"Daughter\"," +
                "  \"স্ত্রী\": \"Wife\"," +
                "  \"স্বামী\": \"Husband\"," +
                "  \"নাতি\": \"Grandson\"," +
                "  \"নাতনী\": \"GrandDaughter\"," +
                "  \"ছেলের বউ\": \"SonsWife\"," +
                "  \"মেয়ের স্বামী\": \"DaughtersHusband\"," +
                "  \"শ্বশুর\": \"Father in law\"," +
                "  \"শাশুড়ি\": \"Mother in law\"," +
                "  \"দাদা\": \"Grandpa\"," +
                "  \"দাদি\": \"Grandma\"," +
                "  \"নানা\": \"Grandfather\"," +
                "  \"নানী\": \"Grandmother\"," +
                "  \"অন্যান্য\": \"Others\"" +
                "}";
        return getKeyByValue(relationshipObject,value);
    }
    public static final Map<String,Integer> iconMapping = ImmutableMap.<String,Integer> builder()
            .put("গর্ভবতী পরিচর্যা-১ম ত্রিমাসিক",R.mipmap.ic_anc_pink)
            .put("গর্ভবতী পরিচর্যা - ২য় ত্রিমাসিক",R.mipmap.ic_anc_pink)
            .put("গর্ভবতী পরিচর্যা - ৩য় ত্রিমাসিক",R.mipmap.ic_anc_pink)
            .put("শারীরিক সমস্যা",R.mipmap.ic_anc_pink)
            .put( "পূর্বের গর্ভের ইতিহাস",R.mipmap.ic_anc_pink)
            .put(HnppConstants.EventType.FAMILY_REGISTRATION,R.drawable.ic_home)
            .put(HnppConstants.EventType.FAMILY_MEMBER_REGISTRATION,R.drawable.rowavatar_member)
            .put(HnppConstants.EventType.UPDATE_FAMILY_MEMBER_REGISTRATION,R.drawable.rowavatar_member)
            .put(HnppConstants.EventType.CHILD_REGISTRATION,R.drawable.rowavatar_child)
            .build();
    public static final Map<String,String> visitTypeMapping = ImmutableMap.<String,String> builder()
            .put("ANC1 Registration","গর্ভবতী পরিচর্যা - ১ম ত্রিমাসিক")
            .put("ANC2 Registration","গর্ভবতী পরিচর্যা - ২য় ত্রিমাসিক")
            .put("ANC3 Registration","গর্ভবতী পরিচর্যা - ৩য় ত্রিমাসিক")
            .put("ANC General Disease","শারীরিক সমস্যা")
            .put( "ANC Pregnancy History","পূর্বের গর্ভের ইতিহাস")
            .put(HnppConstants.HOME_VISIT_FORMS.ANC1_FORM,"গর্ভবতী পরিচর্যা")
            .put(HnppConstants.HOME_VISIT_FORMS.GENERAL_DISEASE,"শারীরিক সমস্যা")
            .put( HnppConstants.HOME_VISIT_FORMS.PREGNANCY_HISTORY,"পূর্বের গর্ভের ইতিহাস")
            .build();
    public static final Map<String,String> eventTypeMapping = ImmutableMap.<String,String> builder()
            .put(HnppConstants.EventType.FAMILY_REGISTRATION,"খানা নিবন্ধন")
            .put(HnppConstants.EventType.FAMILY_MEMBER_REGISTRATION,"সদস্য নিবন্ধন")
            .put(HnppConstants.EventType.UPDATE_FAMILY_MEMBER_REGISTRATION,"সদস্য আপডেট")
            .put(HnppConstants.EventType.CHILD_REGISTRATION,"শিশু নিবন্ধন")
            .put(HnppConstants.EVENT_TYPE.MEMBER_REFERRAL,"Member referral")
            .build();

    private static String getKeyByValue(String mapperObj, String value){
        try {
            JSONObject choiceObject = new JSONObject(mapperObj);
            for (int i = 0; i < choiceObject.names().length(); i++) {
                if (value.equalsIgnoreCase(choiceObject.getString(choiceObject.names().getString(i)))) {
                    value = choiceObject.names().getString(i);
                    return value;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }
}
