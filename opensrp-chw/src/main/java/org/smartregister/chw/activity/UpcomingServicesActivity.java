package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.time.DateUtils;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.adapter.BaseUpcomingServiceAdapter;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.activity.CoreUpcomingServicesActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UpcomingServicesActivity extends CoreUpcomingServicesActivity {

    private RecyclerView dueTodayRV;
    private CustomFontTextView todayServicesTV;

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
        dueTodayRV.setHasFixedSize(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        dueTodayRV.setLayoutManager(layoutManager);
    }

    @Override
    public void refreshServices(List<BaseUpcomingService> serviceList) {
        if (ChwApplication.getApplicationFlavor().splitUpcomingServicesView()) {
            List<BaseUpcomingService> dueNowServiceList = filterDueTodayServiceList(serviceList);

            if (!dueNowServiceList.isEmpty()) {
                updateUi();
                serviceList.removeAll(dueNowServiceList);
                RecyclerView.Adapter dueTodayAdapter = new BaseUpcomingServiceAdapter(this, dueNowServiceList);
                dueTodayRV.setAdapter(dueTodayAdapter);
            }

        }
        super.refreshServices(serviceList);
    }

    private void updateUi() {
        todayServicesTV.setVisibility(View.VISIBLE);
        dueTodayRV.setVisibility(View.VISIBLE);
    }

    private List<BaseUpcomingService> filterDueTodayServiceList(List<BaseUpcomingService> serviceList) {
        Date date = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        List<BaseUpcomingService> dueNowServiceList = new ArrayList<>();
        for (BaseUpcomingService service : serviceList) {
            if (service.getServiceDate().equals(date)) {
                dueNowServiceList.add(service);
            }
        }
        return dueNowServiceList;
    }
}
