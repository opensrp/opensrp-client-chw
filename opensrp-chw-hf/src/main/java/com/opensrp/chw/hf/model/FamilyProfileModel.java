package com.opensrp.chw.hf.model;

import com.opensrp.chw.core.model.CoreFamilyProfileModel;
import com.opensrp.chw.core.utils.FormUtils;

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
