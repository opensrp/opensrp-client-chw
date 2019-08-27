package org.smartregister.chw.core.interactor;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.VisibleForTesting;
import android.util.Pair;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.FamilyChangeContract;
import org.smartregister.chw.core.domain.FamilyMember;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.Utils;
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
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public abstract class CoreFamilyChangeContractInteractor implements FamilyChangeContract.Interactor {

    protected CoreChwApplication coreChwApplication;
    protected Flavor flavor;
    private AppExecutors appExecutors;

    public CoreFamilyChangeContractInteractor() {
        this(new AppExecutors());
    }

    @VisibleForTesting
    CoreFamilyChangeContractInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
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

                String option = familyMember.first; // familyMember.get(Constants.PROFILE_CHANGE_ACTION.ACTION_TYPE);

                final FamilyMember member = familyMember.second;
                member.setFamilyID(familyID);
                member.setPrimaryCareGiver(CoreConstants.PROFILE_CHANGE_ACTION.PRIMARY_CARE_GIVER.equals(option));
                member.setFamilyHead(CoreConstants.PROFILE_CHANGE_ACTION.HEAD_OF_FAMILY.equals(option));

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
        ECSyncHelper syncHelper = coreChwApplication.getEcSyncHelper();

        // update family record
        Pair<List<Client>, List<Event>> clientEventPair = CoreJsonFormUtils.processFamilyUpdateRelations(coreChwApplication, context, familyMember, lastLocationId);

        if (clientEventPair != null && clientEventPair.first != null) {
            for (Client c : clientEventPair.first) {
                // merge and add client
                CoreJsonFormUtils.mergeAndSaveClient(syncHelper, c);
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
        FamilyLibrary.getInstance().getClientProcessorForJava().processClient(syncHelper.getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
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
        String sql = String.format("select %s from %s where %s = '%s' and %s is null and %s is null ",
                flavor.getFamilyMembersSql(familyID),
                Utils.metadata().familyMemberRegister.tableName,
                DBConstants.KEY.RELATIONAL_ID,
                familyID,
                DBConstants.KEY.DATE_REMOVED,
                DBConstants.KEY.DOD
        );

        CommonRepository commonRepository = Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName);
        List<FamilyMember> res = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = commonRepository.queryTable(sql);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                int columncount = cursor.getColumnCount();
                FamilyMember columns = new FamilyMember();
                Date dob = null;
                for (int i = 0; i < columncount; i++) {

                    String value = null;
                    if (!cursor.isNull(i)) {
                        value = String.valueOf(cursor.getString(i));
                    }

                    switch (cursor.getColumnName(i)) {
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
                        default:
                            break;
                    }
                }

                // add if member is above 5 year
                if (dob != null && (getDiffYears(dob, new Date()) >= 5)) {
                    res.add(columns);
                }
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Timber.e(e, e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return res;
    }

    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    public void setFlavor(Flavor flavor) {
        this.flavor = flavor;
    }

    protected abstract void setCoreChwApplication();

    protected abstract void setFlavour();

    public interface Flavor {
        String getFamilyMembersSql(String familyID);
    }
}