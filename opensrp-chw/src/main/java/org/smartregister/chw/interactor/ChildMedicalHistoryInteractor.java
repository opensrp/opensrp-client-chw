package org.smartregister.chw.interactor;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.ChildMedicalHistoryContract;
import org.smartregister.chw.fragment.GrowthNutritionInputFragment;
import org.smartregister.chw.repository.HomeVisitServiceRepository;
import org.smartregister.chw.util.BaseService;
import org.smartregister.chw.util.BaseVaccine;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.HomeVisitServiceDataModel;
import org.smartregister.chw.util.ReceivedVaccine;
import org.smartregister.chw.util.ServiceContent;
import org.smartregister.chw.util.ServiceHeader;
import org.smartregister.chw.util.ServiceLine;
import org.smartregister.chw.util.ServiceTask;
import org.smartregister.chw.util.TaskServiceCalculate;
import org.smartregister.chw.util.Utils;
import org.smartregister.chw.util.VaccineContent;
import org.smartregister.chw.util.VaccineHeader;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.util.VaccinateActionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.smartregister.chw.util.ChildDBConstants.KEY.BIRTH_CERT;
import static org.smartregister.chw.util.ChildDBConstants.KEY.BIRTH_CERT_ISSUE_DATE;
import static org.smartregister.chw.util.ChildDBConstants.KEY.BIRTH_CERT_NOTIFIICATION;
import static org.smartregister.chw.util.ChildDBConstants.KEY.BIRTH_CERT_NUMBER;
import static org.smartregister.chw.util.ChildDBConstants.KEY.ILLNESS_ACTION;
import static org.smartregister.chw.util.ChildDBConstants.KEY.ILLNESS_DATE;
import static org.smartregister.chw.util.ChildDBConstants.KEY.ILLNESS_DESCRIPTION;
import static org.smartregister.chw.util.ChildDBConstants.KEY.VACCINE_CARD;
import static org.smartregister.chw.util.ChildUtils.fixVaccineCasing;
import static org.smartregister.util.Utils.getValue;

public class ChildMedicalHistoryInteractor implements ChildMedicalHistoryContract.Interactor {
    private AppExecutors appExecutors;
    //public List<HomeVisitServiceDataModel> homeVisitServiceDataModels = new ArrayList<>();

    public  ArrayList<BaseService> baseServiceArrayList = new ArrayList<>();

    @VisibleForTesting
    ChildMedicalHistoryInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }
    private HomeVisitServiceRepository homeVisitServiceRepository;

    public ChildMedicalHistoryInteractor() {
        this(new AppExecutors());
        homeVisitServiceRepository =  ChwApplication.getHomeVisitServiceRepository();
    }


//    @Override
//    public void generateHomeVisitServiceList(long lastHomeVisit) {
//        HomeVisit homeVisit = ChwApplication.homeVisitRepository().findByDate(lastHomeVisit);
//        if(homeVisit!=null){
//            homeVisitServiceDataModels = homeVisitServiceRepository.getHomeVisitServiceList(homeVisit.getHomeVisitId());
//        }
//    }

    @Override
    public void fetchFullyImmunizationData(String dob, Map<String, Date> recievedVaccines, final ChildMedicalHistoryContract.InteractorCallBack callBack) {

        List<String> vacList = new ArrayList<>();
        for (String name : recievedVaccines.keySet()) {
            String trimLower = name.replace(" ", "").toLowerCase();
            vacList.add(trimLower);
        }
        final String fullyImmunizationText = ChildUtils.isFullyImmunized(vacList);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.updateFullyImmunization(fullyImmunizationText);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void fetchBirthCertificateData(CommonPersonObjectClient commonPersonObjectClient, final ChildMedicalHistoryContract.InteractorCallBack callBack) {

        String birthCert = getValue(commonPersonObjectClient.getColumnmaps(), BIRTH_CERT, true);
        final ArrayList<String> birthCertificationContent = new ArrayList<>();
        if (!TextUtils.isEmpty(birthCert) && birthCert.equalsIgnoreCase("Yes")) {
            birthCertificationContent.add(getContext().getString(R.string.birth_cert_value, birthCert));
            birthCertificationContent.add(getContext().getString(R.string.birth_cert_date, getValue(commonPersonObjectClient.getColumnmaps(), BIRTH_CERT_ISSUE_DATE, true)));
            birthCertificationContent.add(getContext().getString(R.string.birth_cert_number, getValue(commonPersonObjectClient.getColumnmaps(), BIRTH_CERT_NUMBER, true)));

        } else if (!TextUtils.isEmpty(birthCert) && birthCert.equalsIgnoreCase("No")) {
            birthCertificationContent.add(getContext().getString(R.string.birth_cert_value, birthCert));
            String notification = getValue(commonPersonObjectClient.getColumnmaps(), BIRTH_CERT_NOTIFIICATION, true);

            if (!TextUtils.isEmpty(notification) && notification.equalsIgnoreCase("Yes")) {
                birthCertificationContent.add(getContext().getString(R.string.birth_cert_notification, "Yes"));
                birthCertificationContent.add(getContext().getString(R.string.birth_cert_note_1));
            } else if (!TextUtils.isEmpty(notification) && notification.equalsIgnoreCase("No")) {
                birthCertificationContent.add(getContext().getString(R.string.birth_cert_notification, "No"));
                birthCertificationContent.add(getContext().getString(R.string.birth_cert_note_2));
            }
//            else {
//                birthCertificationContent.add(getContext().getString(R.string.birth_cert_notification,"No"));
//            }
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.updateBirthCertification(birthCertificationContent);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void fetchIllnessData(CommonPersonObjectClient commonPersonObjectClient, final ChildMedicalHistoryContract.InteractorCallBack callBack) {

        final ArrayList<String> illnessContent = new ArrayList<>();

        String illnessDate = getValue(commonPersonObjectClient.getColumnmaps(), ILLNESS_DATE, true);
        if (!TextUtils.isEmpty(illnessDate)) {
            String illnessDescription = getValue(commonPersonObjectClient.getColumnmaps(), ILLNESS_DESCRIPTION, true);
            String illnessAction = getValue(commonPersonObjectClient.getColumnmaps(), ILLNESS_ACTION, true);
            illnessContent.add(getContext().getString(R.string.illness_date_with_value, illnessDate));
            illnessContent.add(getContext().getString(R.string.illness_des_with_value, illnessDescription));
            illnessContent.add(getContext().getString(R.string.illness_action_value, illnessAction));

        }
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.updateIllnessData(illnessContent);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable2);
        final String vaccineCard = getValue(commonPersonObjectClient.getColumnmaps(), VACCINE_CARD, true);
        if (!TextUtils.isEmpty(vaccineCard)) {
            Runnable runnable3 = new Runnable() {
                @Override
                public void run() {
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callBack.updateVaccineCard(vaccineCard);
                        }
                    });
                }
            };
            appExecutors.diskIO().execute(runnable3);
        }
    }

    @Override
    public void setInitialVaccineList(Map<String, Date> recievedVaccines, final ChildMedicalHistoryContract.InteractorCallBack callBack) {

        ArrayList<ReceivedVaccine> receivedVaccineArrayList = new ArrayList<>();
        final ArrayList<BaseVaccine> baseVaccineArrayList = new ArrayList<>();
        List<VaccineRepo.Vaccine> vList = Arrays.asList(VaccineRepo.Vaccine.values());
        for (String name : recievedVaccines.keySet()) {
            for (VaccineRepo.Vaccine vaccine : vList) {
                if (name.equalsIgnoreCase(vaccine.display())) {
                    String stateKey = VaccinateActionUtils.stateKey(vaccine);
                    ReceivedVaccine receivedVaccine = new ReceivedVaccine();
                    receivedVaccine.setVaccineCategory(stateKey);
                    receivedVaccine.setVaccineName(fixVaccineCasing(name).replace("MEASLES", "MCV"));
                    if (receivedVaccine.getVaccineName().contains("MEASLES")) {
                        receivedVaccine.setVaccineName(receivedVaccine.getVaccineName().replace("MEASLES", "MCV"));
                    }
                    receivedVaccine.setVaccineDate(recievedVaccines.get(name));
                    receivedVaccine.setVaccineIndex(vList.indexOf(vaccine));
                    receivedVaccineArrayList.add(receivedVaccine);
                }
            }
        }
        if (receivedVaccineArrayList.size() > 0) {
            Collections.sort(receivedVaccineArrayList, new Comparator<ReceivedVaccine>() {
                public int compare(ReceivedVaccine vaccine1, ReceivedVaccine vaccine2) {
                    if (vaccine1.getVaccineIndex() < vaccine2.getVaccineIndex()) {
                        return -1;
                    } else if (vaccine1.getVaccineIndex() > vaccine2.getVaccineIndex()) {
                        return 1;
                    }
                    return 0;
                }
            });
        }

        String lastCategory = "";
        for (ReceivedVaccine receivedVaccine : receivedVaccineArrayList) {
            if (!receivedVaccine.getVaccineCategory().equalsIgnoreCase(lastCategory)) {
                VaccineHeader vaccineHeader = new VaccineHeader();
                lastCategory = receivedVaccine.getVaccineCategory();
                vaccineHeader.setVaccineHeaderName(receivedVaccine.getVaccineCategory());
                baseVaccineArrayList.add(vaccineHeader);
                VaccineContent content = new VaccineContent();
                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
                String date = DATE_FORMAT.format(receivedVaccine.getVaccineDate());
                content.setVaccineDate(date);
                content.setVaccineName(receivedVaccine.getVaccineName());
                baseVaccineArrayList.add(content);
            } else {
                VaccineContent content = new VaccineContent();
                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
                String date = DATE_FORMAT.format(receivedVaccine.getVaccineDate());
                content.setVaccineDate(date);
                content.setVaccineName(receivedVaccine.getVaccineName());
                baseVaccineArrayList.add(content);
            }
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.updateVaccineData(baseVaccineArrayList);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void fetchGrowthNutritionData(CommonPersonObjectClient commonPersonObjectClient, final ChildMedicalHistoryContract.InteractorCallBack callBack) {
        String initialFeedingValue = getValue(commonPersonObjectClient.getColumnmaps(), ChildDBConstants.KEY.CHILD_BF_HR, true);
        RecurringServiceRecordRepository recurringServiceRecordRepository = ImmunizationLibrary.getInstance().recurringServiceRecordRepository();
        List<ServiceRecord> serviceRecordList = recurringServiceRecordRepository.findByEntityId(commonPersonObjectClient.entityId());
        baseServiceArrayList.clear();
        if (serviceRecordList.size() > 0) {
            Collections.sort(serviceRecordList, new Comparator<ServiceRecord>() {
                public int compare(ServiceRecord serviceRecord1, ServiceRecord serviceRecord2) {
                    if (serviceRecord1.getRecurringServiceId() < serviceRecord2.getRecurringServiceId()) {
                        return -1;
                    } else if (serviceRecord1.getRecurringServiceId() > serviceRecord2.getRecurringServiceId()) {
                        return 1;
                    }
                    return 0;
                }
            });
        }
        //adding exclusive breast feeding initial value from child form
        ServiceRecord initialServiceRecord = new ServiceRecord();
        initialServiceRecord.setType(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue());
        initialServiceRecord.setName(ChildDBConstants.KEY.CHILD_BF_HR);
        initialServiceRecord.setValue(initialFeedingValue);
        serviceRecordList.add(0, initialServiceRecord);
        String lastType = "";
        for (ServiceRecord serviceRecord : serviceRecordList) {
            if (!serviceRecord.getType().equalsIgnoreCase(lastType)) {
                if(!TextUtils.isEmpty(lastType)){
                    ServiceLine serviceLine = new ServiceLine();
                    baseServiceArrayList.add(serviceLine);
                }
                ServiceHeader serviceHeader = new ServiceHeader();
                serviceHeader.setServiceHeaderName(serviceRecord.getType());
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
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.updateGrowthNutrition(baseServiceArrayList);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void fetchDietaryData(CommonPersonObjectClient commonPersonObjectClient, final ChildMedicalHistoryContract.InteractorCallBack callBack) {
        List<HomeVisitServiceDataModel> homeVisitDietary = homeVisitServiceRepository.getLatestThreeEntry(Constants.EventType.MINIMUM_DIETARY_DIVERSITY);
        final ArrayList<BaseService> baseServiceArrayList = new ArrayList<>();
        if(homeVisitDietary.size()>0) {

            ServiceLine serviceLine = new ServiceLine();
            baseServiceArrayList.add(serviceLine);

            ServiceHeader serviceHeader = new ServiceHeader();
            serviceHeader.setServiceHeaderName(getContext().getString(R.string.minimum_dietary_title));
            baseServiceArrayList.add(serviceHeader);
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

            for (HomeVisitServiceDataModel homeVisitServiceDataModel : homeVisitDietary) {
                ServiceTask serviceTask = ChildUtils.createServiceTaskFromEvent(TaskServiceCalculate.TASK_TYPE.Minimum_dietary.name(),
                        homeVisitServiceDataModel.getHomeVisitDetails(), getContext().getString(R.string.minimum_dietary_title),
                        Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.TASK_MINIMUM_DIETARY);
                if(serviceTask.getTaskLabel()!=null){
                    ServiceContent content = new ServiceContent();
                    String date = DATE_FORMAT.format(homeVisitServiceDataModel.getHomeVisitDate());
                    content.setServiceName(serviceTask.getTaskLabel() + " - done " + date);
                    baseServiceArrayList.add(content);
                }

            }
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.updateDietaryData(baseServiceArrayList);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void fetchMuacData(CommonPersonObjectClient commonPersonObjectClient,final ChildMedicalHistoryContract.InteractorCallBack callBack) {

        List<HomeVisitServiceDataModel> homeVisitMUAC = homeVisitServiceRepository.getLatestThreeEntry(Constants.EventType.MUAC);
        final ArrayList<BaseService> baseServiceArrayList = new ArrayList<>();
        if(homeVisitMUAC.size()>0) {

            ServiceLine serviceLine = new ServiceLine();
            baseServiceArrayList.add(serviceLine);

            ServiceHeader serviceHeader = new ServiceHeader();
            serviceHeader.setServiceHeaderName(getContext().getString(R.string.muac_title));
            baseServiceArrayList.add(serviceHeader);
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

            for (HomeVisitServiceDataModel homeVisitServiceDataModel : homeVisitMUAC) {
                ServiceTask serviceTask = ChildUtils.createServiceTaskFromEvent(TaskServiceCalculate.TASK_TYPE.MUAC.name(),
                        homeVisitServiceDataModel.getHomeVisitDetails(), getContext().getString(R.string.muac_title),
                        Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.TASK_MUAC);
                if(serviceTask.getTaskLabel()!=null){
                    ServiceContent content = new ServiceContent();
                    String date = DATE_FORMAT.format(homeVisitServiceDataModel.getHomeVisitDate());
                    content.setServiceName(serviceTask.getTaskLabel() + " - done " + date);
                    baseServiceArrayList.add(content);
                }

            }
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.updateMuacData(baseServiceArrayList);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void fetchLLitnData(CommonPersonObjectClient commonPersonObjectClient,final ChildMedicalHistoryContract.InteractorCallBack callBack) {

        List<HomeVisitServiceDataModel> homeVisitLlitn = homeVisitServiceRepository.getLatestThreeEntry(Constants.EventType.LLITN);
        final ArrayList<BaseService> baseServiceArrayList = new ArrayList<>();
        if(homeVisitLlitn.size()>0) {

            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

            for (HomeVisitServiceDataModel homeVisitServiceDataModel : homeVisitLlitn) {
                ServiceTask serviceTask = ChildUtils.createServiceTaskFromEvent(TaskServiceCalculate.TASK_TYPE.LLITN.name(),
                        homeVisitServiceDataModel.getHomeVisitDetails(), getContext().getString(R.string.llitn_title),
                        Constants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.TASK_LLITN);
                ServiceContent content = new ServiceContent();
                String date = DATE_FORMAT.format(homeVisitServiceDataModel.getHomeVisitDate());
                content.setServiceName(serviceTask.getTaskLabel() + " on " + date);
                baseServiceArrayList.add(content);
            }
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.updateLLitnDataData(baseServiceArrayList);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void fetchEcdData(CommonPersonObjectClient commonPersonObjectClient, final ChildMedicalHistoryContract.InteractorCallBack callBack) {
        String dateOfBirth = getValue(commonPersonObjectClient.getColumnmaps(), DBConstants.KEY.DOB, false);

        List<HomeVisitServiceDataModel> homeVisitEcd = homeVisitServiceRepository.getLatestThreeEntry(Constants.EventType.ECD);
        final ArrayList<BaseService> baseServiceArrayList = new ArrayList<>();
        if(homeVisitEcd.size()>0) {

            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

            for (HomeVisitServiceDataModel homeVisitServiceDataModel : homeVisitEcd) {
                ServiceTask serviceTask = ChildUtils.createECDTaskFromEvent(getContext(),TaskServiceCalculate.TASK_TYPE.ECD.name(),
                        homeVisitServiceDataModel.getHomeVisitDetails(),
                        getContext().getString(R.string.ecd_title));

                String date = DATE_FORMAT.format(homeVisitServiceDataModel.getHomeVisitDate());
                String difference = ChildUtils.getDurationFromTwoDate(Utils.dobStringToDate(dateOfBirth),homeVisitServiceDataModel.getHomeVisitDate());
                ServiceHeader serviceHeader = new ServiceHeader();
                serviceHeader.setServiceHeaderName(date+" ("+difference+")");
                baseServiceArrayList.add(serviceHeader);
                String[] label = ChildUtils.splitStringByNewline(serviceTask.getTaskLabel());
                for(String s : label){
                    ServiceContent content = new ServiceContent();
                    content.setServiceName(s);
                    baseServiceArrayList.add(content);
                }

            }
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.updateEcdDataData(baseServiceArrayList);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    private void addContent(ServiceContent content, ServiceRecord serviceRecord) {
        if (serviceRecord.getType().equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue())) {
            //String[] values = serviceRecord.getValue().split("_");
            if (serviceRecord.getName().equalsIgnoreCase(ChildDBConstants.KEY.CHILD_BF_HR)) {
                content.setServiceName(getContext().getString(R.string.initial_breastfeed_value, WordUtils.capitalize(serviceRecord.getValue())));
            } else if (serviceRecord.getName().equalsIgnoreCase("exclusive breastfeeding0")) {
                content.setServiceName(getContext().getString(R.string.zero_month_breastfeed_value, WordUtils.capitalize(serviceRecord.getValue())));
            } else {
                Object[] objects = ChildUtils.getStringWithNumber(serviceRecord.getName());
                String name = (String) objects[0];
                String number = (String) objects[1];
                content.setServiceName(name + " (" + number + "m): " + serviceRecord.getValue());
            }

        } else {
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
            String date = DATE_FORMAT.format(serviceRecord.getDate());
            content.setServiceName(serviceRecord.getName() + " - done " + date);
        }
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

    }

    public Context getContext() {
        return ChwApplication.getInstance().getApplicationContext();
    }

    public interface Flavor {
    }
}
