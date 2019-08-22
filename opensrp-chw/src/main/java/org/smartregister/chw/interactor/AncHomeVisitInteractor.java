package org.smartregister.chw.interactor;

import org.smartregister.chw.core.interactor.CoreAncHomeVisitInteractor;

public class AncHomeVisitInteractor extends CoreAncHomeVisitInteractor {

    @Override
    public void setFlavor(Flavor flavor) {
        super.setFlavor(new AncHomeVisitInteractorFlv());
    }
}
