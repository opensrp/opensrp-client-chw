package org.smartregister.chw.custom_view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.UpcomingServicesActivity;
import org.smartregister.chw.contract.HomeVisitGrowthNutritionContract;
import org.smartregister.chw.contract.HomeVisitImmunizationContract;
import org.smartregister.chw.interactor.HomeVisitGrowthNutritionInteractor;
import org.smartregister.chw.presenter.HomeVisitImmunizationPresenter;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.GrowthServiceData;
import org.smartregister.chw.util.HomeVisitVaccineGroupDetails;
import org.smartregister.chw.util.ImmunizationState;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.immunization.domain.Vaccine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class UpcomingServicesFragmentView extends LinearLayout implements View.OnClickListener, HomeVisitImmunizationContract.View {


    private HomeVisitImmunizationPresenter presenter;
    private Activity context;
    private CommonPersonObjectClient childClient;
    private Map<String, View> viewMap = new LinkedHashMap<>();

    public UpcomingServicesFragmentView(Context context) {
        super(context);
    }

    public UpcomingServicesFragmentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initUi();

    }

    public UpcomingServicesFragmentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();

    }

    private void initUi() {
        inflate(getContext(), R.layout.view_upcoming_service, this);
        setOrientation(VERTICAL);
        initializePresenter();

    }

    @Override
    public void setActivity(Activity activity) {

        this.context = activity;
    }


    @Override
    public void setChildClient(CommonPersonObjectClient childClient) {
        presenter.setChildClient(childClient);
        this.childClient = childClient;

    }

    @Override
    public void refreshPresenter(List<Alert> alerts, List<Vaccine> vaccines, List<Map<String, Object>> sch) {
        presenter.createAllVaccineGroups(alerts, vaccines, sch);
        presenter.getVaccinesNotGivenLastVisit();
        presenter.calculateCurrentActiveGroup();
        ArrayList<HomeVisitVaccineGroupDetails> homeVisitVaccineGroupDetailsList = presenter.getAllgroups();
        for (HomeVisitVaccineGroupDetails homeVisitVaccineGroupDetail : homeVisitVaccineGroupDetailsList) {
            if (homeVisitVaccineGroupDetail.getAlert().equals(ImmunizationState.DUE)
                    || homeVisitVaccineGroupDetail.getAlert().equals(ImmunizationState.OVERDUE)
                     || homeVisitVaccineGroupDetail.getAlert().equals(ImmunizationState.UPCOMING)) {
                if (homeVisitVaccineGroupDetail.getNotGivenVaccines().size() > 0) {
                    addView(createUpcomingServicesCard(homeVisitVaccineGroupDetail));
               }
            }
        }

        getUpcomingGrowthNutritonData();

    }

    private View createUpcomingServicesCard(HomeVisitVaccineGroupDetails homeVisitVaccineGroupDetail) {
        View view = context.getLayoutInflater().inflate(R.layout.upcoming_service_row, null);
        TextView groupDateTitle = (TextView) view.findViewById(R.id.grou_date_title);
        TextView groupDateStatus = (TextView) view.findViewById(R.id.grou_date_status);
        TextView groupNameTitle = (TextView) view.findViewById(R.id.grou_name_title);
        TextView groupVaccineTitle = (TextView) view.findViewById(R.id.grou_vaccines_title);
        groupVaccineTitle.setText("");
        if (!viewMap.containsKey(homeVisitVaccineGroupDetail.getDueDisplayDate())) {
            viewMap.put(homeVisitVaccineGroupDetail.getDueDisplayDate(), view);
        }
        groupDateTitle.setText(homeVisitVaccineGroupDetail.getDueDisplayDate());
        groupDateStatus.setText(ChildUtils.daysAway(homeVisitVaccineGroupDetail.getDueDate()));
        groupNameTitle.setText(String.format(getContext().getString(R.string.immunizations), homeVisitVaccineGroupDetail.getGroup()));
        for (VaccineRepo.Vaccine vaccine : homeVisitVaccineGroupDetail.getNotGivenVaccines()) {
            if (isBlank(groupVaccineTitle.getText().toString())) {
                groupVaccineTitle.append(vaccine.display().toUpperCase());
            } else {
                groupVaccineTitle.append("\n" + vaccine.display().toUpperCase());
            }
        }

        return view;
    }

    private View createGrowthCard(GrowthServiceData growthServiceData) {
        View view = context.getLayoutInflater().inflate(R.layout.upcoming_service_row, null);
        TextView groupDateTitle = (TextView) view.findViewById(R.id.grou_date_title);
        TextView groupDateStatus = (TextView) view.findViewById(R.id.grou_date_status);
        ((TextView) view.findViewById(R.id.grou_name_title)).setVisibility(GONE);
        ((TextView) view.findViewById(R.id.grou_vaccines_title)).setVisibility(GONE);
        TextView growth = (TextView) view.findViewById(R.id.growth_service_name_title);
        growth.setVisibility(VISIBLE);
        groupDateTitle.setText(growthServiceData.getDisplayAbleDate());
        groupDateStatus.setText(ChildUtils.daysAway(growthServiceData.getDate()));
        growth.setText(growthServiceData.getDisplayName());

        return view;
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

    private void getUpcomingGrowthNutritonData() {
        final HomeVisitGrowthNutritionInteractor homeVisitGrowthNutritionInteractor = new HomeVisitGrowthNutritionInteractor();
        homeVisitGrowthNutritionInteractor.parseRecordServiceData(childClient, new HomeVisitGrowthNutritionContract.InteractorCallBack() {
                    @Override
                    public void updateRecordVisitData(final Map<String, ServiceWrapper> stringServiceWrapperMap) {

//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                        try {
                            ArrayList<GrowthServiceData> growthServiceDataList = homeVisitGrowthNutritionInteractor.getAllDueService(stringServiceWrapperMap);
                            String lastDate = "";
                            View lastView = null;
                            for (Iterator<GrowthServiceData> i = growthServiceDataList.iterator(); i.hasNext(); ) {
                                GrowthServiceData growthServiceData = i.next();
                                View existView = isExistView(growthServiceData);
                                if (existView != null) {
                                    TextView growth = (TextView) existView.findViewById(R.id.growth_service_name_title);
                                    if (growth.getVisibility() == GONE) {
                                        growth.setVisibility(VISIBLE);
                                        growth.setText(growthServiceData.getDisplayName());
                                    } else {
                                        growth.append("\n" + growthServiceData.getDisplayName());
                                    }
                                    lastDate = growthServiceData.getDisplayAbleDate();
                                    i.remove();
                                } else {
                                    if (!lastDate.equalsIgnoreCase(growthServiceData.getDisplayAbleDate())) {
                                        lastDate = growthServiceData.getDisplayAbleDate();
                                        lastView = createGrowthCard(growthServiceData);
                                        addView(lastView);
                                    } else {
                                        if (lastView != null) {
                                            TextView growth = (TextView) lastView.findViewById(R.id.growth_service_name_title);
                                            growth.append("\n" + growthServiceData.getDisplayName());
                                        }

                                    }
                                }

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (context instanceof UpcomingServicesActivity) {
                                UpcomingServicesActivity activity = (UpcomingServicesActivity) context;
                                activity.progressBarVisibility(false);

                            }
                        }
                    }

//                }, 200);
//
//            }
        });
    }


    private View isExistView(GrowthServiceData growthServiceData) {
        for (String date : viewMap.keySet()) {
            if (date.equalsIgnoreCase(growthServiceData.getDisplayAbleDate())) {
                return viewMap.get(date);
            }
        }
        return null;
    }

    @Override
    public void updateImmunizationState() {

        removeAllViews();
        presenter.updateImmunizationState(this);

    }

    @Override
    public void immunizationState(List<Alert> alerts, List<Vaccine> vaccines, List<Map<String, Object>> sch,Map<String, Object> nv) {

        refreshPresenter(alerts, vaccines, sch);

    }


    @Override
    public void onClick(View v) {

    }
}
