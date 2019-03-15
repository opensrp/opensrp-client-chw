package org.smartgresiter.wcaro.interactor;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.util.Pair;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.contract.FamilyChangeContract;
import org.smartgresiter.wcaro.domain.FamilyMember;
import org.smartgresiter.wcaro.util.Constants;
import org.smartgresiter.wcaro.util.JsonFormUtils;
import org.smartgresiter.wcaro.util.Utils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.repository.BaseRepository;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class FamilyChangeContractInteractor implements FamilyChangeContract.Interactor {

    private static String TAG = FamilyChangeContractInteractor.class.getCanonicalName();

    private AppExecutors appExecutors;

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

                final Triple<List<FamilyMember>, String, String> family = processFamily(familyID);
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

                final Triple<List<FamilyMember>, String, String> family = processFamily(familyID);
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
    public void updateFamilyMember(final Context context, final Pair<String, FamilyMember> familyMember, final String familyID, final String lastLocationId, final FamilyChangeContract.Presenter presenter) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                String option =  familyMember.first; // familyMember.get(Constants.PROFILE_CHANGE_ACTION.ACTION_TYPE);

                final FamilyMember member = familyMember.second;
                member.setFamilyID(familyID);
                member.setPrimaryCareGiver(Constants.PROFILE_CHANGE_ACTION.PRIMARY_CARE_GIVER.equals(option));
                member.setFamilyHead(Constants.PROFILE_CHANGE_ACTION.HEAD_OF_FAMILY.equals(option));

                // update the EC client model
                try {
                    updateFamilyRelations(context, member, lastLocationId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {

                        if (member.getPrimaryCareGiver()) {
                            presenter.saveCompleted(null, member.getMemberID());
                        } else {
                            presenter.saveCompleted(member.getMemberID(), null);
                        }
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);

    }

    public void updateFamilyRelations(Context context, FamilyMember familyMember, String lastLocationId) throws Exception {

        // update the ec model
        ECSyncHelper syncHelper = WcaroApplication.getInstance().getEcSyncHelper();

        // update family record
        Pair<List<Client>, List<Event>> clientEventPair = JsonFormUtils.processFamilyUpdateRelations(context, familyMember, lastLocationId);

        if (clientEventPair != null && clientEventPair.first != null) {
            for (Client c : clientEventPair.first) {
                // merge and add client
                JsonFormUtils.mergeAndSaveClient(syncHelper, c);
            }
        }

        if (clientEventPair != null && clientEventPair.second != null) {
            for (Event event : clientEventPair.second) {
                // add events
                syncHelper.addEvent(event.getBaseEntityId(), new JSONObject(org.smartregister.family.util.JsonFormUtils.gson.toJson(event)));
            }
        }

        // call processor
        long lastSyncTimeStamp = Utils.context().allSharedPreferences().fetchLastUpdatedAtDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);
        FamilyLibrary.getInstance().getClientProcessorForJava().processClient(syncHelper.getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
        Utils.context().allSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
    }

    private Triple<List<FamilyMember>, String, String> processFamily(String familyID) {
        Triple<List<FamilyMember>, String, String> res;


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

    private List<FamilyMember> getFamilyMembers(String familyID) {

        String info_columns = DBConstants.KEY.RELATIONAL_ID + " , " +
                DBConstants.KEY.BASE_ENTITY_ID + " , " +
                DBConstants.KEY.FIRST_NAME + " , " +
                DBConstants.KEY.MIDDLE_NAME + " , " +
                DBConstants.KEY.LAST_NAME + " , " +
                DBConstants.KEY.PHONE_NUMBER + " , " +
                DBConstants.KEY.OTHER_PHONE_NUMBER + " , " +
                DBConstants.KEY.HIGHEST_EDU_LEVEL + " , " +
                DBConstants.KEY.DOB + " , " +
                DBConstants.KEY.DOD + " , " +
                DBConstants.KEY.GENDER;

        String sql = String.format("select %s from %s where %s = '%s' and %s is null and %s is null ",
                info_columns,
                Utils.metadata().familyMemberRegister.tableName,
                DBConstants.KEY.RELATIONAL_ID,
                familyID,
                DBConstants.KEY.DATE_REMOVED,
                DBConstants.KEY.DOD
        );

        CommonRepository commonRepository = Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName);
        List<FamilyMember> res = new ArrayList<>();

        Cursor cursor = commonRepository.queryTable(sql);
        try {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                int columncount = cursor.getColumnCount();
                FamilyMember columns = new FamilyMember();
                Date dob = null;
                for (int i = 0; i < columncount; i++) {

                    String value = null;
                    if(!cursor.isNull(i)){
                        value = String.valueOf(cursor.getString(i));
                    }

                    switch (cursor.getColumnName(i)){
                        case DBConstants.KEY.RELATIONAL_ID:
                            columns.setFamilyID(value);
                            break;
                        case DBConstants.KEY.BASE_ENTITY_ID:
                            columns.setMemberID(value);
                            break;
                        case DBConstants.KEY.FIRST_NAME:
                            columns.setFirstName(value);
                            break;
                        case DBConstants.KEY.MIDDLE_NAME:
                            columns.setMiddleName(value);
                            break;
                        case DBConstants.KEY.LAST_NAME:
                            columns.setLastName(value);
                            break;
                        case DBConstants.KEY.PHONE_NUMBER:
                            columns.setPhone(value);
                            break;
                        case DBConstants.KEY.OTHER_PHONE_NUMBER:
                            columns.setOtherPhone(value);
                            break;
                        case DBConstants.KEY.HIGHEST_EDU_LEVEL:
                            columns.setEduLevel(value);
                            break;
                        case DBConstants.KEY.DOB:
                            columns.setDob(value);
                            dob = Utils.dobStringToDate(String.valueOf(cursor.getString(i)));
                            break;
                        case DBConstants.KEY.DOD:
                            columns.setDod(value);
                            break;
                        case DBConstants.KEY.GENDER:
                            columns.setGender(value);
                            break;
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