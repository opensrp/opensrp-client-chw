package org.smartgresiter.wcaro.interactor;

import android.database.Cursor;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.contract.FamilyRemoveMemberContract;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import java.util.HashMap;

public class FamilyRemoveMemberInteractor implements FamilyRemoveMemberContract.Interactor {

    private static String TAG = FamilyRemoveMemberInteractor.class.getCanonicalName();

    private AppExecutors appExecutors;

    private static FamilyRemoveMemberInteractor instance;

    public static FamilyRemoveMemberInteractor getInstance() {
        if (instance == null)
            instance = new FamilyRemoveMemberInteractor();

        return instance;
    }

    @VisibleForTesting
    FamilyRemoveMemberInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public FamilyRemoveMemberInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void removeMember(String familyID, String lastLocationId, JSONObject exitForm, final FamilyRemoveMemberContract.Presenter presenter) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                // process the json object

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        presenter.memberRemoved();
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);

    }

    @Override
    public void removeFamily(String familyID, String lastLocationId, final FamilyRemoveMemberContract.Presenter presenter) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Boolean success = false;
                // close all members

                // close family

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        presenter.onFamilyRemoved(success);
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void processFamilyMember(final String familyID, final CommonPersonObjectClient client, final FamilyRemoveMemberContract.Presenter presenter) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final HashMap<String, String> res = new HashMap<>();
                String info_columns = Constants.RELATIONSHIP.PRIMARY_CAREGIVER + " , " +
                        Constants.RELATIONSHIP.FAMILY_HEAD;

                String sql = String.format("select %s from %s where %s = '%s' ",
                        info_columns,
                        Utils.metadata().familyRegister.tableName,
                        DBConstants.KEY.BASE_ENTITY_ID,
                        familyID
                );

                CommonRepository commonRepository = Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName);

                Cursor cursor = commonRepository.queryTable(sql);
                try {
                    cursor.moveToFirst();

                    while (!cursor.isAfterLast()) {
                        int columncount = cursor.getColumnCount();

                        for (int i = 0; i < columncount; i++) {
                            res.put(cursor.getColumnName(i), String.valueOf(cursor.getString(i)));
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
                        presenter.processMember(res, client);
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getFamilyChildrenCount(final String familyID, final FamilyRemoveMemberContract.InteractorCallback<HashMap<String, Integer>> callback) {

        Runnable runnable = null;
        try {
            runnable = new Runnable() {

                Integer kids = getCount(Constants.TABLE_NAME.CHILD, familyID);
                Integer members = getCount(Constants.TABLE_NAME.FAMILY_MEMBER, familyID);

                @Override
                public void run() {
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {

                            HashMap<String, Integer> results = new HashMap<>();
                            results.put(Constants.TABLE_NAME.CHILD, kids);
                            results.put(Constants.TABLE_NAME.FAMILY_MEMBER, members);
                            callback.onResult(results);
                        }
                    });
                }

            };
        } catch (final Exception e) {
            e.printStackTrace();

            runnable = new Runnable() {

                @Override
                public void run() {
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }

            };
        }

        appExecutors.diskIO().execute(runnable);

    }

    private int getCount(String tableName, String familyID) throws Exception {

        Integer count = null;
        Cursor c = null;
        String mainCondition = String.format(" %s = '%s'", DBConstants.KEY.RELATIONAL_ID, familyID);
        try {

            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();
            String query = sqb.queryForCountOnRegisters(tableName, mainCondition);
            query = sqb.Endquery(query);
            Log.i(getClass().getName(), "2" + query);
            c = commonRepository(tableName).rawCustomQueryForAdapter(query);
            if (c.moveToFirst()) {
                count = c.getInt(0);
            } else {
                count = 0;
            }

        } finally {
            if (c != null) {
                c.close();
            }
        }

        return count;
    }

    private boolean isValidFilterForFts(CommonRepository commonRepository, String filters) {
        return commonRepository.isFts() && filters != null && !StringUtils
                .containsIgnoreCase(filters, "like") && !StringUtils
                .startsWithIgnoreCase(filters.trim(), "and ");
    }

    private CommonRepository commonRepository(String tableName) {
        return WcaroApplication.getInstance().getContext().commonrepository(tableName);
    }

}
