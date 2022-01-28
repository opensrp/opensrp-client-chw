package org.smartregister.chw.util;

import android.content.Context;

import androidx.annotation.Nullable;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.smartregister.chw.anc.contract.BaseAncUpcomingServicesContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.interactor.CoreChildUpcomingServiceInteractor;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.ChwChildDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public final class UpcomingServicesUtil {
    static List<BaseUpcomingService> deepCopy(@Nullable List<BaseUpcomingService> serviceList) {
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

    static String getDueServicesState(List<BaseUpcomingService> serviceList) {
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

        boolean hasDue = false;
        for (BaseUpcomingService service : eligibleServiceList) {
            if (service.getServiceDate() == null) continue;

            int overduePeriod = Days.daysBetween(new DateTime(service.getOverDueDate()).toLocalDate(), new DateTime().toLocalDate()).getDays();
            if (overduePeriod > 0) return CoreConstants.VISIT_STATE.OVERDUE;
            int periodDue = Days.daysBetween(new DateTime(service.getServiceDate()).toLocalDate(), new DateTime().toLocalDate()).getDays();
            hasDue = hasDue || periodDue >= 0;
        }

        return hasDue ? CoreConstants.VISIT_STATE.DUE : null;
    }

    public static void fetchUpcomingDueServicesState(MemberObject memberObject, Context ctx, Consumer<String> onDueStatusFetched) {
        String childGender = ChwChildDao.getChildGender(memberObject.getBaseEntityId());
        int childAge = memberObject.getAge();
        if (!ChwApplication.getApplicationFlavor().showChildrenAboveTwoDueStatus()
                && childAge >= 2
                && !(childGender.equalsIgnoreCase("Female") && childAge >= 9 && childAge <= 11)){
            String dueStatus = "";
            onDueStatusFetched.accept(dueStatus);
            return;
        }

        new CoreChildUpcomingServiceInteractor().getUpComingServices(memberObject, ctx, new BaseAncUpcomingServicesContract.InteractorCallBack() {
            @Override
            public void onDataFetched(List<BaseUpcomingService> serviceList) {
                onDueStatusFetched.accept(getDueServicesState(serviceList));
            }
        });
    }

    public static void fetchFamilyUpcomingDueServicesState(List<MemberObject> memberObjects, Context ctx, Consumer<Map<String, Integer> > onFamilyDueStatesConsumer){
        Map<String, Integer> upcoming = new HashMap<>();
        List<String> fetched = new ArrayList<>();
        for (MemberObject member: memberObjects) {
            fetchUpcomingDueServicesState(member, ctx, new Consumer<String>() {
                @Override
                public void accept(String s) {
                    upcoming.computeIfPresent(s, new BiFunction<String, Integer, Integer>() {
                        @Override
                        public Integer apply(String s, Integer integer) {
                            return integer + 1;
                        }
                    });
                    upcoming.computeIfAbsent(s, new Function<String, Integer>() {
                        @Override
                        public Integer apply(String s) {
                            return 1;
                        }
                    });
                    fetched.add(s);
                    if (fetched.size() >= memberObjects.size()){
                        onFamilyDueStatesConsumer.accept(upcoming);
                    }
                }
            });
        }
    }
}
