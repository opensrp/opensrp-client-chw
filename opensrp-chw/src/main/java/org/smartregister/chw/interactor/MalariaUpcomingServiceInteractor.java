package org.smartregister.chw.interactor;

import android.content.Context;

import org.joda.time.LocalDate;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.interactor.BaseAncUpcomingServicesInteractor;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.core.dao.AncDao;
import org.smartregister.chw.core.dao.ChildDao;
import org.smartregister.chw.core.dao.PNCDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MalariaUpcomingServiceInteractor extends BaseAncUpcomingServicesInteractor {

    @Override
    protected List<BaseUpcomingService> getMemberServices(Context context, MemberObject memberObject) {
        List<BaseUpcomingService> upcomingServices = new ArrayList<>();

        BaseUpcomingService followUP = new BaseUpcomingService();
        followUP.setServiceName(context.getString(R.string.follow_up_visit));
        followUP.setServiceDate(new Date());
        followUP.setOverDueDate(LocalDate.now().plusDays(2).toDate());

        upcomingServices.add(followUP);


        String baseEntityID = memberObject.getBaseEntityId();
        if (PNCDao.isPNCMember(baseEntityID)) {
            upcomingServices.addAll(new PncUpcomingServicesInteractorFlv().getMemberServices(context, memberObject));
        } else if (AncDao.isANCMember(baseEntityID)) {
            upcomingServices.addAll(new AncUpcomingServicesInteractorFlv().getMemberServices(context, memberObject));
        } else if (ChildDao.isChild(baseEntityID)) {
            upcomingServices.addAll(new ChildUpcomingServicesInteractor().getMemberServices(context, memberObject));
        }

        Collections.sort(upcomingServices, (s1, s2) -> s1.getServiceDate().compareTo(s2.getServiceDate()));
        //
        return upcomingServices;
    }
}
