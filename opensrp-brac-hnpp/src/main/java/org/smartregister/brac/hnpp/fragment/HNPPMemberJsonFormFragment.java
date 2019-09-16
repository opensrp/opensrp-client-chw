package org.smartregister.brac.hnpp.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;
import com.vijay.jsonwizard.widgets.FingerPrintViewFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.brac.hnpp.HnppApplication;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.domain.HouseholdId;
import org.smartregister.brac.hnpp.location.SSLocationHelper;
import org.smartregister.brac.hnpp.location.SSLocations;
import org.smartregister.brac.hnpp.repository.HouseholdIdRepository;
import org.smartregister.simprint.SimprintsLibrary;
import org.smartregister.simprint.SimprintsRegisterActivity;
import org.smartregister.util.Utils;

import java.util.ArrayList;

import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;

public class HNPPMemberJsonFormFragment extends JsonWizardFormFragment {
    public HNPPMemberJsonFormFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static HNPPMemberJsonFormFragment getFormFragment(String stepName) {
        HNPPMemberJsonFormFragment jsonFormFragment = new HNPPMemberJsonFormFragment();
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
    public JSONObject getStep(String stepName) {
        return super.getStep(stepName);
    }

    public void updateGuid(String uniqueId){
        JSONObject guIdField = null;
        try {
            guIdField = getFieldJSONObject(getStep("step1").getJSONArray("fields"), "gu_id");
            guIdField.put("value",uniqueId);
            ArrayList<View> formdataviews = new ArrayList<>(getJsonApi().getFormDataViews());
            for(View view : formdataviews){
                if (view instanceof ImageView) {
                    ImageView imageView = (ImageView) view;
                    String key = (String) imageView.getTag(com.vijay.jsonwizard.R.id.key);
                    if (key.equals("finger_print")) {

                        imageView.setImageResource(R.drawable.finger_print_done);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
