package org.smartregister.chw.fragment;

import android.os.Bundle;

import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.activity.HivstResultViewActivity;
import org.smartregister.chw.hivst.fragment.BaseHivstResultViewFragment;
import org.smartregister.chw.hivst.presenter.BaseHivstResultsFragmentPresenter;
import org.smartregister.chw.hivst.util.Constants;
import org.smartregister.chw.model.HivstResultsFragmentModel;
import org.smartregister.chw.hivst.util.DBConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.Utils;

public class HivstResultsViewFragment extends BaseHivstResultViewFragment {

    private String baseEntityId;

    public static HivstResultsViewFragment newInstance(String baseEntityId) {
        HivstResultsViewFragment hivstResultsViewFragment = new HivstResultsViewFragment();
        Bundle b = new Bundle();
        b.putString(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        hivstResultsViewFragment.setArguments(b);
        return hivstResultsViewFragment;
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

    @Override
    public void openResultsForm(CommonPersonObjectClient client) {
        String baseEntityId = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, false);
        String entityId = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.ENTITY_ID, false);
        String kitFor = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.KIT_FOR, false);
        try {
            JSONObject jsonObject = (new FormUtils()).getFormJsonFromRepositoryOrAssets(requireContext(), Constants.FORMS.HIVST_RECORD_RESULTS);
            JSONObject global =  jsonObject.getJSONObject("global");
            global.putOpt("kit_for", kitFor);
            HivstResultViewActivity.startResultsForm(getContext(), jsonObject.toString(), baseEntityId, entityId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
