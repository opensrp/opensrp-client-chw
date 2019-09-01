package org.smartregister.brac.hnpp.interactor;

import org.smartregister.chw.core.interactor.CoreFamilyChangeContractInteractor;
import org.smartregister.chw.core.utils.Utils;

public class HfFamilyChangeContractInteractor implements CoreFamilyChangeContractInteractor.Flavor {
    @Override
    public String getFamilyMembersSql(String familyID) {
        return Utils.getFamilyMembersSql(familyID);
    }
}
