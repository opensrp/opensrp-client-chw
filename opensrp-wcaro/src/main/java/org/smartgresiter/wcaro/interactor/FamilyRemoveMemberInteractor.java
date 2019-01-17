package org.smartgresiter.wcaro.interactor;

import android.database.Cursor;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.smartgresiter.wcaro.contract.FamilyRemoveMemberContract;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import java.util.Date;
import java.util.HashMap;

public class FamilyRemoveMemberInteractor implements FamilyRemoveMemberContract.Interactor {

    private static String TAG = FamilyRemoveMemberInteractor.class.getCanonicalName();

    private AppExecutors appExecutors;

    @VisibleForTesting
    FamilyRemoveMemberInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public FamilyRemoveMemberInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void removeMember(CommonPersonObject client, String lastLocationId) {


    }

    @Override
    public void removeFamily(String familyID, String lastLocationId, FamilyRemoveMemberContract.Presenter presenter) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                // final Triple<List<HashMap<String, String>>, String, String> family = processFamily(familyID);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        // presenter.renderAdultMembersExcludePCG(family.getLeft(), family.getMiddle(), family.getRight());
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void processFamilyMember(final String familyID, final FamilyRemoveMemberContract.Presenter presenter) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final HashMap<String, String> res = new HashMap<>();
                String info_columns = Constants.RELATIONSHIP.PRIMARY_CAREGIVER + " , " +
                        Constants.RELATIONSHIP.FAMILY_HEAD;

                String sql = String.format("select %s from %s where %s = '%s' ",
                        info_columns,
                        Utils.metadata().familyRegister.tableName,
                        DBConstants.KEY.ID,
                        familyID
                );

                CommonRepository commonRepository = Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName);

                Cursor cursor = commonRepository.queryTable(sql);
                try {
                    cursor.moveToFirst();

                    while (!cursor.isAfterLast()) {
                        int columncount = cursor.getColumnCount();

                        Date dob = null;
                        for (int i = 0; i < columncount; i++) {
                            res.put(cursor.getColumnName(i), String.valueOf(cursor.getString(i)));
                            if (cursor.getColumnName(i).equals(DBConstants.KEY.DOB)) {
                                dob = Utils.dobStringToDate(String.valueOf(cursor.getString(i)));
                            }
                        }

                        cursor.moveToNext();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString(), e);
                } finally {
                    cursor.close();
                }

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        presenter.processMember(res);
                    }
                });
            }
        };
    }
}
