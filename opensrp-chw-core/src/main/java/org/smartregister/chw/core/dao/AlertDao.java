package org.smartregister.chw.core.dao;

import org.smartregister.chw.core.domain.AlertState;
import org.smartregister.domain.Alert;
import org.smartregister.repository.AlertRepository;

import java.util.List;

import static org.smartregister.domain.AlertStatus.from;

public class AlertDao extends AbstractDao {

    public static List<Alert> getActiveAlerts(String baseEntityID) {
        String sql = "select (case when status = 'urgent' then 1 else 2 end) state , * from alerts " +
                " where caseID = '" + baseEntityID + "'and status in ('normal','urgent') and expiryDate > date() " +
                " order by state asc , startDate asc  , visitCode asc";

        DataMap<Alert> dataMap = cursor -> new Alert(
                cursor.getString(cursor.getColumnIndex(AlertRepository.ALERTS_CASEID_COLUMN)),
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

    public static void updateOfflineVaccineAlerts(String baseEntityID) {
        String sql = "select alerts.caseID , alerts.startDate , alerts.visitCode , " +
                " alerts.completionDate , vaccines.date , strftime('%Y-%m-%d', vaccines.date / 1000, 'unixepoch') dateGiven " +
                " from alerts " +
                " inner join vaccines on vaccines.base_entity_id = alerts.caseID and replace(vaccines.name,'_','') = alerts.visitCode " +
                " where alerts.caseID = '" + baseEntityID + "' and alerts.status not in ('complete','expired','inProcess') ";

        DataMap<AlertState> dataMap = c -> new AlertState(
                getCursorValue(c, "caseID"),
                getCursorValue(c, "startDate"),
                getCursorValue(c, "visitCode"),
                getCursorValue(c, "dateGiven")
        );

        List<AlertState> states = AbstractDao.readData(sql, dataMap);

        if (states == null || states.size() == 0)
            return;

        for (AlertState alertState : states) {
            String alertUpdate =
                    "update alerts set status = 'complete' , completionDate = '" +
                            alertState.getDateGiven() + "' , offline = 1 where caseID = '" + baseEntityID + "' and visitCode = '" + alertState.getVisitCode() + "'";

            updateDB(alertUpdate);
        }
    }

}
