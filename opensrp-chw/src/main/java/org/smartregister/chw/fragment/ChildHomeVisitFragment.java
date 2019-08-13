package org.smartregister.chw.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
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
import org.smartregister.chw.adapter.ServiceTaskAdapter;
import org.smartregister.chw.contract.ChildHomeVisitContract;
import org.smartregister.chw.custom_view.HomeVisitGrowthAndNutrition;
import org.smartregister.chw.custom_view.ImmunizationView;
import org.smartregister.chw.listener.OnClickServiceTaskAdapter;
import org.smartregister.chw.listener.OnUpdateServiceTask;
import org.smartregister.chw.presenter.ChildHomeVisitPresenter;
import org.smartregister.chw.rule.BirthCertRule;
import org.smartregister.chw.util.BirthCertDataModel;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.chw.util.ObsIllnessDataModel;
import org.smartregister.chw.util.ServiceTask;
import org.smartregister.chw.util.TaskServiceCalculate;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.activity.BaseFamilyProfileActivity;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.util.FormUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.smartregister.chw.util.ChildDBConstants.KEY.BIRTH_CERT;
import static org.smartregister.chw.util.ChildDBConstants.KEY.VACCINE_CARD;
import static org.smartregister.chw.util.Utils.dd_MMM_yyyy;
import static org.smartregister.family.util.Utils.metadata;
import static org.smartregister.util.Utils.dobStringToDate;
import static org.smartregister.util.Utils.getDuration;
import static org.smartregister.util.Utils.getValue;

public class ChildHomeVisitFragment extends DialogFragment implements View.OnClickListener, ChildHomeVisitContract.View {

    private static final String TAG = "ChildHomeVisitFragment";
    public static String DIALOG_TAG = "child_home_visit_dialog";
    private static IntentFilter sIntentFilter;

    static {
        sIntentFilter = new IntentFilter();
        sIntentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        sIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        sIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
    }

    public boolean allVaccineDataLoaded = false;
    public boolean allServicesDataLoaded = false;
    private Context context;
    private CommonPersonObjectClient childClient;
    private TextView nameHeader;
    private TextView textViewBirthCertDueDate, textViewVaccineCardText;
    private TextView textViewObsIllnessDesc;
    private HomeVisitGrowthAndNutrition homeVisitGrowthAndNutritionLayout;
    private View viewBirthLine, viewVaccineCardLine;
    private TextView submit;
    private ImmunizationView immunizationView;
    private LinearLayout layoutBirthCertGroup, layoutVaccineCard;
    private LinearLayout homeVisitLayout;
    private ChildHomeVisitContract.Presenter presenter;
    private CircleImageView circleImageViewBirthStatus, circleImageViewIllnessStatus, circleImageViewVaccineCard;
    private JSONObject illnessJson;
    private JSONObject birthCertJson;
    private boolean isEditMode = false;
    private final BroadcastReceiver mDateTimeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            assert action != null;
            if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                    action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                assignNameHeader();

            }
        }
    };
    private ProgressBar progressBar;
    private RecyclerView taskServiceRecyclerView;
    private ServiceTaskAdapter serviceTaskAdapter;
    private AppExecutors appExecutors;
    private Flavor flavor = new ChildHomeVisitFragmentFlv();
    private OnUpdateServiceTask onUpdateServiceTask = new OnUpdateServiceTask() {
        @Override
        public void onUpdateServiceTask(ServiceTask serviceTask) {
            updateTaskService();
            checkIfSubmitIsToBeEnabled();
        }
    };

    public static ChildHomeVisitFragment newInstance() {
        return new ChildHomeVisitFragment();
    }

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
        textViewVaccineCardText = view.findViewById(R.id.textview_vc_name);
        layoutVaccineCard = view.findViewById(R.id.vc_group);
        viewVaccineCardLine = view.findViewById(R.id.vc_line_view);
        textViewBirthCertDueDate = view.findViewById(R.id.textview_birth_certification_name);
        textViewObsIllnessDesc = view.findViewById(R.id.textview_obser_illness_name);
        taskServiceRecyclerView = view.findViewById(R.id.task_service_recycler_view);
        taskServiceRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        TextView textViewObsIllnessTitle = view.findViewById(R.id.textview_obser_illness);
        textViewObsIllnessTitle.setText(Html.fromHtml(getString(R.string.observations_illness_episodes)));
        view.findViewById(R.id.close).setOnClickListener(this);
        view.findViewById(R.id.vc_group).setOnClickListener(this);
        viewBirthLine = view.findViewById(R.id.birth_line_view);
        submit = view.findViewById(R.id.textview_submit);
        circleImageViewBirthStatus = view.findViewById(R.id.birth_status_circle);
        circleImageViewIllnessStatus = view.findViewById(R.id.obs_illness_status_circle);
        circleImageViewVaccineCard = view.findViewById(R.id.vc_status_circle);
        layoutBirthCertGroup = view.findViewById(R.id.birth_cert_group);
        LinearLayout layoutIllnessGroup = view.findViewById(R.id.obs_illness_prevention_group);
        if (flavor.onObsIllnessVisibility()) {
            layoutIllnessGroup.setVisibility(View.VISIBLE);
        } else {
            layoutIllnessGroup.setVisibility(View.GONE);
        }
        RecyclerView recyclerViewBirthCertData = view.findViewById(R.id.birth_cert_data_recycler);
        RecyclerView recyclerViewIllnessData = view.findViewById(R.id.illness_data_recycler);
        recyclerViewBirthCertData.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewIllnessData.setLayoutManager(new LinearLayoutManager(getActivity()));
        view.findViewById(R.id.textview_submit).setOnClickListener(this);
        layoutBirthCertGroup.setOnClickListener(this);
        layoutVaccineCard.setOnClickListener(this);
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
        getActivity().registerReceiver(mDateTimeChangedReceiver, sIntentFilter);
        appExecutors = new AppExecutors();
    }

    private void assignNameHeader() {
        if (childClient == null || childClient.getColumnmaps() == null)
            return;

        String dob = getValue(childClient.getColumnmaps(), DBConstants.KEY.DOB, false);
        String dobString = Utils.getTranslatedDate(getDuration(dob),context);
        String birthCert = getValue(childClient.getColumnmaps(), BIRTH_CERT, true);

        nameHeader.setText(String.format("%s %s %s, %s \u00B7 %s",
                getValue(childClient.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true),
                getValue(childClient.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true),
                getValue(childClient.getColumnmaps(), DBConstants.KEY.LAST_NAME, true),
                dobString,
                getString(R.string.home_visit)
        ));
        vaccineCardVisibility(dob);

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
                Date date = dobStringToDate(dob);
                textViewBirthCertDueDate.setTextColor(getResources().getColor(R.color.alert_urgent_red));
                textViewBirthCertDueDate.setText(String.format("%s%s", getString(R.string.overdue), dd_MMM_yyyy.format(date)));
            } else {
                Date date = dobStringToDate(dob);
                textViewBirthCertDueDate.setTextColor(getResources().getColor(R.color.grey));
                textViewBirthCertDueDate.setText(String.format("%s%s", getString(R.string.due), dd_MMM_yyyy.format(date)));

            }

        }
    }

    /**
     * vaccine card will be not visible when expired(dob>24month) or already given.
     * For edit it'll display the last state or present status.
     *
     * @param dob
     */

    private void vaccineCardVisibility(String dob) {

        String vaccineCard = getValue(childClient.getColumnmaps(), VACCINE_CARD, true);

        BirthCertRule birthCertRule = new BirthCertRule(dob);
        if (birthCertRule.isExpire(24) ||
                (!TextUtils.isEmpty(vaccineCard) && vaccineCard.equalsIgnoreCase(getString(R.string.yes)))) {
            layoutVaccineCard.setVisibility(View.GONE);
            viewVaccineCardLine.setVisibility(View.GONE);
        } else {
            layoutVaccineCard.setVisibility(View.VISIBLE);
            viewVaccineCardLine.setVisibility(View.VISIBLE);
            textViewVaccineCardText.setVisibility(View.VISIBLE);
            if (birthCertRule.isOverdue(12)) {
                Date date = dobStringToDate(dob);
                textViewVaccineCardText.setTextColor(getResources().getColor(R.color.alert_urgent_red));
                textViewVaccineCardText.setText(String.format("%s%s", getString(R.string.overdue), dd_MMM_yyyy.format(date)));
            } else {
                Date date = dobStringToDate(dob);
                textViewVaccineCardText.setTextColor(getResources().getColor(R.color.grey));
                textViewVaccineCardText.setText(String.format("%s%s", getString(R.string.due), dd_MMM_yyyy.format(date)));

            }
        }
        if (isEditMode) {
            if (birthCertRule.isExpire(24)) {
                layoutVaccineCard.setVisibility(View.GONE);
            } else {
                layoutVaccineCard.setVisibility(View.VISIBLE);
            }
            viewVaccineCardLine.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(vaccineCard)) {
                updateVaccineCard(vaccineCard);
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.vc_group:
                VaccineCardInputDialogFragment dialogFragment = VaccineCardInputDialogFragment.getInstance(textViewVaccineCardText.getText().toString());
                FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                dialogFragment.show(ft, VaccinationDialogFragment.DIALOG_TAG);
                break;
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
                    final String homeVisitId = JsonFormUtils.generateRandomUUIDString();
                    String vaccineCardData = (layoutVaccineCard.getVisibility() == View.VISIBLE &&
                            !TextUtils.isEmpty(textViewVaccineCardText.getText().toString())) ?
                            textViewVaccineCardText.getText().toString() : "";
                    submitData(System.currentTimeMillis() + "", homeVisitId, vaccineCardData);

                }
                break;
            case R.id.close:
                showCloseDialog();
                break;
            case R.id.layout_add_other_family_member:
                ((BaseFamilyProfileActivity) context).startFormActivity(Constants.JSON_FORM.getFamilyMemberRegister(), null, null);
                break;
            default:
                break;
        }
    }

    private void closeScreen() {
        dismiss();
        if (context instanceof ChildProfileActivity) {
            ChildProfileActivity activity = (ChildProfileActivity) context;
            activity.processBackgroundEvent();
        } else if (context instanceof ChildRegisterActivity) {
            ChildUtils.processClientProcessInBackground();
            ((ChildRegisterActivity) getActivity()).refreshList(FetchStatus.fetched);
        }

    }

    private void submitData(final String homeVisitDateLong, final String homeVisitId, final String vaccineCardData) {
        progressBar.setVisibility(View.VISIBLE);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
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

                    Map<String, JSONObject> fields = new HashMap<>();
                    fields.put(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SINGLE_VACCINE, singleVaccineObject);
                    fields.put(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_GROUP_VACCINE, vaccineGroupObject);
                    fields.put(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_VACCINE_NOT_GIVEN, vaccineNotGivenObject);
                    fields.put(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SERVICE, service);
                    fields.put(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_SERVICE_NOT_GIVEN, serviceNotGiven);
                    fields.put(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_BIRTH_CERT, birthCertJson);
                    fields.put(Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.HOME_VISIT_ILLNESS, illnessJson);
                    ChildUtils.updateHomeVisitAsEvent(childClient.entityId(), Constants.EventType.CHILD_HOME_VISIT, Constants.TABLE_NAME.CHILD, fields, ChildDBConstants.KEY.LAST_HOME_VISIT, homeVisitDateLong, homeVisitId);
                    if (((ChildHomeVisitPresenter) presenter).getSaveSize() > 0) {
                        presenter.saveForm();
                    }
                    if (serviceTaskAdapter != null) {
                        serviceTaskAdapter.makeEvent(homeVisitId, childClient.getCaseId());
                    }
                    if (!TextUtils.isEmpty(vaccineCardData)) {
                        ChildUtils.updateVaccineCardAsEvent(context, childClient.getCaseId(), vaccineCardData);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (isEditMode) {
                            saveData();
                            return;
                        }
                        progressBar.setVisibility(View.GONE);
                        closeScreen();

                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
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
                        closeScreen();
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
        //org.smartregister.util.Log.logError("SUBMIT_BTN", "checkAllGiven>>" + isAllImmunizationSelected() + ": " + isAllGrowthSelected());
        return isAllImmunizationSelected() && isAllGrowthSelected() && isAllTaskDone();
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
            if (flavor.onTaskVisibility()) {
                presenter.generateTaskService(isEditMode);
            }

        } else {
            progressBar.setVisibility(View.VISIBLE);
            homeVisitLayout.setVisibility(View.GONE);
        }
    }

    public void forcfullyProgressBarInvisible() {
        progressBar.setVisibility(View.GONE);
        homeVisitLayout.setVisibility(View.VISIBLE);
        if (flavor.onTaskVisibility()) {
            presenter.generateTaskService(isEditMode);
        }
    }

    @Override
    public ChildHomeVisitContract.Presenter initializePresenter() {
        presenter = new ChildHomeVisitPresenter(this);
        return presenter;
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(context, metadata().familyMemberFormActivity);
        intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        Form form = new Form();
        form.setWizard(false);
        form.setActionBarBackground(org.smartregister.family.R.color.customAppThemeBlue);

        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(org.smartregister.family.util.Constants.WizardFormActivity.EnableOnCloseDialog, false);
        startActivityForResult(intent, org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    public void updateBirthStatusTick(String jsonString) {
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
    public void updateObsIllnessStatusTick(String jsonString) {
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

    @Override
    public void updateTaskService() {
        ArrayList<ServiceTask> serviceTasks = presenter.getServiceTasks();
        if (serviceTasks.size() > 0) {
            taskServiceRecyclerView.setVisibility(View.VISIBLE);
            if (serviceTaskAdapter == null) {
                serviceTaskAdapter = new ServiceTaskAdapter((ChildHomeVisitPresenter) presenter, context, new OnClickServiceTaskAdapter() {
                    @Override
                    public void onClick(int position, ServiceTask serviceTask) {
                        if (serviceTask.getTaskType().equalsIgnoreCase(TaskServiceCalculate.TASK_TYPE.Minimum_dietary.name())) {
                            DietaryInputDialogFragment dialogFragment = DietaryInputDialogFragment.getInstance();
                            dialogFragment.setServiceTask(serviceTask, onUpdateServiceTask);
                            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                            dialogFragment.show(ft, DietaryInputDialogFragment.DIALOG_TAG);
                        } else if (serviceTask.getTaskType().equalsIgnoreCase(TaskServiceCalculate.TASK_TYPE.MUAC.name())) {
                            MuacInputDialogFragment dialogFragment = MuacInputDialogFragment.getInstance();
                            dialogFragment.setServiceTask(serviceTask, onUpdateServiceTask);
                            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                            dialogFragment.show(ft, MuacInputDialogFragment.DIALOG_TAG);

                        } else if (serviceTask.getTaskType().equalsIgnoreCase(TaskServiceCalculate.TASK_TYPE.LLITN.name())) {
                            LLITNInputDialogFragment dialogFragment = LLITNInputDialogFragment.getInstance();
                            dialogFragment.setServiceTask(serviceTask, onUpdateServiceTask);
                            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                            dialogFragment.show(ft, LLITNInputDialogFragment.DIALOG_TAG);

                        } else if (serviceTask.getTaskType().equalsIgnoreCase(TaskServiceCalculate.TASK_TYPE.ECD.name())) {
                            try {
                                if (serviceTask.getTaskJson() == null) {
                                    JSONObject form = FormUtils.getInstance(context).getFormJson(Constants.JSON_FORM.ANC_HOME_VISIT.getEarlyChildhoodDevelopment());
                                    String dobString = getValue(childClient.getColumnmaps(), DBConstants.KEY.DOB, false);
                                    form = JsonFormUtils.getEcdWithDatePass(form, dobString);
                                    startFormActivity(form);
                                } else {
                                    JSONObject form = JsonFormUtils.getPreviousECDAsJson(serviceTask.getTaskJson(), childClient.getCaseId());
                                    startFormActivity(form);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();

                            }

                            // open native forms
                        }
                    }
                });
                taskServiceRecyclerView.setAdapter(serviceTaskAdapter);
            } else {
                serviceTaskAdapter.notifyDataSetChanged();
            }
        } else {
            taskServiceRecyclerView.setVisibility(View.GONE);
        }
    }

    private void updateBirthCertData() {
        ArrayList<BirthCertDataModel> data = ((ChildHomeVisitPresenter) presenter).getBirthCertDataList();
        if (data.size() > 0) {
            BirthCertDataModel birthCertDataModel = data.get(0);
            if (birthCertDataModel.isBirthCertHas()) {
                String message = birthCertDataModel.getBirthCertDate() + " (" + birthCertDataModel.getBirthCertNumber() + ")";
                textViewBirthCertDueDate.setText(message);
                updateStatusTick(circleImageViewBirthStatus, true);
                updateLabelText(textViewBirthCertDueDate, true);
            } else {
                textViewBirthCertDueDate.setText(getString(R.string.not_done));
                updateStatusTick(circleImageViewBirthStatus, false);
                updateLabelText(textViewBirthCertDueDate, false);
            }

        }
    }

    private void updateIllnessData() {
        ArrayList<ObsIllnessDataModel> data = ((ChildHomeVisitPresenter) presenter).getIllnessDataList();
        if (data.size() > 0) {
            textViewObsIllnessDesc.setVisibility(View.VISIBLE);
            ObsIllnessDataModel obsIllnessDataModel = data.get(0);
            String message = obsIllnessDataModel.getIllnessDate() + ": " + obsIllnessDataModel.getIllnessDescription() + "\n" + obsIllnessDataModel.getActionTaken();
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

    private void updateLabelText(TextView textView, boolean isYes) {
        textView.setTextColor(isYes ? getResources().getColor(R.color.grey) : getResources().getColor(R.color.alert_urgent_red));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                        JSONObject form = new JSONObject(jsonString);
                        if (form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.BIRTH_CERTIFICATION)
                        ) {
                            presenter.generateBirthCertForm(jsonString);
                        } else if (form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.OBS_ILLNESS)) {
                            presenter.generateObsIllnessForm(jsonString);
                        } else if (form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.ECD)) {
                            ServiceTask serviceTask = ChildUtils.createECDFromJson(context, jsonString);
                            if (serviceTask != null) {
                                for (int i = 0; i < presenter.getServiceTasks().size(); i++) {
                                    ServiceTask serviceTask1 = presenter.getServiceTasks().get(i);
                                    if (serviceTask1.getTaskType().equalsIgnoreCase(TaskServiceCalculate.TASK_TYPE.ECD.name())) {
                                        presenter.getServiceTasks().set(i, serviceTask);
                                        break;
                                    }
                                }
                                updateTaskService();
                                checkIfSubmitIsToBeEnabled();
                            }


                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
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

    public void updateVaccineCard(String option) {
        textViewVaccineCardText.setVisibility(View.VISIBLE);
        if (option.equalsIgnoreCase(context.getString(R.string.yes))) {
            textViewVaccineCardText.setText(context.getString(R.string.yes));
            updateLabelText(textViewVaccineCardText, true);
            updateStatusTick(circleImageViewVaccineCard, true);

        } else {
            textViewVaccineCardText.setText(context.getString(R.string.no));
            updateStatusTick(circleImageViewVaccineCard, false);
            updateLabelText(textViewVaccineCardText, false);
        }

    }

    public void setChildClient(CommonPersonObjectClient childClient) {
        this.childClient = childClient;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mDateTimeChangedReceiver);
        super.onDestroy();
    }

    private boolean isAllGrowthSelected() {
        return homeVisitGrowthAndNutritionLayout.isAllSelected();
    }

    private boolean isAllTaskDone() {
        for (ServiceTask serviceTask : presenter.getServiceTasks()) {
            if (TextUtils.isEmpty(serviceTask.getTaskLabel())) return false;
        }
        return true;
    }

    private boolean isAllImmunizationSelected() {
        return immunizationView.isAllSelected();
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;

    }

    public interface Flavor {
        boolean onTaskVisibility();

        boolean onObsIllnessVisibility();

        boolean onSleepingUnderLLITNVisibility();

        boolean onMUACVisibility();
    }

}