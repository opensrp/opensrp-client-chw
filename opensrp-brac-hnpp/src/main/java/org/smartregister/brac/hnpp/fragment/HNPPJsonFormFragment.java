package org.smartregister.brac.hnpp.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.brac.hnpp.HnppApplication;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.domain.HouseholdId;
import org.smartregister.brac.hnpp.interactor.HnppJsonFormInteractor;
import org.smartregister.brac.hnpp.job.PullHouseholdIdsServiceJob;
import org.smartregister.brac.hnpp.location.SSLocationHelper;
import org.smartregister.brac.hnpp.location.SSLocations;
import org.smartregister.brac.hnpp.repository.HouseholdIdRepository;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.util.Utils;

import java.util.ArrayList;

import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;

public class HNPPJsonFormFragment extends JsonWizardFormFragment {
    public HNPPJsonFormFragment() {
        super();
    }

    public static HNPPJsonFormFragment getFormFragment(String stepName) {
        HNPPJsonFormFragment jsonFormFragment = new HNPPJsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stepName", stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected JsonFormFragmentPresenter createPresenter() {
        return new JsonFormFragmentPresenter(this, HnppJsonFormInteractor.getInstance());
    }

    @Override
    protected JsonFormFragmentViewState createViewState() {
        return super.createViewState();
    }
    private int ssIndex = -1;
    private int villageIndex = -1;
    private boolean isManuallyPressed = false;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isManuallyPressed = true;
            }
        }, 1000);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        super.onItemSelected(parent, view, position, id);
        if (position != -1 && parent instanceof MaterialSpinner) {
            if (((MaterialSpinner) parent).getFloatingLabelText().toString().equalsIgnoreCase(view.getContext().getResources().getString(R.string.ss_name_form_field))) {
                ssIndex = position;
                if(isManuallyPressed){
                    processVillageList(position);
                }
            }
            else if (((MaterialSpinner) parent).getFloatingLabelText().toString().equalsIgnoreCase(view.getContext().getResources().getString(R.string.village_name_form_field))) {
                if(isManuallyPressed){
                    processHouseHoldId(position);
                }
            }
          //  hideKeyBoard();
        }


    }


    @Override
    public JSONObject getStep(String stepName) {
        return super.getStep(stepName);
    }

    public void processVillageList(final int index) {


        Utils.startAsyncTask(new AsyncTask() {
            ArrayList<String> villageList = new ArrayList<>();
            @Override
            protected Object doInBackground(Object[] objects) {
                ArrayList<SSLocations> ssLocations = SSLocationHelper.getInstance().getSsModels().get(index).locations;
                for(SSLocations ssLocations1 : ssLocations){
                    villageList.add(ssLocations1.village.name);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                ArrayList<View> formdataviews = new ArrayList<>(getJsonApi().getFormDataViews());
                for (int i = 0; i < formdataviews.size(); i++) {
                    if (formdataviews.get(i) instanceof MaterialSpinner) {
                        if (!TextUtils.isEmpty(((MaterialSpinner) formdataviews.get(i)).getFloatingLabelText()) &&
                                (((MaterialSpinner) formdataviews.get(i)).getFloatingLabelText().toString().trim()
                                        .equalsIgnoreCase(getContext().getResources().getString(R.string.village_name_form_field)))) {

                            try{
                                JSONObject villageNames = getFieldJSONObject(getStep("step1").getJSONArray("fields"), "village_name");
                                JSONArray jsonArray = new JSONArray();
                                for(String villages : villageList){
                                    jsonArray.put(villages);
                                }
                                villageNames.put(org.smartregister.family.util.JsonFormUtils.VALUES,jsonArray);
                            }catch (Exception e){

                            }


                            MaterialSpinner spinner = (MaterialSpinner) formdataviews.get(i);
                            spinner.setEnabled(true);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), com.vijay.jsonwizard.R.layout.native_form_simple_list_item_1, villageList);
                            spinner.setAdapter(adapter);
                            spinner.setSelection(0, true);
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    if (((MaterialSpinner) parent).getFloatingLabelText().toString().equalsIgnoreCase(view.getContext().getResources().getString(R.string.village_name_form_field))) {
                                        villageIndex = position;
                                        Log.v("SPINNER_SELECT","processVillageList>>onItemSelectedssIndex:"+villageIndex);
                                        if(villageIndex!=-1){
                                            processHouseHoldId(position);
                                        }


                                    }

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            break;
                        }
                    }
                }
            }
        }, null);
    }
    public void processHouseHoldId(final int index) {

        if(ssIndex<0) return;

        Utils.startAsyncTask(new AsyncTask() {
            String moduleId = "";
            String village_id = "";
            String unique_id = "";
            HouseholdId hhid = null;


            @Override
            protected Object doInBackground(Object[] objects) {
                SSLocations ssLocations = SSLocationHelper.getInstance().getSsModels().get(ssIndex).locations.get(index);
                if(HnppConstants.isReleaseBuild()){
                    moduleId = ssLocations.city_corporation_upazila.name+"_"+ssLocations.union_ward.name;
                }else{
                    moduleId = HnppConstants.MODULE_ID_TRAINING;
                }

                HouseholdIdRepository householdIdRepo = HnppApplication.getHNPPInstance().getHouseholdIdRepository();
                village_id = String.valueOf(ssLocations.village.id);
                hhid = householdIdRepo.getNextHouseholdId(village_id);
                if(hhid == null){
                    return "test";
                }
                unique_id = SSLocationHelper.getInstance().generateHouseHoldId(ssLocations, hhid.getOpenmrsId() + "");

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(o instanceof String){
                    String str = (String)o;
                    if(!TextUtils.isEmpty(str) && str.equalsIgnoreCase("test")){

                        showNewIdRetriveaPopup();
                        PullHouseholdIdsServiceJob.scheduleJobImmediately(PullHouseholdIdsServiceJob.TAG);
                        return;
                    }

                }
                ArrayList<View> formdataviews = new ArrayList<>(getJsonApi().getFormDataViews());
                for (int i = 0; i < formdataviews.size(); i++) {
                    if (formdataviews.get(i) instanceof MaterialEditText) {
                        if (!TextUtils.isEmpty(((MaterialEditText) formdataviews.get(i)).getFloatingLabelText()) && ((MaterialEditText) formdataviews.get(i)).getFloatingLabelText().toString().trim().equalsIgnoreCase("খানা নাম্বার")) {
                            ((MaterialEditText) formdataviews.get(i)).setText(unique_id);
                            try {
                                getStep("step1").put("ss_index", ssIndex);
                                getStep("step1").put("village_index", index);
                                JSONObject module_id = getFieldJSONObject(getStep("step1").getJSONArray("fields"), "module_id");
                                JSONObject villageId = getFieldJSONObject(getStep("step1").getJSONArray("fields"), "village_id");

                                villageId.put("value", village_id);
                                module_id.put("value", moduleId);
                                JSONArray jsonArray = new JSONArray();

                                if (hhid != null) {
                                    getStep("step1").put("hhid", hhid.getOpenmrsId());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
        private void showNewIdRetriveaPopup(){
            new AlertDialog.Builder(getActivity()).setMessage("নতুন আইডি আনা হচ্ছে ........। দয়া করে ইন্টারনেট অন রাখুন")
                    .setTitle("আইডি শেষ হয়ে গিয়েছে !!!!").setCancelable(false)
                    .setPositiveButton(R.string.yes_button_label, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                            getActivity().finish();

                        }
                    }).show();
        }

}
