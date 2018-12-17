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

import java.util.Date;

public class NavigationInteractor implements NavigationContract.Interactor {

    AppExecutors appExecutors = new AppExecutors();


    private static NavigationInteractor instance;

    public static NavigationInteractor getInstance() {
        if (instance == null)
            instance = new NavigationInteractor();

        return instance;
    }

    private NavigationInteractor() {

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

    private CommonRepository commonRepository() {
        return WcaroApplication.getInstance().getContext().commonrepository(Constants.TABLE_NAME.FAMILY);
    }

    private Context context(Activity activity) {
        return CoreLibrary.getInstance().context()
                .updateApplicationContext(activity.getApplicationContext());
    }


    private int getCount(String tableName) throws Exception {

        Integer count = null;
        Cursor c = null;
        try {

            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();
            if (isValidFilterForFts(commonRepository(), "")) {
                String sql = sqb.countQueryFts(tableName, null, null, null);
                Log.i(getClass().getName(), sql);
                count = commonRepository().countSearchIds(sql);
            } else {
                String query = sqb.queryForCountOnRegisters(tableName, null);
                query = sqb.Endquery(query);
                Log.i(getClass().getName(), query);
                c = commonRepository().rawCustomQueryForAdapter(query);
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
    public Date Sync() {
        return null;
    }

}
