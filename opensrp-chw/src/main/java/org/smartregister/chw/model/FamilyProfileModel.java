package org.smartregister.chw.model;

import org.smartregister.chw.core.model.CoreFamilyProfileModel;
import org.smartregister.family.domain.FamilyEventClient;

public class FamilyProfileModel extends CoreFamilyProfileModel {
    public FamilyProfileModel(String familyName) {
        super(familyName);
    }

    @Override
    public void updateWra(FamilyEventClient familyEventClient) {
        new FamilyProfileModelFlv().updateWra(familyEventClient);
    }

    public interface Flavor {
        void updateWra(FamilyEventClient familyEventClient);
    }
}
