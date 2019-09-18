package org.smartregister.brac.hnpp.utils;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.Utils;

public class HnppMemberObject extends MemberObject {

    protected String houseHoldId = "";
     public HnppMemberObject(CommonPersonObjectClient pc){
         super(pc);
         this.houseHoldId = Utils.getValue(pc.getColumnmaps(), HnppConstants.KEY.HOUSE_HOLD_ID, false);

     }

    public String getHouseHoldId() {
        return houseHoldId;
    }
}
