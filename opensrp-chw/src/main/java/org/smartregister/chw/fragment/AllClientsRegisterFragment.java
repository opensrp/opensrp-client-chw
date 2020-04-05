package org.smartregister.chw.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import org.smartregister.chw.R;
import org.smartregister.chw.core.fragment.CoreAllClientsRegisterFragment;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.FamilyDao;
import org.smartregister.chw.model.FamilyDetailsModel;
import org.smartregister.chw.util.AllClientsUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class AllClientsRegisterFragment extends CoreAllClientsRegisterFragment {
    public static final String REGISTER_TYPE = "register_type";

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        View dueOnlyLayout = view.findViewById(R.id.due_only_layout);
        dueOnlyLayout.setVisibility(View.GONE);
    }

    @Override
    protected void goToClientDetailActivity(@NonNull CommonPersonObjectClient commonPersonObjectClient) {

        String registerType = commonPersonObjectClient.getDetails().get(REGISTER_TYPE);

        Bundle bundle = new Bundle();
        FamilyDetailsModel familyDetailsModel = FamilyDao.getFamilyDetail(commonPersonObjectClient.entityId());

        if(familyDetailsModel != null) {
            bundle.putString(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, familyDetailsModel.getBaseEntityId());
            bundle.putString(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_HEAD, familyDetailsModel.getFamilyHead());
            bundle.putString(org.smartregister.family.util.Constants.INTENT_KEY.PRIMARY_CAREGIVER, familyDetailsModel.getPrimaryCareGiver());
            bundle.putString(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_NAME, familyDetailsModel.getFamilyName());
        }

        if (registerType != null) {
            switch (registerType) {
                case CoreConstants.REGISTER_TYPE.CHILD:
                    AllClientsUtils.goToChildProfile(this.getActivity(), commonPersonObjectClient, bundle);
                    break;
                case CoreConstants.REGISTER_TYPE.ANC:
                case CoreConstants.REGISTER_TYPE.PNC:
                case CoreConstants.REGISTER_TYPE.MALARIA:
                case CoreConstants.REGISTER_TYPE.FAMILY_PLANNING:
                    AllClientsUtils.goToAdultMemberProfile(this.getActivity(), commonPersonObjectClient, bundle);
                    break;
                default:
                    AllClientsUtils.goToOtherMemberProfile(this.getActivity(), commonPersonObjectClient, bundle,
                            familyDetailsModel.getFamilyHead(), familyDetailsModel.getPrimaryCareGiver());
                    break;
            }
        } else {
            if (familyDetailsModel != null) {
                AllClientsUtils.goToOtherMemberProfile(this.getActivity(), commonPersonObjectClient, bundle,
                        familyDetailsModel.getFamilyHead(), familyDetailsModel.getPrimaryCareGiver());
            }
        }
    }
}
