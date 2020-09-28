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
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.utils.ChildHomeVisit;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.PersonDao;
import org.smartregister.chw.rules.LmhHomeAlertRule;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.ArrayList;
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
    }

    @Override
    public void refreshServices(List<BaseUpcomingService> serviceList) {
        if (ChwApplication.getApplicationFlavor().splitUpcomingServicesView()) {
            filterAndPopulateDueTodayServices(serviceList);
            // filter update view

            boolean isDue = isVisitDue();
            List<BaseUpcomingService> expiredServices = new ArrayList<>();
            for (BaseUpcomingService filterService : deepCopy(serviceList)) {
                List<BaseUpcomingService> expiredVaccines = new ArrayList<>();
                for (BaseUpcomingService vaccine : filterService.getUpcomingServiceList()) {
                    if (new LocalDate(vaccine.getExpiryDate()).isBefore(new LocalDate())) {
                        expiredVaccines.add(vaccine);
                    }
                }

                filterService.setUpcomingServiceList(expiredVaccines);
                if (filterService.getUpcomingServiceList().size() > 0)
                    expiredServices.add(filterService);
            }

            List<BaseUpcomingService> upcomingServices = new ArrayList<>();
            for (BaseUpcomingService service : expiredServices) {
                if (service.getServiceDate() != null && isDue)
                    upcomingServices.add(service);
            }

            super.refreshServices(upcomingServices);

        }else{
            super.refreshServices(serviceList);
        }
    }

    private List<BaseUpcomingService> deepCopy(@Nullable List<BaseUpcomingService> serviceList){
        if(serviceList == null) return null;

        List<BaseUpcomingService> result = new ArrayList<>();

        for(BaseUpcomingService service: serviceList){
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

    private boolean isVisitDue() {
        ChildHomeVisit childHomeVisit = ChildUtils.getLastHomeVisit(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD, memberObject.getBaseEntityId());
        String yearOfBirth = PersonDao.getDob(memberObject.getBaseEntityId());
        LmhHomeAlertRule alertRule = new LmhHomeAlertRule(
                ChwApplication.getInstance().getApplicationContext(), yearOfBirth, childHomeVisit.getLastHomeVisitDate(), childHomeVisit.getVisitNotDoneDate(), childHomeVisit.getDateCreated());
        return (CoreChwApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(alertRule, CoreConstants.RULE_FILE.HOME_VISIT)).equalsIgnoreCase("Due");
    }

    private void filterAndPopulateDueTodayServices(List<BaseUpcomingService> serviceList){
        boolean isDue = isVisitDue();
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
            if (service.getServiceDate() != null && isDue)
                dueNowServiceList.add(service);
        }


        if (!dueNowServiceList.isEmpty()) {
            todayServicesTV.setVisibility(View.VISIBLE);
            dueTodayRV.setVisibility(View.VISIBLE);
            RecyclerView.Adapter<?> dueTodayAdapter = new BaseUpcomingServiceAdapter(this, dueNowServiceList);
            dueTodayRV.setAdapter(dueTodayAdapter);
        }
    }

}
