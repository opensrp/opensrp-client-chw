package org.smartregister.chw.job;

import com.google.gson.reflect.TypeToken;

import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.domain.HomeVisit;
import org.smartregister.chw.domain.HomeVisitIndicatorInfo;
import org.smartregister.chw.repository.HomeVisitIndicatorInfoRepository;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Contains the home visit service processor
 *
 * @author Allan
 */
public class HomeVisitIndicatorInfoProcessorFlv implements ChwIndicatorGeneratingJob.Flavor {

    public static final String HOME_VISIT_INDICATOR_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    private static final String HOME_VISIT_INFO_LAST_PROCESSED_DATE = "home_visit_info_last_processed_date";
    private static final String TAG = HomeVisitIndicatorInfoProcessorFlv.class.getCanonicalName();

    /**
     * Get the latest home visit entries then parses the service JSON details and saves them
     * in the home visit indicator info table for use by indicator queries
     */
    @Override
    public void processHomeVisitDetails() {
        String lastProcessedDate = ChwApplication.getInstance().getContext().allSharedPreferences().getPreference(HOME_VISIT_INFO_LAST_PROCESSED_DATE);
        List<HomeVisit> homeVisitList = ChwApplication.homeVisitRepository().getLatestHomeVisitsLaterThanDate(lastProcessedDate);
        Timber.d(TAG, "processHomeVisitDetails#lastprocessedDate :: %s", lastProcessedDate);
        HomeVisitIndicatorInfoRepository indicatorInfoRepo = ChwApplication.homeVisitIndicatorInfoRepository();
        String serviceGivenJSONString;
        String serviceNotGivenJSONString;
        boolean serviceGiven;
        HashMap<String, ServiceWrapper> serviceWrapperMap;
        for (HomeVisit homeVisit : homeVisitList) {
            serviceGivenJSONString = homeVisit.getServicesGiven().toString();
            serviceNotGivenJSONString = homeVisit.getServiceNotGiven().toString();
            // Build home visit indicator info and persist info
            if (!serviceGivenJSONString.isEmpty()) {
                try {
                    serviceWrapperMap = ChildUtils.gsonConverter.fromJson(serviceGivenJSONString, new TypeToken<HashMap<String, ServiceWrapper>>() {
                    }.getType());
                    serviceGiven = true;
                    saveHomeVisitInfo(serviceWrapperMap, homeVisit, indicatorInfoRepo, serviceGiven);
                } catch (Exception ex) {
                    Timber.e(ex.toString());
                }
            }
            if (!serviceNotGivenJSONString.isEmpty()) {
                try {
                    serviceWrapperMap = ChildUtils.gsonConverter.fromJson(serviceNotGivenJSONString, new TypeToken<HashMap<String, ServiceWrapper>>() {
                    }.getType());
                    serviceGiven = false;
                    saveHomeVisitInfo(serviceWrapperMap, homeVisit, indicatorInfoRepo, serviceGiven);
                } catch (Exception ex) {
                    Timber.e(ex.toString());
                }
            }
            ChwApplication.getInstance().getContext().
                    allSharedPreferences().savePreference(HOME_VISIT_INFO_LAST_PROCESSED_DATE, new SimpleDateFormat(HOME_VISIT_INDICATOR_DATE_FORMAT, Locale.getDefault()).format(homeVisit.getCreatedAt()));
        }
    }

    private void saveHomeVisitInfo(HashMap<String, ServiceWrapper> serviceWrapperMap, HomeVisit homeVisit, HomeVisitIndicatorInfoRepository indicatorInfoRepo, boolean serviceGiven) {
        String vaccineDateFormat = "yyyy-MM-dd";
        HomeVisitIndicatorInfo homeVisitIndicatorInfo;
        if (serviceWrapperMap != null) {
            for (String key : serviceWrapperMap.keySet()) {
                ServiceWrapper serviceWrapper = serviceWrapperMap.get(key);
                homeVisitIndicatorInfo = new HomeVisitIndicatorInfo();
                homeVisitIndicatorInfo.setBaseEntityId(homeVisit.getBaseEntityId());
                homeVisitIndicatorInfo.setLastHomeVisitDate(homeVisit.getDate());
                homeVisitIndicatorInfo.setHomeVisitId(homeVisit.getId());
                homeVisitIndicatorInfo.setService(serviceWrapper.getServiceType().getName());
                homeVisitIndicatorInfo.setServiceDate(parseDate(serviceWrapper.getVaccineDateAsString(), vaccineDateFormat));
                homeVisitIndicatorInfo.setServiceUpdateDate(parseDate(serviceWrapper.getUpdatedVaccineDateAsString(), vaccineDateFormat));
                homeVisitIndicatorInfo.setServiceGiven(serviceGiven);
                homeVisitIndicatorInfo.setUpdatedAt(new Date(homeVisit.getUpdatedAt()));
                homeVisitIndicatorInfo.setCreatedAt(homeVisit.getCreatedAt());
                indicatorInfoRepo.addHomeVisitInfo(homeVisitIndicatorInfo);
            }
        }
    }

    private Date parseDate(String date, String format) {
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.parse(date);
        } catch (ParseException pe) {
            Log.logError(TAG, "Error parsing the date");
            return null;
        }
    }
}
