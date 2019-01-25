package org.smartgresiter.wcaro.fragment;

import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.custom_view.HomeVisitGrowthAndNutrition;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.ServiceSchedule;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.util.DatePickerUtils;
import org.smartregister.util.Utils;

import java.util.Calendar;

public class GrowthNutritionInputFragment extends DialogFragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    public static GrowthNutritionInputFragment getInstance(String title, String type, ServiceWrapper serviceWrapper,
                                                           CommonPersonObjectClient commonPersonObjectClient) {
        GrowthNutritionInputFragment growthNutritionInputFragment = new GrowthNutritionInputFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.INTENT_KEY.GROWTH_IMMUNIZATION_TYPE, type);
        bundle.putSerializable(Constants.INTENT_KEY.GROWTH_SERVICE_WRAPPER, serviceWrapper);
        bundle.putString(Constants.INTENT_KEY.GROWTH_TITLE, title);
        bundle.putSerializable(Constants.INTENT_KEY.CHILD_COMMON_PERSON, commonPersonObjectClient);
        growthNutritionInputFragment.setArguments(bundle);
        return growthNutritionInputFragment;
    }

    private TextView textViewTitle;
    private Button buttonSave;
    private String type, title;
    private LinearLayout layoutExclusiveFeeding, layoutVitaminBar;
    private TextView textViewVitamin;
    private DatePicker datePicker;
    private String isFeeding = "";
    private ServiceWrapper serviceWrapper;
    private CommonPersonObjectClient commonPersonObjectClient;
    private LinearLayout context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_NoActionBar);
    }

    @Override
    public void onStart() {
        super.onStart();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getDialog().getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_growth_nutrition, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        textViewTitle = view.findViewById(R.id.textview_vaccine_title);
        buttonSave = view.findViewById(R.id.save_btn);
        layoutExclusiveFeeding = view.findViewById(R.id.exclusive_feeding_bar);
        layoutVitaminBar = view.findViewById(R.id.vitamin_a_bar);
        textViewVitamin = view.findViewById(R.id.textview_vitamin);
        datePicker = view.findViewById(R.id.earlier_date_picker);
        DatePickerUtils.themeDatePicker(datePicker, new char[]{'d', 'm', 'y'});
        (view.findViewById(R.id.close)).setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        ((RadioGroup) view.findViewById(R.id.radio_group_exclusive)).setOnCheckedChangeListener(this);
        parseBundleAndSetData();

    }

    public void setContext(LinearLayout context) {
        this.context = context;
    }

    private void saveButtonDisable(boolean value) {
        if (value) {
            buttonSave.setAlpha(0.3f);
        } else {
            buttonSave.setAlpha(1.0f);
        }
    }

    private void parseBundleAndSetData() {
        type = getArguments().getString(Constants.INTENT_KEY.GROWTH_IMMUNIZATION_TYPE, GROWTH_TYPE.EXCLUSIVE.name());
        title = getArguments().getString(Constants.INTENT_KEY.GROWTH_TITLE, getString(R.string.growth_and_nutrition));
        title = StringUtils.capitalize(title);
        textViewTitle.setText(title);
        if (type.equalsIgnoreCase(GROWTH_TYPE.EXCLUSIVE.getValue())) {
            visibleExclusiveBar();
        } else {
            textViewVitamin.setText(getString(R.string.vitamin_given, title));
            visibleVitaminBar();
        }
        serviceWrapper = (ServiceWrapper) getArguments().getSerializable(Constants.INTENT_KEY.GROWTH_SERVICE_WRAPPER);
        commonPersonObjectClient = (CommonPersonObjectClient) getArguments().getSerializable(Constants.INTENT_KEY.CHILD_COMMON_PERSON);

    }

    private void visibleExclusiveBar() {
        layoutExclusiveFeeding.setVisibility(View.VISIBLE);
        layoutVitaminBar.setVisibility(View.GONE);
        saveButtonDisable(true);
    }

    private void visibleVitaminBar() {
        layoutExclusiveFeeding.setVisibility(View.GONE);
        layoutVitaminBar.setVisibility(View.VISIBLE);

        saveButtonDisable(false);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {

            case R.id.yes:
                saveButtonDisable(false);
                isFeeding = "yes";
                break;
            case R.id.no:
                saveButtonDisable(false);
                isFeeding = "no";
                break;
        }
    }

    private void saveExclusiveFeedingData() {
        if (serviceWrapper == null || TextUtils.isEmpty(isFeeding)) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        DateTime dateTime = new DateTime(calendar.getTime());
        serviceWrapper.setValue(isFeeding);
        serviceWrapper.setUpdatedVaccineDate(dateTime, true);

        ServiceWrapper[] arrayTags = {serviceWrapper};
        SaveServiceTask backgroundTask = new SaveServiceTask();
        String providerId = ImmunizationLibrary.getInstance().context().allSharedPreferences().fetchRegisteredANM();

        backgroundTask.setProviderId(providerId);
        Utils.startAsyncTask(backgroundTask, arrayTags);
    }

    private void saveVitaminAData() {
        if (serviceWrapper == null) {
            return;
        }
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        DateTime dateTime = new DateTime(calendar.getTime());

        serviceWrapper.setUpdatedVaccineDate(dateTime, false);
        ServiceWrapper[] arrayTags = {serviceWrapper};
        SaveServiceTask backgroundTask = new SaveServiceTask();
        String providerId = ImmunizationLibrary.getInstance().context().allSharedPreferences().fetchRegisteredANM();

        backgroundTask.setProviderId(providerId);
        Utils.startAsyncTask(backgroundTask, arrayTags);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_btn:
                if (type.equalsIgnoreCase(GROWTH_TYPE.EXCLUSIVE.getValue())) {
                    saveExclusiveFeedingData();
                } else {
                    saveVitaminAData();
                }

                break;
            case R.id.close:
                if (context instanceof HomeVisitGrowthAndNutrition && serviceWrapper != null) {
                    HomeVisitGrowthAndNutrition homeVisitGrowthAndNutrition = (HomeVisitGrowthAndNutrition) context;
                    homeVisitGrowthAndNutrition.notVisitSetState(type, serviceWrapper);

                }
                dismiss();
                break;
        }
    }

    public enum GROWTH_TYPE {
        EXCLUSIVE("Exclusive breastfeeding"), MNP("MNP"), VITAMIN("Vitamin A"), DEWORMING("Deworming");
        private String value;

        GROWTH_TYPE(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private ServiceWrapper saveService;

    public class SaveServiceTask extends AsyncTask<ServiceWrapper, Void, ServiceWrapper> {

        private String providerId;


        public void setProviderId(String providerId) {
            this.providerId = providerId;
        }

        @Override
        protected void onPostExecute(ServiceWrapper serviceWrapper) {
            saveService = serviceWrapper;
            dismiss();
        }

        @Override
        protected ServiceWrapper doInBackground(ServiceWrapper... params) {

            //ArrayList<ServiceWrapper> list = new ArrayList<>();
            ServiceWrapper serviceWrapper = null;

            for (ServiceWrapper tag : params) {
                saveService(tag, commonPersonObjectClient.entityId(), providerId, null);
                //list.add(tag);
                //serviceId=tag.getServiceType().getId()+"";
                //tag.getDbKey();
                ServiceSchedule.updateOfflineAlerts(tag.getType(), commonPersonObjectClient.entityId(), Utils.dobToDateTime(commonPersonObjectClient));
                serviceWrapper = tag;
            }
            return serviceWrapper;

//            RecurringServiceRecordRepository recurringServiceRecordRepository = ImmunizationLibrary.getInstance().recurringServiceRecordRepository();
//            List<ServiceRecord> serviceRecordList = recurringServiceRecordRepository.findByEntityId(commonPersonObjectClient.entityId());
//
//            RecurringServiceTypeRepository recurringServiceTypeRepository = ImmunizationLibrary.getInstance().recurringServiceTypeRepository();
//            List<ServiceType> serviceTypes = recurringServiceTypeRepository.fetchAll();
//            String[] alertArray = VaccinateActionUtils.allAlertNames(serviceTypes);
//
//            AlertService alertService = ImmunizationLibrary.getInstance().context().alertService();
//            List<Alert> alertList = alertService.findByEntityIdAndAlertNames(commonPersonObjectClient.entityId(), alertArray);
//
//            return Triple.of(list, serviceRecordList, alertList);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (context instanceof HomeVisitGrowthAndNutrition && saveService != null) {
            HomeVisitGrowthAndNutrition homeVisitGrowthAndNutrition = (HomeVisitGrowthAndNutrition) context;
            homeVisitGrowthAndNutrition.setState(type, saveService);

        }
    }

    public static void saveService(ServiceWrapper tag, String baseEntityId, String providerId, String locationId) {
        if (tag.getUpdatedVaccineDate() == null) {
            return;
        }

        RecurringServiceRecordRepository recurringServiceRecordRepository = ImmunizationLibrary.getInstance().recurringServiceRecordRepository();

        ServiceRecord serviceRecord = new ServiceRecord();
        if (tag.getDbKey() != null) {
            serviceRecord = recurringServiceRecordRepository.find(tag.getDbKey());
            serviceRecord.setDate(tag.getUpdatedVaccineDate().toDate());
        } else {
            serviceRecord.setDate(tag.getUpdatedVaccineDate().toDate());

            serviceRecord.setBaseEntityId(baseEntityId);
            serviceRecord.setRecurringServiceId(tag.getTypeId());
            serviceRecord.setDate(tag.getUpdatedVaccineDate().toDate());
            serviceRecord.setAnmId(providerId);
            serviceRecord.setValue(tag.getValue());
            serviceRecord.setLocationId(org.smartregister.family.util.Utils.context().allSharedPreferences().fetchDefaultLocalityId(providerId));
            serviceRecord.setChildLocationId(org.smartregister.family.util.Utils.context().allSharedPreferences().fetchDefaultLocalityId(providerId));
            serviceRecord.setTeam(org.smartregister.family.util.Utils.context().allSharedPreferences().fetchDefaultTeam(providerId));
            serviceRecord.setTeamId(org.smartregister.family.util.Utils.context().allSharedPreferences().fetchDefaultTeamId(providerId));

        }

        recurringServiceRecordRepository.add(serviceRecord);
        tag.setDbKey(serviceRecord.getId());
    }
}
