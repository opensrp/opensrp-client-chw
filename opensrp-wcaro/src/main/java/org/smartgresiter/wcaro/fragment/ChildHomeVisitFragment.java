package org.smartgresiter.wcaro.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.activity.ChildProfileActivity;
import org.smartgresiter.wcaro.activity.ChildRegisterActivity;
import org.smartgresiter.wcaro.contract.ChildHomeVisitContract;
import org.smartgresiter.wcaro.custom_view.HomeVisitGrowthAndNutrition;
import org.smartgresiter.wcaro.custom_view.HomeVisitImmunizationView;
import org.smartgresiter.wcaro.presenter.ChildHomeVisitPresenter;
import org.smartgresiter.wcaro.util.ChildDBConstants;
import org.smartgresiter.wcaro.util.ChildUtils;
import org.smartgresiter.wcaro.util.Constants;
import org.smartgresiter.wcaro.util.HomeVisitVaccineGroupDetails;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.activity.BaseFamilyProfileActivity;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.VaccineWrapper;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.BIRTH_CERT;
import static org.smartregister.util.Utils.getValue;

public class ChildHomeVisitFragment extends DialogFragment implements View.OnClickListener, ChildHomeVisitContract.View {


    private static final String TAG = "ChildHomeVisitFragment";
    public static String DIALOG_TAG = "child_home_visit_dialog";
    Context context;
    CommonPersonObjectClient childClient;
    private TextView nameHeader;
    private HomeVisitGrowthAndNutrition homeVisitGrowthAndNutritionLayout;
    public boolean allVaccineStateFullfilled = false;
    private TextView submit;
    private HomeVisitImmunizationView homeVisitImmunizationView;
    private LinearLayout layoutBirthCertGroup, layoutIllnessGroup;
    private ChildHomeVisitContract.Presenter presenter;
    private CircleImageView circleImageViewBirthStatus, circleImageViewIllnessStatus;
    private String birthCertGiven=BIRTH_CERT_TYPE.NOT_GIVEN.name();
    private  JSONObject illnessJson;
    private String jsonString;

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_NoActionBar);
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
        circleImageViewBirthStatus = view.findViewById(R.id.birth_status_circle);
        circleImageViewIllnessStatus = view.findViewById(R.id.obs_illness_status_circle);
        layoutBirthCertGroup = view.findViewById(R.id.birth_cert_group);
        layoutIllnessGroup = view.findViewById(R.id.obs_illness_prevention_group);
        view.findViewById(R.id.textview_submit).setOnClickListener(this);
        layoutBirthCertGroup.setOnClickListener(this);
        layoutIllnessGroup.setOnClickListener(this);
        homeVisitGrowthAndNutritionLayout = view.findViewById(R.id.growth_and_nutrition_group);
        homeVisitImmunizationView = (HomeVisitImmunizationView) view.findViewById(R.id.home_visit_immunization_view);
        homeVisitImmunizationView.setActivity(getActivity());
        homeVisitImmunizationView.setChildClient(childClient);
        initializePresenter();
        ((ChildHomeVisitPresenter) presenter).setChildClient(childClient);
        assignNameHeader();
        submitButtonEnableDisable(false);
    }

    private void assignNameHeader() {
        String dobString = org.smartregister.family.util.Utils.getDuration(org.smartregister.family.util.Utils.getValue(childClient.getColumnmaps(), DBConstants.KEY.DOB, false));
        String birthCert = getValue(childClient.getColumnmaps(), BIRTH_CERT, true);

        nameHeader.setText(String.format("%s %s, %s - Home Visit",
                getValue(childClient.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true),
                getValue(childClient.getColumnmaps(), DBConstants.KEY.LAST_NAME, true),
                dobString
        ));
        if (!TextUtils.isEmpty(birthCert)) {
            layoutBirthCertGroup.setVisibility(View.GONE);
        }
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
        return new ChildHomeVisitFragment();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.birth_cert_group:

                presenter.startBirthCertForm();
                break;
            case R.id.obs_illness_prevention_group:
                presenter.startObsIllnessCertForm();
                break;
            case R.id.textview_submit:
                if (checkAllGiven()) {
//                    ChildUtils.updateClientStatusAsEvent(childClient.entityId(), Constants.EventType.CHILD_HOME_VISIT, ChildDBConstants.KEY.LAST_HOME_VISIT, System.currentTimeMillis() + "", Constants.TABLE_NAME.CHILD);

                    if (getActivity() instanceof ChildRegisterActivity) {
                        ((ChildRegisterActivity) getActivity()).refreshList(FetchStatus.fetched);
                    }
                    if (((ChildHomeVisitPresenter) presenter).getSaveSize() > 0) {
                        presenter.saveForm();
                    }
                    try {
                        JSONArray vaccineGroup = homeVisitImmunizationView.getGroupVaccinesGivenThisVisit();
                        JSONArray singleVaccine = homeVisitImmunizationView.getSingleVaccinesGivenThisVisit();

                        JSONObject singleVaccineObject = new JSONObject().put("singleVaccinesGiven",singleVaccine);
                        JSONObject vaccineGroupObject = new JSONObject().put("groupVaccinesGiven",vaccineGroup);
                        JSONObject service = new JSONObject((new Gson()).toJson(homeVisitGrowthAndNutritionLayout.returnSaveStateMap()));
                        ChildUtils.updateHomeVisitAsEvent(childClient.entityId(), Constants.EventType.CHILD_HOME_VISIT, Constants.TABLE_NAME.CHILD, singleVaccineObject, vaccineGroupObject, service, birthCertGiven, illnessJson,ChildDBConstants.KEY.LAST_HOME_VISIT,System.currentTimeMillis() + "");

//                        ChildUtils.addToHomeVisitTable(childClient.getCaseId(),singleVaccineObject,vaccineGroupObject,service,birthCertGiven,illnessJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dismiss();
                }
                break;
            case R.id.close:
                showCloseDialog();
                break;
            case R.id.layout_add_other_family_member:
                ((BaseFamilyProfileActivity) context).startFormActivity(Constants.JSON_FORM.FAMILY_MEMBER_REGISTER, null, null);
                break;
        }
    }

    private void showCloseDialog() {
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


    @Override
    public ChildHomeVisitContract.Presenter initializePresenter() {
        presenter = new ChildHomeVisitPresenter(this);
        return presenter;
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(context, org.smartregister.family.util.Utils.metadata().familyMemberFormActivity);
        intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        Form form = new Form();
        form.setWizard(false);
        form.setActionBarBackground(org.smartregister.family.R.color.customAppThemeBlue);

        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

        startActivityForResult(intent, org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    public void updateBirthStatusTick() {
       birthCertGiven= BIRTH_CERT_TYPE.GIVEN.name();
        updateStatusTick(circleImageViewBirthStatus, true);
    }

    @Override
    public void updateObsIllnessStatusTick() {
        try {
            illnessJson=new JSONObject().put("obsIllness",jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateStatusTick(circleImageViewIllnessStatus, true);
    }

    private void updateStatusTick(CircleImageView imageView, boolean isCheck) {
        if (isCheck) {
            imageView.setImageResource(R.drawable.ic_checked);
            imageView.setColorFilter(getResources().getColor(R.color.white));
            imageView.setCircleBackgroundColor(getResources().getColor(R.color.alert_complete_green));
            imageView.setBorderColor(getResources().getColor(R.color.alert_complete_green));

        } else {
            imageView.setImageResource(R.drawable.ic_checked);
            imageView.setColorFilter(getResources().getColor(R.color.white));
            imageView.setCircleBackgroundColor(getResources().getColor(R.color.pnc_circle_yellow));
            imageView.setBorderColor(getResources().getColor(R.color.pnc_circle_yellow));
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == Activity.RESULT_OK) {
            try {
                jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                Log.d("JSONResult", jsonString);

                JSONObject form = new JSONObject(jsonString);
                if (form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.BIRTH_CERTIFICATION)
                        || form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.OBS_ILLNESS)
                        ) {
                    presenter.generateBirthIllnessForm(jsonString);
                }
            } catch (Exception e) {
                Log.e(DIALOG_TAG, Log.getStackTraceString(e));
            }

        }
    }

    /**
     * show close dialog if user press back button instend of cross button
     */

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
        if (getView() == null) return;
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    showCloseDialog();
                    return true;
                }
                return false;
            }
        });

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
    public enum BIRTH_CERT_TYPE{GIVEN, NOT_GIVEN}
}