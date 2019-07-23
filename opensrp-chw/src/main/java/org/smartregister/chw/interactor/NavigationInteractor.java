package org.smartregister.chw.interactor;

import android.database.Cursor;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.NavigationContract;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.chw.util.ChwDBConstants;
import org.smartregister.chw.util.Constants;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;

import java.text.MessageFormat;
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
            mainCondition = String.format(" where %s is null AND %s", DBConstants.KEY.DATE_REMOVED, ChildDBConstants.childAgeLimitFilter());
        } else if (tableName.equalsIgnoreCase(Constants.TABLE_NAME.FAMILY)) {
            mainCondition = String.format(" where %s is null ", DBConstants.KEY.DATE_REMOVED);
        } else if (tableName.equalsIgnoreCase(Constants.TABLE_NAME.ANC_MEMBER)) {
            StringBuilder stb = new StringBuilder();

            stb.append(MessageFormat.format(" inner join {0} ", Constants.TABLE_NAME.FAMILY_MEMBER));
            stb.append(MessageFormat.format(" on {0}.{1} = {2}.{3} ", Constants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.BASE_ENTITY_ID,
                    Constants.TABLE_NAME.ANC_MEMBER, DBConstants.KEY.BASE_ENTITY_ID));

            stb.append(MessageFormat.format(" inner join {0} ", Constants.TABLE_NAME.FAMILY));
            stb.append(MessageFormat.format(" on {0}.{1} = {2}.{3} ", Constants.TABLE_NAME.FAMILY, DBConstants.KEY.BASE_ENTITY_ID,
                    Constants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.RELATIONAL_ID));

            stb.append(MessageFormat.format(" where {0}.{1} is null ", Constants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.DATE_REMOVED));
            stb.append(MessageFormat.format(" and {0}.{1} is 0 ", Constants.TABLE_NAME.ANC_MEMBER, org.smartregister.chw.anc.util.DBConstants.KEY.IS_CLOSED));


            mainCondition = stb.toString();
        }
        else if (tableName.equalsIgnoreCase(Constants.TABLE_NAME.MALARIA_CONFIRMATION)) {
            StringBuilder stb = new StringBuilder();

            stb.append(MessageFormat.format(" inner join {0} ", Constants.TABLE_NAME.FAMILY_MEMBER));
            stb.append(MessageFormat.format(" on {0}.{1} = {2}.{3} ", Constants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.BASE_ENTITY_ID,
                    Constants.TABLE_NAME.MALARIA_CONFIRMATION, DBConstants.KEY.BASE_ENTITY_ID));

            stb.append(MessageFormat.format(" inner join {0} ", Constants.TABLE_NAME.FAMILY));
            stb.append(MessageFormat.format(" on {0}.{1} = {2}.{3} ", Constants.TABLE_NAME.FAMILY, DBConstants.KEY.BASE_ENTITY_ID,
                    Constants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.RELATIONAL_ID));

            stb.append(MessageFormat.format(" where {0}.{1} is null ", Constants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.DATE_REMOVED));
            stb.append(MessageFormat.format(" and {0}.{1} = 1 ", Constants.TABLE_NAME.MALARIA_CONFIRMATION, org.smartregister.chw.malaria.util.DBConstants.KEY.MALARIA));

            mainCondition = stb.toString();
        }
        else if(tableName.equalsIgnoreCase(Constants.TABLE_NAME.ANC_PREGNANCY_OUTCOME)){
            mainCondition = String.format("where %s is 0", ChwDBConstants.IS_CLOSED);
        }else {
            mainCondition = " where 1 = 1 ";
        }
        try {

            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();
            String query = MessageFormat.format("select count(*) from {0} {1}", tableName, mainCondition);
            query = sqb.Endquery(query);
            Timber.i("2%s", query);
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
