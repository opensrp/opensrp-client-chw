package org.smartregister.chw.interactor;

import org.smartregister.family.util.DBConstants;

public abstract class DefaultFamilyChangeContractInteractorFlv implements FamilyChangeContractInteractor.Flavor {
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
                DBConstants.KEY.GENDER + " , " +
                DBConstants.KEY.HIGHEST_EDU_LEVEL;

        return info_columns;
    }
}
