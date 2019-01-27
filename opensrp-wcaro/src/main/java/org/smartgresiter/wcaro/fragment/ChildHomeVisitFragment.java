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

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.activity.ChildProfileActivity;
import org.smartgresiter.wcaro.activity.ChildRegisterActivity;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.contract.ChildRegisterContract;
import org.smartgresiter.wcaro.custom_view.HomeVisitGrowthAndNutrition;
import org.smartgresiter.wcaro.custom_view.HomeVisitImmunizationView;
import org.smartgresiter.wcaro.interactor.ChildRegisterInteractor;
import org.smartgresiter.wcaro.model.ChildRegisterModel;
import org.smartgresiter.wcaro.repository.WcaroRepository;
import org.smartgresiter.wcaro.util.ChildDBConstants;
import org.smartgresiter.wcaro.util.ChildUtils;
import org.smartgresiter.wcaro.util.Constants;
import org.smartgresiter.wcaro.util.HomeVisitVaccineGroupDetails;
import org.smartgresiter.wcaro.util.JsonFormUtils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.activity.BaseFamilyProfileActivity;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.BIRTH_CERT;
import static org.smartregister.util.JsonFormUtils.getFieldJSONObject;
import static org.smartregister.util.Utils.getValue;

public class ChildHomeVisitFragment extends DialogFragment implements View.OnClickListener{


    private static final String TAG ="ChildHomeVisitFragment" ;
    public static String DIALOG_TAG = "child_home_visit_dialog";
    Context context;
    CommonPersonObjectClient childClient;
    private TextView nameHeader;
    private HomeVisitGrowthAndNutrition homeVisitGrowthAndNutritionLayout;
    public boolean allVaccineStateFullfilled = false;
    private TextView submit;
    private HomeVisitImmunizationView homeVisitImmunizationView;
    private LinearLayout layoutBirthCertGroup,layoutIllnessGroup;
    private HashMap<String,Pair<Client, Event>> saveList=new HashMap<>();
    private CircleImageView circleImageViewBirthStatus,circleImageViewIllnessStatus;


    public void setContext(Context context) {
        this.context = context;
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
        circleImageViewBirthStatus=view.findViewById(R.id.birth_status_circle);
        circleImageViewIllnessStatus=view.findViewById(R.id.obs_illness_status_circle);
        layoutBirthCertGroup=view.findViewById(R.id.birth_cert_group);
        layoutIllnessGroup=view.findViewById(R.id.obs_illness_prevention_group);
        view.findViewById(R.id.textview_submit).setOnClickListener(this);
        layoutBirthCertGroup.setOnClickListener(this);
        layoutIllnessGroup.setOnClickListener(this);
        homeVisitGrowthAndNutritionLayout = view.findViewById(R.id.growth_and_nutrition_group);

        homeVisitImmunizationView = (HomeVisitImmunizationView) view.findViewById(R.id.home_visit_immunization_view);
        homeVisitImmunizationView.setActivity(getActivity());
        homeVisitImmunizationView.setChildClient(childClient);
        assignNameHeader();
        submitButtonEnableDisable(false);
    }

    private void assignNameHeader() {
        String dobString = org.smartregister.family.util.Utils.getDuration(org.smartregister.family.util.Utils.getValue(childClient.getColumnmaps(), DBConstants.KEY.DOB, false));
        String birthCert=getValue(childClient.getColumnmaps(), BIRTH_CERT, true);

        nameHeader.setText(
                getValue(childClient.getColumnmaps(),DBConstants.KEY.FIRST_NAME , true) + " " +
                        getValue(childClient.getColumnmaps(), DBConstants.KEY.LAST_NAME, true) + ", " +
                        dobString + " - Home Visit"
        );
        if(!TextUtils.isEmpty(birthCert)){
            layoutBirthCertGroup.setVisibility(View.GONE);
        }
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
            case R.id.birth_cert_group:

                try {
                    startBirthCertForm();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.obs_illness_prevention_group:
                try {
                    startIllnessForm();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.textview_submit:
                if (checkAllGiven()) {
                    ChildUtils.updateClientStatusAsEvent(childClient.entityId(), Constants.EventType.CHILD_HOME_VISIT, ChildDBConstants.KEY.LAST_HOME_VISIT, System.currentTimeMillis()+"", Constants.TABLE_NAME.CHILD);

                    if (getActivity() instanceof ChildRegisterActivity) {
                        ((ChildRegisterActivity) getActivity()).refreshList(FetchStatus.fetched);
                    }
                    if(saveList.size()>0){
                        saveFormData();
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

    private void saveFormData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(String json:saveList.keySet()){
                    Pair<Client, Event> pair=saveList.get(json);
                    saveRegistration(pair,json);
                }
            }
        }).start();
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

    public void startBirthCertForm() throws Exception {
        JSONObject form = getFormUtils().getFormJson(Constants.JSON_FORM.BIRTH_CERTIFICATION);
        String dobString = org.smartregister.family.util.Utils.getDuration(org.smartregister.family.util.Utils.getValue
                (childClient.getColumnmaps(), DBConstants.KEY.DOB, false));

        JSONObject revForm=JsonFormUtils.getBirthCertFormAsJson(form,childClient.getCaseId(),"",dobString);
        startFormActivity(revForm);
    }
    public void startIllnessForm() throws Exception {
        JSONObject form = getFormUtils().getFormJson(Constants.JSON_FORM.OBS_ILLNESS);
        String dobString = org.smartregister.family.util.Utils.getDuration(org.smartregister.family.util.Utils.getValue
                (childClient.getColumnmaps(), DBConstants.KEY.DOB, false));

        JSONObject revForm=JsonFormUtils.getOnsIllnessFormAsJson(form,childClient.getCaseId(),"",dobString);
        startFormActivity(revForm);
    }
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(context, org.smartregister.family.util.Utils.metadata().familyMemberFormActivity);
        intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        Form form = new Form();
        form.setWizard(false);
        form.setActionBarBackground(org.smartregister.family.R.color.customAppThemeBlue);

        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

        startActivityForResult(intent, org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON);
    }
    public void saveBirthIllnessForm(String jsonString) {
        try {

            Pair<Client, Event> pair = JsonFormUtils.processBirthAndIllnessForm(org.smartregister.family.util.Utils.context().allSharedPreferences(),jsonString);
            if (pair == null) {
                return;
            }
            JSONObject form = new JSONObject(jsonString);
            if(form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.BIRTH_CERTIFICATION)){
                updateStatusTick(circleImageViewBirthStatus,true);
                saveList.put(jsonString,pair);

            }
            if(form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.OBS_ILLNESS)){
                updateStatusTick(circleImageViewIllnessStatus,true);
                saveList.put(jsonString,pair);

            }


        } catch (Exception e) {
            Log.e(DIALOG_TAG, Log.getStackTraceString(e));
        }
    }
    private void saveRegistration(Pair<Client, Event> pair, String jsonString) {

        try {

            Client baseClient = pair.first;
            Event baseEvent = pair.second;

            if (baseClient != null) {
                JSONObject clientjsonFromForm = new JSONObject(org.smartregister.family.util.JsonFormUtils.gson.toJson(baseClient));
                WcaroRepository pathRepository = new WcaroRepository(context, WcaroApplication.getInstance().getContext());
                EventClientRepository eventClientRepository = new EventClientRepository(pathRepository);
                JSONObject clientJson = eventClientRepository.getClient(WcaroApplication.getInstance().getRepository().getReadableDatabase(),baseClient.getBaseEntityId());
                updateClientAttributes(clientjsonFromForm,clientJson);
                getSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);

            }

            if (baseEvent != null) {
                JSONObject eventJson = new JSONObject(org.smartregister.family.util.JsonFormUtils.gson.toJson(baseEvent));
                getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson);
            }

            if (baseClient != null || baseEvent != null) {
                String imageLocation = org.smartregister.family.util.JsonFormUtils.getFieldValue(jsonString, org.smartregister.family.util.Constants.KEY.PHOTO);
                org.smartregister.family.util.JsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);

            }

            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());


        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }
    private static void updateClientAttributes(JSONObject clientjsonFromForm, JSONObject clientJson) {
        try {
            JSONObject formAttributes = clientjsonFromForm.getJSONObject("attributes");
            JSONObject clientAttributes = clientJson.getJSONObject("attributes");
            Iterator<String> keys = formAttributes.keys();

            while(keys.hasNext()) {
                String key = keys.next();
                clientAttributes.put(key,formAttributes.get(key));

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    public AllSharedPreferences getAllSharedPreferences() {
        return org.smartregister.family.util.Utils.context().allSharedPreferences();
    }

    public UniqueIdRepository getUniqueIdRepository() {
        return FamilyLibrary.getInstance().getUniqueIdRepository();
    }

    public ECSyncHelper getSyncHelper() {
        return FamilyLibrary.getInstance().getEcSyncHelper();
    }

    public ClientProcessorForJava getClientProcessorForJava() {
        return FamilyLibrary.getInstance().getClientProcessorForJava();
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
                if (form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.BIRTH_CERTIFICATION)
                  || form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.OBS_ILLNESS)
                        ) {
                    saveBirthIllnessForm(jsonString);
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