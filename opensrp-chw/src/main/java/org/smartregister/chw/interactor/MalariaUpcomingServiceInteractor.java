package org.smartregister.chw.interactor;

import android.content.Context;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.interactor.BaseAncUpcomingServicesInteractor;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.core.dao.AncDao;
import org.smartregister.chw.core.dao.ChildDao;
import org.smartregister.chw.core.dao.PNCDao;
import org.smartregister.chw.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MalariaUpcomingServiceInteractor extends BaseAncUpcomingServicesInteractor {

    @Override
    protected List<BaseUpcomingService> getMemberServices(Context context, MemberObject memberObject) {
        List<BaseUpcomingService> upcomingServices = new ArrayList<>();
        String baseEntityID = memberObject.getBaseEntityId();

        Utils.malariaUpcomingServices(baseEntityID, context, upcomingServices);

        if (PNCDao.isPNCMember(baseEntityID)) {
            upcomingServices.addAll(new PncUpcomingServicesInteractorFlv().getMemberServices(context, memberObject));
        } else if (AncDao.isANCMember(baseEntityID)) {
            upcomingServices.addAll(new AncUpcomingServicesInteractorFlv().getMemberServices(context, memberObject));
        } else if (ChildDao.isChild(baseEntityID)) {
            upcomingServices.addAll(new ChildUpcomingServicesInteractor().getMemberServices(context, memberObject));
        }

        Collections.sort(upcomingServices, (s1, s2) -> s1.getServiceDate().compareTo(s2.getServiceDate()));

        return upcomingServices;
    }
}
