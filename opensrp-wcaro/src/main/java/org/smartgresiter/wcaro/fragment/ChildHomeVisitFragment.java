package org.smartgresiter.wcaro.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.activity.ChildProfileActivity;
import org.smartgresiter.wcaro.contract.ChildRegisterContract;
import org.smartgresiter.wcaro.custom_view.HomeVisitGrowthAndNutrition;
import org.smartgresiter.wcaro.interactor.ChildRegisterInteractor;
import org.smartgresiter.wcaro.listener.ImmunizationStateChangeListener;
import org.smartgresiter.wcaro.model.ChildRegisterModel;
import org.smartgresiter.wcaro.task.VaccinationAsyncTask;
import org.smartgresiter.wcaro.util.ChildUtils;
import org.smartgresiter.wcaro.util.Constants;
import org.smartgresiter.wcaro.util.HomeVisitVaccineGroupDetails;
import org.smartgresiter.wcaro.util.ImmunizationState;
import org.smartgresiter.wcaro.util.JsonFormUtils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.family.activity.BaseFamilyProfileActivity;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.util.DateUtil;
import org.smartregister.util.FormUtils;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.smartgresiter.wcaro.util.Constants.IMMUNIZATION_CONSTANT.DATE;
import static org.smartgresiter.wcaro.util.Constants.IMMUNIZATION_CONSTANT.VACCINE;
import static org.smartregister.immunization.util.VaccinatorUtils.generateScheduleList;
import static org.smartregister.immunization.util.VaccinatorUtils.nextVaccineDue;
import static org.smartregister.immunization.util.VaccinatorUtils.receivedVaccines;
import static org.smartregister.util.Utils.getValue;
import static org.smartregister.util.Utils.startAsyncTask;

public class ChildHomeVisitFragment extends DialogFragment implements View.OnClickListener,ChildRegisterContract.InteractorCallBack {


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
    private boolean allVaccineStateFullfilled = false;
    private TextView submit;


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
        submit = (TextView)view.findViewById(R.id.textview_submit);
        view.findViewById(R.id.textview_submit).setOnClickListener(this);

        ((LinearLayout) view.findViewById(R.id.immunization_group)).setOnClickListener(this);
         single_immunization_group = ((LinearLayout) view.findViewById(R.id.immunization_name_group));

        ((LinearLayout) view.findViewById(R.id.immunization_group)).setOnClickListener(this);
        multiple_immunization_group = ((LinearLayout) view.findViewById(R.id.immunization_group));



        homeVisitGrowthAndNutritionLayout=view.findViewById(R.id.growth_and_nutrition_group);
        textview_group_immunization_primary_text = (TextView)view.findViewById(R.id.textview_group_immunization);
        textview_group_immunization_secondary_text = (TextView)view.findViewById(R.id.textview_immunization_group_secondary_text);

        textview_immunization_primary_text = (TextView)view.findViewById(R.id.textview_immunization);
        textview_immunization_secondary_text = (TextView)view.findViewById(R.id.textview_immunization_secondary_text);

        immunization_status_circle = ((CircleImageView)view.findViewById(R.id.immunization_status_circle));
        immunization_group_status_circle = ((CircleImageView)view.findViewById(R.id.immunization_group_status_circle));

        assignNameHeader();
        submitButtonEnableDisable(false);
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
//                    + " must implementfonre WeightActionListener");
//        }
    }
    private void updateGrowthData(){
        homeVisitGrowthAndNutritionLayout.setData(this,getActivity().getFragmentManager(),childClient);
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
        String dobString = org.smartregister.util.Utils.getValue(childClient.getColumnmaps(), "dob", false);

        switch (v.getId()) {
            case R.id.textview_submit:
                if(checkAllGiven()) {
                    ChildUtils.updateClientStatusAsEvent(childClient.entityId(), "Child Home Visit", "last_home_visit", "" + System.currentTimeMillis(), "ec_child");
                    dismiss();
                }
                break;
            case R.id.close:
                dismiss();
                break;
            case  R.id.immunization_group:
//                ChildImmunizationFragment childImmunizationFragment = ChildImmunizationFragment.newInstance(new Bundle());
//                childImmunizationFragment.setChildDetails(childClient);
////                childHomeVisitFragment.setFamilyBaseEntityId(getFamilyBaseEntityId());
//                childImmunizationFragment.show(ft,ChildImmunizationFragment.TAG);
                if (!TextUtils.isEmpty(dobString)) {
                    DateTime dateTime = new DateTime(dobString);
                    Date dob = dateTime.toDate();

                    List<Vaccine> vaccines = (List<Vaccine>)v.getTag(R.id.vaccinelist);

                    HomeVisitVaccineGroupDetails duevaccines = (HomeVisitVaccineGroupDetails)v.getTag(R.id.nextduevaccinelist);

                    CustomMultipleVaccinationDialogFragment customVaccinationDialogFragment = CustomMultipleVaccinationDialogFragment.newInstance(dob,vaccines,createVaccineWrappers(duevaccines));
//                childHomeVisitFragment.setFamilyBaseEntityId(getFamilyBaseEntityId());
                    customVaccinationDialogFragment.setContext(getActivity());
                    customVaccinationDialogFragment.setChildDetails(childClient);
                    customVaccinationDialogFragment.show(ft,ChildImmunizationFragment.TAG);
                }
                break;
            case R.id.immunization_name_group:
                if (!TextUtils.isEmpty(dobString)) {
                    DateTime dateTime = new DateTime(dobString);
                    Date dob = dateTime.toDate();
                    VaccineRepo.Vaccine vaccine = (VaccineRepo.Vaccine) v.getTag(R.id.singlenextvaccine);
                    VaccineWrapper vaccineWrapper = new VaccineWrapper();
                    vaccineWrapper.setVaccine(vaccine);
                    vaccineWrapper.setName(vaccine.display());
                    vaccineWrapper.setDefaultName(vaccine.display());
                    ArrayList<VaccineWrapper> vaccineWrappers = new ArrayList<VaccineWrapper>();
                    vaccineWrappers.add(vaccineWrapper);

                    List<Vaccine> vaccines = (List<Vaccine>)v.getTag(R.id.vaccinelist);

                    CustomVaccinationDialogFragment customVaccinationDialogFragment = CustomVaccinationDialogFragment.newInstance(dob,vaccines,vaccineWrappers);
//                childHomeVisitFragment.setFamilyBaseEntityId(getFamilyBaseEntityId());
                    customVaccinationDialogFragment.setContext(getActivity());
                    customVaccinationDialogFragment.setChildDetails(childClient);
                    customVaccinationDialogFragment.setDisableConstraints(true);
                    customVaccinationDialogFragment.show(getActivity().getFragmentManager(),ChildImmunizationFragment.TAG);
                }

                break;
            case R.id.layout_add_other_family_member:
                ((BaseFamilyProfileActivity) context).startFormActivity(Constants.JSON_FORM.FAMILY_MEMBER_REGISTER, null, null);
                break;
        }
    }
    private void submitButtonEnableDisable(boolean isEnable){
        if(isEnable){
            submit.setAlpha(1.0f);
        }else{
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

    public void checkIfSubmitIsToBeEnabled(){
        if(checkAllGiven()){
            submitButtonEnableDisable(true);
        }else {
            submitButtonEnableDisable(false);
        }
    }

    private ArrayList<VaccineWrapper> createVaccineWrappers(HomeVisitVaccineGroupDetails vaccines) {

        ArrayList<VaccineWrapper> vaccineWrappers = new ArrayList<VaccineWrapper>();
        for(VaccineRepo.Vaccine vaccine : vaccines.getDueVaccines()) {
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
        if (isBlank(entityId)) {
            Triple<String, String, String> triple = Triple.of(formName, metadata, currentLocationId);
            interactor.getNextUniqueId(triple, this,familyId);
            return;
        }

        JSONObject form = getFormAsJson(formName, entityId, currentLocationId,familyId);
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateGrowthData();
            }
        },100);

    }

    public void updateImmunizationState() {
//        if(vaccinationAsyncTask!=null && !vaccinationAsyncTask.isCancelled()){
//            vaccinationAsyncTask.cancel(true);
//        }
        vaccinationAsyncTask=new VaccinationAsyncTask(childClient.getCaseId(), childClient.getColumnmaps(),notGivenVaccines, new ImmunizationStateChangeListener() {
            @Override
            public void onImmunicationStateChange(List<Alert> alerts, List<Vaccine> vaccines, String stateKey, Map<String, Object> nv, ImmunizationState state) {
                ImmunizationState(alerts,vaccines,stateKey,nv,state);
            }
        });
        startAsyncTask(vaccinationAsyncTask,null);
    }

    public void ImmunizationState(List<Alert> alerts, List<Vaccine> vaccines, String stateKey, Map<String, Object> nv, ImmunizationState state){
        Map<String, Date> recievedVaccines = receivedVaccines(vaccines);
        recievedVaccines.size();
        String givenVaccines = "";
        String lastVaccine = "";
        String lastVaccineGivenDate = "";

        for(int i = 0;i<vaccines.size();i++){
            if(i != 0) {
                givenVaccines = givenVaccines + "," + vaccines.get(i).getName();
            }else{
                givenVaccines = vaccines.get(i).getName();
            }
            if(i == vaccines.size()-1){
                lastVaccine = vaccines.get(i).getName();
                lastVaccineGivenDate = DateUtil.formatDate(new DateTime(vaccines.get(i).getDate().getTime()).toLocalDate(),"dd MMM yyyy");
            }

        }
        textview_group_immunization_primary_text.setText("Immunizations"+"("+stateKey+")");
        textview_group_immunization_secondary_text.setText(givenVaccines);

        VaccineRepo.Vaccine vaccine = (VaccineRepo.Vaccine) nv.get(VACCINE);

        single_immunization_group.setTag(R.id.singlenextvaccine,vaccine);
        single_immunization_group.setTag(R.id.vaccinelist,vaccines);


        textview_immunization_primary_text.setText(vaccine.display());
        if(state.equals(ImmunizationState.DUE)) {
            DateTime dueDate = (DateTime) nv.get(DATE);

            textview_immunization_secondary_text.setText("Due "+ dueDate.toString());
        }else if(state.equals(ImmunizationState.OVERDUE)) {
            DateTime dueDate = (DateTime) nv.get(DATE);
            String duedateString = DateUtil.formatDate(dueDate.toLocalDate(),"dd MMM yyyy");
            textview_immunization_secondary_text.setTextColor(getResources().getColor(R.color.alert_urgent_red));

            textview_immunization_secondary_text.setText("Overdue "+ duedateString);
            single_immunization_group.setOnClickListener(this);
        }else if(state.equals(ImmunizationState.NO_ALERT)){
            if(notGivenVaccines.size()>0) {
                textview_immunization_primary_text.setText(notGivenVaccines.get(notGivenVaccines.size()-1).getDefaultName());
                textview_immunization_secondary_text.setText("Due "+ notGivenVaccines.get(notGivenVaccines.size()-1).getVaccineDateAsString());

                immunization_status_circle.setImageResource(R.drawable.ic_checked);
                immunization_status_circle.setColorFilter(getResources().getColor(R.color.white));
                immunization_status_circle.setCircleBackgroundColor(getResources().getColor(R.color.pnc_circle_yellow));

                immunization_group_status_circle.setImageResource(R.drawable.ic_checked);
                immunization_group_status_circle.setColorFilter(getResources().getColor(R.color.white));
                immunization_group_status_circle.setCircleBackgroundColor(getResources().getColor(R.color.pnc_circle_yellow));

                allVaccineStateFullfilled = true;

            }else{
                textview_immunization_primary_text.setText(lastVaccine);
                textview_immunization_secondary_text.setText(lastVaccineGivenDate);

                immunization_status_circle.setImageResource(R.drawable.ic_checked);
                immunization_status_circle.setColorFilter(getResources().getColor(R.color.white));
                immunization_status_circle.setCircleBackgroundColor(getResources().getColor(R.color.alert_complete_green));

                immunization_group_status_circle.setImageResource(R.drawable.ic_checked);
                immunization_group_status_circle.setColorFilter(getResources().getColor(R.color.white));
                immunization_group_status_circle.setCircleBackgroundColor(getResources().getColor(R.color.alert_complete_green));

            }

        }else{
            textview_immunization_secondary_text.setText("");

        }

        ArrayList<HomeVisitVaccineGroupDetails> homeVisitVaccineGroupDetailsArrayList = determineNextDueGroup(alerts, vaccines);

        HomeVisitVaccineGroupDetails nextDueGroup = null;
        HomeVisitVaccineGroupDetails lastGivenGroup = null;


        for(int i = 0; i < homeVisitVaccineGroupDetailsArrayList.size();i++){
            if(!homeVisitVaccineGroupDetailsArrayList.get(i).getAlert().name().equalsIgnoreCase(ImmunizationState.NO_ALERT.name())){
                if(homeVisitVaccineGroupDetailsArrayList.get(i).getGivenVaccines().size() == 0 && homeVisitVaccineGroupDetailsArrayList.get(i).getDueVaccines().size()>0){
                    if(!hasAllVaccineOfCurrentGroupNotGiven(homeVisitVaccineGroupDetailsArrayList.get(i))) {
                        nextDueGroup = homeVisitVaccineGroupDetailsArrayList.get(i);
                        break;
                    }
                }
            }
        }

        if(nextDueGroup != null) {
            textview_group_immunization_primary_text.setText("Immunizations" + "(" + nextDueGroup.getGroup() + ")");
            multiple_immunization_group.setTag(R.id.nextduevaccinelist, nextDueGroup);
            multiple_immunization_group.setTag(R.id.vaccinelist, vaccines);

            multiple_immunization_group.setOnClickListener(this);
            if(hasAllVaccineOfCurrentGroupNotGiven(nextDueGroup)) {

                    immunization_group_status_circle.setImageResource(R.drawable.ic_checked);
                    immunization_group_status_circle.setColorFilter(getResources().getColor(R.color.white));
                    immunization_group_status_circle.setCircleBackgroundColor(getResources().getColor(R.color.pnc_circle_yellow));
            }
        }else{
            for(int i = 0; i < homeVisitVaccineGroupDetailsArrayList.size();i++){
                if(!homeVisitVaccineGroupDetailsArrayList.get(i).getAlert().name().equalsIgnoreCase(ImmunizationState.NO_ALERT.name())){
                    if(homeVisitVaccineGroupDetailsArrayList.get(i).getGivenVaccines().size() >0){
                        lastGivenGroup = homeVisitVaccineGroupDetailsArrayList.get(i);
                    }
                }
            }
            if(lastGivenGroup == null){
                for(int i = 0; i < homeVisitVaccineGroupDetailsArrayList.size();i++){
                    if(!homeVisitVaccineGroupDetailsArrayList.get(i).getAlert().name().equalsIgnoreCase(ImmunizationState.NO_ALERT.name())){
                            lastGivenGroup = homeVisitVaccineGroupDetailsArrayList.get(i);
                    }
                }
            }

            textview_group_immunization_primary_text.setText("Immunizations" + "(" + lastGivenGroup.getGroup() + ")");
            boolean allVaccineOfCurrentGroupGiven = hasAllVaccineOfCurrentGroupGiven(lastGivenGroup);
            if(allVaccineOfCurrentGroupGiven){
                immunization_group_status_circle.setImageResource(R.drawable.ic_checked);
                immunization_group_status_circle.setColorFilter(getResources().getColor(R.color.white));
                immunization_group_status_circle.setCircleBackgroundColor(getResources().getColor(R.color.alert_complete_green));
            }else {
                immunization_group_status_circle.setImageResource(R.drawable.ic_checked);
                immunization_group_status_circle.setColorFilter(getResources().getColor(R.color.white));
                immunization_group_status_circle.setCircleBackgroundColor(getResources().getColor(R.color.pnc_circle_yellow));
            }

        }
        if(nextDueGroup == null){
            nextDueGroup = lastGivenGroup;
        }

        ArrayList<HomeVisitVaccineGroupDetails> notGivenGroup = notGivenGroup(nextDueGroup,homeVisitVaccineGroupDetailsArrayList);
        if(notGivenGroup.size()>0){
            String overDueVaccinesNotGivenInVisit = "";
            ArrayList<VaccineRepo.Vaccine> notgivenVaccines = new ArrayList<VaccineRepo.Vaccine>();
            for(HomeVisitVaccineGroupDetails notgivenGroup : notGivenGroup){
                for(VaccineRepo.Vaccine vaccinenotgiven : notgivenGroup.getNotGivenVaccines()){
                    if(!isInNotGivenThisVisitGroup(vaccinenotgiven)){
                        overDueVaccinesNotGivenInVisit = overDueVaccinesNotGivenInVisit + vaccinenotgiven.display()+",";
                        notgivenVaccines.add(vaccinenotgiven);
                    }

                }
            }
            if (overDueVaccinesNotGivenInVisit.endsWith(",")){
                overDueVaccinesNotGivenInVisit = overDueVaccinesNotGivenInVisit.substring(0, overDueVaccinesNotGivenInVisit.length() - 1);
            }
            if(notgivenVaccines.size()>0) {
                textview_immunization_primary_text.setText(overDueVaccinesNotGivenInVisit);
            }else if (notGivenVaccines.size()>0){
                overDueVaccinesNotGivenInVisit = "";

                for(VaccineWrapper vaccineWrapper : notGivenVaccines){

                            overDueVaccinesNotGivenInVisit = overDueVaccinesNotGivenInVisit + vaccineWrapper.getDefaultName().toUpperCase()+",";



                }
            }
        }else{

        }

        checkIfSubmitIsToBeEnabled();

    }

    private boolean hasAllVaccineOfCurrentGroupNotGiven(HomeVisitVaccineGroupDetails lastGivenGroup) {
        boolean allvaccinegiven = false;
        for(VaccineRepo.Vaccine given: lastGivenGroup.getDueVaccines()){
            if(isInNotGivenThisVisitGroup(given)){
                allvaccinegiven = true;
                break;
            }
        }
        return allvaccinegiven;
    }

    private boolean hasAllVaccineOfCurrentGroupGiven(HomeVisitVaccineGroupDetails lastGivenGroup) {
        boolean allvaccinegiven = true;
        for(VaccineRepo.Vaccine given: lastGivenGroup.getGivenVaccines()){
            if(isInNotGivenThisVisitGroup(given)){
                allvaccinegiven = false;
                break;
            }
        }
        return allvaccinegiven;
    }

    private boolean isInNotGivenThisVisitGroup(VaccineRepo.Vaccine vaccinenotgiven) {
        boolean isNotGivenThisVisit = false;
        for(VaccineWrapper vaccineWrapper :notGivenVaccines){
            if(vaccinenotgiven.display().equalsIgnoreCase(vaccineWrapper.getName())){
                isNotGivenThisVisit = true;
                break;
            }
        }
        return isNotGivenThisVisit;
    }

    private ArrayList<HomeVisitVaccineGroupDetails> notGivenGroup(HomeVisitVaccineGroupDetails nextDueGroup, ArrayList<HomeVisitVaccineGroupDetails> homeVisitVaccineGroupDetailsArrayList) {
        ArrayList<HomeVisitVaccineGroupDetails> notGiven = new ArrayList<HomeVisitVaccineGroupDetails>();
        int indexofnextGroup = 0;
        for(int i = 0;i<homeVisitVaccineGroupDetailsArrayList.size();i++) {
            if(nextDueGroup!=null) {
                if (homeVisitVaccineGroupDetailsArrayList.get(i).getGroup().equalsIgnoreCase(nextDueGroup.getGroup())) {
                    indexofnextGroup = i;
                }
            }
        }

        for(int i = 0;i<indexofnextGroup;i++) {
            if(homeVisitVaccineGroupDetailsArrayList.get(i).getDueVaccines().size()>homeVisitVaccineGroupDetailsArrayList.get(i).getGivenVaccines().size()){
                notGiven.add(homeVisitVaccineGroupDetailsArrayList.get(i));
            }
        }

        return notGiven;

    }


    public void setChildClient(CommonPersonObjectClient childClient) {
        this.childClient = childClient;
    }
    @Override
    public void onDestroy() {
        if(context instanceof ChildProfileActivity){
            ChildProfileActivity activity=(ChildProfileActivity)context;
            activity.updateImmunizationData();
        }
        super.onDestroy();
    }
    private void resetGrowthData(){
        homeVisitGrowthAndNutritionLayout.resetAll();
    }
    private boolean isAllGrowthSelected(){
        return homeVisitGrowthAndNutritionLayout.isAllSelected();
    }

    public void updateNotGivenVaccine(VaccineWrapper name) {
        if(!notGivenVaccines.contains(name)) {
            notGivenVaccines.add(name);
        }
    }

    public ArrayList<HomeVisitVaccineGroupDetails> determineNextDueGroup(List<Alert> alerts, List<Vaccine> vaccines){
        ArrayList<HomeVisitVaccineGroupDetails> homeVisitVaccineGroupDetailsArrayList = new ArrayList<HomeVisitVaccineGroupDetails>();
        Map<String, Date> receivedvaccines = receivedVaccines(vaccines);
        List<VaccineRepo.Vaccine> vList = Arrays.asList(VaccineRepo.Vaccine.values());
        ArrayList<String> vaccineGroupName= new ArrayList<String>();
        String groupName = "";
        for(VaccineRepo.Vaccine vaccine : vList){
            if(vaccine.category().equalsIgnoreCase("child")) {
//                if (!VaccinateActionUtils.stateKey(vaccine).equalsIgnoreCase(groupName)) {
//                    groupName = VaccinateActionUtils.stateKey(vaccine);
//                    vaccineGroupName.add(groupName);
//                }
                if(!vaccineGroupName.contains(VaccinateActionUtils.stateKey(vaccine))){
                    vaccineGroupName.add(VaccinateActionUtils.stateKey(vaccine));
                }
            }
        }
        for(String emptyname : vaccineGroupName){
            if(isBlank(emptyname)){
                vaccineGroupName.remove(emptyname);
            }
        }
        for(int i = 0;i<vaccineGroupName.size();i++){
            HomeVisitVaccineGroupDetails homeVisitVaccineGroupDetails = new HomeVisitVaccineGroupDetails();
            homeVisitVaccineGroupDetails.setGroup(vaccineGroupName.get(i));
            homeVisitVaccineGroupDetailsArrayList.add(homeVisitVaccineGroupDetails);
        }

        homeVisitVaccineGroupDetailsArrayList = assignDueVaccine(vList,homeVisitVaccineGroupDetailsArrayList,alerts);
        homeVisitVaccineGroupDetailsArrayList = assignGivenVaccine(homeVisitVaccineGroupDetailsArrayList,receivedvaccines);


        return  homeVisitVaccineGroupDetailsArrayList;
    }

    private ArrayList<HomeVisitVaccineGroupDetails> assignGivenVaccine(ArrayList<HomeVisitVaccineGroupDetails> homeVisitVaccineGroupDetailsArrayList, Map<String, Date> receivedvaccines) {

        for(int i = 0;i<homeVisitVaccineGroupDetailsArrayList.size();i++){
            ArrayList<VaccineRepo.Vaccine> dueVaccines = homeVisitVaccineGroupDetailsArrayList.get(i).getDueVaccines();
            for(VaccineRepo.Vaccine checkVaccine: dueVaccines){
                if(isReceived(checkVaccine.display(),receivedvaccines)){
                    homeVisitVaccineGroupDetailsArrayList.get(i).getGivenVaccines().add(checkVaccine);
                }
            }
            homeVisitVaccineGroupDetailsArrayList.get(i).calculateNotGivenVaccines();
        }
        return homeVisitVaccineGroupDetailsArrayList;
    }

    private ArrayList<HomeVisitVaccineGroupDetails> assignDueVaccine(List<VaccineRepo.Vaccine> vList, ArrayList<HomeVisitVaccineGroupDetails> homeVisitVaccineGroupDetailsArrayList, List<Alert> alerts) {
        for(int i = 0;i<homeVisitVaccineGroupDetailsArrayList.size();i++){
            for(VaccineRepo.Vaccine vaccine : vList){
                if(VaccinateActionUtils.stateKey(vaccine).equalsIgnoreCase(homeVisitVaccineGroupDetailsArrayList.get(i).getGroup())){
                    if(hasAlert(vaccine,alerts)){
                        homeVisitVaccineGroupDetailsArrayList.get(i).getDueVaccines().add(vaccine);
                        homeVisitVaccineGroupDetailsArrayList.get(i).setAlert(assignAlert(vaccine,alerts));
                    }
                }
            }
        }


        return homeVisitVaccineGroupDetailsArrayList;
    }

    private ImmunizationState assignAlert(VaccineRepo.Vaccine vaccine, List<Alert> alerts) {
        ImmunizationState state = ImmunizationState.NO_ALERT;
        for(Alert alert : alerts){
            if(alert.scheduleName().equalsIgnoreCase(vaccine.display())){
                state = alertState(alert);
            }
        }
        return state;
    }

    private boolean hasAlert(VaccineRepo.Vaccine vaccine, List<Alert> alerts) {
        boolean hasAlert = false;
        for(Alert alert : alerts){
            if(alert.scheduleName().equalsIgnoreCase(vaccine.display())){
                hasAlert = true;
            }
        }

        return hasAlert;
    }

    public ImmunizationState alertState(Alert toProcess){
        ImmunizationState state = ImmunizationState.NO_ALERT;
        if (toProcess == null) {
            state = ImmunizationState.NO_ALERT;
        } else if (toProcess.status().value().equalsIgnoreCase(ImmunizationState.NORMAL.name())) {
            state = ImmunizationState.DUE;
        } else if (toProcess.status().value().equalsIgnoreCase(ImmunizationState.UPCOMING.name())) {
            state = ImmunizationState.UPCOMING;
        } else if (toProcess.status().value().equalsIgnoreCase(ImmunizationState.URGENT.name())) {
            state = ImmunizationState.OVERDUE;
        } else if (toProcess.status().value().equalsIgnoreCase(ImmunizationState.EXPIRED.name())) {
            state = ImmunizationState.EXPIRED;
        }
        return state;
    }

    private boolean isReceived(String s, Map<String, Date> receivedvaccines) {
        boolean isReceived = false;
        for (String name : receivedvaccines.keySet()) {
            if (s.equalsIgnoreCase(name)){
                isReceived = true;
            }
        }
        return isReceived;
    }
}