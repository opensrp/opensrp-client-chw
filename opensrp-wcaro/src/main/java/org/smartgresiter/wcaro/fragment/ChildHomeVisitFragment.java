package org.smartgresiter.wcaro.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.activity.ChildProfileActivity;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.contract.ChildRegisterContract;
import org.smartgresiter.wcaro.interactor.ChildRegisterInteractor;
import org.smartgresiter.wcaro.model.ChildRegisterModel;
import org.smartgresiter.wcaro.presenter.ChildProfilePresenter;
import org.smartgresiter.wcaro.util.Constants;
import org.smartgresiter.wcaro.util.JsonFormUtils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.family.activity.BaseFamilyProfileActivity;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.util.DateUtil;
import org.smartregister.util.FormUtils;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.smartgresiter.wcaro.fragment.AddMemberFragment.DIALOG_TAG;
import static org.smartgresiter.wcaro.util.Constants.TABLE_NAME.CHILD;
import static org.smartregister.immunization.domain.State.EXPIRED;
import static org.smartregister.immunization.util.VaccinatorUtils.generateScheduleList;
import static org.smartregister.immunization.util.VaccinatorUtils.nextVaccineDue;
import static org.smartregister.immunization.util.VaccinatorUtils.receivedVaccines;
import static org.smartregister.util.Utils.getValue;
import static org.smartregister.util.Utils.startAsyncTask;
import static org.smartregister.view.contract.AlertStatus.UPCOMING;

public class ChildHomeVisitFragment extends DialogFragment implements View.OnClickListener,ChildRegisterContract.InteractorCallBack {


    public static String DIALOG_TAG = "child_home_visit_dialog";
    protected ProgressDialog progressDialog;
    Context context;
    String childBaseEntityId;
    CommonPersonObjectClient childClient;
    private TextView nameHeader;
    private TextView textview_group_immunization_secondary_text;

    public static final String DATE = "date";
    public static final String VACCINE = "vaccine";

    public static final String NORMAL = "normal";
    public static final String UPCOMING = "upcoming";
    public static final String URGENT = "urgent";
    public static final String EXPIRED = "expired";
    private TextView textview_group_immunization_primary_text;
    private TextView textview_immunization_primary_text;
    private TextView textview_immunization_secondary_text;


    public void setContext(Context context){
        this.context = context;
    }
    public void setChildBaseEntityId(String childBaseEntityId){
        this.childBaseEntityId = childBaseEntityId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_NoActionBar);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
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
        ((LinearLayout) view.findViewById(R.id.immunization_group)).setOnClickListener(this);
        textview_group_immunization_primary_text = (TextView)view.findViewById(R.id.textview_group_immunization);
        textview_group_immunization_secondary_text = (TextView)view.findViewById(R.id.textview_immunization_group_secondary_text);

        textview_immunization_primary_text = (TextView)view.findViewById(R.id.textview_immunization);
        textview_immunization_secondary_text = (TextView)view.findViewById(R.id.textview_immunization_secondary_text);


        assignNameHeader();
    }

    private void assignNameHeader() {
        String dobString = org.smartregister.family.util.Utils.getDuration(org.smartregister.family.util.Utils.getValue(childClient.getColumnmaps(), DBConstants.KEY.DOB, false));

        nameHeader.setText(
                getValue(childClient.getColumnmaps(),"first_name",true)+" "+
                getValue(childClient.getColumnmaps(),"last_name",true)+", "+
        dobString+" - Home Visit"
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
//                    + " must implement WeightActionListener");
//        }
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
        switch (v.getId()) {
            case R.id.close:
                dismiss();
                break;
            case  R.id.immunization_group:
                FragmentTransaction ft = ((ChildProfileActivity)context).getFragmentManager().beginTransaction();
                ChildImmunizationFragment childImmunizationFragment = ChildImmunizationFragment.newInstance(new Bundle());
                childImmunizationFragment.setChildDetails(childClient);
//                childHomeVisitFragment.setFamilyBaseEntityId(getFamilyBaseEntityId());
                childImmunizationFragment.show(ft,ChildImmunizationFragment.TAG);
                break;
            case R.id.layout_add_other_family_member:
                ((BaseFamilyProfileActivity) context).startFormActivity(Constants.JSON_FORM.FAMILY_MEMBER_REGISTER, null, null);
                break;
        }
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
            startForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight(),familyId);
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

    public void startForm(String formName, String entityId, String metadata, String currentLocationId,String familyId) throws Exception {
        interactor = new ChildRegisterInteractor();
        if (StringUtils.isBlank(entityId)) {
            Triple<String, String, String> triple = Triple.of(formName, metadata, currentLocationId);
            interactor.getNextUniqueId(triple, this,familyId);
            return;
        }

        JSONObject form = getFormAsJson(formName, entityId, currentLocationId,familyId);
        startFormActivity(form);
    }


    public void startFormActivity(JSONObject form) {
        Intent intent = new Intent(context, org.smartregister.family.util.Utils.metadata().familyMemberFormActivity);
        intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, form.toString());
        startActivityForResult(intent, org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON);
//        startRegistration();
    }

    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId,String familyID) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        return JsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId,familyID);
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
    }

    public void updateImmunizationState() {
        startAsyncTask(new VaccinationAsyncTask(),null);
    }

    public void ImmunizationState(List<Vaccine> vaccines, String stateKey, Map<String, Object> nv, State state){
        Map<String, Date> recievedVaccines = receivedVaccines(vaccines);
        recievedVaccines.size();
        String givenVaccines = "";
        for(int i = 0;i<vaccines.size();i++){
            if(i != 0) {
                givenVaccines = givenVaccines + "," + vaccines.get(i).getName();
            }else{
                givenVaccines = vaccines.get(i).getName();
            }

        }
        textview_group_immunization_primary_text.setText("Immunizations"+"("+stateKey+")");
        textview_group_immunization_secondary_text.setText(givenVaccines);

        VaccineRepo.Vaccine vaccine = (VaccineRepo.Vaccine) nv.get(VACCINE);


        textview_immunization_primary_text.setText(vaccine.display());
        if(state.equals(State.DUE)) {
            DateTime dueDate = (DateTime) nv.get(DATE);

            textview_immunization_secondary_text.setText("Due "+ dueDate.toString());
        }else if(state.equals(State.OVERDUE)) {
            DateTime dueDate = (DateTime) nv.get(DATE);
            String duedateString = DateUtil.formatDate(dueDate.toLocalDate(),"dd MMM yyyy");
            textview_immunization_secondary_text.setTextColor(getResources().getColor(R.color.alert_urgent_red));

            textview_immunization_secondary_text.setText("Overdue "+ duedateString);
        }else{
            textview_immunization_secondary_text.setText("");

        }
    }

    private class VaccinationAsyncTask extends AsyncTask {

        private List<Vaccine> vaccines = new ArrayList<>();
        private List<Alert> alerts = new ArrayList<>();
        private SmartRegisterClient client;
        private Cursor cursor;
        String stateKey = "";
        private State state;
        private Map<String, Object> nv;

        private VaccinationAsyncTask() {
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            alerts = WcaroApplication.getInstance().getContext().alertService().findByEntityIdAndAlertNames(childClient.entityId(), VaccinateActionUtils.allAlertNames("child"));
            vaccines = WcaroApplication.getInstance().vaccineRepository().findByEntityId(childClient.entityId());
            Map<String, Date> recievedVaccines = receivedVaccines(vaccines);

            List<Map<String, Object>> sch = generateScheduleList("child", new DateTime(org.smartregister.family.util.Utils.getValue(childClient.getColumnmaps(), DBConstants.KEY.DOB, false)), recievedVaccines, alerts);

            List<VaccineRepo.Vaccine> vList = Arrays.asList(VaccineRepo.Vaccine.values());
            nv = nextVaccineDue(sch, vList);
            if (nv != null) {
                DateTime dueDate = (DateTime) nv.get(DATE);
                VaccineRepo.Vaccine vaccine = (VaccineRepo.Vaccine) nv.get(VACCINE);
                stateKey = VaccinateActionUtils.stateKey(vaccine);
                String ALERT = "alert";
                if (nv.get(ALERT) == null) {
                    state = State.NO_ALERT;
                } else if (((Alert) nv.get(ALERT)).status().value().equalsIgnoreCase(NORMAL)) {
                    state = State.DUE;
                } else if (((Alert) nv.get(ALERT)).status().value().equalsIgnoreCase(UPCOMING)) {
                    Calendar today = Calendar.getInstance();
                    today.set(Calendar.HOUR_OF_DAY, 0);
                    today.set(Calendar.MINUTE, 0);
                    today.set(Calendar.SECOND, 0);
                    today.set(Calendar.MILLISECOND, 0);

                    if (dueDate.getMillis() >= (today.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)) && dueDate.getMillis() < (today.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS))) {
                        state = State.UPCOMING_NEXT_7_DAYS;
                    } else {
                        state = State.UPCOMING;
                    }
                } else if (((Alert) nv.get(ALERT)).status().value().equalsIgnoreCase(URGENT)) {
                    state = State.OVERDUE;
                } else if (((Alert) nv.get(ALERT)).status().value().equalsIgnoreCase(EXPIRED)) {
                    state = State.EXPIRED;
                }
            } else {
                state = State.WAITING;
            }




            return null;
        }


        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            ImmunizationState(vaccines,stateKey,nv,state);

        }



    }


    public void setChildClient(CommonPersonObjectClient childClient) {
        this.childClient = childClient;
    }

    public enum State {
        DUE,
        OVERDUE,
        UPCOMING_NEXT_7_DAYS,
        UPCOMING,
        INACTIVE,
        LOST_TO_FOLLOW_UP,
        EXPIRED,
        WAITING,
        NO_ALERT,
        FULLY_IMMUNIZED
    }

}