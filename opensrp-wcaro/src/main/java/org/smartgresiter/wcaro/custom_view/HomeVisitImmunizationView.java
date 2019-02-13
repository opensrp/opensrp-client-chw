package org.smartgresiter.wcaro.custom_view;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.contract.HomeVisitImmunizationContract;
import org.smartgresiter.wcaro.fragment.ChildHomeVisitFragment;
import org.smartgresiter.wcaro.fragment.ChildImmunizationFragment;
import org.smartgresiter.wcaro.fragment.CustomMultipleVaccinationDialogFragment;
import org.smartgresiter.wcaro.fragment.CustomVaccinationDialogFragment;
import org.smartgresiter.wcaro.presenter.HomeVisitImmunizationPresenter;
import org.smartgresiter.wcaro.util.HomeVisitVaccineGroupDetails;
import org.smartgresiter.wcaro.util.ImmunizationState;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.apache.commons.lang3.text.WordUtils.capitalize;
import static org.smartgresiter.wcaro.util.Constants.IMMUNIZATION_CONSTANT.DATE;
import static org.smartregister.util.StringUtil.humanize;

public class HomeVisitImmunizationView extends LinearLayout implements View.OnClickListener, HomeVisitImmunizationContract.View {
    public static final String TAG = "HomeVisitImmunization";
    private HomeVisitImmunizationContract.Presenter presenter;
    private CommonPersonObjectClient commonPersonObjectClient;
    private FragmentManager fragmentManager;
    private ChildHomeVisitFragment childHomeVisitFragment;
    private TextView textview_group_immunization_primary_text;
    private TextView textview_group_immunization_secondary_text;
    private TextView textview_immunization_primary_text;
    private TextView textview_immunization_secondary_text;
    private CircleImageView immunization_status_circle;
    private CircleImageView immunization_group_status_circle;
    private LinearLayout multiple_immunization_group;
    private LinearLayout single_immunization_group;
    Activity context;
    private boolean isInEditMode = false;


    public HomeVisitImmunizationView(Context context) {
        super(context);
        initUi();
    }

    public HomeVisitImmunizationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();

    }

    public HomeVisitImmunizationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();

    }

    @Override
    public void setActivity(Activity activity) {
        this.context = activity;
    }

    private void initUi() {
        inflate(getContext(), R.layout.view_immunization, this);
        textview_group_immunization_primary_text = (TextView) findViewById(R.id.textview_group_immunization);
        textview_group_immunization_secondary_text = (TextView) findViewById(R.id.textview_immunization_group_secondary_text);
        textview_immunization_primary_text = (TextView) findViewById(R.id.textview_immunization);
        textview_immunization_secondary_text = (TextView) findViewById(R.id.textview_immunization_secondary_text);
        immunization_status_circle = ((CircleImageView) findViewById(R.id.immunization_status_circle));
        immunization_group_status_circle = ((CircleImageView) findViewById(R.id.immunization_group_status_circle));
        single_immunization_group = ((LinearLayout) findViewById(R.id.immunization_name_group));
        multiple_immunization_group = ((LinearLayout) findViewById(R.id.immunization_group));
        initializePresenter();

    }

    @Override
    public void setChildClient(CommonPersonObjectClient childClient) {
        presenter.setChildClient(childClient);
    }

    @Override
    public void refreshPresenter(List<Alert> alerts, List<Vaccine> vaccines, List<Map<String, Object>> sch) {
        presenter.createAllVaccineGroups(alerts, vaccines, sch);
        presenter.getVaccinesNotGivenLastVisit();
        presenter.calculateCurrentActiveGroup();
        presenter.setGroupVaccineText(sch);
        presenter.setSingleVaccineText(presenter.getVaccinesDueFromLastVisit(), sch);

        if (presenter.isPartiallyComplete()) {
            textview_group_immunization_primary_text.setText("Immunizations" + " (" + presenter.getCurrentActiveGroup().getGroup().replace("weeks", "w").replace("months", "m") + ")");
            textview_group_immunization_secondary_text.setTextColor(getResources().getColor(android.R.color.darker_gray));
            textview_group_immunization_secondary_text.setText(presenter.getGroupImmunizationSecondaryText());
            immunization_group_status_circle.setImageResource(R.drawable.ic_checked);
            immunization_group_status_circle.setColorFilter(getResources().getColor(R.color.white));
            immunization_group_status_circle.setCircleBackgroundColor(getResources().getColor(R.color.pnc_circle_yellow));
            immunization_group_status_circle.setBorderColor(getResources().getColor(R.color.pnc_circle_yellow));
            multiple_immunization_group.setOnClickListener(null);
        } else if (presenter.isComplete()) {
            textview_group_immunization_primary_text.setText("Immunizations" + " (" + presenter.getCurrentActiveGroup().getGroup().replace("weeks", "w").replace("months", "m") + ")");
            textview_group_immunization_secondary_text.setTextColor(getResources().getColor(android.R.color.darker_gray));
            textview_group_immunization_secondary_text.setText(presenter.getGroupImmunizationSecondaryText());
            immunization_group_status_circle.setImageResource(R.drawable.ic_checked);
            immunization_group_status_circle.setColorFilter(getResources().getColor(R.color.white));
            immunization_group_status_circle.setCircleBackgroundColor(getResources().getColor(R.color.alert_complete_green));
            immunization_group_status_circle.setBorderColor(getResources().getColor(R.color.alert_complete_green));
            multiple_immunization_group.setOnClickListener(null);
        } else if (presenter.groupIsDue()) {
            textview_group_immunization_primary_text.setText("Immunizations" + " (" + presenter.getCurrentActiveGroup().getGroup().replace("weeks", "w").replace("months", "m") + ")");
            if(presenter.getCurrentActiveGroup().getAlert().equals(ImmunizationState.OVERDUE)) {
                textview_group_immunization_secondary_text.setText("Overdue " + presenter.getCurrentActiveGroup().getDueDisplayDate());
                textview_group_immunization_secondary_text.setTextColor(getResources().getColor(R.color.alert_urgent_red));
            }else if(presenter.getCurrentActiveGroup().getAlert().equals(ImmunizationState.OVERDUE)){
                textview_group_immunization_secondary_text.setText("Due " + presenter.getCurrentActiveGroup().getDueDisplayDate());
                textview_group_immunization_secondary_text.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
            multiple_immunization_group.setTag(R.id.nextduevaccinelist, presenter.getCurrentActiveGroup());
            multiple_immunization_group.setTag(R.id.vaccinelist, vaccines);
            multiple_immunization_group.setOnClickListener(this);
        }
        if (presenter.getVaccinesDueFromLastVisit().size() > 0) {
            String vaccinesDueLastVisit = "";
            for (int i = 0; i < presenter.getVaccinesDueFromLastVisit().size(); i++) {
                String vaccineName = presenter.getVaccinesDueFromLastVisit().get(i).display();
                if(vaccineName.toLowerCase().contains("rota")||vaccineName.toLowerCase().contains("penta")){
                    vaccineName = capitalize(vaccineName.toLowerCase());
                }else{
                    vaccineName = vaccineName.toUpperCase();
                }
                vaccinesDueLastVisit = vaccinesDueLastVisit + vaccineName + ",";
            }
            if (vaccinesDueLastVisit.endsWith(",")) {
                vaccinesDueLastVisit = vaccinesDueLastVisit.substring(0, vaccinesDueLastVisit.length() - 1);
            }
            textview_immunization_primary_text.setText(vaccinesDueLastVisit);
            single_immunization_group.setTag(R.id.nextduevaccinelist, presenter.getVaccinesDueFromLastVisitStillDueState());
            single_immunization_group.setOnClickListener(this);

            if (presenter.getVaccinesDueFromLastVisitStillDueState().size() == 0) {
                if (presenter.isSingleVaccineGroupPartialComplete()) {
                    textview_immunization_secondary_text.setText(presenter.getSingleImmunizationSecondaryText());
                    textview_immunization_secondary_text.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    immunization_status_circle.setImageResource(R.drawable.ic_checked);
                    immunization_status_circle.setColorFilter(getResources().getColor(R.color.white));
                    immunization_status_circle.setCircleBackgroundColor(getResources().getColor(R.color.pnc_circle_yellow));
                    immunization_status_circle.setBorderColor(getResources().getColor(R.color.pnc_circle_yellow));
                }
                if (presenter.isSingleVaccineGroupComplete()) {
                    textview_immunization_secondary_text.setText(presenter.getSingleImmunizationSecondaryText());
                    textview_immunization_secondary_text.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    immunization_status_circle.setImageResource(R.drawable.ic_checked);
                    immunization_status_circle.setColorFilter(getResources().getColor(R.color.white));
                    immunization_status_circle.setCircleBackgroundColor(getResources().getColor(R.color.alert_complete_green));
                    immunization_status_circle.setBorderColor(getResources().getColor(R.color.alert_complete_green));
                }
            }else if(presenter.getVaccinesDueFromLastVisitStillDueState().size()>0){

                String SingleImmunizationSecondaryText = getSingleImmunizationSecondaryText(presenter.getVaccinesDueFromLastVisitStillDueState(),sch,alerts);
                textview_immunization_secondary_text.setText(SingleImmunizationSecondaryText);
                if(SingleImmunizationSecondaryText.toLowerCase().contains(ImmunizationState.DUE.toString().toLowerCase())){
                    textview_immunization_secondary_text.setTextColor(getResources().getColor(android.R.color.darker_gray));
                }
                if(SingleImmunizationSecondaryText.toLowerCase().contains(ImmunizationState.OVERDUE.toString().toLowerCase())){
                    textview_immunization_secondary_text.setTextColor(getResources().getColor(R.color.alert_urgent_red));
                }

            }
        } else {
            single_immunization_group.setVisibility(View.GONE);
        }
    }

    private String getSingleImmunizationSecondaryText(ArrayList<VaccineRepo.Vaccine> vaccinesDueFromLastVisitStillDueState, List<Map<String, Object>> sch, List<Alert> alerts) {
        String toReturn = "";
        ImmunizationState currentState = ImmunizationState.NO_ALERT;
        for(VaccineRepo.Vaccine vaccine : vaccinesDueFromLastVisitStillDueState){
            ImmunizationState state = presenter.getHomeVisitImmunizationInteractor().assignAlert(vaccine,alerts);
            if((currentState.equals(ImmunizationState.DUE)&&state.equals(ImmunizationState.OVERDUE))||
                    (currentState.equals(ImmunizationState.NO_ALERT)&&state.equals(ImmunizationState.OVERDUE))||
                    (currentState.equals(ImmunizationState.NO_ALERT)&&state.equals(ImmunizationState.DUE))){
                    currentState = state;
                for (Map<String, Object> toprocess : sch) {
                    if (((VaccineRepo.Vaccine) (toprocess.get("vaccine"))).name().equalsIgnoreCase(vaccine.name())) {
                    DateTime dueDate = (DateTime) toprocess.get(DATE);
                    String duedateString = DateUtil.formatDate(dueDate.toLocalDate(), "dd MMM yyyy");
                    toReturn = capitalize(state.toString().toLowerCase()) + " "+duedateString;
                    }
                }
            }
        }
        return toReturn;
    }

    private String immunizationsGivenThisVisitafterCompletion() {
        String immunizationGivenThisVisit = "";
        for (VaccineWrapper vaccineWrapper : presenter.getVaccinesGivenThisVisit()) {
            immunizationGivenThisVisit = immunizationGivenThisVisit + vaccineWrapper.getName().toUpperCase() + ",";
        }
        if (immunizationGivenThisVisit.equalsIgnoreCase("")) {
            immunizationGivenThisVisit = getAllGivenVaccines();
        }
        if (immunizationGivenThisVisit.endsWith(",")) {
            immunizationGivenThisVisit = immunizationGivenThisVisit.substring(0, immunizationGivenThisVisit.length() - 1);
        }
        return immunizationGivenThisVisit;
    }

    private String getAllGivenVaccines() {
        String allImmunizationGivenThisVisit = "";
        for (HomeVisitVaccineGroupDetails toprocess : presenter.getAllgroups()) {
            for (VaccineRepo.Vaccine given : toprocess.getGivenVaccines()) {
                allImmunizationGivenThisVisit = allImmunizationGivenThisVisit + given.display().toUpperCase() + ",";
            }
        }
        return allImmunizationGivenThisVisit;
    }


    @Override
    public void onClick(View v) {
        FragmentTransaction ft = context.getFragmentManager().beginTransaction();
        String dobString = org.smartregister.util.Utils.getValue(presenter.getchildClient().getColumnmaps(), "dob", false);

        switch (v.getId()) {

            case R.id.immunization_group:
                if (!TextUtils.isEmpty(dobString)) {
                    DateTime dateTime = new DateTime(dobString);
                    Date dob = dateTime.toDate();
                    List<Vaccine> vaccines = (List<Vaccine>) v.getTag(R.id.vaccinelist);
                    HomeVisitVaccineGroupDetails duevaccines = (HomeVisitVaccineGroupDetails) v.getTag(R.id.nextduevaccinelist);
                    CustomMultipleVaccinationDialogFragment customVaccinationDialogFragment = CustomMultipleVaccinationDialogFragment.newInstance(dob, vaccines, presenter.createVaccineWrappers(duevaccines));
                    customVaccinationDialogFragment.setContext(context);
                    customVaccinationDialogFragment.setChildDetails(presenter.getchildClient());
                    customVaccinationDialogFragment.setView(this);
                    customVaccinationDialogFragment.show(ft, ChildImmunizationFragment.TAG);
                }
                break;
            case R.id.immunization_name_group:
                if (!TextUtils.isEmpty(dobString)) {
                    DateTime dateTime = new DateTime(dobString);
                    Date dob = dateTime.toDate();
                    ArrayList<VaccineWrapper> vaccineWrappers = new ArrayList<VaccineWrapper>();
                    ArrayList<VaccineRepo.Vaccine> vaccinesList = (ArrayList<VaccineRepo.Vaccine>) v.getTag(R.id.nextduevaccinelist);
                    for (VaccineRepo.Vaccine vaccine : vaccinesList) {
                        VaccineWrapper vaccineWrapper = new VaccineWrapper();
                        vaccineWrapper.setVaccine(vaccine);
                        vaccineWrapper.setName(vaccine.display());
                        vaccineWrapper.setDefaultName(vaccine.display());
                        vaccineWrappers.add(vaccineWrapper);
                    }
                    List<Vaccine> vaccines = (List<Vaccine>) v.getTag(R.id.vaccinelist);
                    if (vaccineWrappers.size() == 1) {
                        CustomVaccinationDialogFragment customVaccinationDialogFragment = CustomVaccinationDialogFragment.newInstance(dob, vaccines, vaccineWrappers);
                        customVaccinationDialogFragment.setContext(context);
                        customVaccinationDialogFragment.setChildDetails(presenter.getchildClient());
                        customVaccinationDialogFragment.setView(this);
                        customVaccinationDialogFragment.setDisableConstraints(true);
                        customVaccinationDialogFragment.show(context.getFragmentManager(), ChildImmunizationFragment.TAG);
                    } else if (vaccineWrappers.size() > 1) {
                        CustomMultipleVaccinationDialogFragment customVaccinationDialogFragment = CustomMultipleVaccinationDialogFragment.newInstance(dob, vaccines, vaccineWrappers);
                        customVaccinationDialogFragment.setContext(context);
                        customVaccinationDialogFragment.setChildDetails(presenter.getchildClient());
                        customVaccinationDialogFragment.setView(this);
                        customVaccinationDialogFragment.show(ft, ChildImmunizationFragment.TAG);
                    }
                }

                break;
        }
    }

    @Override
    public void undoVaccines() {
        presenter.undoGivenVaccines();
    }

    @Override
    public HomeVisitImmunizationContract.Presenter initializePresenter() {
        presenter = new HomeVisitImmunizationPresenter(this);
        return presenter;
    }

    @Override
    public HomeVisitImmunizationContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void updateImmunizationState() {
        presenter.updateImmunizationState(this);
    }

    @Override
    public void immunizationState(List<Alert> alerts, List<Vaccine> vaccines, List<Map<String, Object>> sch) {
        refreshPresenter(alerts, vaccines, sch);
        if ((presenter.isComplete() || presenter.isPartiallyComplete()) && (presenter.isSingleVaccineGroupPartialComplete() || presenter.isSingleVaccineGroupComplete())) {
            ((ChildHomeVisitFragment) (((Activity) context).getFragmentManager().findFragmentByTag(ChildHomeVisitFragment.DIALOG_TAG))).allVaccineStateFullfilled = true;
        } else {
            ((ChildHomeVisitFragment) (((Activity) context).getFragmentManager().findFragmentByTag(ChildHomeVisitFragment.DIALOG_TAG))).allVaccineStateFullfilled = false;
        }
        ((ChildHomeVisitFragment) (((Activity) context).getFragmentManager().findFragmentByTag(ChildHomeVisitFragment.DIALOG_TAG))).checkIfSubmitIsToBeEnabled();
    }

    public JSONArray getSingleVaccinesGivenThisVisit() {
        ArrayList<VaccineWrapper> singleVaccinesGivenThisVisit = new ArrayList<VaccineWrapper>();
        Stack<VaccineRepo.Vaccine> vaccinesStack = new Stack<VaccineRepo.Vaccine>();
        for (VaccineRepo.Vaccine vaccinedueLastVisit : presenter.getVaccinesDueFromLastVisit()) {
            vaccinesStack.add(vaccinedueLastVisit);
            for (VaccineWrapper givenThisVisit : presenter.getVaccinesGivenThisVisit()) {
                if (!vaccinesStack.isEmpty()) {
                    if (givenThisVisit.getDefaultName().equalsIgnoreCase(vaccinesStack.peek().display())) {
                        vaccinesStack.pop();
                        singleVaccinesGivenThisVisit.add(givenThisVisit);
                    }
                }
            }
        }
        return getVaccineWrapperListAsJson(singleVaccinesGivenThisVisit);
    }

    public JSONArray getGroupVaccinesGivenThisVisit() {
        ArrayList<VaccineWrapper> groupVaccinesGivenThisVisit = new ArrayList<VaccineWrapper>();
        for (VaccineWrapper givenThisVisit : presenter.getVaccinesGivenThisVisit()) {
            boolean isInSingleVaccineList = false;
            for (VaccineRepo.Vaccine vaccineDueFromLastVisit : presenter.getVaccinesDueFromLastVisit()) {
                if (vaccineDueFromLastVisit.display().equalsIgnoreCase(givenThisVisit.getName())) {
                    isInSingleVaccineList = true;
                }
            }
            if (!isInSingleVaccineList) {
                groupVaccinesGivenThisVisit.add(givenThisVisit);
            }
        }
        JSONArray jsonObject = getVaccineWrapperListAsJson(groupVaccinesGivenThisVisit);
        return jsonObject;
    }

    private JSONArray getVaccineWrapperListAsJson(ArrayList<VaccineWrapper> groupVaccinesGivenThisVisit) {
        JSONArray arrayOfWrapper = new JSONArray();
        for (VaccineWrapper wrapper : groupVaccinesGivenThisVisit) {
            try {
                JSONObject wrapperObject = new JSONObject((new Gson()).toJson(wrapper));
                JSONObject vaccineObject = new JSONObject((new Gson()).toJson(wrapper.getVaccine()));
                wrapperObject.put("vaccine", vaccineObject);
                arrayOfWrapper.put(wrapperObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayOfWrapper;
    }

    public void setEditMode(boolean isEditMode) {
        this.isInEditMode = isEditMode;
    }
}
