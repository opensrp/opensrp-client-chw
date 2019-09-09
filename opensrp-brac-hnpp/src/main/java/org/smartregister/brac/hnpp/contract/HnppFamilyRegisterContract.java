package org.smartregister.brac.hnpp.contract;

import org.smartregister.family.contract.FamilyRegisterContract;
/**
 * Created by keyamn on 12/11/2018.
 */
public interface HnppFamilyRegisterContract  extends FamilyRegisterContract{


    interface InteractorCallBack extends FamilyRegisterContract.InteractorCallBack {

        void updateHouseHoldId(String houseHoldId);

    }
}
