package org.smartregister.chw.interactor;

import android.content.Context;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseUpcomingService;

import java.util.ArrayList;
import java.util.List;

public class AncUpcomingServicesInteractorFlv extends DefaultAncUpcomingServicesInteractorFlv {

    @Override
    public List<BaseUpcomingService> getMemberServices(Context context, MemberObject memberObject) {
        List<BaseUpcomingService> services = new ArrayList<>();
        evaluateHealthFacility(services, memberObject, context);
        return services;
    }
}
