package org.smartgresiter.wcaro.interactor;

import android.database.Cursor;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.apache.commons.lang3.tuple.Triple;
import org.smartgresiter.wcaro.contract.FamilyChangeContract;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

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
    public void updateFamilyMember(HashMap<String, String> familyMember, String familyID, FamilyChangeContract.Presenter presenter) {

        if (familyMember != null) {

            String option = familyMember.get(Constants.PROFILE_CHANGE_ACTION.ACTION_TYPE);
            String memberID = familyMember.get(DBConstants.KEY.BASE_ENTITY_ID);

            String phone = familyMember.get(Constants.JsonAssets.FAMILY_MEMBER.PHONE_NUMBER);
            String otherPhone = familyMember.get(Constants.JsonAssets.FAMILY_MEMBER.OTHER_PHONE_NUMBER);
            String eduLevel = familyMember.get(Constants.JsonAssets.FAMILY_MEMBER.HIGHEST_EDUCATION_LEVEL);

            // update family record

            // update the EC client model

            switch (option){
                case Constants.PROFILE_CHANGE_ACTION.PRIMARY_CARE_GIVER:

                    break;
                case Constants.PROFILE_CHANGE_ACTION.HEAD_OF_FAMILY:

                    break;
            }

        }
        presenter.saveCompleted();
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
                DBConstants.KEY.DOB + " , " +
                DBConstants.KEY.GENDER;

        String sql = String.format("select %s from %s where %s = '%s' ",
                info_columns,
                Utils.metadata().familyMemberRegister.tableName,
                DBConstants.KEY.RELATIONAL_ID,
                familyID
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
                if (getDiffYears(dob, new Date()) > 5) {
                    res.add(columns);
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