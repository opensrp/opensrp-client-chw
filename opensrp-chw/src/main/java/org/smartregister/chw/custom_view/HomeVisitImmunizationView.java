//package org.smartregister.chw.custom_view;
//
//import android.app.Activity;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
//import android.content.Context;
//import android.graphics.Typeface;
//import android.text.Html;
//import android.text.TextUtils;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.google.gson.Gson;
//
//import org.apache.commons.lang3.StringUtils;
//import org.joda.time.DateTime;
//import org.joda.time.Months;
//import org.joda.time.Weeks;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.smartregister.chw.R;
//import org.smartregister.chw.contract.HomeVisitImmunizationContract;
//import org.smartregister.chw.fragment.ChildHomeVisitFragment;
//import org.smartregister.chw.fragment.ChildImmunizationFragment;
//import org.smartregister.chw.fragment.VaccinationDialogFragment;
//import org.smartregister.chw.util.ChildUtils;
//import org.smartregister.chw.presenter.HomeVisitImmunizationPresenter;
//import org.smartregister.chw.util.HomeVisitVaccineGroup;
//import org.smartregister.chw.util.ImmunizationState;
//import org.smartregister.commonregistry.CommonPersonObjectClient;
//import org.smartregister.domain.Alert;
//import org.smartregister.family.util.DBConstants;
//import org.smartregister.immunization.db.VaccineRepo;
//import org.smartregister.immunization.domain.Vaccine;
//import org.smartregister.immunization.domain.VaccineWrapper;
//import org.smartregister.util.DateUtil;
//
//import java.text.MessageFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Stack;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//import io.reactivex.Observable;
//
//import static org.apache.commons.lang3.text.WordUtils.capitalize;
//import static org.smartregister.chw.util.ChildUtils.fixVaccineCasing;
//import static org.smartregister.chw.util.Constants.IMMUNIZATION_CONSTANT.DATE;
//
//public class HomeVisitImmunizationView extends LinearLayout implements View.OnClickListener, HomeVisitImmunizationContract.View {
//    public static final String TAG = "HomeVisitImmunization";
//    private HomeVisitImmunizationContract.Presenter presenter;
//    private TextView textview_group_immunization_primary_text;
//    private TextView textview_group_immunization_secondary_text;
//    private TextView textview_immunization_primary_text;
//    private TextView textview_immunization_secondary_text;
//    private CircleImageView immunization_status_circle;
//    private CircleImageView immunization_group_status_circle;
//    private LinearLayout multiple_immunization_group;
//    private LinearLayout single_immunization_group;
//    private View lineImmunization, lineImmunizationUndue;
//    private Activity context;
//    private boolean isInEditMode = false;
//    private LinearLayout immunization_undue_groups_holder;
//    private ArrayList<String> elligibleVaccineGroups = new ArrayList<String>();
//    private LinearLayout immunization_done_before_active_groups__holder;
//
//
//    public HomeVisitImmunizationView(Context context) {
//        super(context);
//        initUi();
//    }
//
//    public HomeVisitImmunizationView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        initUi();
//
//    }
//
//    public HomeVisitImmunizationView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        initUi();
//
//    }
//
//    @Override
//    public void setActivity(Activity activity) {
//        this.context = activity;
//    }
//    private void initUi() {
//        inflate(getContext(), R.layout.view_immunization, this);
//        textview_group_immunization_primary_text = (TextView) findViewById(R.id.textview_group_immunization);
//        textview_group_immunization_secondary_text = (TextView) findViewById(R.id.textview_immunization_group_secondary_text);
//        textview_immunization_primary_text = (TextView) findViewById(R.id.textview_immunization);
//        textview_immunization_secondary_text = (TextView) findViewById(R.id.textview_immunization_secondary_text);
//        immunization_status_circle = ((CircleImageView) findViewById(R.id.immunization_status_circle));
//        immunization_group_status_circle = ((CircleImageView) findViewById(R.id.immunization_group_status_circle));
//        single_immunization_group = ((LinearLayout) findViewById(R.id.immunization_name_group));
//        lineImmunization = findViewById(R.id.line_group_immunization);
//        lineImmunizationUndue = findViewById(R.id.line_immunization_undue_groups);
//        multiple_immunization_group = ((LinearLayout) findViewById(R.id.immunization_group));
//        immunization_undue_groups_holder = ((LinearLayout) findViewById(R.id.immunization_undue_groups_holder));
//        immunization_done_before_active_groups__holder = ((LinearLayout) findViewById(R.id.immunization_done_before_active_groups_holder));
//
//        initializePresenter();
//
//    }
//
//    @Override
//    public void setChildClient(CommonPersonObjectClient childClient) {
//        presenter.setChildClient(childClient);
//    }
//
//    @Override
//    public void refreshPresenter(List<Alert> alerts, List<Vaccine> vaccines, List<Map<String, Object>> sch) {
//        presenter.createAllVaccineGroups(alerts, vaccines, sch);
//        presenter.getVaccinesNotGivenLastVisit();
//        presenter.calculateCurrentActiveGroup();
//        presenter.setGroupVaccineText(sch);
//        presenter.setSingleVaccineText(presenter.getVaccinesDueFromLastVisit(), sch);
//
//        if (presenter.isPartiallyComplete() || presenter.isComplete()) {
//            String value = presenter.getCurrentActiveGroup().getGroup();
//            String immunizations;
//            if (value.contains("birth")) {
//                immunizations = MessageFormat.format(getContext().getString(R.string.immunizations_count), value);
//
//            } else {
//                immunizations = MessageFormat.format(getContext().getString(R.string.immunizations_count), value.replace("weeks", "w").replace("months", "m").replace(" ", ""));
//
//            }
//            textview_group_immunization_primary_text.setText(immunizations);
//            textview_group_immunization_secondary_text.setVisibility(VISIBLE);
//            textview_group_immunization_secondary_text.setTextColor(getResources().getColor(android.R.color.darker_gray));
//            textview_group_immunization_secondary_text.setText(presenter.getGroupImmunizationSecondaryText());
//            immunization_group_status_circle.setImageResource(R.drawable.ic_checked);
//            immunization_group_status_circle.setColorFilter(getResources().getColor(R.color.white));
//
//            int color_res = (presenter.isPartiallyComplete()) ? R.color.pnc_circle_yellow : R.color.alert_complete_green;
//
//            immunization_group_status_circle.setCircleBackgroundColor(getResources().getColor(color_res));
//            immunization_group_status_circle.setBorderColor(getResources().getColor(color_res));
//            multiple_immunization_group.setTag(R.id.nextduevaccinelist, presenter.getCurrentActiveGroup());
//            multiple_immunization_group.setTag(R.id.vaccinelist, vaccines);
//            multiple_immunization_group.setOnClickListener(this);
//
//        } else if (presenter.groupIsDue()) {
//            String value = presenter.getCurrentActiveGroup().getGroup();
//            String immunizations;
//            if (value.contains("birth")) {
//                immunizations = MessageFormat.format(getContext().getString(R.string.immunizations_count), value);
//
//            } else {
//                immunizations = MessageFormat.format(getContext().getString(R.string.immunizations_count), value.replace("weeks", "w").replace("months", "m").replace(" ", ""));
//
//            }
//            textview_group_immunization_primary_text.setText(immunizations);
//
//            String message = MessageFormat.format("{0} {1}",
//                    ((presenter.getCurrentActiveGroup().getAlert().equals(ImmunizationState.OVERDUE)) ? context.getResources().getString(R.string.overdue) : context.getResources().getString(R.string.due)),
//                    presenter.getCurrentActiveGroup().getDueDisplayDate()
//            );
//            int color_res = ((presenter.getCurrentActiveGroup().getAlert().equals(ImmunizationState.OVERDUE)) ? R.color.alert_urgent_red : android.R.color.darker_gray);
//            textview_group_immunization_secondary_text.setVisibility(VISIBLE);
//            textview_group_immunization_secondary_text.setText(message);
//            textview_group_immunization_secondary_text.setTextColor(getResources().getColor(color_res));
//
//            multiple_immunization_group.setTag(R.id.nextduevaccinelist, presenter.getCurrentActiveGroup());
//            multiple_immunization_group.setTag(R.id.vaccinelist, vaccines);
//            multiple_immunization_group.setOnClickListener(this);
//        }
//
//        if (presenter.getVaccinesDueFromLastVisit().size() > 0) {
//            StringBuilder vaccinesDueLastVisitBuilder = new StringBuilder();
//            for (int i = 0; i < presenter.getVaccinesDueFromLastVisit().size(); i++) {
//                vaccinesDueLastVisitBuilder.append(
//                        fixVaccineCasing(presenter.getVaccinesDueFromLastVisit().get(i).display())
//                ).append(",");
//            }
//            String vaccinesDueLastVisit = vaccinesDueLastVisitBuilder.toString();
//            if (vaccinesDueLastVisit.endsWith(",")) {
//                vaccinesDueLastVisit = vaccinesDueLastVisit.substring(0, vaccinesDueLastVisit.length() - 1);
//            }
//
//            textview_immunization_primary_text.setText(vaccinesDueLastVisit);
//            single_immunization_group.setTag(R.id.nextduevaccinelist, presenter.getVaccinesDueFromLastVisitStillDueState());
//            single_immunization_group.setOnClickListener(this);
//
//            if (presenter.getVaccinesDueFromLastVisitStillDueState().size() == 0) {
//                if (presenter.isSingleVaccineGroupPartialComplete() || presenter.isSingleVaccineGroupComplete()) {
//                    textview_immunization_secondary_text.setVisibility(VISIBLE);
//                    textview_immunization_secondary_text.setText(presenter.getSingleImmunizationSecondaryText());
//                    textview_immunization_secondary_text.setTextColor(getResources().getColor(android.R.color.darker_gray));
//                    immunization_status_circle.setImageResource(R.drawable.ic_checked);
//                    immunization_status_circle.setColorFilter(getResources().getColor(R.color.white));
//
//                    int color_res = (presenter.isSingleVaccineGroupPartialComplete()) ? R.color.pnc_circle_yellow : R.color.alert_complete_green;
//                    immunization_status_circle.setCircleBackgroundColor(getResources().getColor(color_res));
//                    immunization_status_circle.setBorderColor(getResources().getColor(color_res));
//                }
//            } else if (presenter.getVaccinesDueFromLastVisitStillDueState().size() > 0) {
//                String SingleImmunizationSecondaryText = getSingleImmunizationSecondaryText(presenter.getVaccinesDueFromLastVisitStillDueState(), sch, alerts);
//                textview_immunization_secondary_text.setVisibility(VISIBLE);
//                textview_immunization_secondary_text.setText(SingleImmunizationSecondaryText);
//                if (SingleImmunizationSecondaryText.toLowerCase().contains(context.getResources().getString(R.string.due).toLowerCase())) {
//                    textview_immunization_secondary_text.setTextColor(getResources().getColor(android.R.color.darker_gray));
//                }
//                if (SingleImmunizationSecondaryText.toLowerCase().contains(context.getResources().getString(R.string.overdue).toLowerCase())) {
//                    textview_immunization_secondary_text.setTextColor(getResources().getColor(R.color.alert_urgent_red));
//                }
//            }
//        } else {
//            single_immunization_group.setVisibility(View.GONE);
//            lineImmunization.setVisibility(GONE);
//
//        }
//        inflateGroupsDone(sch);
//        inflateGroupsNotEnabled();
//
//    }
//
//    private String getSingleImmunizationSecondaryText(ArrayList<VaccineRepo.Vaccine> vaccinesDueFromLastVisitStillDueState, List<Map<String, Object>> sch, List<Alert> alerts) {
//        String toReturn = "";
//        ImmunizationState currentState = ImmunizationState.NO_ALERT;
//        for (VaccineRepo.Vaccine vaccine : vaccinesDueFromLastVisitStillDueState) {
//            ImmunizationState state = (ChildUtils.assignAlert(vaccine, alerts));
//            if ((currentState.equals(ImmunizationState.DUE) && state.equals(ImmunizationState.OVERDUE)) ||
//                    (currentState.equals(ImmunizationState.NO_ALERT) && state.equals(ImmunizationState.OVERDUE)) ||
//                    (currentState.equals(ImmunizationState.NO_ALERT) && state.equals(ImmunizationState.DUE))) {
//                currentState = state;
//                for (Map<String, Object> toprocess : sch) {
//                    if (((VaccineRepo.Vaccine) (toprocess.get("vaccine"))).name().equalsIgnoreCase(vaccine.name())) {
//                        DateTime dueDate = (DateTime) toprocess.get(DATE);
//                        String duedateString = DateUtil.formatDate(dueDate.toLocalDate(), "dd MMM yyyy");
//
//                        String status = state.toString();
//                        if(currentState.equals(ImmunizationState.DUE)){
//                            status = context.getResources().getString(R.string.due);
//                        }else if(currentState.equals(ImmunizationState.OVERDUE)){
//                            status = context.getResources().getString(R.string.overdue);
//                        }
//
//                        toReturn = status + " " + capitalize(duedateString.toLowerCase());
//                    }
//                }
//            }
//        }
//        return toReturn;
//    }
//
//    private void inflateGroupsDone(List<Map<String, Object>> sch) {
//        immunization_done_before_active_groups__holder.removeAllViews();
//        ArrayList<HomeVisitVaccineGroup> groupsDoneBeforeCurrentActive = findGroupsDoneBeforeActive();
//        for (int i = 0; i < groupsDoneBeforeCurrentActive.size(); i++) {
//            LinearLayout vaccineGroupNotDue = (LinearLayout) ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.multiple_vaccine_layout, null);
////            View lineView=vaccineGroupNotDue.findViewById(R.id.line_view);
////            if(i==(groupsDoneBeforeCurrentActive.size()-1)){
////                lineView.setVisibility(GONE);
////            }
//            String immunizations;
//            String value = groupsDoneBeforeCurrentActive.get(i).getGroup();
//            if (value.contains("birth")) {
//                immunizations = MessageFormat.format(getContext().getString(R.string.immunizations_count), value);
//
//            } else {
//                immunizations = MessageFormat.format(getContext().getString(R.string.immunizations_count), value.replace("weeks", "w").replace("months", "m").replace(" ", ""));
//
//            }
//            TextView groupImmunizationTitle = ((TextView) vaccineGroupNotDue.findViewById(R.id.textview_group_immunization));
//            groupImmunizationTitle.setText(immunizations);
//            TextView secondaryText = ((TextView) vaccineGroupNotDue.findViewById(R.id.textview_immunization_group_secondary_text));
//            secondaryText.setText(getGivenBeforeActiveGroupVaccineText(sch, new HomeVisitVaccineGroup[]{groupsDoneBeforeCurrentActive.get(i)}));
//
//            groupImmunizationTitle.setTextColor(getContext().getResources().getColor(R.color.black));
//            secondaryText.setTextColor(getContext().getResources().getColor(android.R.color.darker_gray));
//            secondaryText.setTypeface(secondaryText.getTypeface(), Typeface.NORMAL);
//
//            CircleImageView immunization_group_done_status_circle = (CircleImageView) vaccineGroupNotDue.findViewById(R.id.immunization_group_status_circle);
//            if (inflatedGroupsDoneIsComplete(groupsDoneBeforeCurrentActive.get(i))) {
//                immunization_group_done_status_circle.setImageResource(R.drawable.ic_checked);
//                immunization_group_done_status_circle.setColorFilter(getResources().getColor(R.color.white));
//                int color_res = R.color.alert_complete_green;
//                immunization_group_done_status_circle.setCircleBackgroundColor(getResources().getColor(color_res));
//                immunization_group_done_status_circle.setBorderColor(getResources().getColor(color_res));
//            } else {
//                immunization_group_done_status_circle.setImageResource(R.drawable.ic_checked);
//                immunization_group_done_status_circle.setColorFilter(getResources().getColor(R.color.white));
//                int color_res = R.color.pnc_circle_yellow;
//                immunization_group_done_status_circle.setCircleBackgroundColor(getResources().getColor(color_res));
//                immunization_group_done_status_circle.setBorderColor(getResources().getColor(color_res));
//            }
//            immunization_done_before_active_groups__holder.addView(vaccineGroupNotDue);
//        }
//    }
//
//    private boolean inflatedGroupsDoneIsComplete(HomeVisitVaccineGroup homeVisitVaccineGroup) {
//        return (homeVisitVaccineGroup.getNotGivenVaccines().size() <= 0);
//    }
//
//    public void inflateGroupsNotEnabled() {
//        immunization_undue_groups_holder.removeAllViews();
//        ArrayList<HomeVisitVaccineGroup> inActiveDueGroups = findDueInactiveGroups();
//        if (inActiveDueGroups.size() == 0) {
//            lineImmunizationUndue.setVisibility(GONE);
//        }
//        for (int i = 0; i < inActiveDueGroups.size(); i++) {
//            LinearLayout vaccineGroupNotDue = (LinearLayout) ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.multiple_vaccine_layout, null);
//            View lineView = vaccineGroupNotDue.findViewById(R.id.line_view);
//            if (i == (inActiveDueGroups.size() - 1)) {
//                lineView.setVisibility(GONE);
//            }
//            String immunizations;
//            String value = inActiveDueGroups.get(i).getGroup();
//            if (value.contains("birth")) {
//                immunizations = MessageFormat.format(getContext().getString(R.string.immunizations_count), value);
//
//            } else {
//                immunizations = MessageFormat.format(getContext().getString(R.string.immunizations_count), value.replace("weeks", "w").replace("months", "m").replace(" ", ""));
//
//            }
//            ((TextView) vaccineGroupNotDue.findViewById(R.id.textview_group_immunization)).setText(immunizations);
//            String text = getContext().getString(R.string.fill_earler_immunization);
//            ((TextView) vaccineGroupNotDue.findViewById(R.id.textview_immunization_group_secondary_text)).setText(Html.fromHtml(text));
//            immunization_undue_groups_holder.addView(vaccineGroupNotDue);
//        }
//
//    }
//
//    private ArrayList<HomeVisitVaccineGroup> findDueInactiveGroups() {
//        setAgeVaccineListElligibleGroups();
//        ArrayList<HomeVisitVaccineGroup> inActiveDueGroups = new ArrayList<HomeVisitVaccineGroup>();
//        ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupDetails = presenter.getAllgroups();
//        HomeVisitVaccineGroup currentActiveGroup = presenter.getCurrentActiveGroup();
//        int indexofCurrentActiveGroup = 0;
//        for (int i = 0; i < homeVisitVaccineGroupDetails.size(); i++) {
//            if (homeVisitVaccineGroupDetails.get(i).getGroup().equalsIgnoreCase(currentActiveGroup.getGroup())) {
//                indexofCurrentActiveGroup = i;
//            }
//        }
//        for (int i = indexofCurrentActiveGroup + 1; i < homeVisitVaccineGroupDetails.size(); i++) {
////            if(homeVisitVaccineGroupDetails.get(i).getAlert().equals(ImmunizationState.DUE)||homeVisitVaccineGroupDetails.get(i).getAlert().equals(ImmunizationState.OVERDUE)){
//            if (inElligibleVaccineMap(homeVisitVaccineGroupDetails.get(i))) {
//                inActiveDueGroups.add(homeVisitVaccineGroupDetails.get(i));
//            }
////            }
//        }
//        return inActiveDueGroups;
//    }
//
//    private ArrayList<HomeVisitVaccineGroup> findGroupsDoneBeforeActive() {
//        ArrayList<HomeVisitVaccineGroup> groupsDoneBeforeActive = new ArrayList<HomeVisitVaccineGroup>();
//        ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupDetails = presenter.getAllgroups();
//        HomeVisitVaccineGroup currentActiveGroup = presenter.getCurrentActiveGroup();
//        int indexofCurrentActiveGroup = 0;
//        for (int i = 0; i < homeVisitVaccineGroupDetails.size(); i++) {
//            if (homeVisitVaccineGroupDetails.get(i).getGroup().equalsIgnoreCase(currentActiveGroup.getGroup())) {
//                indexofCurrentActiveGroup = i;
//            }
//        }
//        for (int i = 0; i < indexofCurrentActiveGroup; i++) {
////            if(homeVisitVaccineGroupDetails.get(i).getAlert().equals(ImmunizationState.DUE)||homeVisitVaccineGroupDetails.get(i).getAlert().equals(ImmunizationState.OVERDUE)){
//            if (isGroupDoneThisVisit(homeVisitVaccineGroupDetails.get(i))) {
//                groupsDoneBeforeActive.add(homeVisitVaccineGroupDetails.get(i));
//            }
//
////            }
//        }
//        return groupsDoneBeforeActive;
//    }
//
//    public boolean isGroupDoneThisVisit(HomeVisitVaccineGroup homeVisitVaccineGroup) {
//        boolean toReturn = false;
//        for (VaccineRepo.Vaccine vaccine : homeVisitVaccineGroup.getDueVaccines()) {
//            for (VaccineWrapper vaccineGivenThisVisit : presenter.getVaccinesGivenThisVisit()) {
//                if (vaccineGivenThisVisit.getVaccine().display().equalsIgnoreCase(vaccine.display())) {
//                    toReturn = true;
//                }
//            }
//        }
//        for (VaccineRepo.Vaccine vaccine : homeVisitVaccineGroup.getDueVaccines()) {
//            for (VaccineWrapper vaccineGivenThisVisit : presenter.getNotGivenVaccines()) {
//                if (vaccineGivenThisVisit.getVaccine().display().equalsIgnoreCase(vaccine.display())) {
//                    toReturn = true;
//                }
//            }
//        }
//        for (VaccineRepo.Vaccine vaccine : homeVisitVaccineGroup.getDueVaccines()) {
//            for (VaccineRepo.Vaccine vaccineDueFromLastVisit : presenter.getVaccinesDueFromLastVisit()) {
//                if (vaccineDueFromLastVisit.display().equalsIgnoreCase(vaccine.display())) {
//                    toReturn = false;
//                }
//            }
//        }
//        return toReturn;
//    }
//
//    private boolean inElligibleVaccineMap(HomeVisitVaccineGroup homeVisitVaccineGroup) {
//        for (String string : elligibleVaccineGroups) {
//            if (string.equalsIgnoreCase(homeVisitVaccineGroup.getGroup())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        FragmentTransaction ft = context.getFragmentManager().beginTransaction();
//        String dobString = org.smartregister.util.Utils.getValue(presenter.getchildClient().getColumnmaps(), "dob", false);
//
//        switch (v.getId()) {
//
//            case R.id.immunization_group:
//                if (!TextUtils.isEmpty(dobString)) {
//                    DateTime dateTime = new DateTime(dobString);
//                    Date dob = dateTime.toDate();
//                    List<Vaccine> vaccines = (List<Vaccine>) v.getTag(R.id.vaccinelist);
//                    HomeVisitVaccineGroup duevaccines = (HomeVisitVaccineGroup) v.getTag(R.id.nextduevaccinelist);
//                    VaccinationDialogFragment customVaccinationDialogFragment = VaccinationDialogFragment.newInstance(dob,presenter.getNotGivenVaccines(),presenter.createGivenVaccineWrappers(duevaccines), presenter.createVaccineWrappers(duevaccines));
//                    customVaccinationDialogFragment.setChildDetails(presenter.getchildClient());
//                    customVaccinationDialogFragment.setView(this);
//                    customVaccinationDialogFragment.show(ft, ChildImmunizationFragment.TAG);
//                }
//                break;
//            case R.id.immunization_name_group:
//                if (!TextUtils.isEmpty(dobString)) {
//                    DateTime dateTime = new DateTime(dobString);
//                    Date dob = dateTime.toDate();
//                    ArrayList<VaccineWrapper> vaccineWrappers = new ArrayList<VaccineWrapper>();
//                    ArrayList<VaccineRepo.Vaccine> vaccinesList = (ArrayList<VaccineRepo.Vaccine>) v.getTag(R.id.nextduevaccinelist);
//                    for (VaccineRepo.Vaccine vaccine : vaccinesList) {
//                        VaccineWrapper vaccineWrapper = new VaccineWrapper();
//                        vaccineWrapper.setVaccine(vaccine);
//                        vaccineWrapper.setName(vaccine.display());
//                        vaccineWrapper.setDefaultName(vaccine.display());
//                        vaccineWrappers.add(vaccineWrapper);
//                    }
//                    List<Vaccine> vaccines = (List<Vaccine>) v.getTag(R.id.vaccinelist);
//                    if (vaccineWrappers.size() >= 1) {
//                        VaccinationDialogFragment customVaccinationDialogFragment = VaccinationDialogFragment.newInstance(dob, new ArrayList<VaccineWrapper>(),new ArrayList<VaccineWrapper>(), vaccineWrappers);
//                        customVaccinationDialogFragment.setChildDetails(presenter.getchildClient());
//                        customVaccinationDialogFragment.setView(this);
//                        customVaccinationDialogFragment.show(ft, ChildImmunizationFragment.TAG);
//                    }
//                }
//
//                break;
//            default:
//                break;
//        }
//    }
//
//    public Observable undoVaccine() {
//        return ((HomeVisitImmunizationPresenter) presenter).undoVaccine();
//    }
//
//    @Override
//    public HomeVisitImmunizationContract.Presenter initializePresenter() {
//        presenter = new HomeVisitImmunizationPresenter(this);
//        return presenter;
//    }
//
//    @Override
//    public HomeVisitImmunizationContract.Presenter getPresenter() {
//        return presenter;
//    }
//
//    @Override
//    public void updateImmunizationState() {
//        presenter.updateImmunizationState(this);
//    }
//
//    @Override
//    public void immunizationState(List<Alert> alerts, List<Vaccine> vaccines,Map<String, Date> receivedVaccine, List<Map<String, Object>> sch) {
//        refreshPresenter(alerts, vaccines, sch);
//        ChildHomeVisitFragment childHomeVisitFragment = (ChildHomeVisitFragment) context.getFragmentManager().findFragmentByTag(ChildHomeVisitFragment.DIALOG_TAG);
//        if (childHomeVisitFragment == null) {
//            return;
//        }
//        if ((presenter.isComplete() || presenter.isPartiallyComplete()) && (presenter.isSingleVaccineGroupPartialComplete() || presenter.isSingleVaccineGroupComplete())) {
//            childHomeVisitFragment.allVaccineStateFullfilled = true;
//        } else {
//            childHomeVisitFragment.allVaccineStateFullfilled = false;
//        }
//        childHomeVisitFragment.allVaccineDataLoaded = true;
//        childHomeVisitFragment.progressBarInvisible();
//        childHomeVisitFragment.checkIfSubmitIsToBeEnabled();
//    }
//
//    public JSONArray getSingleVaccinesGivenThisVisit() {
//        ArrayList<VaccineWrapper> singleVaccinesGivenThisVisit = new ArrayList<>();
//        Stack<VaccineRepo.Vaccine> vaccinesStack = new Stack<>();
//        for (VaccineRepo.Vaccine vaccinedueLastVisit : presenter.getVaccinesDueFromLastVisit()) {
//            vaccinesStack.add(vaccinedueLastVisit);
//            for (VaccineWrapper givenThisVisit : presenter.getVaccinesGivenThisVisit()) {
//                if (!vaccinesStack.isEmpty() && givenThisVisit.getDefaultName().equalsIgnoreCase(vaccinesStack.peek().display())) {
//                    vaccinesStack.pop();
//                    singleVaccinesGivenThisVisit.add(givenThisVisit);
//                }
//            }
//        }
//        return getVaccineWrapperListAsJson(singleVaccinesGivenThisVisit);
//    }
//
//    public JSONArray getGroupVaccinesGivenThisVisit() {
//        ArrayList<VaccineWrapper> groupVaccinesGivenThisVisit = new ArrayList<>();
//        for (VaccineWrapper givenThisVisit : presenter.getVaccinesGivenThisVisit()) {
//            boolean isInSingleVaccineList = false;
//            for (VaccineRepo.Vaccine vaccineDueFromLastVisit : presenter.getVaccinesDueFromLastVisit()) {
//                if (vaccineDueFromLastVisit.display().equalsIgnoreCase(givenThisVisit.getName())) {
//                    isInSingleVaccineList = true;
//                }
//            }
//            if (!isInSingleVaccineList) {
//                groupVaccinesGivenThisVisit.add(givenThisVisit);
//            }
//        }
//        JSONArray jsonObject = getVaccineWrapperListAsJson(groupVaccinesGivenThisVisit);
//        return jsonObject;
//    }
//    public ArrayList<VaccineWrapper> getNotGivenVaccine(){
//        return presenter.getNotGivenVaccines();
//    }
//
//    private JSONArray getVaccineWrapperListAsJson(ArrayList<VaccineWrapper> groupVaccinesGivenThisVisit) {
//        JSONArray arrayOfWrapper = new JSONArray();
//        for (VaccineWrapper wrapper : groupVaccinesGivenThisVisit) {
//            try {
//                JSONObject wrapperObject = new JSONObject(ChildUtils.gsonConverter.toJson(wrapper));
//                JSONObject vaccineObject = new JSONObject(ChildUtils.gsonConverter.toJson(wrapper.getVaccine()));
//                wrapperObject.put("vaccine", vaccineObject);
//                arrayOfWrapper.put(wrapperObject);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return arrayOfWrapper;
//    }
//
//    public void setEditMode(boolean isEditMode) {
//        this.isInEditMode = isEditMode;
//    }
//
//    public void setAgeVaccineListElligibleGroups() {
//        String dobString = org.smartregister.util.Utils.getValue(presenter.getchildClient().getColumnmaps(), DBConstants.KEY.DOB, false);
//        if (!TextUtils.isEmpty(dobString)) {
//            DateTime dateTime = new DateTime(dobString);
//            DateTime now = new DateTime();
//            int weeks = Weeks.weeksBetween(dateTime, now).getWeeks();
//            int months = Months.monthsBetween(dateTime, now).getMonths();
//            if (weeks >= 6) {
//                elligibleVaccineGroups.add("6 weeks");
//            }
//            if (weeks >= 10) {
//                elligibleVaccineGroups.add("10 weeks");
//            }
//            if (weeks >= 6) {
//                elligibleVaccineGroups.add("14 weeks");
//            }
//            if (months >= 9) {
//                elligibleVaccineGroups.add("9 months");
//            }
//            if (months >= 15) {
//                elligibleVaccineGroups.add("15 months");
//            }
//        }
//    }
//
//
//    public String getGivenBeforeActiveGroupVaccineText(List<Map<String, Object>> sch, HomeVisitVaccineGroup[] allgroups) {
//        ArrayList<VaccineRepo.Vaccine> allgivenVaccines = new ArrayList<VaccineRepo.Vaccine>();
//        for (HomeVisitVaccineGroup group : allgroups) {
//            allgivenVaccines.addAll(group.getGivenVaccines());
//        }
//
//        LinkedHashMap<DateTime, ArrayList<VaccineRepo.Vaccine>> groupedByDate = groupVaccines(allgivenVaccines, sch);
//
//        String notGiven = addNotGivenVaccines(sch, allgroups).trim();
//        StringBuilder groupSecondaryText = new StringBuilder();
//        Iterator<Map.Entry<DateTime, ArrayList<VaccineRepo.Vaccine>>> iterator = groupedByDate.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<DateTime, ArrayList<VaccineRepo.Vaccine>> entry = iterator.next();
//            DateTime dueDate = entry.getKey();
//            ArrayList<VaccineRepo.Vaccine> vaccines = entry.getValue();
//            // now work with key and value...
//            for (VaccineRepo.Vaccine vaccineGiven : vaccines) {
//                groupSecondaryText.append(fixVaccineCasing(vaccineGiven.display())).append(", ");
//            }
//
//            if (groupSecondaryText.toString().endsWith(", ")) {
//                groupSecondaryText = new StringBuilder(groupSecondaryText.toString().trim());
//                groupSecondaryText = new StringBuilder(groupSecondaryText.substring(0, groupSecondaryText.length() - 1));
//            }
//
//            groupSecondaryText.append(getContext().getString(R.string.given_on_with_spaces)).append(DateUtil.formatDate(dueDate.toLocalDate(), "dd MMM yyyy"));
//
//            if (StringUtils.isNotBlank(notGiven) || iterator.hasNext()) {
//                groupSecondaryText.append(" \u00B7 ");
//            }
//        }
//
//        groupSecondaryText.append(notGiven);
//        return groupSecondaryText.toString();
//    }
//
//    /**
//     * Groups vaccines by date
//     *
//     * @param givenVaccines
//     * @param sch
//     * @return
//     */
//    private LinkedHashMap<DateTime, ArrayList<VaccineRepo.Vaccine>> groupVaccines(ArrayList<VaccineRepo.Vaccine> givenVaccines, List<Map<String, Object>> sch) {
//        LinkedHashMap<DateTime, ArrayList<VaccineRepo.Vaccine>> groupedByDate = new LinkedHashMap<DateTime, ArrayList<VaccineRepo.Vaccine>>();
//
//        for (VaccineRepo.Vaccine vaccineGiven : givenVaccines) {
//            for (Map<String, Object> mapToProcess : sch) {
//                if (((VaccineRepo.Vaccine) mapToProcess.get("vaccine")).display().equalsIgnoreCase(vaccineGiven.display())) {
//                    if (groupedByDate.get(mapToProcess.get("date")) == null) {
//                        ArrayList<VaccineRepo.Vaccine> givenVaccinesAtDate = new ArrayList<VaccineRepo.Vaccine>();
//                        givenVaccinesAtDate.add(vaccineGiven);
//                        groupedByDate.put((DateTime) mapToProcess.get("date"), givenVaccinesAtDate);
//                    } else {
//                        groupedByDate.get(mapToProcess.get("date")).add(vaccineGiven);
//                    }
//                }
//            }
//        }
//
//        return groupedByDate;
//    }
//
//    private String addNotGivenVaccines(List<Map<String, Object>> sch, HomeVisitVaccineGroup[] allgroups) {
//        ArrayList<VaccineRepo.Vaccine> allgivenVaccines = new ArrayList<VaccineRepo.Vaccine>();
//        for (HomeVisitVaccineGroup group : allgroups) {
//            allgivenVaccines.addAll(group.getNotGivenVaccines());
//        }
//
//        LinkedHashMap<DateTime, ArrayList<VaccineRepo.Vaccine>> groupedByDate = groupVaccines(allgivenVaccines, sch);
//
//        StringBuilder groupSecondaryText = new StringBuilder();
//        Iterator<Map.Entry<DateTime, ArrayList<VaccineRepo.Vaccine>>> iterator = groupedByDate.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<DateTime, ArrayList<VaccineRepo.Vaccine>> entry = iterator.next();
//            ArrayList<VaccineRepo.Vaccine> vaccines = entry.getValue();
//            // now work with key and value...
//            for (VaccineRepo.Vaccine vaccineGiven : vaccines) {
//                groupSecondaryText.append(fixVaccineCasing(vaccineGiven.display())).append(", ");
//            }
//
//            if (groupSecondaryText.toString().endsWith(", ")) {
//                groupSecondaryText = new StringBuilder(groupSecondaryText.toString().trim());
//                groupSecondaryText = new StringBuilder(groupSecondaryText.substring(0, groupSecondaryText.length() - 1));
//            }
//
//            groupSecondaryText.append(getContext().getString(R.string.not_given_with_spaces));
//            if (iterator.hasNext()) {
//                groupSecondaryText.append(" \u00B7 ");
//            }
//        }
//
//        return groupSecondaryText.toString();
//    }
//}
