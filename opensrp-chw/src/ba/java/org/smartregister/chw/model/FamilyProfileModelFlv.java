package org.smartregister.chw.model;

import org.smartregister.family.domain.FamilyEventClient;

import static com.opensrp.chw.core.utils.FormUtils.updateWraForBA;

public class FamilyProfileModelFlv implements FamilyProfileModel.Flavor {
    @Override
    public void updateWra(FamilyEventClient familyEventClient) {
        updateWraForBA(familyEventClient);
    }
}
