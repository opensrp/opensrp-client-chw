package org.smartregister.chw.interactor;

import android.content.Context;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.interactor.BaseAncUpcomingServicesInteractor;
import org.smartregister.chw.anc.model.BaseUpcomingService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MalariaUpcomingServiceInteractor extends BaseAncUpcomingServicesInteractor {

    @Override
    protected List<BaseUpcomingService> getMemberServices(Context context, MemberObject memberObject) {
        List<BaseUpcomingService> upcomingServices = new ArrayList<>();
        upcomingServices.addAll(new AncUpcomingServicesInteractorFlv().getMemberServices(context, memberObject));
        upcomingServices.addAll(new PncUpcomingServicesInteractorFlv().getMemberServices(context, memberObject));
        //TODO add malaria and child

        Collections.sort(upcomingServices, (s1, s2) -> s1.getServiceDate().compareTo(s2.getServiceDate()));
        //
        return upcomingServices;
    }
}
