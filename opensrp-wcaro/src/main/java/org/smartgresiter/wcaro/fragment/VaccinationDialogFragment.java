package org.smartgresiter.wcaro.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vijay.jsonwizard.customviews.CheckBox;
import com.vijay.jsonwizard.customviews.RadioButton;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.contract.HomeVisitImmunizationContract;
import org.smartregister.domain.Alert;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.ServiceSchedule;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.util.Utils;
import org.smartregister.util.DatePickerUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.smartgresiter.wcaro.util.ChildUtils.fixVaccineCasing;

@SuppressLint("ValidFragment")
public class VaccinationDialogFragment extends ChildImmunizationFragment implements View.OnClickListener {
    private List<VaccineWrapper> tags;
    //    private VaccinationActionListener listener;
    private Date dateOfBirth;
    private List<Vaccine> issuedVaccines;
    public static final String DIALOG_TAG = "VaccinationDialogFragment";
    public static final String WRAPPER_TAG = "tag";
    private boolean disableConstraints;
    private Calendar dcToday;
    private DialogInterface.OnDismissListener onDismissListener;
    private Integer defaultImageResourceID;
    private Integer defaultErrorImageResourceID;
    private HomeVisitImmunizationContract.View homeVisitImmunizationView;
    private int selectCount=0;
    public void setContext(Activity context) {
        this.context = context;
    }

    private Activity context;
    private Button saveBtn;
    private LinearLayout multipleVaccineDatePickerView,singleVaccineAddView,vaccinationNameLayout;
    private CheckBox checkBoxNoVaccine;
    private LayoutInflater inflater;
    private DatePicker earlierDatePicker;
    private TextView textViewAddDate;

    public static VaccinationDialogFragment newInstance(Date dateOfBirth,
                                                        List<Vaccine> issuedVaccines,
                                                        ArrayList<VaccineWrapper> tags) {

        VaccinationDialogFragment customVaccinationDialogFragment = new VaccinationDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(WRAPPER_TAG, tags);
        customVaccinationDialogFragment.setArguments(args);
        customVaccinationDialogFragment.setDateOfBirth(dateOfBirth);
        customVaccinationDialogFragment.setIssuedVaccines(issuedVaccines);
        customVaccinationDialogFragment.setDisableConstraints(true);

        return customVaccinationDialogFragment;
    }

    public static VaccinationDialogFragment newInstance(Date dateOfBirth,
                                                        List<Vaccine> issuedVaccines,
                                                        ArrayList<VaccineWrapper> tags, boolean disableConstraints) {

        VaccinationDialogFragment customVaccinationDialogFragment = new VaccinationDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(WRAPPER_TAG, tags);
        customVaccinationDialogFragment.setArguments(args);
        customVaccinationDialogFragment.setDateOfBirth(dateOfBirth);
        customVaccinationDialogFragment.setIssuedVaccines(issuedVaccines);
        customVaccinationDialogFragment.setDisableConstraints(disableConstraints);

        return customVaccinationDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_NoActionBar);
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.fragment_vaccination_dialog_view, container, false);
        initUi(dialogView);
        return dialogView;
    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parseBundleData();
        updateDatePicker(earlierDatePicker);
        updateVaccineList();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_date_separately:
                showSingleVaccineDetailsView();
                break;
            case R.id.checkbox_no_vaccination:
                checkBoxNoVaccine.toggle();
                break;
            case R.id.save_btn:
                if(selectCount==0)return;
                dismiss();

                ArrayList<VaccineWrapper> tagsToUpdate = new ArrayList<VaccineWrapper>();
                ArrayList<VaccineWrapper> UngiventagsToUpdate = new ArrayList<VaccineWrapper>();

                int day = earlierDatePicker.getDayOfMonth();
                int month = earlierDatePicker.getMonth();
                int year = earlierDatePicker.getYear();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                DateTime dateTime = new DateTime(calendar.getTime());
                if (tags.size() == 1) {
                    VaccineWrapper tag = tags.get(0);
                    String radioName = findSelectRadio(vaccinationNameLayout);
                    if (radioName != null) {
                        tag.setName(radioName);
                    }

                    if (validateVaccinationDate(tag, dateTime.toDate())) {
                        tag.setUpdatedVaccineDate(dateTime, false);
                        tagsToUpdate.add(tag);
                    } else {
                        Toast.makeText(VaccinationDialogFragment.this.getActivity(),
                                String.format(getString(R.string.cannot_record_vaccine), tag.getName()),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    List<String> selectedCheckboxes = findSelectedCheckBoxes(vaccinationNameLayout);
                    for (String checkedName : selectedCheckboxes) {
                        VaccineWrapper tag = searchWrapperByName(checkedName);
                        if (tag != null) {
                            if (validateVaccinationDate(tag, dateTime.toDate())) {
                                tag.setUpdatedVaccineDate(dateTime, false);
                                tagsToUpdate.add(tag);
                            } else {
                                Toast.makeText(VaccinationDialogFragment.this.getActivity(),
                                        String.format(getString(R.string.cannot_record_vaccine),
                                                tag.getName()), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    List<String> UnselectedCheckboxes = findUnSelectedCheckBoxes(vaccinationNameLayout);
                    for (String checkedName : UnselectedCheckboxes) {
                        VaccineWrapper tag = searchWrapperByName(checkedName);
                        if (tag != null) {
                            if (validateVaccinationDate(tag, dateTime.toDate())) {
                                tag.setUpdatedVaccineDate(dateTime, false);
                                UngiventagsToUpdate.add(tag);
                            } else {
                                Toast.makeText(VaccinationDialogFragment.this.getActivity(),
                                        String.format(getString(R.string.cannot_record_vaccine),
                                                tag.getName()), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                }
                onVaccinateEarlier(tagsToUpdate, view);
                homeVisitImmunizationView.getPresenter().assigntoGivenVaccines(tagsToUpdate);
                ///////handle not given
                for(VaccineWrapper tags:UngiventagsToUpdate) {
                    homeVisitImmunizationView.getPresenter().updateNotGivenVaccine(tags);
                }
                break;
            case R.id.close:
                dismiss();
                break;
        }
    }


    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setIssuedVaccines(List<Vaccine> issuedVaccines) {
        this.issuedVaccines = issuedVaccines;
    }

    public void setDisableConstraints(boolean disableConstraints) {
        this.disableConstraints = disableConstraints;
        if (disableConstraints) {
            Calendar dcToday = Calendar.getInstance();
            VaccineSchedule.standardiseCalendarDate(dcToday);
            this.dcToday = dcToday;
        }
    }
    private void parseBundleData(){
        Bundle bundle = getArguments();
        final Serializable serializable = bundle.getSerializable(WRAPPER_TAG);
        if (serializable != null && serializable instanceof ArrayList) {
            tags = (ArrayList<VaccineWrapper>) serializable;
        }

        if (tags == null || tags.isEmpty()) {
            return;
        }
    }
    private void initUi(View dialogView){
        multipleVaccineDatePickerView=dialogView.findViewById(R.id.multiple_vaccine_date_pickerview);
        singleVaccineAddView=dialogView.findViewById(R.id.single_vaccine_add_layout);
        saveBtn=dialogView.findViewById(R.id.save_btn);
        View noVaccineLayout=dialogView.findViewById(R.id.checkbox_no_vaccination);
        noVaccineLayout.setOnClickListener(this);
        checkBoxNoVaccine=noVaccineLayout.findViewById(R.id.select);
        checkBoxNoVaccine.setChecked(false);
        checkBoxNoVaccine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    resetAllSelectedVaccine();
                }

            }
        });
        saveBtn= (Button) dialogView.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);
        vaccinationNameLayout= (LinearLayout) dialogView.findViewById(R.id.vaccination_name_layout);
        earlierDatePicker= (DatePicker) dialogView.findViewById(R.id.earlier_date_picker);
        textViewAddDate= (TextView) dialogView.findViewById(R.id.add_date_separately);
        textViewAddDate.setOnClickListener(this);
        ((ImageView) dialogView.findViewById(R.id.close)).setOnClickListener(this);
    }
    private void resetAllSelectedVaccine(){
        Map<CheckBox,String> getSelectedCheckBox=getSelectedCheckBoxes();
        for (CheckBox checkBox:getSelectedCheckBox.keySet()){
            checkBox.toggle();
        }
        multipleVaccineDatePickerView.setAlpha(0.3f);

    }
    private void updateVaccineList(){
        if(tags==null) return;
        for (VaccineWrapper vaccineWrapper : tags) {

            View vaccinationName = inflater.inflate(R.layout.custom_vaccine_name_check, null);
            TextView vaccineView = (TextView) vaccinationName.findViewById(R.id.vaccine);


            VaccineRepo.Vaccine vaccine = vaccineWrapper.getVaccine();
            if (vaccineWrapper.getVaccine() != null) {
                vaccineView.setText(fixVaccineCasing(vaccine.display()));
            } else {
                vaccineView.setText(vaccineWrapper.getName());
            }
            vaccinationNameLayout.addView(vaccinationName);
        }

        selectCount=vaccinationNameLayout.getChildCount();
        for (int i = 0; i < vaccinationNameLayout.getChildCount(); i++) {
            View childView = vaccinationNameLayout.getChildAt(i);
            final CheckBox childSelect = (CheckBox) childView.findViewById(R.id.select);
            childSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        selectCount++;
                    }else{
                        selectCount--;
                    }
                    updateSaveButton();
                }
            });
            childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    childSelect.toggle();
                }
            });
        }
    }
    private void showSingleVaccineDetailsView(){
        multipleVaccineDatePickerView.setVisibility(View.GONE);
        ArrayList<VaccineWrapper> vaccineWrappers = new ArrayList<VaccineWrapper>();
        List<String> selectedCheckboxes = findSelectedCheckBoxes(vaccinationNameLayout);
        singleVaccineAddView.removeAllViews();
        for (String checkedName : selectedCheckboxes) {

            VaccineWrapper tag = searchWrapperByName(checkedName);
            String dobString = org.smartregister.util.Utils.getValue(getChildDetails().getColumnmaps(), DBConstants.KEY.DOB, false);

            if (tag != null) {
                if (!TextUtils.isEmpty(dobString)) {
//                    DateTime dateTime = new DateTime(dobString);
//                    Date dob = dateTime.toDate();
                    View layout = inflater.inflate(R.layout.custom_single_vaccine_view, null);
                    TextView question=layout.findViewById(R.id.vaccines_given_title_question);
                    DatePicker datePicker=layout.findViewById(R.id.earlier_date_picker);
                    question.setText(getString(R.string.when_vaccine,checkedName));
                    updateDatePicker(datePicker);

                    vaccineWrappers.add(tag);
                    singleVaccineAddView.addView(layout);

                }
            }
        }

    }

    private void updateSaveButton(){
        if(saveBtn!=null){
            if(selectCount==0){
                checkBoxNoVaccine.setChecked(true);
                multipleVaccineDatePickerView.setAlpha(0.3f);
                //saveBtn.setAlpha(0.3f);
            }else{
                checkBoxNoVaccine.setChecked(false);
                multipleVaccineDatePickerView.setAlpha(1.0f);
                //saveBtn.setAlpha(1.0f);
            }

        }
    }
    private void updateDatePicker(DatePicker datePicker) {
        DateTime minDate = new DateTime(dateOfBirth);
        DateTime dcToday = ServiceSchedule.standardiseDateTime(DateTime.now());
        DateTime maxDate = ServiceSchedule.standardiseDateTime(dcToday);

        datePicker.setMinDate(minDate.getMillis());
        datePicker.setMaxDate(maxDate.getMillis());

    }

    @Override
    public void onResume() {
        super.onResume();
        updateSaveButton();
    }

    private boolean validateVaccinationDate(VaccineWrapper vaccine, Date date) {
        // Assuming that the vaccine wrapper provided to this method isn't one where there's more than one vaccine in a wrapper
        Date minDate = null;
        Date maxDate = null;

        if (disableConstraints) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateOfBirth);
            VaccineSchedule.standardiseCalendarDate(calendar);

            minDate = calendar.getTime();
            maxDate = dcToday.getTime();
        }
        Calendar vaccineDate = Calendar.getInstance();
        vaccineDate.setTime(date);
        VaccineSchedule.standardiseCalendarDate(vaccineDate);
        boolean result;

        // A null min date means the vaccine is not due (probably because the prerequisite hasn't been done yet)
        result = minDate != null;

        // Check if vaccination was done before min date
        if (minDate != null) {
            Calendar min = Calendar.getInstance();
            min.setTime(minDate);
            VaccineSchedule.standardiseCalendarDate(min);

            result = min.getTimeInMillis() <= vaccineDate.getTimeInMillis();
        }

        // A null max date means the vaccine doesn't have a max date check
        //Check if vaccination was done after the max date
        if (maxDate != null) {
            Calendar max = Calendar.getInstance();
            max.setTime(maxDate);
            VaccineSchedule.standardiseCalendarDate(max);

            result = result && vaccineDate.getTimeInMillis() <= max.getTimeInMillis();
        }

        return result;
    }

    @Override
    public void onStart() {
        super.onStart();

        // without a handler, the window sizes itself correctly
        // but the keyboard does not show up
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Window window = null;
                if (getDialog() != null) {
                    window = getDialog().getWindow();
                }

                if (window == null) {
                    return;
                }

                Point size = new Point();

                Display display = window.getWindowManager().getDefaultDisplay();
                display.getSize(size);

                window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                window.setGravity(Gravity.CENTER);
            }
        });
    }

    private VaccineWrapper searchWrapperByName(String name) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }

        for (VaccineWrapper tag : tags) {
            if (tag.getVaccine() != null) {
                if (tag.getVaccine().display().equalsIgnoreCase(name)) {
                    return tag;
                }
            } else {
                if (tag.getName().equalsIgnoreCase(name)) {
                    return tag;
                }
            }
        }
        return null;
    }
    private Map<CheckBox,String> getSelectedCheckBoxes(){
        Map<CheckBox,String> vaccineCheckMap=new LinkedHashMap<>();
        for (int i = 0; i < vaccinationNameLayout.getChildCount(); i++) {
            View chilView = vaccinationNameLayout.getChildAt(i);
            CheckBox selectChild = (CheckBox) chilView.findViewById(R.id.select);
            if (selectChild.isChecked()) {
                TextView childVaccineView = (TextView) chilView.findViewById(R.id.vaccine);
                String checkedName = childVaccineView.getText().toString();
                vaccineCheckMap.put(selectChild,checkedName);
            }
        }
        return vaccineCheckMap;
    }


    private List<String> findSelectedCheckBoxes(LinearLayout vaccinationNameLayout) {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < vaccinationNameLayout.getChildCount(); i++) {
            View chilView = vaccinationNameLayout.getChildAt(i);
            CheckBox selectChild = (CheckBox) chilView.findViewById(R.id.select);
            if (selectChild.isChecked()) {
                TextView childVaccineView = (TextView) chilView.findViewById(R.id.vaccine);
                String checkedName = childVaccineView.getText().toString();
                names.add(checkedName);
            }
        }

        return names;
    }

    private List<String> findUnSelectedCheckBoxes(LinearLayout vaccinationNameLayout) {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < vaccinationNameLayout.getChildCount(); i++) {
            View chilView = vaccinationNameLayout.getChildAt(i);
            CheckBox selectChild = (CheckBox) chilView.findViewById(R.id.select);
            if (!selectChild.isChecked()) {
                TextView childVaccineView = (TextView) chilView.findViewById(R.id.vaccine);
                String checkedName = childVaccineView.getText().toString();
                names.add(checkedName);
            }
        }

        return names;
    }

    private String findSelectRadio(LinearLayout vaccinationNameLayout) {
        for (int i = 0; i < vaccinationNameLayout.getChildCount(); i++) {
            View chilView = vaccinationNameLayout.getChildAt(i);
            RadioButton radioChild = (RadioButton) chilView.findViewById(R.id.radio);
            if (radioChild.getVisibility() == View.VISIBLE && radioChild.isChecked()) {
                TextView childVaccineView = (TextView) chilView.findViewById(R.id.vaccine);
                return childVaccineView.getText().toString();
            }
        }
        return null;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public Integer getDefaultImageResourceID() {
        return defaultImageResourceID;
    }

    public void setDefaultImageResourceID(Integer defaultImageResourceID) {
        this.defaultImageResourceID = defaultImageResourceID;
    }

    public Integer getDefaultErrorImageResourceID() {
        return defaultErrorImageResourceID;
    }

    public void setDefaultErrorImageResourceID(Integer defaultErrorImageResourceID) {
        this.defaultErrorImageResourceID = defaultErrorImageResourceID;
    }

    @Override
    public void updateAgeViews() {
    }

    @Override
    public void updateChildIdViews() {
    }

    public void addVaccineGroup(int canvasId, org.smartregister.immunization.domain.jsonmapping.VaccineGroup vaccineGroupData, List<Vaccine> vaccineList, List<Alert> alerts) {
    }

    @Override
    public void updateVaccineGroupViews(View view, final ArrayList<VaccineWrapper> wrappers, final List<Vaccine> vaccineList, final boolean undo) {
        ((ChildHomeVisitFragment) context.getFragmentManager().findFragmentByTag("child_home_visit_dialog")).updateImmunizationState();
    }

    public void setView(HomeVisitImmunizationContract.View homeVisitImmunizationView) {
        this.homeVisitImmunizationView = homeVisitImmunizationView;
    }


}
