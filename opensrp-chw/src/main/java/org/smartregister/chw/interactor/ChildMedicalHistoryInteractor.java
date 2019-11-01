package org.smartregister.chw.interactor;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.repository.VisitDetailsRepository;
import org.smartregister.chw.anc.repository.VisitRepository;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.contract.ChildMedicalHistoryContract;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.ServiceTask;
import org.smartregister.chw.core.utils.TaskServiceCalculate;
import org.smartregister.chw.core.utils.VaccineScheduleUtil;
import org.smartregister.chw.util.BaseService;
import org.smartregister.chw.util.BaseVaccine;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.ReceivedVaccine;
import org.smartregister.chw.util.ServiceContent;
import org.smartregister.chw.util.ServiceHeader;
import org.smartregister.chw.util.ServiceLine;
import org.smartregister.chw.util.Utils;
import org.smartregister.chw.util.VaccineContent;
import org.smartregister.chw.util.VaccineHeader;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.jsonmapping.Vaccine;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ChildMedicalHistoryInteractor implements ChildMedicalHistoryContract.Interactor {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private AppExecutors appExecutors;

    private ArrayList<BaseService> baseServiceArrayList = new ArrayList<>();

    private VisitRepository visitRepository;
    private VisitDetailsRepository visitDetailsRepository;

    private Context context;

    @VisibleForTesting
    public ChildMedicalHistoryInteractor() {
        Timber.v("constructor");
    }

    public ChildMedicalHistoryInteractor(AppExecutors appExecutors, VisitRepository visitRepository, VisitDetailsRepository visitDetailsRepository, Context context) {
        this.appExecutors = appExecutors;
        this.visitRepository = visitRepository;
        this.visitDetailsRepository = visitDetailsRepository;
        this.context = context;
    }

    @Override
    public void fetchBirthCertificateData(CommonPersonObjectClient commonPersonObjectClient, final ChildMedicalHistoryContract.InteractorCallBack callBack) {

        Runnable runnable = () -> {
            List<Visit> visits = visitRepository.getVisits(commonPersonObjectClient.getCaseId(), Constants.EventType.BIRTH_CERTIFICATION);
            if (visits == null || visits.size() < 1)
                return;

            Visit visit = visits.get(0);
            visit.setVisitDetails(VisitUtils.getVisitGroups(visitDetailsRepository.getVisits(visit.getVisitId())));

            String birthCert = NCUtils.getText(visit.getVisitDetails().get(ChildDBConstants.KEY.BIRTH_CERT)).trim();
            String birthCertDate = NCUtils.getText(visit.getVisitDetails().get(ChildDBConstants.KEY.BIRTH_CERT_ISSUE_DATE)).trim();
            String birthCertNumber = NCUtils.getText(visit.getVisitDetails().get(ChildDBConstants.KEY.BIRTH_CERT_NUMBER)).trim();
            String notification = NCUtils.getText(visit.getVisitDetails().get(ChildDBConstants.KEY.BIRTH_CERT_NOTIFIICATION)).trim();

            final ArrayList<String> birthCertificationContent = new ArrayList<>();
            if (!TextUtils.isEmpty(birthCert) && birthCert.equalsIgnoreCase("Yes")) {
                birthCertificationContent.add(getContext().getString(R.string.birth_cert_value, birthCert));
                birthCertificationContent.add(getContext().getString(R.string.birth_cert_date, birthCertDate));
                birthCertificationContent.add(getContext().getString(R.string.birth_cert_number, birthCertNumber));

            } else if (!TextUtils.isEmpty(birthCert) && birthCert.equalsIgnoreCase("No")) {
                birthCertificationContent.add(getContext().getString(R.string.birth_cert_value, birthCert));

                if (!TextUtils.isEmpty(notification) && notification.equalsIgnoreCase("Yes")) {
                    birthCertificationContent.add(getContext().getString(R.string.birth_cert_notification, getContext().getString(R.string.yes)));
                    birthCertificationContent.add(getContext().getString(R.string.birth_cert_note_1));
                } else if (!TextUtils.isEmpty(notification) && notification.equalsIgnoreCase("No")) {
                    birthCertificationContent.add(getContext().getString(R.string.birth_cert_notification, getContext().getString(R.string.no)));
                    birthCertificationContent.add(getContext().getString(R.string.birth_cert_note_2));
                }
//            else {
//                birthCertificationContent.add(getContext().getString(R.string.birth_cert_notification,"No"));
//            }
            }

            appExecutors.mainThread().execute(() -> callBack.updateBirthCertification(birthCertificationContent));
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void fetchIllnessData(CommonPersonObjectClient commonPersonObjectClient, final ChildMedicalHistoryContract.InteractorCallBack callBack) {

        Runnable runnable = () -> {
            final ArrayList<String> illnessContent = new ArrayList<>();
            List<Visit> visits = visitRepository.getVisits(commonPersonObjectClient.getCaseId(), Constants.EventType.OBS_ILLNESS);

            if (visits == null || visits.size() < 1)
                return;

            Visit visit = visits.get(0);
            visit.setVisitDetails(VisitUtils.getVisitGroups(visitDetailsRepository.getVisits(visit.getVisitId())));

            String illnessDate = NCUtils.getText(visit.getVisitDetails().get(ChildDBConstants.KEY.ILLNESS_DATE)).trim();
            String illnessDescription = NCUtils.getText(visit.getVisitDetails().get(ChildDBConstants.KEY.ILLNESS_DESCRIPTION)).trim();
            String illnessAction = NCUtils.getText(visit.getVisitDetails().get(ChildDBConstants.KEY.ILLNESS_ACTION)).trim();

            if (!TextUtils.isEmpty(illnessDate)) {
                illnessContent.add(getContext().getString(R.string.illness_date_with_value, illnessDate));
                illnessContent.add(getContext().getString(R.string.illness_des_with_value, illnessDescription));
                illnessContent.add(getContext().getString(R.string.illness_action_value, illnessAction));
            }

            // return data to main thread
            appExecutors.mainThread().execute(() -> callBack.updateIllnessData(illnessContent));
        };

        // execute in a background thread
        appExecutors.diskIO().execute(runnable);


        final String vaccineCard = org.smartregister.util.Utils.getValue(commonPersonObjectClient.getColumnmaps(), ChildDBConstants.KEY.VACCINE_CARD, true);
        if (!TextUtils.isEmpty(vaccineCard)) {
            Runnable runnable3 = () -> appExecutors.mainThread().execute(() -> callBack.updateVaccineCard(vaccineCard));
            appExecutors.diskIO().execute(runnable3);
        }
    }

    @Override
    public void setInitialVaccineList(Map<String, Date> recievedVaccines, final ChildMedicalHistoryContract.InteractorCallBack callBack) {

        ArrayList<ReceivedVaccine> receivedVaccineArrayList = new ArrayList<>();
        final ArrayList<BaseVaccine> baseVaccineArrayList = new ArrayList<>();
        List<VaccineGroup> vaccineGroups = VaccineScheduleUtil.getVaccineGroups(CoreChwApplication.getInstance().getApplicationContext(), CoreConstants.SERVICE_GROUPS.CHILD);
        for (String name : recievedVaccines.keySet()) {
            VaccineGroup vaccineGroup = getVaccineGroupNameByVaccine(name.replace("_", " ").toLowerCase(), vaccineGroups);
            if (vaccineGroup == null) continue;
            String stateKey = vaccineGroup.name;
            ReceivedVaccine receivedVaccine = new ReceivedVaccine();
            receivedVaccine.setVaccineCategory(stateKey);
            receivedVaccine.setVaccineName(CoreChildUtils.fixVaccineCasing(name).replace("MEASLES", "MCV"));
            if (receivedVaccine.getVaccineName().contains("MEASLES")) {
                receivedVaccine.setVaccineName(receivedVaccine.getVaccineName().replace("MEASLES", "MCV"));
            }
            receivedVaccine.setVaccineDate(recievedVaccines.get(name));
            receivedVaccine.setVaccineIndex(vaccineGroups.indexOf(vaccineGroup));
            receivedVaccineArrayList.add(receivedVaccine);
        }
        if (receivedVaccineArrayList.size() > 0) {
            Collections.sort(receivedVaccineArrayList, (vaccine1, vaccine2) -> {
                if (vaccine1.getVaccineIndex() < vaccine2.getVaccineIndex()) {
                    return -1;
                } else if (vaccine1.getVaccineIndex() > vaccine2.getVaccineIndex()) {
                    return 1;
                }
                return 0;
            });
        }

        String lastCategory = "";
        for (ReceivedVaccine receivedVaccine : receivedVaccineArrayList) {
            if (!receivedVaccine.getVaccineCategory().equalsIgnoreCase(lastCategory)) {
                VaccineHeader vaccineHeader = new VaccineHeader();
                lastCategory = receivedVaccine.getVaccineCategory();
                vaccineHeader.setVaccineHeaderName(Utils.getImmunizationHeaderLanguageSpecific(getContext(), receivedVaccine.getVaccineCategory()));
                baseVaccineArrayList.add(vaccineHeader);
                VaccineContent content = new VaccineContent();
                String date = DATE_FORMAT.format(receivedVaccine.getVaccineDate());
                content.setVaccineDate(date);
                content.setVaccineName(receivedVaccine.getVaccineName());
                baseVaccineArrayList.add(content);
            } else {
                VaccineContent content = new VaccineContent();
                String date = DATE_FORMAT.format(receivedVaccine.getVaccineDate());
                content.setVaccineDate(date);
                content.setVaccineName(receivedVaccine.getVaccineName());
                baseVaccineArrayList.add(content);
            }
        }
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> callBack.updateVaccineData(baseVaccineArrayList));
        appExecutors.diskIO().execute(runnable);
    }

    private VaccineGroup getVaccineGroupNameByVaccine(String name, List<VaccineGroup> vaccineGroups) {
        for (VaccineGroup vaccineGroup : vaccineGroups) {

            for (Vaccine vaccine : vaccineGroup.vaccines) {
                if (vaccine.name.equalsIgnoreCase(name)) {
                    return vaccineGroup;
                }
            }

        }
        return null;
    }

    @Override
    public void fetchGrowthNutritionData(CommonPersonObjectClient commonPersonObjectClient, final ChildMedicalHistoryContract.InteractorCallBack callBack) {
        List<ServiceRecord> serviceRecordList = new ArrayList<>();
        Long serviceId = 1L;

        //fetchbfdata()
        List<Visit> visits = visitRepository.getVisits(commonPersonObjectClient.getCaseId(), Constants.EventType.EXCLUSIVE_BREASTFEEDING);

        for (Visit visit : visits) {
            ServiceRecord serviceRecord = new ServiceRecord();
            List<VisitDetail> visitDetails = visitDetailsRepository.getVisits(visit.getVisitId());
            if (visitDetails != null && visitDetails.size() > 0) {

                serviceRecord.setType(Constants.EventType.EXCLUSIVE_BREASTFEEDING);
                serviceRecord.setName(visitDetails.get(0).getPreProcessedJson());
                serviceRecord.setValue(visitDetails.get(0).getHumanReadable());
                serviceRecord.setDate(visitDetails.get(0).getCreatedAt());
                serviceRecord.setRecurringServiceId(serviceId);
                serviceRecordList.add(serviceRecord);
                serviceId++;
            }

        }
        //fetchMNp data()
        List<Visit> mnpVisit = visitRepository.getVisits(commonPersonObjectClient.getCaseId(), Constants.EventType.MNP);

        for (Visit visit : mnpVisit) {
            ServiceRecord serviceRecord = new ServiceRecord();
            List<VisitDetail> visitDetails = visitDetailsRepository.getVisits(visit.getVisitId());
            if (visitDetails != null && visitDetails.size() > 0) {

                serviceRecord.setType(Constants.EventType.MNP);
                serviceRecord.setName(visitDetails.get(0).getHumanReadable());
                serviceRecord.setDate(visitDetails.get(0).getCreatedAt());
                serviceRecord.setRecurringServiceId(serviceId);
                serviceRecordList.add(serviceRecord);
                serviceId++;
            }

        }
        //fetvitamindata
        List<Visit> vitaminVisit = visitRepository.getVisits(commonPersonObjectClient.getCaseId(), Constants.EventType.VITAMIN_A);

        for (Visit visit : vitaminVisit) {
            ServiceRecord serviceRecord = new ServiceRecord();
            List<VisitDetail> visitDetails = visitDetailsRepository.getVisits(visit.getVisitId());
            if (visitDetails != null && visitDetails.size() > 0) {

                serviceRecord.setType(Constants.EventType.VITAMIN_A);
                serviceRecord.setName(visitDetails.get(0).getDetails());
                serviceRecord.setDate(visitDetails.get(0).getCreatedAt());
                serviceRecord.setRecurringServiceId(serviceId);
                serviceRecordList.add(serviceRecord);
                serviceId++;
            }

        }
        //fetch deworming
        List<Visit> dewormingVisit = visitRepository.getVisits(commonPersonObjectClient.getCaseId(), Constants.EventType.DEWORMING);

        for (Visit visit : dewormingVisit) {
            ServiceRecord serviceRecord = new ServiceRecord();
            List<VisitDetail> visitDetails = visitDetailsRepository.getVisits(visit.getVisitId());
            if (visitDetails != null && visitDetails.size() > 0) {

                serviceRecord.setType(Constants.EventType.DEWORMING);
                serviceRecord.setName(visitDetails.get(0).getDetails());
                serviceRecord.setDate(visitDetails.get(0).getCreatedAt());
                serviceRecord.setRecurringServiceId(serviceId);
                serviceRecordList.add(serviceRecord);
                serviceId++;
            }

        }


        baseServiceArrayList.clear();
        if (serviceRecordList.size() > 0) {
            Collections.sort(serviceRecordList, (serviceRecord1, serviceRecord2) -> {
                if (serviceRecord1.getRecurringServiceId() < serviceRecord2.getRecurringServiceId()) {
                    return -1;
                } else if (serviceRecord1.getRecurringServiceId() > serviceRecord2.getRecurringServiceId()) {
                    return 1;
                }
                return 0;
            });
        }
        //adding exclusive breast feeding initial value from child form some country it has an option to display
        ServiceRecord initialServiceRecord = new ServiceRecord();
        String initialFeedingValue = org.smartregister.util.Utils.getValue(commonPersonObjectClient.getColumnmaps(), ChildDBConstants.KEY.CHILD_BF_HR, true);
        if (!TextUtils.isEmpty(initialFeedingValue)) {
            initialServiceRecord.setType(Constants.EventType.EXCLUSIVE_BREASTFEEDING);
            initialServiceRecord.setName(ChildDBConstants.KEY.CHILD_BF_HR);
            initialServiceRecord.setValue(Utils.getYesNoAsLanguageSpecific(getContext(), initialFeedingValue));
            serviceRecordList.add(0, initialServiceRecord);
        }

        String lastType = "";
        for (ServiceRecord serviceRecord : serviceRecordList) {
            if (serviceRecord.getType().equalsIgnoreCase(Constants.EventType.MNP))
                continue;
            if (!serviceRecord.getType().equalsIgnoreCase(lastType)) {
                if (!TextUtils.isEmpty(lastType)) {
                    ServiceLine serviceLine = new ServiceLine();
                    baseServiceArrayList.add(serviceLine);
                }
                ServiceHeader serviceHeader = new ServiceHeader();
                serviceHeader.setServiceHeaderName(getServiceTypeLanguageSpecific(getContext(), serviceRecord.getType()));
                baseServiceArrayList.add(serviceHeader);
                ServiceContent content = new ServiceContent();
                addContent(content, serviceRecord);
                baseServiceArrayList.add(content);
                lastType = serviceRecord.getType();
            } else {
                ServiceContent content = new ServiceContent();
                addContent(content, serviceRecord);
                baseServiceArrayList.add(content);
            }
        }
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> callBack.updateGrowthNutrition(baseServiceArrayList));
        appExecutors.diskIO().execute(runnable);
    }

    public static String getServiceTypeLanguageSpecific(Context context, String value) {
        if (value.equalsIgnoreCase(Constants.EventType.EXCLUSIVE_BREASTFEEDING)) {
            return context.getString(R.string.exclusive_breastfeeding);
        } else if (value.equalsIgnoreCase(Constants.EventType.VITAMIN_A)) {
            return context.getString(R.string.vitamin_a);
        } else if (value.equalsIgnoreCase(Constants.EventType.DEWORMING)) {
            return context.getString(R.string.deworming);
        }
        return value;
    }

    @Override
    public void fetchDietaryData(CommonPersonObjectClient commonPersonObjectClient, final ChildMedicalHistoryContract.InteractorCallBack callBack) {
        List<Visit> homeVisitDietary = visitRepository.getUniqueDayLatestThreeVisits(commonPersonObjectClient.getCaseId(), Constants.EventType.MINIMUM_DIETARY_DIVERSITY);
        final ArrayList<BaseService> baseServiceArrayList = new ArrayList<>();
        if (homeVisitDietary.size() > 0) {

            ServiceLine serviceLine = new ServiceLine();
            baseServiceArrayList.add(serviceLine);

            ServiceHeader serviceHeader = new ServiceHeader();
            serviceHeader.setServiceHeaderName(getContext().getString(R.string.minimum_dietary_title));
            baseServiceArrayList.add(serviceHeader);

            for (Visit homeVisitServiceDataModel : homeVisitDietary) {
                List<VisitDetail> visitDetails = visitDetailsRepository.getVisits(homeVisitServiceDataModel.getVisitId());
                if (visitDetails != null && visitDetails.size() > 0) {
                    String dietaryText = visitDetails.get(0).getHumanReadable();
                    dietaryText = TextUtils.isEmpty(dietaryText) ? visitDetails.get(0).getDetails() : dietaryText;
                    if (!TextUtils.isEmpty(dietaryText)) {
                        ServiceContent content = new ServiceContent();
                        String date = DATE_FORMAT.format(homeVisitServiceDataModel.getDate());
                        content.setServiceName(Utils.getYesNoAsLanguageSpecific(getContext(), dietaryText) + " - " + getContext().getString(R.string.done) + " " + date);
                        baseServiceArrayList.add(content);
                    }
                }
            }
        }
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> callBack.updateDietaryData(baseServiceArrayList));
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void fetchMuacData(CommonPersonObjectClient commonPersonObjectClient, final ChildMedicalHistoryContract.InteractorCallBack callBack) {
        List<Visit> homeVisitMUAC = visitRepository.getUniqueDayLatestThreeVisits(commonPersonObjectClient.getCaseId(), Constants.EventType.MUAC);
        final ArrayList<BaseService> baseServiceArrayList = new ArrayList<>();
        if (homeVisitMUAC.size() > 0) {

            ServiceLine serviceLine = new ServiceLine();
            baseServiceArrayList.add(serviceLine);

            ServiceHeader serviceHeader = new ServiceHeader();
            serviceHeader.setServiceHeaderName(getContext().getString(R.string.muac_title));
            baseServiceArrayList.add(serviceHeader);

            for (Visit homeVisitServiceDataModel : homeVisitMUAC) {
                List<VisitDetail> visitDetails = visitDetailsRepository.getVisits(homeVisitServiceDataModel.getVisitId());
                if (visitDetails != null && visitDetails.size() > 0) {
                    String muacText = visitDetails.get(0).getHumanReadable();
                    muacText = TextUtils.isEmpty(muacText) ? visitDetails.get(0).getDetails() : muacText;
                    if (!TextUtils.isEmpty(muacText)) {
                        ServiceContent content = new ServiceContent();
                        String date = DATE_FORMAT.format(homeVisitServiceDataModel.getDate());
                        content.setServiceName(muacText + " - " + getContext().getString(R.string.done) + " " + date);
                        baseServiceArrayList.add(content);
                    }
                }

            }
        }
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> callBack.updateMuacData(baseServiceArrayList));
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void fetchLLitnData(CommonPersonObjectClient commonPersonObjectClient, final ChildMedicalHistoryContract.InteractorCallBack callBack) {
        List<Visit> homeVisitLlitn = visitRepository.getUniqueDayLatestThreeVisits(commonPersonObjectClient.getCaseId(), Constants.EventType.LLITN);

        final ArrayList<BaseService> baseServiceArrayList = new ArrayList<>();
        if (homeVisitLlitn.size() > 0) {

            for (Visit homeVisitServiceDataModel : homeVisitLlitn) {
                List<VisitDetail> visitDetails = visitDetailsRepository.getVisits(homeVisitServiceDataModel.getVisitId());
                if (visitDetails != null && visitDetails.size() > 0) {
                    ServiceContent content = new ServiceContent();
                    String date = DATE_FORMAT.format(homeVisitServiceDataModel.getDate());
                    content.setServiceName(Utils.getYesNoAsLanguageSpecific(getContext(), visitDetails.get(0).getHumanReadable()) + " " + getContext().getString(R.string.on) + " " + date);
                    baseServiceArrayList.add(content);
                }
            }
        }
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> callBack.updateLLitnDataData(baseServiceArrayList));
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void fetchEcdData(CommonPersonObjectClient commonPersonObjectClient, final ChildMedicalHistoryContract.InteractorCallBack callBack) {
        String dateOfBirth = org.smartregister.util.Utils.getValue(commonPersonObjectClient.getColumnmaps(), DBConstants.KEY.DOB, false);
        List<Visit> homeVisitEcd = visitRepository.getUniqueDayLatestThreeVisits(commonPersonObjectClient.getCaseId(), Constants.EventType.ECD);
        final ArrayList<BaseService> baseServiceArrayList = new ArrayList<>();
        if (homeVisitEcd.size() > 0) {


            for (Visit homeVisitServiceDataModel : homeVisitEcd) {
                ServiceTask serviceTask = ChildUtils.createECDTaskFromEvent(getContext(), TaskServiceCalculate.TASK_TYPE.ECD.name(),
                        homeVisitServiceDataModel.getJson(),
                        getContext().getString(R.string.ecd_title));

                String date = DATE_FORMAT.format(homeVisitServiceDataModel.getDate());
                String difference = ChildUtils.getDurationFromTwoDate(Utils.dobStringToDate(dateOfBirth), homeVisitServiceDataModel.getDate());
                ServiceHeader serviceHeader = new ServiceHeader();
                serviceHeader.setServiceHeaderName(date + " (" + org.smartregister.family.util.Utils.getTranslatedDate(difference, getContext()) + ")");
                baseServiceArrayList.add(serviceHeader);
                String[] label = ChildUtils.splitStringByNewline(serviceTask.getTaskLabel());
                for (String s : label) {
                    ServiceContent content = new ServiceContent();
                    content.setServiceName(s);
                    baseServiceArrayList.add(content);
                }
            }
        }
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> callBack.updateEcdDataData(baseServiceArrayList));
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void fetchFullyImmunizationData(String dob, Map<String, Date> recievedVaccines, final ChildMedicalHistoryContract.InteractorCallBack callBack) {

        List<String> vacList = new ArrayList<>();
        for (String name : recievedVaccines.keySet()) {
            String trimLower = name.replace(" ", "").replace("_", "").toLowerCase();
            vacList.add(trimLower);
        }
        final String fullyImmunizationText = ChildUtils.isFullyImmunized(vacList);
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> callBack.updateFullyImmunization(fullyImmunizationText));
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        Timber.v("onDestroy");
    }

    private void addContent(ServiceContent content, ServiceRecord serviceRecord) {
        if (serviceRecord.getType().equalsIgnoreCase(Constants.EventType.EXCLUSIVE_BREASTFEEDING)) {
            //String[] values = serviceRecord.getValue().split("_");
            if (serviceRecord.getName().equalsIgnoreCase(ChildDBConstants.KEY.CHILD_BF_HR)) {
                content.setServiceName(getContext().getString(R.string.initial_breastfeed_value, WordUtils.capitalize(serviceRecord.getValue())));
            } else if (serviceRecord.getName().equalsIgnoreCase("exclusive_breastfeeding0")) {
                content.setServiceName(getContext().getString(R.string.zero_month_breastfeed_value, WordUtils.capitalize(serviceRecord.getValue())));
            } else {
                Object[] objects = ChildUtils.getStringWithNumber(serviceRecord.getName());
                String name = (String) objects[0];
                String number = (String) objects[1];
                content.setServiceName(name.replace("_", "") + " (" + number + "" + getContext().getString(R.string.abbrv_months) + "): " + serviceRecord.getValue());
            }

        } else {
            String date = DATE_FORMAT.format(serviceRecord.getDate());
            content.setServiceName(serviceRecord.getName() + " -  " + getContext().getString(R.string.done) + " " + date);
        }
    }

    public Context getContext() {
        return context;
    }

    public interface Flavor {
    }
}