package org.smartgresiter.wcaro.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.activity.ChildProfileActivity;
import org.smartgresiter.wcaro.activity.ChildRegisterActivity;
import org.smartgresiter.wcaro.contract.ChildRegisterContract;
import org.smartgresiter.wcaro.custom_view.HomeVisitGrowthAndNutrition;
import org.smartgresiter.wcaro.custom_view.HomeVisitImmunizationView;
import org.smartgresiter.wcaro.interactor.ChildRegisterInteractor;
import org.smartgresiter.wcaro.model.ChildRegisterModel;
import org.smartgresiter.wcaro.task.VaccinationAsyncTask;
import org.smartgresiter.wcaro.util.ChildDBConstants;
import org.smartgresiter.wcaro.util.ChildUtils;
import org.smartgresiter.wcaro.util.Constants;
import org.smartgresiter.wcaro.util.HomeVisitVaccineGroupDetails;
import org.smartgresiter.wcaro.util.JsonFormUtils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.activity.BaseFamilyProfileActivity;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.util.FormUtils;
import org.smartregister.util.Utils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.smartregister.util.Utils.getValue;

public class ChildHomeVisitFragment extends DialogFragment implements View.OnClickListener, ChildRegisterContract.InteractorCallBack {


    public static String DIALOG_TAG = "child_home_visit_dialog";
    protected ProgressDialog progressDialog;
    Context context;
    String childBaseEntityId;
    CommonPersonObjectClient childClient;
    private VaccinationAsyncTask vaccinationAsyncTask;
    private TextView nameHeader;
    private TextView textview_group_immunization_secondary_text;
    private TextView textview_group_immunization_primary_text;
    private TextView textview_immunization_primary_text;
    private TextView textview_immunization_secondary_text;
    private LinearLayout single_immunization_group;
    ArrayList<VaccineWrapper> notGivenVaccines = new ArrayList<VaccineWrapper>();
    private CircleImageView immunization_status_circle;
    private CircleImageView immunization_group_status_circle;
    private LinearLayout multiple_immunization_group;
    private HomeVisitGrowthAndNutrition homeVisitGrowthAndNutritionLayout;
    public boolean allVaccineStateFullfilled = false;
    private TextView submit;
    private ArrayList<VaccineWrapper> vaccinesGivenThisVisit = new ArrayList<VaccineWrapper>();
    private HomeVisitImmunizationView homeVisitImmunizationView;


    public void setContext(Context context) {
        this.context = context;
    }

    public void setChildBaseEntityId(String childBaseEntityId) {
        this.childBaseEntityId = childBaseEntityId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_NoActionBar);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.fragment_child_home_visit, container, false);

        return dialogView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameHeader = (TextView) view.findViewById(R.id.textview_name_header);
        view.findViewById(R.id.close).setOnClickListener(this);
        submit = (TextView) view.findViewById(R.id.textview_submit);
        view.findViewById(R.id.textview_submit).setOnClickListener(this);

        homeVisitGrowthAndNutritionLayout = view.findViewById(R.id.growth_and_nutrition_group);

        homeVisitImmunizationView = (HomeVisitImmunizationView) view.findViewById(R.id.home_visit_immunization_view);
        homeVisitImmunizationView.setActivity(getActivity());
        homeVisitImmunizationView.setChildClient(childClient);
        assignNameHeader();
        submitButtonEnableDisable(false);
    }

    private void assignNameHeader() {
        String dobString = org.smartregister.family.util.Utils.getDuration(org.smartregister.family.util.Utils.getValue(childClient.getColumnmaps(), DBConstants.KEY.DOB, false));

        nameHeader.setText(
                getValue(childClient.getColumnmaps(), "first_name", true) + " " +
                        getValue(childClient.getColumnmaps(), "last_name", true) + ", " +
                        dobString + " - Home Visit"
        );
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
//        try {
//            // Instantiate the WeightActionListener so we can send events to the host
//            listener = (WeightActionListener) activity;
//        } catch (ClassCastException e) {
//            // The activity doesn't implement the interface, throw exception
//            throw new ClassCastException(activity.toString()
//                    + " must implementfonre WeightActionListener");
//        }
    }

    private void updateGrowthData() {
        homeVisitGrowthAndNutritionLayout.setData(this, getActivity().getFragmentManager(), childClient);
    }


    @Override
    public void onStart() {
        super.onStart();
        // without a handler, the window sizes itself correctly
        // but the keyboard does not show up
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getDialog().getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

            }
        });

    }


    public static ChildHomeVisitFragment newInstance() {
        ChildHomeVisitFragment addMemberFragment = new ChildHomeVisitFragment();
        return addMemberFragment;
    }


    @Override
    public void onClick(View v) {
        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        String dobString = org.smartregister.util.Utils.getValue(childClient.getColumnmaps(), DBConstants.KEY.DOB, false);

        switch (v.getId()) {
            case R.id.textview_submit:
                if (checkAllGiven()) {
                    ChildUtils.updateClientStatusAsEvent(childClient.entityId(), Constants.EventType.CHILD_HOME_VISIT, ChildDBConstants.KEY.LAST_HOME_VISIT, System.currentTimeMillis(), Constants.TABLE_NAME.CHILD);

                    if (getActivity() instanceof ChildRegisterActivity) {
                        ((ChildRegisterActivity) getActivity()).refreshList(FetchStatus.fetched);
                    }
                    dismiss();
                }
                break;
            case R.id.close:
                AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.AppThemeAlertDialog)
                        .setTitle("Undo Changes and Exit")
                        .setMessage("Would you like to undo the changes in this home visit and exit ?")
                        .setNegativeButton(com.vijay.jsonwizard.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                resetGrowthData();
                                undoGivenVaccines();
                                dismiss();
                            }
                        })
                        .setPositiveButton(com.vijay.jsonwizard.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        })
                        .create();

                dialog.show();
                break;
            case R.id.layout_add_other_family_member:
                ((BaseFamilyProfileActivity) context).startFormActivity(Constants.JSON_FORM.FAMILY_MEMBER_REGISTER, null, null);
                break;
        }
    }

    private void submitButtonEnableDisable(boolean isEnable) {
        if (isEnable) {
            submit.setAlpha(1.0f);
        } else {
            submit.setAlpha(0.3f);
        }

    }

    private boolean checkAllGiven() {
        return allVaccineStateFullfilled && isAllGrowthSelected();
//        boolean checkallgiven = false;
//        if(allVaccineStateFullfilled){
//            checkallgiven = true;
//        }else{
//            checkallgiven = false;
//        }
//        if(isAllGrowthSelected()){
//            checkallgiven = true;
//        }else{
//            checkallgiven = false;
//        }
//
//        return  checkallgiven;
    }

    public void checkIfSubmitIsToBeEnabled() {
        if (checkAllGiven()) {
            submitButtonEnableDisable(true);
        } else {
            submitButtonEnableDisable(false);
        }
    }

    private ArrayList<VaccineWrapper> createVaccineWrappers(HomeVisitVaccineGroupDetails vaccines) {

        ArrayList<VaccineWrapper> vaccineWrappers = new ArrayList<VaccineWrapper>();
        for (VaccineRepo.Vaccine vaccine : vaccines.getDueVaccines()) {
            VaccineWrapper vaccineWrapper = new VaccineWrapper();
            vaccineWrapper.setVaccine(vaccine);
            vaccineWrapper.setName(vaccine.display());
            vaccineWrapper.setDefaultName(vaccine.display());
            vaccineWrappers.add(vaccineWrapper);
        }

        return vaccineWrappers;
    }

    public void displayShortToast(int resourceId) {
        Utils.showShortToast(context, this.getString(resourceId));
    }

    public void displayToast(int stringID) {
        Utils.showShortToast(context, this.getString(stringID));
    }

    @Override
    public void onNoUniqueId() {
        displayShortToast(R.string.no_unique_id);
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId, String familyId) {
        try {
            startForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight(), familyId);
        } catch (Exception e) {
            Log.e(DIALOG_TAG, Log.getStackTraceString(e));
            displayToast(R.string.error_unable_to_start_form);
        }
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        hideProgressDialog();
    }

    public void showProgressDialog(int saveMessageStringIdentifier) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setTitle(getString(saveMessageStringIdentifier));
            progressDialog.setMessage(getString(org.smartregister.R.string.please_wait_message));
        }
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    ChildRegisterInteractor interactor;

    public void startForm(String formName, String entityId, String metadata, String currentLocationId, String familyId) throws Exception {
        interactor = new ChildRegisterInteractor();
        if (isBlank(entityId)) {
            Triple<String, String, String> triple = Triple.of(formName, metadata, currentLocationId);
            interactor.getNextUniqueId(triple, this, familyId);
            return;
        }

        JSONObject form = getFormAsJson(formName, entityId, currentLocationId, familyId);
        startFormActivity(form);
    }

    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(context, org.smartregister.family.util.Utils.metadata().familyMemberFormActivity);
        intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        Form form = new Form();
        form.setWizard(false);
        form.setActionBarBackground(org.smartregister.family.R.color.family_actionbar);

        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

        startActivityForResult(intent, org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId, String familyID) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        return JsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId, familyID);
    }

    public void saveForm(String jsonString, boolean isEditMode) {
        ChildRegisterModel model = new ChildRegisterModel();
        try {

            showProgressDialog(R.string.saving_dialog_title);

            Pair<Client, Event> pair = model.processRegistration(jsonString);
            if (pair == null) {
                return;
            }

            interactor.saveRegistration(pair, jsonString, isEditMode, this);

        } catch (Exception e) {
            Log.e(DIALOG_TAG, Log.getStackTraceString(e));
        }
    }

    FormUtils formUtils = null;

    private FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(org.smartregister.family.util.Utils.context().applicationContext());
            } catch (Exception e) {
                Log.e(ChildRegisterModel.class.getCanonicalName(), e.getMessage(), e);
            }
        }
        return formUtils;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == Activity.RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                Log.d("JSONResult", jsonString);

                JSONObject form = new JSONObject(jsonString);
                if (form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(org.smartregister.family.util.Utils.metadata().familyRegister.registerEventType)
                        || form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals("Child Registration")
                        ) {
                    saveForm(jsonString, false);
                }
            } catch (Exception e) {
                Log.e(DIALOG_TAG, Log.getStackTraceString(e));
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateImmunizationState();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateGrowthData();
            }
        }, 100);

    }

    public void updateImmunizationState() {
        homeVisitImmunizationView.updateImmunizationState();
    }

    public void setChildClient(CommonPersonObjectClient childClient) {
        this.childClient = childClient;
    }

    @Override
    public void onDestroy() {
        if (context instanceof ChildProfileActivity) {
            ChildProfileActivity activity = (ChildProfileActivity) context;
            activity.updateImmunizationData();
        }
        super.onDestroy();
    }

    private void resetGrowthData() {
        homeVisitGrowthAndNutritionLayout.resetAll();
    }

    private boolean isAllGrowthSelected() {
        return homeVisitGrowthAndNutritionLayout.isAllSelected();
    }

    public void undoGivenVaccines() {
        homeVisitImmunizationView.undoVaccines();

    }
}