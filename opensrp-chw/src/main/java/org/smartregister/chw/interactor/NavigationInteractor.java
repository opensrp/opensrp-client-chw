package org.smartregister.chw.interactor;

import android.database.Cursor;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.NavigationContract;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.chw.util.Constants;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;

import java.util.Date;

import timber.log.Timber;

public class NavigationInteractor implements NavigationContract.Interactor {

    private static NavigationInteractor instance;
    AppExecutors appExecutors = new AppExecutors();

    private NavigationInteractor() {

    }

    public static NavigationInteractor getInstance() {
        if (instance == null)
            instance = new NavigationInteractor();

        return instance;
    }

    @Override
    public Date getLastSync() {
        return null;
    }

    private boolean isValidFilterForFts(CommonRepository commonRepository, String filters) {
        return commonRepository.isFts() && filters != null && !StringUtils
                .containsIgnoreCase(filters, "like") && !StringUtils
                .startsWithIgnoreCase(filters.trim(), "and ");
    }

    private CommonRepository commonRepository(String tableName) {
        return ChwApplication.getInstance().getContext().commonrepository(tableName);
    }

    private int getCount(String tableName) {

        int count;
        Cursor c = null;
        String mainCondition;
        if (tableName.equalsIgnoreCase(Constants.TABLE_NAME.CHILD)) {
            mainCondition = String.format(" %s is null AND %s", DBConstants.KEY.DATE_REMOVED, ChildDBConstants.childAgeLimitFilter());
        } else if (tableName.equalsIgnoreCase(Constants.TABLE_NAME.FAMILY)) {
            mainCondition = String.format(" %s is null ", DBConstants.KEY.DATE_REMOVED);
        } else {
            mainCondition = " 1 = 1 ";
        }
        try {

            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();
            if (isValidFilterForFts(commonRepository(tableName), "")) {
                String sql = sqb.countQueryFts(tableName, null, mainCondition, null);
                Timber.i("1%s", sql);
                count = commonRepository(tableName).countSearchIds(sql);
            } else {
                String query = sqb.queryForCountOnRegisters(tableName, mainCondition);
                query = sqb.Endquery(query);
                Timber.i("2%s", query);
                c = commonRepository(tableName).rawCustomQueryForAdapter(query);
                if (c.moveToFirst()) {
                    count = c.getInt(0);
                } else {
                    count = 0;
                }
            }

        } finally {
            if (c != null) {
                c.close();
            }
        }


        return count;
    }

    @Override
    public void getRegisterCount(final String tableName, final NavigationContract.InteractorCallback<Integer> callback) {
        if (callback != null) {
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Integer finalCount = getCount(tableName);
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onResult(finalCount);
                            }
                        });
                    } catch (final Exception e) {
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(e);
                            }
                        });
                    }
                }
            });

        }
    }

    @Override
    public Date Sync() {
        Date res = null;
        try {
            res = new Date(getLastCheckTimeStamp());
        } catch (Exception e) {
            Timber.e(e.toString());
        }
        return res;
    }

    private Long getLastCheckTimeStamp() {
        return ChwApplication.getInstance().getEcSyncHelper().getLastCheckTimeStamp();
    }
}
