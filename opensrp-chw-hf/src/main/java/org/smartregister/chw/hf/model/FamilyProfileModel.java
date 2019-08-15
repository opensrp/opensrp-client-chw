package org.smartregister.chw.hf.model;

import org.smartregister.chw.core.model.CoreFamilyProfileModel;
import org.smartregister.chw.core.utils.FormUtils;
import org.smartregister.family.domain.FamilyEventClient;

public class FamilyProfileModel extends CoreFamilyProfileModel {
    public FamilyProfileModel(String familyName) {
        super(familyName);
    }

    @Override
    public void updateWra(FamilyEventClient familyEventClient) {
        FormUtils.updateWraForBA(familyEventClient);
    }
}
