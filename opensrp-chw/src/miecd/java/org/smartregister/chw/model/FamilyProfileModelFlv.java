package org.smartregister.chw.model;

import org.smartregister.chw.core.utils.FormUtils;
import org.smartregister.family.domain.FamilyEventClient;

public class FamilyProfileModelFlv implements FamilyProfileModel.Flavor {
    @Override
    public void updateWra(FamilyEventClient familyEventClient) {
        FormUtils.updateWraForBA(familyEventClient);
    }
}
