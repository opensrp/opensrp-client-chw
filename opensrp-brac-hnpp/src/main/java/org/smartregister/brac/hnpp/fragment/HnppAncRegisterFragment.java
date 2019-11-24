package org.smartregister.brac.hnpp.fragment;

import android.content.Intent;

import org.smartregister.brac.hnpp.activity.AncMemberProfileActivity;
import org.smartregister.brac.hnpp.activity.HnppFamilyOtherMemberProfileActivity;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.Constants;

import java.util.HashMap;

public class HnppAncRegisterFragment extends AncRegisterFragment {

    @Override
    protected void openProfile(CommonPersonObjectClient client) {

        HashMap<String, String> detailsMap = CoreChwApplication.ancRegisterRepository().getFamilyNameAndPhone(Utils.getValue(client.getColumnmaps(), org.smartregister.family.util.DBConstants.KEY.FAMILY_HEAD, false));

        String familyName = "";
        String familyHeadPhone = "";
        if (detailsMap != null) {
            familyName = detailsMap.get(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_NAME);
            familyHeadPhone = detailsMap.get(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_PHONE);
        }
        String familyId = org.smartregister.util.Utils.getValue(client.getColumnmaps(), ChildDBConstants.KEY.RELATIONAL_ID, false);
        String houseHoldHead = org.smartregister.family.util.Utils.getValue(client.getColumnmaps(), HnppConstants.KEY.HOUSE_HOLD_NAME, true);
        String address = org.smartregister.family.util.Utils.getValue(client.getColumnmaps(), HnppConstants.KEY.VILLAGE_NAME, true);

        Intent intent = new Intent(getActivity(), HnppFamilyOtherMemberProfileActivity.class);
        intent.putExtras(getArguments());
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, client.getCaseId());
        intent.putExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, familyId);
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON, client);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, familyId);
        intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, familyId);
        intent.putExtra(Constants.INTENT_KEY.VILLAGE_TOWN, address);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_NAME, houseHoldHead);
        startActivity(intent);
    }
}
