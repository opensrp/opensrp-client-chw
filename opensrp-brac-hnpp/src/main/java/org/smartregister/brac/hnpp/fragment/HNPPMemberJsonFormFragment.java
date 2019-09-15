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
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        super.onItemSelected(parent, view, position, id);
    }

    @Override
    protected JsonFormFragmentViewState createViewState() {
        return super.createViewState();
    }


    @Override
    public JSONObject getStep(String stepName) {
        return super.getStep(stepName);
    }

//    @Override
//    public void startSimprintsRegistration(String projectId,String userId,String moduleId) {
//        if(!TextUtils.isEmpty(projectId) && !TextUtils.isEmpty(userId) &&  !TextUtils.isEmpty(moduleId)){
//            SimprintsLibrary.init(getActivity(),projectId,userId);
//            SimprintsRegisterActivity.startSimprintsRegisterActivity(getActivity(),moduleId, JsonFormConstants.ACTIVITY_REQUEST_CODE.REQUEST_CODE_REGISTER);
//
//        }else {
//            Toast.makeText(getActivity(),"Project Id or user id or module id should not be empty",Toast.LENGTH_LONG).show();
//        }
//    }
}
