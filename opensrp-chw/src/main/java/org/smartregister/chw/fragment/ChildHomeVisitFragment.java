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

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.ChildProfileActivity;
import org.smartregister.chw.activity.ChildRegisterActivity;
import org.smartregister.chw.contract.ChildHomeVisitContract;
import org.smartregister.chw.custom_view.HomeVisitGrowthAndNutrition;
import org.smartregister.chw.custom_view.ImmunizationView;
import org.smartregister.chw.presenter.ChildHomeVisitPresenter;
import org.smartregister.chw.rule.BirthCertRule;
import org.smartregister.chw.util.BirthIllnessData;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.activity.BaseFamilyProfileActivity;
import org.smartregister.family.util.DBConstants;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static org.smartregister.chw.util.ChildDBConstants.KEY.BIRTH_CERT;
import static org.smartregister.chw.util.Utils.dd_MMM_yyyy;
import static org.smartregister.util.Utils.getValue;

public class ChildHomeVisitFragment extends DialogFragment implements View.OnClickListener, ChildHomeVisitContract.View {


    private static final String TAG = "ChildHomeVisitFragment";
    public static String DIALOG_TAG = "child_home_visit_dialog";
    private Context context;
    private CommonPersonObjectClient childClient;
    private TextView nameHeader;
    private TextView textViewBirthCertDueDate;
    private TextView textViewObsIllnessDesc;
    private HomeVisitGrowthAndNutrition homeVisitGrowthAndNutritionLayout;
    private View viewBirthLine;
    public boolean allVaccineDataLoaded = false;
    public boolean allServicesDataLoaded = false;
    private TextView submit;
    private ImmunizationView immunizationView;
    private LinearLayout layoutBirthCertGroup;
    private LinearLayout homeVisitLayout;
    private ChildHomeVisitContract.Presenter presenter;
    private CircleImageView circleImageViewBirthStatus, circleImageViewIllnessStatus;
    private JSONObject illnessJson;
    private JSONObject birthCertJson;
    private String jsonString;
    private boolean isEditMode = false;
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

        return (ViewGroup) inflater.inflate(R.layout.fragment_child_home_visit, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeVisitLayout = view.findViewById(R.id.home_visit_layout);
        progressBar = view.findViewById(R.id.progress_bar);
        nameHeader = view.findViewById(R.id.textview_name_header);
        textViewBirthCertDueDate = view.findViewById(R.id.textview_birth_certification_name);
        textViewObsIllnessDesc = view.findViewById(R.id.textview_obser_illness_name);
        TextView textViewObsIllnessTitle = view.findViewById(R.id.textview_obser_illness);
        textViewObsIllnessTitle.setText(Html.fromHtml(getString(R.string.observations_illness_episodes)));
        view.findViewById(R.id.close).setOnClickListener(this);
        viewBirthLine = view.findViewById(R.id.birth_line_view);
        submit = view.findViewById(R.id.textview_submit);
        circleImageViewBirthStatus = view.findViewById(R.id.birth_status_circle);
        circleImageViewIllnessStatus = view.findViewById(R.id.obs_illness_status_circle);
        layoutBirthCertGroup = view.findViewById(R.id.birth_cert_group);
        LinearLayout layoutIllnessGroup = view.findViewById(R.id.obs_illness_prevention_group);
        RecyclerView recyclerViewBirthCertData = view.findViewById(R.id.birth_cert_data_recycler);
        RecyclerView recyclerViewIllnessData = view.findViewById(R.id.illness_data_recycler);
        recyclerViewBirthCertData.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewIllnessData.setLayoutManager(new LinearLayoutManager(getActivity()));
        view.findViewById(R.id.textview_submit).setOnClickListener(this);
        layoutBirthCertGroup.setOnClickListener(this);
        layoutIllnessGroup.setOnClickListener(this);
        homeVisitGrowthAndNutritionLayout = view.findViewById(R.id.growth_and_nutrition_group);
        immunizationView = view.findViewById(R.id.immunization_view);
        initializePresenter();
        ((ChildHomeVisitPresenter) presenter).setChildClient(childClient);
        assignNameHeader();
        updateGrowthData();
        if (isEditMode) {
            immunizationView.setChildClient(this, getActivity(), childClient, true);
            ((ChildHomeVisitPresenter) presenter).getLastEditData();
            submitButtonEnableDisable(false);
        } else {
            immunizationView.setChildClient(this, getActivity(), childClient, false);
            submitButtonEnableDisable(false);
        }
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

        if (!isEditMode && !TextUtils.isEmpty(birthCert)) {
            layoutBirthCertGroup.setVisibility(View.GONE);
            viewBirthLine.setVisibility(View.GONE);
        } else {
            layoutBirthCertGroup.setVisibility(View.VISIBLE);
            viewBirthLine.setVisibility(View.VISIBLE);
            //DateTime ddd = Utils.dobStringToDateTime(dob);
            //check wether it's due or overdue - overdue is 12m+
            BirthCertRule birthCertRule = new BirthCertRule(dob);
            if (birthCertRule.isOverdue(12)) {
                Date date = org.smartregister.family.util.Utils.dobStringToDate(dob);
                textViewBirthCertDueDate.setTextColor(getResources().getColor(R.color.alert_urgent_red));
                textViewBirthCertDueDate.setText(String.format("%s%s", getString(R.string.overdue), dd_MMM_yyyy.format(date)));
            } else {
                Date date = org.smartregister.family.util.Utils.dobStringToDate(dob);
                textViewBirthCertDueDate.setTextColor(getResources().getColor(R.color.grey));
                textViewBirthCertDueDate.setText(String.format("%s%s", getString(R.string.due), dd_MMM_yyyy.format(date)));

            }

        }
    }


    private void updateGrowthData() {
        homeVisitGrowthAndNutritionLayout.setData(this, getActivity().getFragmentManager(), childClient, isEditMode);
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
                // String selectedForm = "Birth";
                presenter.startBirthCertForm(birthCertJson);
                break;
            case R.id.obs_illness_prevention_group:
                // selectedForm = "illness";
                presenter.startObsIllnessCertForm(illnessJson);
                break;
            case R.id.textview_submit:
                if (checkAllGiven()) {
                    saveCommonData().subscribeOn(Schedulers.io())
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
                                    if (getActivity() instanceof ChildRegisterActivity) {
                                        ((ChildRegisterActivity) getActivity()).refreshList(FetchStatus.fetched);
                                    }
                                    if (isEditMode) {
                                        saveData();
                                        return;
                                    }
                                    dismiss();
                                }
                            })
                            .subscribe();
                }
                break;
            case R.id.close:
                showCloseDialog();
                break;
            case R.id.layout_add_other_family_member:
                ((BaseFamilyProfileActivity) context).startFormActivity(Constants.JSON_FORM.FAMILY_MEMBER_REGISTER, null, null);
                break;
            default:
                break;
        }
    }

    private Observable saveCommonData() {
        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {
                try {
                    //JSONArray vaccineGroup = homeVisitImmunizationView.getGroupVaccinesGivenThisVisit();
                    //JSONArray singleVaccine = homeVisitImmunizationView.getSingleVaccinesGivenThisVisit();
                    //not needed given vaccine track.
                    JSONObject singleVaccineObject = new JSONObject().put("singleVaccinesGiven", new JSONArray());
                    JSONObject vaccineGroupObject = new JSONObject().put("groupVaccinesGiven", new JSONArray());
                    //end of not used
                    JSONObject vaccineNotGivenObject;
                    if (isEditMode) {
                        vaccineNotGivenObject = new JSONObject().put("vaccineNotGiven", new JSONArray(ChildUtils.gsonConverter.toJson(immunizationView.getNotGivenVaccine())));
                    } else {
                        vaccineNotGivenObject = new JSONObject().put("vaccineNotGiven", new JSONArray(ChildUtils.gsonConverter.toJson(immunizationView.getNotGivenVaccine())));

                    }
                    JSONObject service = new JSONObject(ChildUtils.gsonConverter.toJson(homeVisitGrowthAndNutritionLayout.returnSaveStateMap()));
                    JSONObject serviceNotGiven = new JSONObject(ChildUtils.gsonConverter.toJson(homeVisitGrowthAndNutritionLayout.returnNotSaveStateMap()));

                    if (illnessJson == null) {
                        illnessJson = new JSONObject();
                    }
                    if (birthCertJson == null) {
                        birthCertJson = new JSONObject();
                    }
                    ChildUtils.updateHomeVisitAsEvent(childClient.entityId(), Constants.EventType.CHILD_HOME_VISIT, Constants.TABLE_NAME.CHILD, singleVaccineObject, vaccineGroupObject, vaccineNotGivenObject, service, serviceNotGiven, birthCertJson, illnessJson, ChildDBConstants.KEY.LAST_HOME_VISIT, System.currentTimeMillis() + "");
                    if (((ChildHomeVisitPresenter) presenter).getSaveSize() > 0) {
                        ((ChildHomeVisitPresenter) presenter).saveForm();
                    }
                    emitter.onComplete();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
        Observable undoGrowthData = homeVisitGrowthAndNutritionLayout.undoGrowthData();
        Observable undoVaccine;
        undoVaccine = immunizationView.undoVaccine();
        Observable.zip(undoGrowthData, undoVaccine, new BiFunction() {
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

    private void saveData() {
        Observable.zip(immunizationView.undoPreviousGivenVaccine(), immunizationView.saveGivenThisVaccine(), new BiFunction() {
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


    public void submitButtonEnableDisable(boolean isEnable) {
        if (isEnable) {
            submit.setAlpha(1.0f);
        } else {
            submit.setAlpha(0.3f);
        }

    }

    private boolean checkAllGiven() {
        //if(isEditMode) return true;
        org.smartregister.util.Log.logError("SUBMIT_BTN", "checkAllGiven>>" + isAllImmunizationSelected() + ": " + isAllGrowthSelected());
        return isAllImmunizationSelected() && isAllGrowthSelected();
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

    public void forcfullyProgressBarInvisible() {
        progressBar.setVisibility(View.GONE);
        homeVisitLayout.setVisibility(View.VISIBLE);
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
        try {
            if (TextUtils.isEmpty(jsonString)) {
                birthCertJson = new JSONObject().put("birtCert", ((ChildHomeVisitPresenter) presenter).getEditedBirthCertFormJson());
            } else {
                birthCertJson = new JSONObject().put("birtCert", jsonString);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateBirthCertData();
    }


    @Override
    public void updateObsIllnessStatusTick() {
        try {
            if (TextUtils.isEmpty(jsonString)) {
                illnessJson = new JSONObject().put("birtCert", ((ChildHomeVisitPresenter) presenter).getEditedIllnessJson());
            } else {
                illnessJson = new JSONObject().put("obsIllness", jsonString);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateStatusTick(circleImageViewIllnessStatus, true);
        updateIllnessData();
    }

    private void updateBirthCertData() {
        ArrayList<BirthIllnessData> data = ((ChildHomeVisitPresenter) presenter).getBirthCertDataList();
        if (data.size() > 0) {
            BirthIllnessData birthIllnessData = data.get(0);
            if (birthIllnessData.isBirthCertHas()) {
                String message = birthIllnessData.getBirthCertDate() + " (" + birthIllnessData.getBirthCertNumber() + ")";
                textViewBirthCertDueDate.setText(message);
                updateStatusTick(circleImageViewBirthStatus, true);
            } else {
                textViewBirthCertDueDate.setText(getString(R.string.not_done));
                updateStatusTick(circleImageViewBirthStatus, false);
            }

        }


    }

    private void updateIllnessData() {
        ArrayList<BirthIllnessData> data = ((ChildHomeVisitPresenter) presenter).getIllnessDataList();
        if (data.size() > 0) {
            textViewObsIllnessDesc.setVisibility(View.VISIBLE);
            BirthIllnessData birthIllnessData = data.get(0);
            String message = birthIllnessData.getIllnessDate() + ": " + birthIllnessData.getIllnessDescription() + "\n" + birthIllnessData.getActionTaken();
            textViewObsIllnessDesc.setText(message);

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
        if (!isEditMode) {
            updateImmunizationState();
        }
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
        if (immunizationView.getVisibility() == View.VISIBLE) immunizationView.updatePosition();
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

    private boolean isAllImmunizationSelected() {
        return immunizationView.isAllSelected();
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;

    }

    public enum BIRTH_CERT_TYPE {GIVEN, NOT_GIVEN}
}