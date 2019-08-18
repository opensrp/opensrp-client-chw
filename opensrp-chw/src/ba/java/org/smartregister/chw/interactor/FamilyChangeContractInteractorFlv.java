package org.smartregister.chw.interactor;

import org.smartregister.chw.core.interactor.CoreFamilyChangeContractInteractor;
import org.smartregister.family.util.DBConstants;

public class FamilyChangeContractInteractorFlv implements CoreFamilyChangeContractInteractor.Flavor {
    @Override
    public String getFamilyMembersSql(String familyID) {
        String info_columns = DBConstants.KEY.RELATIONAL_ID + " , " +
                DBConstants.KEY.BASE_ENTITY_ID + " , " +
                DBConstants.KEY.FIRST_NAME + " , " +
                DBConstants.KEY.MIDDLE_NAME + " , " +
                DBConstants.KEY.LAST_NAME + " , " +
                DBConstants.KEY.PHONE_NUMBER + " , " +
                DBConstants.KEY.OTHER_PHONE_NUMBER + " , " +
                DBConstants.KEY.DOB + " , " +
                DBConstants.KEY.DOD + " , " +
                DBConstants.KEY.GENDER;

        return info_columns;
    }
}
