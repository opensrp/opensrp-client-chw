package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.LocalDate;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.adapter.BaseUpcomingServiceAdapter;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.activity.CoreUpcomingServicesActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.ArrayList;
import java.util.List;

public class UpcomingServicesActivity extends CoreUpcomingServicesActivity {

    private RecyclerView dueTodayRV;
    private CustomFontTextView todayServicesTV;
    private RecyclerView upcomingServicesRV;
    private CustomFontTextView upcomingServiceTv;

    public static void startMe(Activity activity, MemberObject memberObject) {
        Intent intent = new Intent(activity, UpcomingServicesActivity.class);
        intent.putExtra(Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    @Override
    public void setUpView() {
        super.setUpView();
        dueTodayRV = findViewById(R.id.today_services_recyclerView);
        todayServicesTV = findViewById(R.id.today_services);
        upcomingServicesRV = findViewById(R.id.recyclerView);
        upcomingServiceTv = findViewById(R.id.upcoming_services);
    }

    @Override
    public void refreshServices(List<BaseUpcomingService> serviceList) {
        if (ChwApplication.getApplicationFlavor().splitUpcomingServicesView()) {
            filterAndPopulateDueTodayServices(serviceList);
        } else {
            setUpcomingServiceViews();
            super.refreshServices(serviceList);
        }
    }

    private List<BaseUpcomingService> deepCopy(@Nullable List<BaseUpcomingService> serviceList) {
        if (serviceList == null) return null;

        List<BaseUpcomingService> result = new ArrayList<>();

        for (BaseUpcomingService service : serviceList) {
            BaseUpcomingService copy = new BaseUpcomingService();
            copy.setServiceName(service.getServiceName());
            copy.setServiceDate(service.getOverDueDate());
            copy.setExpiryDate(service.getExpiryDate());
            copy.setOverDueDate(service.getOverDueDate());

            copy.setUpcomingServiceList(deepCopy(service.getUpcomingServiceList()));
            result.add(copy);
        }

        return result;
    }

    private void filterAndPopulateDueTodayServices(List<BaseUpcomingService> serviceList) {
        List<BaseUpcomingService> eligibleServiceList = new ArrayList<>();
        for (BaseUpcomingService filterService : deepCopy(serviceList)) {
            List<BaseUpcomingService> eligibleVaccines = new ArrayList<>();
            for (BaseUpcomingService vaccine : filterService.getUpcomingServiceList()) {
                if (vaccine.getExpiryDate() == null || new LocalDate(vaccine.getExpiryDate()).isAfter(new LocalDate())) {
                    eligibleVaccines.add(vaccine);
                }
            }

            filterService.setUpcomingServiceList(eligibleVaccines);
            if (filterService.getUpcomingServiceList().size() > 0)
                eligibleServiceList.add(filterService);
        }

        List<BaseUpcomingService> dueNowServiceList = new ArrayList<>();
        for (BaseUpcomingService service : eligibleServiceList) {
            if (service.getServiceDate() != null)
                dueNowServiceList.add(service);
        }


        if (!dueNowServiceList.isEmpty()) {
            todayServicesTV.setVisibility(View.VISIBLE);
            dueTodayRV.setVisibility(View.VISIBLE);
            RecyclerView.Adapter<?> dueTodayAdapter = new BaseUpcomingServiceAdapter(this, dueNowServiceList);
            dueTodayRV.setAdapter(dueTodayAdapter);
        }
    }

    protected void setUpcomingServiceViews() {
        upcomingServicesRV.setVisibility(View.VISIBLE);
        upcomingServiceTv.setVisibility(View.VISIBLE);
    }

}