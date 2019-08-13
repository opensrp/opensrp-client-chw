package org.smartregister.chw.hf.fragement;

import android.content.Intent;
import android.view.View;

import org.smartregister.chw.core.fragment.BaseReferralRegisterFragment;
import org.smartregister.chw.hf.activity.ChildProfileActivity;
import org.smartregister.chw.hf.presenter.ReferralFragmentPresenter;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.DBConstants;

public class ReferralRegisterFragment extends BaseReferralRegisterFragment {
    @Override
    protected void initializePresenter() {
        presenter = new ReferralFragmentPresenter(this);
    }

    @Override
    protected void onViewClicked(View view) {
        CommonPersonObjectClient client = (CommonPersonObjectClient) view.getTag();
        Intent intent = new Intent(getActivity(), ChildProfileActivity.class);
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.BASE_ENTITY_ID, client.getColumnmaps().get(DBConstants.KEY.BASE_ENTITY_ID));
        startActivity(intent);

    }
}
