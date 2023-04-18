package org.smartregister.chw.interactor;

import android.content.Context;

import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.core.interactor.CoreHivProfileInteractor;
import org.smartregister.chw.hiv.contract.BaseHivProfileContract;
import org.smartregister.chw.hiv.dao.HivDao;
import org.smartregister.chw.hiv.domain.HivMemberObject;
import org.smartregister.dao.AbstractDao;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class CbhsProfileInteractor extends CoreHivProfileInteractor {
    public CbhsProfileInteractor(Context context) {
        super(context);
    }

    @Override
    protected Alert getAlerts(Context context, String baseEntityID) {
        try {
            List<BaseUpcomingService> baseUpcomingServices = new ArrayList<>(new CbhsUpcomingServicesInteractor().getMemberServices(context, toMember(HivDao.getMember(baseEntityID))));
            if (baseUpcomingServices.size() > 0) {
                Comparator<BaseUpcomingService> comparator = (o1, o2) -> o1.getServiceDate().compareTo(o2.getServiceDate());
                Collections.sort(baseUpcomingServices, comparator);

                BaseUpcomingService baseUpcomingService = baseUpcomingServices.get(0);
                Date serviceDate = baseUpcomingService.getServiceDate();
                String serviceName = baseUpcomingService.getServiceName();
                AlertStatus upcomingServiceAlertStatus = serviceDate != null && serviceDate.before(new Date()) ? AlertStatus.urgent : AlertStatus.normal;
                String formattedServiceDate = serviceDate != null ? AbstractDao.getDobDateFormat().format(serviceDate) : null;
                return new Alert(baseEntityID, serviceName, serviceName, upcomingServiceAlertStatus, formattedServiceDate, "", true);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    @Override
    public void updateProfileHivStatusInfo(HivMemberObject memberObject, BaseHivProfileContract.InteractorCallback callback) {
        super.updateProfileHivStatusInfo(memberObject, callback);
    }
}
