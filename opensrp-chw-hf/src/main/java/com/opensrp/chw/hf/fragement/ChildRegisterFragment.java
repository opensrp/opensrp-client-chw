package com.opensrp.chw.hf.fragement;

import android.content.Intent;

import com.opensrp.chw.core.fragment.CoreChildRegisterFragment;
import com.opensrp.chw.hf.activity.ChildProfileActivity;

import org.smartregister.commonregistry.CommonPersonObjectClient;

import timber.log.Timber;

public class ChildRegisterFragment extends CoreChildRegisterFragment {
    @Override
    public void goToChildDetailActivity(CommonPersonObjectClient patient,
                                        boolean launchDialog) {
        if (launchDialog) {
            Timber.i(patient.name);
        }

        Intent intent = new Intent(getActivity(), ChildProfileActivity.class);
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        startActivity(intent);
    }
}
