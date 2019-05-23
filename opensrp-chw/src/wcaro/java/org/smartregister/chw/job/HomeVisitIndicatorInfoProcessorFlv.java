package org.smartregister.chw.job;

import com.google.gson.reflect.TypeToken;

import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.domain.HomeVisit;
import org.smartregister.chw.domain.HomeVisitIndicatorInfo;
import org.smartregister.chw.repository.HomeVisitIndicatorInfoRepository;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.immunization.domain.ServiceWrapper;

import java.util.HashMap;
import java.util.List;

/**
 * Contains the home visit service processor
 *
 * @author Allan
 */
public class HomeVisitIndicatorInfoProcessorFlv implements ChwIndicatorGeneratingJob.HomeVisitInfoProcessorFlv {

    private static final String HOME_VISIT_INFO_LAST_PROCESSED_DATE = "home_visit_info_last_processed_date";

    /**
     * Get the latest home visit entries then parses the service JSON details and saves them
     * in the home visit indicator info table for use by indicator queries
     */
    @Override
    public void processHomeVisitDetails() {
        String lastProcessedDate = ChwApplication.getInstance().getContext().allSharedPreferences().getPreference(HOME_VISIT_INFO_LAST_PROCESSED_DATE);
        List<HomeVisit> homeVisitList = ChwApplication.homeVisitRepository().getLatestHomeVisitsByDate(lastProcessedDate);
        HomeVisitIndicatorInfoRepository indicatorInfoRepo = ChwApplication.homeVisitIndicatorInfoRepository();
        String serviceJSONString;
        HashMap<String, ServiceWrapper> serviceWrapperMap;
        HomeVisitIndicatorInfo homeVisitIndicatorInfo;
        for (HomeVisit homeVisit : homeVisitList) {
            serviceJSONString = homeVisit.getServicesGiven().toString();
            if (!serviceJSONString.isEmpty()) {
                serviceWrapperMap = ChildUtils.gsonConverter.fromJson(serviceJSONString, new TypeToken<HashMap<String, ServiceWrapper>>() {
                }.getType());
                // Build home visit indicator info and persist info
                if (serviceWrapperMap != null) {
                    for (String key : serviceWrapperMap.keySet()) {
                        ServiceWrapper serviceWrapper = serviceWrapperMap.get(key);
                        homeVisitIndicatorInfo = new HomeVisitIndicatorInfo();
                        homeVisitIndicatorInfo.setBaseEntityId(homeVisit.getBaseEntityId());
                        homeVisitIndicatorInfo.setLastHomeVisitDate(homeVisit.getDate().getTime());
                        homeVisitIndicatorInfo.setHomeVisitId(homeVisit.getId());
                        homeVisitIndicatorInfo.setService(serviceWrapper.getServiceType().getName());
                        homeVisitIndicatorInfo.setServiceDate(serviceWrapper.getVaccineDateAsString());
                        homeVisitIndicatorInfo.setServiceUpdateDate(serviceWrapper.getUpdatedVaccineDate().toString());
                        homeVisitIndicatorInfo.setUpdatedAt(homeVisit.getUpdatedAt());
                        indicatorInfoRepo.saveHomeVisitInfo(homeVisitIndicatorInfo);
                    }
                    ChwApplication.getInstance().getContext().allSharedPreferences().savePreference(HOME_VISIT_INFO_LAST_PROCESSED_DATE, homeVisit.getCreatedAt().toString());
                }
            }
        }
    }
}
