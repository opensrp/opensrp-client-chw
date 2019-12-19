package org.smartregister.chw.interactor;

import android.content.Context;

import org.joda.time.LocalDate;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.core.interactor.CoreFamilyPlanningProfileInteractor;
import org.smartregister.chw.dao.FamilyDao;
import org.smartregister.chw.fp.contract.BaseFpProfileContract;
import org.smartregister.chw.fp.dao.FpDao;
import org.smartregister.chw.fp.domain.FpMemberObject;
import org.smartregister.dao.AbstractDao;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class FamilyPlanningProfileInteractor extends CoreFamilyPlanningProfileInteractor {
    private Context context;

    public FamilyPlanningProfileInteractor(Context context) {
        this.context = context;
    }

    @Override
    public void updateProfileFpStatusInfo(final FpMemberObject memberObject, final BaseFpProfileContract.InteractorCallback callback) {
        Runnable runnable = new Runnable() {

            Date lastVisitDate = getLastVisitDate(memberObject); // CoreFamilyPlanningProfileInteractor#getLastVisitDate
            AlertStatus familyAlert = FamilyDao.getFamilyAlertStatus(memberObject.getFamilyBaseEntityId());
            Alert upcomingService = getAlerts(context, memberObject.getBaseEntityId());

            @Override
            public void run() {
                appExecutors.mainThread().execute(() -> {
                    callback.refreshFamilyStatus(familyAlert);
                    callback.refreshLastVisit(lastVisitDate);
                    if (upcomingService == null) {
                        callback.refreshUpComingServicesStatus("", AlertStatus.complete, new Date());
                    } else {
                        callback.refreshUpComingServicesStatus(upcomingService.scheduleName(), upcomingService.status(), new LocalDate(upcomingService.startDate()).toDate());
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    private MemberObject toMember(FpMemberObject memberObject) {
        MemberObject res = new MemberObject();
        res.setBaseEntityId(memberObject.getBaseEntityId());
        res.setFirstName(memberObject.getFirstName());
        res.setLastName(memberObject.getLastName());
        res.setMiddleName(memberObject.getMiddleName());
        res.setDob(memberObject.getAge());
        return res;
    }

    private Alert getAlerts(Context context, String baseEntityID) {
        try {
            List<BaseUpcomingService> baseUpcomingServices = new ArrayList<>(new FpUpcomingServicesInteractor().getMemberServices(context, toMember(FpDao.getMember(baseEntityID))));
            if (baseUpcomingServices.size() > 0) {
                Comparator<BaseUpcomingService> comparator = (o1, o2) -> o1.getServiceDate().compareTo(o2.getServiceDate());
                Collections.sort(baseUpcomingServices, comparator);

                BaseUpcomingService baseUpcomingService = baseUpcomingServices.get(0);
                return new Alert(
                        baseEntityID,
                        baseUpcomingService.getServiceName(),
                        baseUpcomingService.getServiceName(),
                        baseUpcomingService.getServiceDate().before(new Date()) ? AlertStatus.urgent : AlertStatus.normal,
                        AbstractDao.getDobDateFormat().format(baseUpcomingService.getServiceDate()),
                        "",
                        true
                );
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return null;
    }
}
