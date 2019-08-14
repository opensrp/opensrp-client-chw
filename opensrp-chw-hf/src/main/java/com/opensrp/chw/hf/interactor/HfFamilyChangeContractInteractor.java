package com.opensrp.chw.hf.interactor;

import com.opensrp.chw.core.interactor.CoreFamilyChangeContractInteractor;
import com.opensrp.chw.core.utils.Utils;

public class HfFamilyChangeContractInteractor implements CoreFamilyChangeContractInteractor.Flavor {
    @Override
    public String getFamilyMembersSql(String familyID) {
        return Utils.getFamilyMembersSql(familyID);
    }
}
