package org.smartregister.chw.interactor;

import android.content.Context;

import org.joda.time.LocalDate;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.core.dao.AncDao;
import org.smartregister.chw.core.dao.ChildDao;
import org.smartregister.chw.core.dao.PNCDao;
import org.smartregister.chw.core.dao.VisitDao;
import org.smartregister.chw.core.interactor.CoreMalariaProfileInteractor;
import org.smartregister.chw.malaria.contract.MalariaProfileContract;
import org.smartregister.chw.malaria.domain.MemberObject;
import org.smartregister.dao.AbstractDao;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class MalariaProfileInteractor extends CoreMalariaProfileInteractor {
    private Context context;

    public MalariaProfileInteractor(Context context) {
        this.context = context;
    }

    public void refreshProfileInfo(MemberObject memberObject, MalariaProfileContract.InteractorCallBack callback) {
        Runnable runnable = () -> {
            this.appExecutors.mainThread().execute(() -> {
                callback.refreshFamilyStatus(AlertStatus.normal);
                Alert alert = this.getAlerts(context, memberObject.getBaseEntityId());
                callback.refreshMedicalHistory(VisitDao.memberHasVisits(memberObject.getBaseEntityId()));
                if (alert != null) {
                    callback.refreshUpComingServicesStatus(alert.scheduleName(), alert.status(), (new LocalDate(alert.startDate())).toDate());
                }

            });
        };
        this.appExecutors.diskIO().execute(runnable);
    }

    private Alert getAlerts(Context context, String baseEntityID) {
        List<BaseUpcomingService> baseUpcomingServices = new ArrayList<>();
        org.smartregister.chw.anc.domain.MemberObject memberObject;

        if (PNCDao.isPNCMember(baseEntityID)) {
            memberObject = PNCDao.getMember(baseEntityID);
            baseUpcomingServices.addAll(new PncUpcomingServicesInteractorFlv().getMemberServices(context, memberObject));
        } else if (AncDao.isANCMember(baseEntityID)) {
            memberObject = AncDao.getMember(baseEntityID);
            baseUpcomingServices.addAll(new AncUpcomingServicesInteractorFlv().getMemberServices(context, memberObject));
        } else if (ChildDao.isChild(baseEntityID)) {
            memberObject = ChildDao.getMember(baseEntityID);
            baseUpcomingServices.addAll(new ChildUpcomingServicesInteractor().getMemberServices(context, memberObject));
        }

        try {
            // malaria follow up visit
            BaseUpcomingService followUP = new BaseUpcomingService();
            followUP.setServiceName(context.getString(R.string.follow_up_visit));
            followUP.setServiceDate(new Date());
            followUP.setOverDueDate(LocalDate.now().plusDays(2).toDate());

            baseUpcomingServices.add(followUP);
        } catch (Exception e) {
            Timber.e(e);
        }

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
        } else {
            return null;
        }
    }
}
