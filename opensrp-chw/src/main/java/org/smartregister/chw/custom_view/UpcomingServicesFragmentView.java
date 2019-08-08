package org.smartregister.chw.custom_view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.UpcomingServicesActivity;
import org.smartregister.chw.contract.HomeVisitGrowthNutritionContract;
import org.smartregister.chw.contract.ImmunizationContact;
import org.smartregister.chw.interactor.HomeVisitGrowthNutritionInteractor;
import org.smartregister.chw.presenter.ImmunizationViewPresenter;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.GrowthServiceData;
import org.smartregister.chw.util.HomeVisitVaccineGroup;
import org.smartregister.chw.util.ImmunizationState;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.ServiceWrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class UpcomingServicesFragmentView extends LinearLayout implements View.OnClickListener, ImmunizationContact.View {


    private ImmunizationViewPresenter presenter;
    private Map<String, View> viewMap = new LinkedHashMap<>();
    private CommonPersonObjectClient childClient;
    private Activity context;

    @Override
    public Context getMyContext() {
        return context;
    }

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

    public void setChildClient(Activity context, CommonPersonObjectClient childClient) {
        this.childClient = childClient;
        this.context = context;
        removeAllViews();
        presenter.fetchImmunizationData(childClient, "");
    }


    private View createUpcomingServicesCard(HomeVisitVaccineGroup homeVisitVaccineGroupDetail) {
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
    public ImmunizationContact.Presenter initializePresenter() {
        presenter = new ImmunizationViewPresenter(context);
        return presenter;
    }

    @Override
    public void allDataLoaded() {

    }

    @Override
    public void updateSubmitBtn() {
        //no need to do
    }

    @Override
    public void onUpdateNextPosition() {
        //no need to do
    }

    @Override
    public void updateAdapter(int position, Context context) {
        ArrayList<HomeVisitVaccineGroup> homeVisitVaccineGroupList = presenter.getHomeVisitVaccineGroupDetails();
        for (HomeVisitVaccineGroup homeVisitVaccineGroup : homeVisitVaccineGroupList) {
            if (homeVisitVaccineGroup.getNotGivenVaccines().size() > 0 && (homeVisitVaccineGroup.getAlert().equals(ImmunizationState.DUE)
                    || homeVisitVaccineGroup.getAlert().equals(ImmunizationState.OVERDUE)
                    || homeVisitVaccineGroup.getAlert().equals(ImmunizationState.UPCOMING))) {
                addView(createUpcomingServicesCard(homeVisitVaccineGroup));
            }
        }

        getUpcomingGrowthNutritonData(context);

    }


    private void getUpcomingGrowthNutritonData(final Context context) {
        final HomeVisitGrowthNutritionInteractor homeVisitGrowthNutritionInteractor = new HomeVisitGrowthNutritionInteractor();
        homeVisitGrowthNutritionInteractor.parseRecordServiceData(childClient, new HomeVisitGrowthNutritionContract.InteractorCallBack() {
            @Override
            public void updateNotGivenRecordVisitData(Map<String, ServiceWrapper> stringServiceWrapperMap) {
                //No need to do anything
            }

            @Override
            public void allDataLoaded() {

            }

            @Override
            public void updateGivenRecordVisitData(final Map<String, ServiceWrapper> stringServiceWrapperMap) {

                try {
                    ArrayList<GrowthServiceData> growthServiceDataList = homeVisitGrowthNutritionInteractor.getAllDueService(stringServiceWrapperMap, context);
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
    public void onClick(View v) {

    }
}
