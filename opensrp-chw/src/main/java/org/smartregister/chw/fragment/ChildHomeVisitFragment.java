package org.smartregister.chw.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.ChildProfileActivity;
import org.smartregister.chw.activity.ChildRegisterActivity;
import org.smartregister.chw.adapter.HomeVisitBirthAndIllnessDataAdapter;
import org.smartregister.chw.contract.ChildHomeVisitContract;
import org.smartregister.chw.custom_view.HomeVisitGrowthAndNutrition;
import org.smartregister.chw.custom_view.HomeVisitImmunizationView;
import org.smartregister.chw.presenter.ChildHomeVisitPresenter;
import org.smartregister.chw.util.BirthIllnessData;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.HomeVisitVaccineGroupDetails;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.activity.BaseFamilyProfileActivity;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.VaccineWrapper;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static org.smartregister.chw.util.ChildDBConstants.KEY.BIRTH_CERT;
import static org.smartregister.util.Utils.getValue;

public class ChildHomeVisitFragment extends DialogFragment implements View.OnClickListener, ChildHomeVisitContract.View {


    private static final String TAG = "ChildHomeVisitFragment";
    public static String DIALOG_TAG = "child_home_visit_dialog";
    Context context;
    CommonPersonObjectClient childClient;
    private TextView nameHeader, textViewBirthCertDueDate, textViewObsIllnessTitle, textViewObsIllnessDesc;
    private HomeVisitGrowthAndNutrition homeVisitGrowthAndNutritionLayout;
    private View viewBirthLine;
    public boolean allVaccineStateFullfilled = false;
    public boolean allVaccineDataLoaded = false;
    public boolean allServicesDataLoaded = false;
    private TextView submit;
    private HomeVisitImmunizationView homeVisitImmunizationView;
    private LinearLayout layoutBirthCertGroup, layoutIllnessGroup, homeVisitLayout;
    private ChildHomeVisitContract.Presenter presenter;
    private CircleImageView circleImageViewBirthStatus, circleImageViewIllnessStatus;
    private String birthCertGiven = BIRTH_CERT_TYPE.NOT_GIVEN.name();
    private JSONObject illnessJson;
    private JSONObject birthCertJson;
    private String jsonString;
    private boolean isEditMode = false;
    private String selectedForm;
    private RecyclerView recyclerViewBirthCertData, recyclerViewIllnessData;
    private ProgressBar progressBar;

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
        homeVisitLayout = view.findViewById(R.id.home_visit_layout);
        progressBar = view.findViewById(R.id.progress_bar);
        nameHeader = view.findViewById(R.id.textview_name_header);
        textViewBirthCertDueDate = view.findViewById(R.id.textview_birth_certification_name);
        textViewObsIllnessDesc = view.findViewById(R.id.textview_obser_illness_name);
        textViewObsIllnessTitle = view.findViewById(R.id.textview_obser_illness);
        textViewObsIllnessTitle.setText(Html.fromHtml(getString(R.string.observations_illness_episodes)));
        view.findViewById(R.id.close).setOnClickListener(this);
        viewBirthLine = view.findViewById(R.id.birth_line_view);
        submit = view.findViewById(R.id.textview_submit);
        circleImageViewBirthStatus = view.findViewById(R.id.birth_status_circle);
        circleImageViewIllnessStatus = view.findViewById(R.id.obs_illness_status_circle);
        layoutBirthCertGroup = view.findViewById(R.id.birth_cert_group);
        layoutIllnessGroup = view.findViewById(R.id.obs_illness_prevention_group);
        recyclerViewBirthCertData = view.findViewById(R.id.birth_cert_data_recycler);
        recyclerViewIllnessData = view.findViewById(R.id.illness_data_recycler);
        recyclerViewBirthCertData.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewIllnessData.setLayoutManager(new LinearLayoutManager(getActivity()));
        view.findViewById(R.id.textview_submit).setOnClickListener(this);
        layoutBirthCertGroup.setOnClickListener(this);
        layoutIllnessGroup.setOnClickListener(this);
        homeVisitGrowthAndNutritionLayout = view.findViewById(R.id.growth_and_nutrition_group);
        homeVisitImmunizationView = view.findViewById(R.id.home_visit_immunization_view);
        homeVisitImmunizationView.setActivity(getActivity());
        homeVisitImmunizationView.setChildClient(childClient);
        homeVisitImmunizationView.setEditMode(isEditMode);
        initializePresenter();
        ((ChildHomeVisitPresenter) presenter).setChildClient(childClient);
        assignNameHeader();
        submitButtonEnableDisable(false);
        updateGrowthData();
    }

    private void assignNameHeader() {
        String dob = org.smartregister.family.util.Utils.getValue(childClient.getColumnmaps(), DBConstants.KEY.DOB, false);
        String dobString = org.smartregister.family.util.Utils.getDuration(dob);
        String birthCert = getValue(childClient.getColumnmaps(), BIRTH_CERT, true);

        nameHeader.setText(String.format("%s %s %s, %s \u00B7 %s",
                getValue(childClient.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true),
                getValue(childClient.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true),
                getValue(childClient.getColumnmaps(), DBConstants.KEY.LAST_NAME, true),
                dobString,
                getString(R.string.home_visit)
        ));
        // dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : "";
        String status = ChildUtils.getBirthCertDueStatus(dob);

        if (!TextUtils.isEmpty(birthCert)) {
            layoutBirthCertGroup.setVisibility(View.GONE);
            viewBirthLine.setVisibility(View.GONE);
        } else {
            layoutBirthCertGroup.setVisibility(View.VISIBLE);
            viewBirthLine.setVisibility(View.VISIBLE);
            //DateTime ddd = Utils.dobStringToDateTime(dob);
            textViewBirthCertDueDate.setText(ChildUtils.dueOverdueCalculation(status, dob));

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
                selectedForm = "Birth";
                presenter.startBirthCertForm(birthCertJson);
                break;
            case R.id.obs_illness_prevention_group:
                selectedForm = "illness";
                presenter.startObsIllnessCertForm(illnessJson);
                break;
            case R.id.textview_submit:
                if (checkAllGiven()) {
//                    ChildUtils.updateClientStatusAsEvent(childClient.entityId(), Constants.EventType.CHILD_HOME_VISIT, ChildDBConstants.KEY.LAST_HOME_VISIT, System.currentTimeMillis() + "", Constants.TABLE_NAME.CHILD);


                    try {
                        JSONArray vaccineGroup = homeVisitImmunizationView.getGroupVaccinesGivenThisVisit();
                        JSONArray singleVaccine = homeVisitImmunizationView.getSingleVaccinesGivenThisVisit();

                        JSONObject singleVaccineObject = new JSONObject().put("singleVaccinesGiven", singleVaccine);
                        JSONObject vaccineGroupObject = new JSONObject().put("groupVaccinesGiven", vaccineGroup);
                        JSONObject service = new JSONObject((new Gson()).toJson(homeVisitGrowthAndNutritionLayout.returnSaveStateMap()));
                        if (illnessJson == null) {
                            illnessJson = new JSONObject();
                        }
                        ChildUtils.updateHomeVisitAsEvent(childClient.entityId(), Constants.EventType.CHILD_HOME_VISIT, Constants.TABLE_NAME.CHILD, singleVaccineObject, vaccineGroupObject, service, birthCertGiven, illnessJson, ChildDBConstants.KEY.LAST_HOME_VISIT, System.currentTimeMillis() + "");

//                        ChildUtils.addToHomeVisitTable(childClient.getCaseId(),singleVaccineObject,vaccineGroupObject,service,birthCertGiven,illnessJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (getActivity() instanceof ChildRegisterActivity) {
                        ((ChildRegisterActivity) getActivity()).refreshList(FetchStatus.fetched);
                    }
                    if (((ChildHomeVisitPresenter) presenter).getSaveSize() > 0) {
                        presenter.saveForm();
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
                .setTitle(getString(R.string.confirm_form_close))
                .setMessage(R.string.confirm_form_close_explanation)
                .setNegativeButton(com.vijay.jsonwizard.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        undoRecord();

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

    private void undoRecord() {
        Observable.zip( homeVisitGrowthAndNutritionLayout.undoGrowthData(), homeVisitImmunizationView.undoVaccine(), new BiFunction() {
                    @Override
                    public Object apply(Object o, Object o2) throws Exception {
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                })
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        progressBar.setVisibility(View.GONE);
                        dismiss();
                    }
                })
                .subscribe();
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

    public void progressBarInvisible() {
        if (allVaccineDataLoaded && allServicesDataLoaded) {
            progressBar.setVisibility(View.GONE);
            homeVisitLayout.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            homeVisitLayout.setVisibility(View.GONE);
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
        intent.putExtra(org.smartregister.family.util.Constants.WizardFormActivity.EnableOnCloseDialog, false);
        startActivityForResult(intent, org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    public void updateBirthStatusTick() {
        birthCertGiven = BIRTH_CERT_TYPE.GIVEN.name();
        try {
            birthCertJson = new JSONObject().put("birtCert", jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateBirthCertData();
    }


    @Override
    public void updateObsIllnessStatusTick() {
        try {
            illnessJson = new JSONObject().put("obsIllness", jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateStatusTick(circleImageViewIllnessStatus, true);
        updateIllnessData();
    }

    private HomeVisitBirthAndIllnessDataAdapter birthCertDataAdapter, illnessDataAdapter;

    private void updateBirthCertData() {
        ArrayList<BirthIllnessData> data = ((ChildHomeVisitPresenter) presenter).getBirthCertDataList();
        if (data.size() > 0) {
            BirthIllnessData birthIllnessData = data.get(0);
            if (birthIllnessData.isBirthCertHas()) {
                String message = birthIllnessData.getBirthCertDate() + " (" + birthIllnessData.getBirthCertNumber() + ")";
                textViewBirthCertDueDate.setText(message);
                updateStatusTick(circleImageViewBirthStatus, true);
            } else {
                textViewBirthCertDueDate.setText(getString(R.string.not_provided));
                updateStatusTick(circleImageViewBirthStatus, false);
            }

//            recyclerViewBirthCertData.setVisibility(View.VISIBLE);
//            birthCertDataAdapter = new HomeVisitBirthAndIllnessDataAdapter();
//            birthCertDataAdapter.setData(data);
//            recyclerViewBirthCertData.setAdapter(birthCertDataAdapter);
//            recyclerViewBirthCertData.setLayoutFrozen(true);

        }


    }

    private void updateIllnessData() {
        ArrayList<BirthIllnessData> data = ((ChildHomeVisitPresenter) presenter).getIllnessDataList();
        if (data.size() > 0) {
            textViewObsIllnessDesc.setVisibility(View.VISIBLE);
            BirthIllnessData birthIllnessData = data.get(0);
            String message = birthIllnessData.getIllnessDate() + ": " + birthIllnessData.getIllnessDescription() + "\n" + birthIllnessData.getActionTaken();
            textViewObsIllnessDesc.setText(message);
//            recyclerViewIllnessData.setVisibility(View.VISIBLE);
//            illnessDataAdapter = new HomeVisitBirthAndIllnessDataAdapter();
//            illnessDataAdapter.setData(data);
//            recyclerViewIllnessData.setAdapter(illnessDataAdapter);
//            recyclerViewIllnessData.setLayoutFrozen(true);

        } else {
            textViewObsIllnessDesc.setVisibility(View.GONE);
        }


    }

    private void updateStatusTick(CircleImageView imageView, boolean isCheck) {

        imageView.setImageResource(R.drawable.ic_checked);
        imageView.setColorFilter(getResources().getColor(R.color.white));
        imageView.setCircleBackgroundColor(getResources().getColor(
                ((isCheck) ? R.color.alert_complete_green : R.color.pnc_circle_yellow))
        );
        imageView.setBorderColor(getResources().getColor(
                ((isCheck) ? R.color.alert_complete_green : R.color.pnc_circle_yellow))
        );

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON:
                if (resultCode == Activity.RESULT_OK) {
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
//                else{
//                    if(selectedForm.equalsIgnoreCase("Birth")){
//                        updateStatusTick(circleImageViewBirthStatus, false);
//                        textViewBirthCertDueDate.setText(R.string.not_given);
//                    }else if(selectedForm.equalsIgnoreCase("illness")){
//                        updateStatusTick(circleImageViewIllnessStatus, false);
//                    }
//                }
                break;
        }
    }

    /**
     * show close dialog if user press back button instend of cross button
     */

    @Override
    public void onResume() {
        super.onResume();
        updateImmunizationState();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                updateGrowthData();
//            }
//        }, 100);
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

    private boolean isAllGrowthSelected() {
        return homeVisitGrowthAndNutritionLayout.isAllSelected();
    }
    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public enum BIRTH_CERT_TYPE {GIVEN, NOT_GIVEN}
}