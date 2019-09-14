package org.smartregister.brac.hnpp.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.brac.hnpp.HnppApplication;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.activity.HNPPJsonFormActivity;
import org.smartregister.brac.hnpp.domain.HouseholdId;
import org.smartregister.brac.hnpp.location.SSLocationForm;
import org.smartregister.brac.hnpp.location.SSLocationHelper;
import org.smartregister.brac.hnpp.location.SSLocations;
import org.smartregister.brac.hnpp.repository.HouseholdIdRepository;
import org.smartregister.util.Utils;

import java.util.ArrayList;

public class HNPPJsonFormFragment extends JsonWizardFormFragment {
    public HNPPJsonFormFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static HNPPJsonFormFragment getFormFragment(String stepName) {
        HNPPJsonFormFragment jsonFormFragment = new HNPPJsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stepName", stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    protected JsonFormFragmentViewState createViewState() {
        return super.createViewState();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        super.onItemSelected(parent, view, position, id);
        if (position != -1 && parent instanceof MaterialSpinner) {
            if (((MaterialSpinner) parent).getFloatingLabelText().toString().equalsIgnoreCase(view.getContext().getResources().getString(R.string.village_name_form_field))) {

                processUniqueId(position);
            }
        }


    }

    @Override
    public JSONObject getStep(String stepName) {
        return super.getStep(stepName);
    }

    public void processUniqueId(final int index) {


        Utils.startAsyncTask(new AsyncTask() {

            String unique_id = "";
            HouseholdId hhid = null;
            @Override
            protected Object doInBackground(Object[] objects) {
                    SSLocations ssLocations = SSLocationHelper.getInstance().getSsLocationForms().get(index).locations;

                    HouseholdIdRepository householdIdRepo = HnppApplication.getHNPPInstance().getHouseholdIdRepository();
                    hhid = householdIdRepo.getNextHouseholdId(String.valueOf(ssLocations.village.id));
                    unique_id = SSLocationHelper.getInstance().generateHouseHoldId(ssLocations, hhid.getOpenmrsId() + "");

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                ArrayList<View> formdataviews = getJsonApi().getFormDataViews();
                for (int i = 0; i < formdataviews.size(); i++) {
                    if (formdataviews.get(i) instanceof MaterialEditText) {
                        if (((MaterialEditText) formdataviews.get(i)).getFloatingLabelText().toString().trim().equalsIgnoreCase(formdataviews.get(i).getContext().getResources().getString(R.string.unique_id_form_field))) {
                            ((MaterialEditText) formdataviews.get(i)).setText(unique_id);
                            try {
                                getStep("step1").put("index",index);
                                if(hhid!=null){
                                    getStep("step1").put("hhid",hhid.getOpenmrsId());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                }
            }
        }, null);
    }

}
