package org.smartregister.chw.interactor;

import com.opensrp.chw.core.interactor.CoreFamilyChangeContractInteractor;

import static com.opensrp.chw.core.utils.Utils.getFamilyMembersSqlForBA;

public class FamilyChangeContractInteractorFlv implements CoreFamilyChangeContractInteractor.Flavor {
    @Override
    public String getFamilyMembersSql(String familyID) {
        return getFamilyMembersSqlForBA();
    }
}
