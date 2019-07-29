package com.opensrp.chw.hf.model;

import com.opensrp.chw.core.utils.FormUtils;

import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.model.BaseFamilyProfileModel;

public class FamilyProfileModel extends BaseFamilyProfileModel {
    public FamilyProfileModel(String familyName) {
        super(familyName);
    }

    @Override
    public void updateWra(FamilyEventClient familyEventClient) {
        FormUtils.updateWraForBA(familyEventClient);
    }
}
