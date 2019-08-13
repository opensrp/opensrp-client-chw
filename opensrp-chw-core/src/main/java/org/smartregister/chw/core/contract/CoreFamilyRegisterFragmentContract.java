package org.smartregister.chw.core.contract;

import org.smartregister.family.contract.FamilyRegisterFragmentContract;

public interface CoreFamilyRegisterFragmentContract {

    interface Presenter extends FamilyRegisterFragmentContract.Presenter {

        String getDueFilterCondition();

    }

}
