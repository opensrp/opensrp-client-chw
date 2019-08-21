package org.smartregister.chw.core.custom_views;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.GrowthServiceData;
import org.smartregister.chw.core.utils.HomeVisitVaccineGroup;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.immunization.db.VaccineRepo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class UpcomingServicesFragmentView extends LinearLayout implements View.OnClickListener {
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
            if (isBlank(groupVaccineTitle.getText().toString())) {
                groupVaccineTitle.append(vaccine.display().toUpperCase());
            } else {
                groupVaccineTitle.append("\n" + vaccine.display().toUpperCase());
            }
        }

        return view;
    }

    // TODO upcoming services
    private void getUpcomingGrowthNutritonData(final Context context) {
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


    public UpcomingServicesFragmentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();

    }

    public void setChildClient(Activity context, CommonPersonObjectClient childClient) {
        this.childClient = childClient;
        this.context = context;
        removeAllViews();
    }

    @Override
    public void onClick(View v) {
        //// TODO: 15/08/19
    }
}
