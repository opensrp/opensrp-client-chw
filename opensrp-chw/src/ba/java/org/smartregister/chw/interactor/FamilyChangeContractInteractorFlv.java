package org.smartregister.chw.interactor;

import org.smartregister.chw.core.interactor.CoreFamilyChangeContractInteractor;

import static org.smartregister.chw.core.utils.Utils.getFamilyMembersSqlForBA;

public class FamilyChangeContractInteractorFlv implements CoreFamilyChangeContractInteractor.Flavor {
    @Override
    public String getFamilyMembersSql(String familyID) {
        return getFamilyMembersSqlForBA();
    }
}
