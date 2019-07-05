package org.smartregister.chw.interactor;

import org.smartregister.chw.anc.interactor.BaseAncUpcomingServicesInteractor;
import org.smartregister.chw.anc.model.BaseUpcomingService;

import java.util.List;

public class AncUpcomingServicesInteractor extends BaseAncUpcomingServicesInteractor {

    private Flavor flavor = new AncUpcomingServicesInteractorFlv();

    /**
     * This method is already in a thread
     *
     * @param memberID
     * @return
     */
    @Override
    protected List<BaseUpcomingService> getMemberServices(String memberID) {
        return flavor.getMemberServices(memberID);
    }

    public interface Flavor {
        List<BaseUpcomingService> getMemberServices(String memberID);
    }
}
