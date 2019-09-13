package org.smartregister.chw.core.interactor;

import org.smartregister.chw.core.contract.CoreApplication;
import org.smartregister.chw.core.contract.NavigationContract;
import org.smartregister.chw.core.dao.NavigationDao;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.util.AppExecutors;

import java.util.Date;

import timber.log.Timber;

public class NavigationInteractor implements NavigationContract.Interactor {

    private static NavigationInteractor instance;
    private AppExecutors appExecutors = new AppExecutors();
    private CoreApplication coreApplication;

    private NavigationInteractor() {

    }

    public static NavigationInteractor getInstance() {
        if (instance == null) {
            instance = new NavigationInteractor();
        }

        return instance;
    }

    @Override
    public Date getLastSync() {
        return null;
    }

    @Override
    public void getRegisterCount(final String tableName,
                                 final NavigationContract.InteractorCallback<Integer> callback) {
        if (callback != null) {
            appExecutors.diskIO().execute(() -> {
                try {
                    final Integer finalCount = getCount(tableName);
                    appExecutors.mainThread().execute(() -> callback.onResult(finalCount));
                } catch (final Exception e) {
                    appExecutors.mainThread().execute(() -> callback.onError(e));
                }
            });

        }
    }

    private int getCount(String tableName) {
        switch (tableName.toLowerCase().trim()) {
            case CoreConstants.TABLE_NAME.CHILD:
                String sql_child = "select count(*) from ec_child c " +
                        "inner join ec_family_member m on c.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                        "inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                        "where m.date_removed is null and m.is_closed = 0 " +
                        "and ((( julianday('now') - julianday(c.dob))/365.25) < 5) and c.is_closed = 0 " +
                        "and (( ifnull(entry_point,'') <> 'PNC' ) or (ifnull(entry_point,'') = 'PNC' and date(c.dob, '+28 days') <= date())) ";
                return NavigationDao.getQueryCount(sql_child);

            case CoreConstants.TABLE_NAME.FAMILY:
                String sql_family = "select count(*) from ec_family where date_removed is null";
                return NavigationDao.getQueryCount(sql_family);

            case CoreConstants.TABLE_NAME.ANC_MEMBER:
                String sql_anc_member = "select count(*) " +
                        "from ec_anc_register r " +
                        "inner join ec_family_member m on r.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                        "inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                        "where m.date_removed is null and m.is_closed = 0 and r.is_closed = 0 ";
                return NavigationDao.getQueryCount(sql_anc_member);

            case CoreConstants.TABLE_NAME.TASK:
                String sql_task = "select count(*) from task inner join " +
                        "ec_family_member member on member.base_entity_id = task.for COLLATE NOCASE " +
                        "WHERE task.status =\"READY\" and member.date_removed is null ";
                return NavigationDao.getQueryCount(sql_task);

            case CoreConstants.TABLE_NAME.ANC_PREGNANCY_OUTCOME:

                String sql_pregnancy = "select count(*) " +
                        "from ec_pregnancy_outcome p " +
                        "inner join ec_family_member m on p.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                        "inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                        "where m.date_removed is null and p.delivery_date is not null and p.is_closed = 0 and m.is_closed = 0 ";
                return NavigationDao.getQueryCount(sql_pregnancy);

            case CoreConstants.TABLE_NAME.MALARIA_CONFIRMATION:

                String sql_malaria = "select count(*) " +
                        "from ec_malaria_confirmation p " +
                        "inner join ec_family_member m on p.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                        "inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE " +
                        "where m.date_removed is null and p.is_closed = 0 ";
                return NavigationDao.getQueryCount(sql_malaria);

            default:
                return NavigationDao.getTableCount(tableName);
        }
    }

    @Override
    public Date sync() {
        Date res = null;
        try {
            res = new Date(getLastCheckTimeStamp());
        } catch (Exception e) {
            Timber.e(e.toString());
        }
        return res;
    }

    @Override
    public void setApplication(CoreApplication coreApplication) {
        this.coreApplication = coreApplication;
    }

    private Long getLastCheckTimeStamp() {
        return coreApplication.getEcSyncHelper().getLastCheckTimeStamp();
    }
}