package org.smartregister.chw.interactor;

import org.smartregister.chw.core.interactor.CoreAncUpcomingServicesInteractor;

public class AncUpcomingServicesInteractor extends CoreAncUpcomingServicesInteractor {
    @Override
    public void setFlavor(Flavor flavor) {
        super.setFlavor(new AncUpcomingServicesInteractorFlv());
    }
}
