package org.smartregister.chw.interactor;

import android.content.Context;

import org.joda.time.LocalDate;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.core.dao.PNCDao;
import org.smartregister.chw.core.dao.VisitDao;
import org.smartregister.chw.core.interactor.CoreFamilyPlanningProfileInteractor;
import org.smartregister.chw.dao.FamilyDao;
import org.smartregister.chw.fp.contract.BaseFpProfileContract;
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

            Date lastVisitDate = getLastVisitDate(memberObject);
            AlertStatus familyAlert = FamilyDao.getFamilyAlertStatus(memberObject.getBaseEntityId());
            Alert upcomingService = getAlerts(context, memberObject.getBaseEntityId());

            @Override
            public void run() {
                appExecutors.mainThread().execute(() -> {
                    callback.refreshLastVisit(lastVisitDate);
                    callback.refreshFamilyStatus(familyAlert);
                    callback.refreshMedicalHistory(VisitDao.memberHasVisits(memberObject.getBaseEntityId()));
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

    private Alert getAlerts(Context context, String baseEntityID) {
        List<BaseUpcomingService> baseUpcomingServices = new ArrayList<>();
        MemberObject memberObject = PNCDao.getMember(baseEntityID);
        try {
            baseUpcomingServices.addAll(new PncUpcomingServicesInteractorFlv().getMemberServices(context, memberObject));
            baseUpcomingServices.addAll(new AncUpcomingServicesInteractorFlv().getMemberServices(context, memberObject));
            //TODO add upcoming services for malaria, child & family planning
            if (baseUpcomingServices.size() > 0) {
                Comparator<BaseUpcomingService> comparator = (o1, o2) -> o1.getServiceDate().compareTo(o2.getServiceDate());
                Collections.sort(baseUpcomingServices, comparator);

                BaseUpcomingService baseUpcomingService = baseUpcomingServices.get(0);
                return new Alert(
                        memberObject.getBaseEntityId(),
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

    private Date getLastVisitDate(FpMemberObject memberObject) {
        Date lastVisitDate = null;
        Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), "FP Home Visit");
        if (lastVisit != null) {
            lastVisitDate = lastVisit.getDate();
        }

        return lastVisitDate;
    }
}
