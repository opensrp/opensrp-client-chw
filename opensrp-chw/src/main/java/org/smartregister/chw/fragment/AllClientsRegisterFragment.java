package org.smartregister.chw.fragment;

import androidx.annotation.NonNull;

import org.smartregister.chw.core.fragment.CoreAllClientsRegisterFragment;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class AllClientsRegisterFragment extends CoreAllClientsRegisterFragment {

    @Override
    protected void goToClientDetailActivity(@NonNull CommonPersonObjectClient commonPersonObjectClient) {

//        String registerType = commonPersonObjectClient.getDetails().get(HfReferralUtils.REGISTER_TYPE);
//
//        Bundle bundle = new Bundle();
//        FamilyDetailsModel familyDetailsModel = FamilyDao.getFamilyDetail(commonPersonObjectClient.entityId());
//        bundle.putString(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, familyDetailsModel.getBaseEntityId());
//        bundle.putString(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_HEAD, familyDetailsModel.getFamilyHead());
//        bundle.putString(org.smartregister.family.util.Constants.INTENT_KEY.PRIMARY_CAREGIVER, familyDetailsModel.getPrimaryCareGiver());
//        bundle.putString(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_NAME, familyDetailsModel.getFamilyName());
//
//        if (registerType != null) {
//            switch (registerType) {
//                case Constants.CHILD:
//                    AllClientsUtils.goToChildProfile(this.getActivity(), commonPersonObjectClient, bundle);
//                    break;
//                case Constants.ANC:
//                case Constants.PNC:
//                case Constants.MALARIA:
//                case Constants.FAMILY_PLANNING:
//                    AllClientsUtils.goToAdultMemberProfile(this.getActivity(), commonPersonObjectClient, bundle);
//                    break;
//                default:
//                    AllClientsUtils.goToOtherMemberProfile(this.getActivity(), commonPersonObjectClient, bundle,
//                            familyDetailsModel.getFamilyHead(), familyDetailsModel.getPrimaryCareGiver());
//                    break;
//            }
//        } else {
//            AllClientsUtils.goToOtherMemberProfile(this.getActivity(), commonPersonObjectClient, bundle,
//                    familyDetailsModel.getFamilyHead(), familyDetailsModel.getPrimaryCareGiver());
//        }
    }
}
