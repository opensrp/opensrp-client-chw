package org.smartregister.chw.core.custom_views;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.activity.CoreUpcomingServicesActivity;
import org.smartregister.chw.core.contract.ImmunizationContact;
import org.smartregister.chw.core.enums.ImmunizationState;
import org.smartregister.chw.core.interactor.HomeVisitGrowthNutritionInteractor;
import org.smartregister.chw.core.presenter.ImmunizationViewPresenter;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.GrowthServiceData;
import org.smartregister.chw.core.utils.HomeVisitVaccineGroup;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.immunization.db.VaccineRepo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class UpcomingServicesFragmentView extends LinearLayout implements View.OnClickListener, ImmunizationContact.View {
    private ImmunizationViewPresenter presenter;
    private Map<String, View> viewMap = new LinkedHashMap<>();
    private CommonPersonObjectClient childClient;
    private Activity context;

    public UpcomingServicesFragmentView(Context context) {
        super(context);
    }

    public UpcomingServicesFragmentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initUi();

    }

    private void initUi() {
        inflate(getContext(), R.layout.view_upcoming_service, this);
        setOrientation(VERTICAL);
        initializePresenter();

    }

    @Override
    public ImmunizationContact.Presenter initializePresenter() {
        presenter = new ImmunizationViewPresenter(this);
        return presenter;
    }

    @Override
    public void allDataLoaded() {
        //// TODO: 15/08/19
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

    private View createUpcomingServicesCard(HomeVisitVaccineGroup homeVisitVaccineGroupDetail) {
        View view = context.getLayoutInflater().inflate(R.layout.upcoming_service_row, null);
        TextView groupDateTitle = view.findViewById(R.id.grou_date_title);
        TextView groupDateStatus = view.findViewById(R.id.grou_date_status);
        TextView groupNameTitle = view.findViewById(R.id.grou_name_title);
        TextView groupVaccineTitle = view.findViewById(R.id.grou_vaccines_title);
        groupVaccineTitle.setText("");
        if (!viewMap.containsKey(homeVisitVaccineGroupDetail.getDueDisplayDate())) {
            viewMap.put(homeVisitVaccineGroupDetail.getDueDisplayDate(), view);
        }
        groupDateTitle.setText(homeVisitVaccineGroupDetail.getDueDisplayDate());
        groupDateStatus.setText(CoreChildUtils.daysAway(homeVisitVaccineGroupDetail.getDueDate()));
        groupNameTitle.setText(String.format(getContext().getString(R.string.immunizations), homeVisitVaccineGroupDetail.getGroup()));
        for (VaccineRepo.Vaccine vaccine : homeVisitVaccineGroupDetail.getNotGivenVaccines()) {
            if (StringUtils.isBlank(groupVaccineTitle.getText().toString())) {
                groupVaccineTitle.append(vaccine.display().toUpperCase());
            } else {
                groupVaccineTitle.append("\n" + vaccine.display().toUpperCase());
            }
        }

        return view;
    }

    private void getUpcomingGrowthNutritonData(final Context context) {
        final HomeVisitGrowthNutritionInteractor homeVisitGrowthNutritionInteractor = new HomeVisitGrowthNutritionInteractor();
        homeVisitGrowthNutritionInteractor.parseRecordServiceData(childClient, stringServiceWrapperMap -> {
            try {
                ArrayList<GrowthServiceData> growthServiceDataList = homeVisitGrowthNutritionInteractor.getAllDueService(stringServiceWrapperMap, context);
                String lastDate = "";
                View lastView = null;
                for (Iterator<GrowthServiceData> i = growthServiceDataList.iterator(); i.hasNext(); ) {
                    GrowthServiceData growthServiceData = i.next();
                    View existView = isExistView(growthServiceData);
                    if (existView != null) {
                        TextView growth = existView.findViewById(R.id.growth_service_name_title);
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
                                TextView growth = lastView.findViewById(R.id.growth_service_name_title);
                                growth.append("\n" + growthServiceData.getDisplayName());
                            }

                        }
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (context instanceof CoreUpcomingServicesActivity) {
                    CoreUpcomingServicesActivity activity = (CoreUpcomingServicesActivity) context;
                    activity.progressBarVisibility(false);

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

    private View createGrowthCard(GrowthServiceData growthServiceData) {
        View view = context.getLayoutInflater().inflate(R.layout.upcoming_service_row, null);
        TextView groupDateTitle = view.findViewById(R.id.grou_date_title);
        TextView groupDateStatus = view.findViewById(R.id.grou_date_status);
        view.findViewById(R.id.grou_name_title).setVisibility(GONE);
        view.findViewById(R.id.grou_vaccines_title).setVisibility(GONE);
        TextView growth = view.findViewById(R.id.growth_service_name_title);
        growth.setVisibility(VISIBLE);
        groupDateTitle.setText(growthServiceData.getDisplayAbleDate());
        groupDateStatus.setText(CoreChildUtils.daysAway(growthServiceData.getDate()));
        growth.setText(growthServiceData.getDisplayName());

        return view;
    }

    @Override
    public Context getMyContext() {
        if (context == null) {
            return getContext();
        }
        return context;
    }


    public UpcomingServicesFragmentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();

    }

    public void setChildClient(Activity context, CommonPersonObjectClient childClient) {
        this.childClient = childClient;
        this.context = context;
        removeAllViews();
        presenter.fetchImmunizationData(childClient, "");
    }

    @Override
    public void onClick(View v) {
        //// TODO: 15/08/19
    }
}
