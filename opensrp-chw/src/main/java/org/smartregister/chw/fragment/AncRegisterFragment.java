package org.smartregister.chw.fragment;

import org.smartregister.chw.activity.AncHomeVisitActivity;
import org.smartregister.chw.activity.AncMemberProfileActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.fragment.CoreAncRegisterFragment;
import org.smartregister.chw.core.presenter.AncRegisterFragmentPresenter;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.chw.model.AncRegisterFragmentModel;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.HashMap;

public class AncRegisterFragment extends CoreAncRegisterFragment {
    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new AncRegisterFragmentPresenter(this, new AncRegisterFragmentModel(), null);
    }

    @Override
    protected void openProfile(CommonPersonObjectClient client) {

        HashMap<String, String> detailsMap = CoreChwApplication.ancRegisterRepository().getFamilyNameAndPhone(Utils.getValue(client.getColumnmaps(), org.smartregister.family.util.DBConstants.KEY.FAMILY_HEAD, false));

        String familyName = "";
        String familyHeadPhone = "";
        if (detailsMap != null) {
            familyName = detailsMap.get(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_NAME);
            familyHeadPhone = detailsMap.get(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.FAMILY_HEAD_PHONE);
        }

        AncMemberProfileActivity.startMe(getActivity(), new MemberObject(client), familyName, familyHeadPhone);
    }

    @Override
    protected void openHomeVisit(CommonPersonObjectClient client) {
        AncHomeVisitActivity.startMe(getActivity(), new MemberObject(client), false);
    }
}
