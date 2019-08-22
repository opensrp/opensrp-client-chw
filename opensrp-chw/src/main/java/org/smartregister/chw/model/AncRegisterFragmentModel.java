package org.smartregister.chw.model;

import org.smartregister.chw.core.model.CoreAncRegisterFragmentModel;

public class AncRegisterFragmentModel extends CoreAncRegisterFragmentModel {
    @Override
    public void setFlavor(Flavor flavor) {
        super.setFlavor(new AncRegisterFragmentModelFlv());
    }
}
