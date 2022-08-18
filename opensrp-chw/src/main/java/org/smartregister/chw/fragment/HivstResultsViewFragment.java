package org.smartregister.chw.fragment;

import android.os.Bundle;

import org.smartregister.chw.hivst.fragment.BaseHivstResultRegisterFragment;
import org.smartregister.chw.hivst.presenter.BaseHivstResultsFragmentPresenter;
import org.smartregister.chw.hivst.util.Constants;
import org.smartregister.chw.model.HivstResultsFragmentModel;

public class HivstResultsViewFragment extends BaseHivstResultRegisterFragment {

    private String baseEntityId;

    public static HivstResultsViewFragment newInstance(String baseEntityId) {
        HivstResultsViewFragment hivstResultRegisterFragment = new HivstResultsViewFragment();
        Bundle b = new Bundle();
        b.putString(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        hivstResultRegisterFragment.setArguments(b);
        return hivstResultRegisterFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            this.baseEntityId = getArguments().getString(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializePresenter() {
        presenter = new BaseHivstResultsFragmentPresenter(baseEntityId, this, new HivstResultsFragmentModel(), null);
    }
}
