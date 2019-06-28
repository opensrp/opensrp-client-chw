package org.smartregister.chw.interactor;

import org.smartregister.chw.anc.model.BaseUpcomingService;

import java.util.ArrayList;
import java.util.List;

public class AncUpcomingServicesInteractorFlv implements AncUpcomingServicesInteractor.Flavor {

    @Override
    public List<BaseUpcomingService> getMemberServices(String memberID) {
        List<BaseUpcomingService> services = new ArrayList<>();
        return services;
    }

}
