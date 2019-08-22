package org.smartregister.chw.core.dao;

import org.smartregister.domain.Alert;
import org.smartregister.repository.AlertRepository;

import java.util.List;

import static org.smartregister.domain.AlertStatus.from;

public class AlertDao extends AbstractDao {

    public static List<Alert> getActiveAlerts(String baseEntityID) {
        String sql = "select (case when status = 'urgent' then 1 else 2 end) state , * from alerts " +
                " where caseID = '" + baseEntityID + "'and status in ('normal','urgent') " +
                " order by state asc , startDate asc ";

        DataMap<Alert> dataMap = cursor -> new Alert(cursor.getString(cursor.getColumnIndex(AlertRepository.ALERTS_CASEID_COLUMN)),
                cursor.getString(cursor.getColumnIndex(AlertRepository.ALERTS_SCHEDULE_NAME_COLUMN)),
                cursor.getString(cursor.getColumnIndex(AlertRepository.ALERTS_VISIT_CODE_COLUMN)),
                from(cursor.getString(cursor.getColumnIndex(AlertRepository.ALERTS_STATUS_COLUMN))),
                cursor.getString(cursor.getColumnIndex(AlertRepository.ALERTS_STARTDATE_COLUMN)),
                cursor.getString(cursor.getColumnIndex(AlertRepository.ALERTS_EXPIRYDATE_COLUMN)),
                cursor.getInt(cursor.getColumnIndex(AlertRepository.ALERTS_OFFLINE_COLUMN)) == 1)
                .withCompletionDate(
                        cursor.getString(cursor.getColumnIndex(AlertRepository.ALERTS_COMPLETIONDATE_COLUMN)));

        return AbstractDao.readData(sql, dataMap);
    }

}
