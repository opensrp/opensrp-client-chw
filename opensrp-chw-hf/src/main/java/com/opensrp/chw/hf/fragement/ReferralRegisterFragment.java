package com.opensrp.chw.hf.fragement;

import android.content.Intent;
import android.view.View;

import com.opensrp.chw.core.fragment.BaseReferralRegisterFragment;
import com.opensrp.chw.hf.activity.ChildProfileActivity;
import com.opensrp.chw.hf.presenter.ReferralFragmentPresenter;

import org.smartregister.commonregistry.CommonPersonObjectClient;

public class ReferralRegisterFragment extends BaseReferralRegisterFragment {
    @Override
    protected void initializePresenter() {
        presenter = new ReferralFragmentPresenter(this);
    }

    @Override
    protected void onViewClicked(View view) {
        CommonPersonObjectClient client = (CommonPersonObjectClient) view.getTag();
        Intent intent = new Intent(getActivity(), ChildProfileActivity.class);
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.BASE_ENTITY_ID, client.getCaseId());
        startActivity(intent);

    }
}
