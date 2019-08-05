package com.opensrp.chw.hf.interactor;

import com.opensrp.chw.core.interactor.CoreFamilyChangeContractInteractor;

import static com.opensrp.chw.core.utils.Utils.getFamilyMembersSqlForBA;

public class HfFamilyChangeContractInteractor implements CoreFamilyChangeContractInteractor.Flavor {
    @Override
    public String getFamilyMembersSql(String familyID) {
        return getFamilyMembersSqlForBA();
    }
}
