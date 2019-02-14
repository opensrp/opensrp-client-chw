package org.smartgresiter.wcaro.interactor;

import android.app.Activity;
import android.database.Cursor;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.contract.NavigationContract;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;

import java.util.Date;

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
    public String getUser() {
        return null;
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
        return WcaroApplication.getInstance().getContext().commonrepository(tableName);
    }

    private Context context(Activity activity) {
        return CoreLibrary.getInstance().context()
                .updateApplicationContext(activity.getApplicationContext());
    }


    private int getCount(String tableName) throws Exception {

        Integer count = null;
        Cursor c = null;
        String mainCondition = String.format(" %s is null ", DBConstants.KEY.DATE_REMOVED);
        try {

            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();
            if (isValidFilterForFts(commonRepository(tableName), "")) {
                String sql = sqb.countQueryFts(tableName, null, mainCondition, null);
                Log.i(getClass().getName(), "1" + sql);
                count = commonRepository(tableName).countSearchIds(sql);
            } else {
                String query = sqb.queryForCountOnRegisters(tableName, mainCondition);
                query = sqb.Endquery(query);
                Log.i(getClass().getName(), "2" + query);
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
    public void getFamilyCount(final NavigationContract.InteractorCallback<Integer> callback) {
        if (callback != null) {
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Integer finalCount = getCount(Constants.TABLE_NAME.FAMILY);
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
    public void getChildrenCount(final NavigationContract.InteractorCallback<Integer> callback) {
        if (callback != null) {
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Integer finalCount = getCount(Constants.TABLE_NAME.CHILD);
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
        try{
            res = new Date(getLastCheckTimeStamp());
        }catch (Exception e){

        }
        return res;
    }

    public Long getLastCheckTimeStamp() {
        return WcaroApplication.getInstance().getEcSyncHelper().getLastCheckTimeStamp();
    }
}
