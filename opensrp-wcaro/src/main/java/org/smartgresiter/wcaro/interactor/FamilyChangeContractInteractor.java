package org.smartgresiter.wcaro.interactor;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.contract.FamilyChangeContract;
import org.smartgresiter.wcaro.util.Constants;
import org.smartgresiter.wcaro.util.JsonFormUtils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.BaseRepository;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class FamilyChangeContractInteractor implements FamilyChangeContract.Interactor {

    private static String TAG = FamilyChangeContractInteractor.class.getCanonicalName();

    private AppExecutors appExecutors;

    HashMap<String, String> eduMap = null;

    @VisibleForTesting
    FamilyChangeContractInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public FamilyChangeContractInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void getAdultMembersExcludeHOF(final String familyID, final FamilyChangeContract.Presenter presenter) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final Triple<List<HashMap<String, String>>, String, String> family = processFamily(familyID);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        presenter.renderAdultMembersExcludeHOF(family.getLeft(), family.getMiddle(), family.getRight());
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getAdultMembersExcludePCG(final String familyID, final FamilyChangeContract.Presenter presenter) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final Triple<List<HashMap<String, String>>, String, String> family = processFamily(familyID);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        presenter.renderAdultMembersExcludePCG(family.getLeft(), family.getMiddle(), family.getRight());
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void updateFamilyMember(final Context context, final HashMap<String, String> familyMember, final String familyID, final String lastLocationId, final FamilyChangeContract.Presenter presenter) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {


                final String option = familyMember.get(Constants.PROFILE_CHANGE_ACTION.ACTION_TYPE);
                final String memberID = familyMember.get(DBConstants.KEY.BASE_ENTITY_ID);

                String phone = familyMember.get(Constants.JsonAssets.FAMILY_MEMBER.PHONE_NUMBER);
                String otherPhone = familyMember.get(Constants.JsonAssets.FAMILY_MEMBER.OTHER_PHONE_NUMBER);
                String eduLevel = familyMember.get(Constants.JsonAssets.FAMILY_MEMBER.HIGHEST_EDUCATION_LEVEL);

                // update the EC client model
                try {
                    save(context, familyID, memberID, phone, otherPhone, eduLevel, option, lastLocationId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {

                        switch (option) {
                            case Constants.PROFILE_CHANGE_ACTION.PRIMARY_CARE_GIVER:
                                presenter.saveCompleted(null, memberID);
                                break;
                            case Constants.PROFILE_CHANGE_ACTION.HEAD_OF_FAMILY:
                                presenter.saveCompleted(memberID, null);
                                break;
                        }
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);

    }

    private void save(Context context, String familyID, String memberID, String phone, String otherPhone, String eduLevel, String option, String lastLocationId) throws Exception {

        // update the ec model
        ECSyncHelper syncHelper = WcaroApplication.getInstance().getEcSyncHelper();
        // update family record

        Client familyClient = syncHelper.convert(syncHelper.getClient(familyID), Client.class);
        Map<String, List<String>> relationships = familyClient.getRelationships();
        switch (option) {
            case Constants.PROFILE_CHANGE_ACTION.PRIMARY_CARE_GIVER:

                relationships.put(Constants.RELATIONSHIP.PRIMARY_CAREGIVER, toStringList(memberID));
                familyClient.setRelationships(relationships);

                break;
            case Constants.PROFILE_CHANGE_ACTION.HEAD_OF_FAMILY:

                relationships.put(Constants.RELATIONSHIP.FAMILY_HEAD, toStringList(memberID));
                familyClient.setRelationships(relationships);

                break;
        }

        JSONObject metadata = FormUtils.getInstance(context)
                .getFormJson(Utils.metadata().familyRegister.formName)
                .getJSONObject(org.smartregister.family.util.JsonFormUtils.METADATA);

        metadata.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_LOCATION, lastLocationId);

        FormTag formTag = new FormTag();
        formTag.providerId = Utils.context().allSharedPreferences().fetchRegisteredANM();
        formTag.appVersion = FamilyLibrary.getInstance().getApplicationVersion();
        formTag.databaseVersion = FamilyLibrary.getInstance().getDatabaseVersion();

        Event eventFamily = JsonFormUtils.createEvent(new JSONArray(), metadata, formTag, familyID, Utils.metadata().familyRegister.updateEventType,
                Utils.metadata().familyRegister.tableName);

        Event eventMember = JsonFormUtils.createEvent(new JSONArray(), metadata, formTag, memberID, Utils.metadata().familyMemberRegister.updateEventType,
                Utils.metadata().familyMemberRegister.tableName);

        eventMember.addObs(new Obs("concept", "text", Constants.FORM_CONSTANTS.CHANGE_CARE_GIVER.PHONE_NUMBER.CODE, "",
                toList(phone), new ArrayList<>(), null, DBConstants.KEY.PHONE_NUMBER));

        eventMember.addObs(new Obs("concept", "text", Constants.FORM_CONSTANTS.CHANGE_CARE_GIVER.OTHER_PHONE_NUMBER.CODE, Constants.FORM_CONSTANTS.CHANGE_CARE_GIVER.OTHER_PHONE_NUMBER.PARENT_CODE,
                toList(otherPhone), new ArrayList<>(), null, DBConstants.KEY.OTHER_PHONE_NUMBER));

        eventMember.addObs(new Obs("concept", "text", Constants.FORM_CONSTANTS.CHANGE_CARE_GIVER.HIGHEST_EDU_LEVEL.CODE, "",
                toList(eduMap().get(eduLevel)), toList(eduLevel), null, DBConstants.KEY.HIGHEST_EDU_LEVEL));


        // merge and add client
        JsonFormUtils.mergeAndSaveClient(syncHelper, familyClient);

        // add events
        syncHelper.addEvent(eventFamily.getBaseEntityId(), new JSONObject(org.smartregister.family.util.JsonFormUtils.gson.toJson(eventFamily)));
        syncHelper.addEvent(eventMember.getBaseEntityId(), new JSONObject(org.smartregister.family.util.JsonFormUtils.gson.toJson(eventMember)));


        // call processor
        long lastSyncTimeStamp = Utils.context().allSharedPreferences().fetchLastUpdatedAtDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);
        FamilyLibrary.getInstance().getClientProcessorForJava().processClient(syncHelper.getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
        Utils.context().allSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());

    }

    private HashMap<String, String> eduMap() {
        if (eduMap == null) {
            eduMap = new HashMap<>();
            eduMap.put("None", "1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            eduMap.put("Primary", "1713AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            eduMap.put("Secondary", "1714AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            eduMap.put("Post-secondary", "159785AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        }
        return eduMap;
    }

    private List<Object> toList(String... vals) {
        List<Object> res = new ArrayList<>();
        for (String s : vals) {
            res.add(s);
        }
        return res;
    }

    private List<String> toStringList(String... vals) {
        List<String> res = new ArrayList<>();
        for (String s : vals) {
            res.add(s);
        }
        return res;
    }


    private Triple<List<HashMap<String, String>>, String, String> processFamily(String familyID) {
        Triple<List<HashMap<String, String>>, String, String> res;


        CommonRepository commonRepository = Utils.context().commonrepository(Utils.metadata().familyRegister.tableName);

        final CommonPersonObject personObject = commonRepository.findByBaseEntityId(familyID);
        final CommonPersonObjectClient client =
                new CommonPersonObjectClient(personObject.getCaseId(), personObject.getDetails(), "");
        client.setColumnmaps(personObject.getColumnmaps());

        res = Triple.of(
                getFamilyMembers(familyID),
                Utils.getValue(client.getColumnmaps(), DBConstants.KEY.PRIMARY_CAREGIVER, false),
                Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FAMILY_HEAD, false)
        );

        return res;
    }

    private List<HashMap<String, String>> getFamilyMembers(String familyID) {

        String info_columns = DBConstants.KEY.RELATIONAL_ID + " , " +
                DBConstants.KEY.BASE_ENTITY_ID + " , " +
                DBConstants.KEY.FIRST_NAME + " , " +
                DBConstants.KEY.MIDDLE_NAME + " , " +
                DBConstants.KEY.LAST_NAME + " , " +
                DBConstants.KEY.PHONE_NUMBER + " , " +
                DBConstants.KEY.OTHER_PHONE_NUMBER + " , " +
                DBConstants.KEY.HIGHEST_EDU_LEVEL + " , " +
                DBConstants.KEY.DOB + " , " +
                DBConstants.KEY.GENDER;

        String sql = String.format("select %s from %s where %s = '%s' and %s is null ",
                info_columns,
                Utils.metadata().familyMemberRegister.tableName,
                DBConstants.KEY.RELATIONAL_ID,
                familyID,
                DBConstants.KEY.DATE_REMOVED
        );

        CommonRepository commonRepository = Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName);
        List<HashMap<String, String>> res = new ArrayList<>();

        Cursor cursor = commonRepository.queryTable(sql);
        try {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                int columncount = cursor.getColumnCount();
                HashMap<String, String> columns = new HashMap<String, String>();
                Date dob = null;
                for (int i = 0; i < columncount; i++) {
                    columns.put(cursor.getColumnName(i), String.valueOf(cursor.getString(i)));
                    if (cursor.getColumnName(i).equals(DBConstants.KEY.DOB)) {
                        dob = Utils.dobStringToDate(String.valueOf(cursor.getString(i)));
                    }
                }
                // add if member is above 5 year
                if (dob != null) {
                    if (getDiffYears(dob, new Date()) >= 5) {
                        res.add(columns);
                    }
                }
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        } finally {
            cursor.close();
        }

        return res;
    }

    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }
}