package org.smartregister.chw.core.interactor;

import org.joda.time.LocalDate;
import org.smartregister.chw.core.dao.AlertDao;
import org.smartregister.chw.core.dao.VisitDao;
import org.smartregister.chw.malaria.contract.MalariaProfileContract;
import org.smartregister.chw.malaria.domain.MemberObject;
import org.smartregister.chw.malaria.interactor.BaseMalariaProfileInteractor;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;

import java.util.List;

public class CoreMalariaProfileInteractor extends BaseMalariaProfileInteractor {

    @Override
    public void refreshProfileInfo(MemberObject memberObject, MalariaProfileContract.InteractorCallBack callback) {
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> {
            callback.refreshFamilyStatus(AlertStatus.normal);
            Alert alert = getLatestAlert(memberObject.getBaseEntityId());

            callback.refreshMedicalHistory(VisitDao.memberHasVisits(memberObject.getBaseEntityId()));
            if (alert != null)
                callback.refreshUpComingServicesStatus(alert.scheduleName(), alert.status(), new LocalDate(alert.startDate()).toDate());

        });
        appExecutors.diskIO().execute(runnable);
    }

    private Alert getLatestAlert(String baseEntityID) {
        List<Alert> alerts = AlertDao.getActiveAlertsForVaccines(baseEntityID);

        if (alerts.size() > 0)
            return alerts.get(0);

        return null;
    }

}
