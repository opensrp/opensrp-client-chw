package org.smartgresiter.wcaro.interactor;

import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import org.smartgresiter.wcaro.contract.MedicalHistoryContract;
import org.smartgresiter.wcaro.fragment.GrowthNutritionInputFragment;
import org.smartgresiter.wcaro.util.BaseService;
import org.smartgresiter.wcaro.util.BaseVaccine;
import org.smartgresiter.wcaro.util.BirthCertification;
import org.smartgresiter.wcaro.util.ChildUtils;
import org.smartgresiter.wcaro.util.Constants;
import org.smartgresiter.wcaro.util.GrowthNutrition;
import org.smartgresiter.wcaro.util.ObsIllness;
import org.smartgresiter.wcaro.util.ReceivedVaccine;
import org.smartgresiter.wcaro.util.ServiceContent;
import org.smartgresiter.wcaro.util.ServiceHeader;
import org.smartgresiter.wcaro.util.VaccineContent;
import org.smartgresiter.wcaro.util.VaccineHeader;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.AppExecutors;
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

import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.BIRTH_CERT;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.BIRTH_CERT_CAREGIVER;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.BIRTH_CERT_INSTRUC;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.BIRTH_CERT_ISSUE_DATE;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.BIRTH_CERT_NOTIFIICATION;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.BIRTH_CERT_NUMBER;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.ILLNESS_ACTION;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.ILLNESS_DATE;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.ILLNESS_DESCRIPTION;
import static org.smartregister.util.Utils.fillValue;
import static org.smartregister.util.Utils.getValue;

public class MedicalHistoryInteractor implements MedicalHistoryContract.Interactor {
    private AppExecutors appExecutors;

    @VisibleForTesting
    MedicalHistoryInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public MedicalHistoryInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void fetchFullyImmunizationData(String dob, Map<String, Date> recievedVaccines, final MedicalHistoryContract.InteractorCallBack callBack) {
        String dobString = dob.contains("y") ? dob.substring(0, dob.indexOf("y")) : dob;
        int year = 0;
        try {
            year = Integer.parseInt(dobString);
        } catch (Exception e) {

        }
        List<String> vacList = new ArrayList<>();
        for (String name : recievedVaccines.keySet()) {
            String trimLower = name.replace(" ", "").toLowerCase();
            vacList.add(trimLower);
        }
        final String fullyImmunizationText = ChildUtils.isFullyImmunized(year, vacList);
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
    public void fetchBirthAndIllnessData(CommonPersonObjectClient commonPersonObjectClient,final MedicalHistoryContract.InteractorCallBack callBack) {
        String birthCert=getValue(commonPersonObjectClient.getColumnmaps(), BIRTH_CERT, true);
        final ArrayList<BirthCertification> birthCertificationArrayList=new ArrayList<>();
        if(TextUtils.isEmpty(birthCert) && birthCert.equalsIgnoreCase("Yes")){
            BirthCertification birthCertification=new BirthCertification();
            birthCertification.setHasBirthCert(birthCert);
            birthCertification.setBirthCertIssueDate(getValue(commonPersonObjectClient.getColumnmaps(),BIRTH_CERT_ISSUE_DATE,true));
            birthCertification.setBirthCertNumber(getValue(commonPersonObjectClient.getColumnmaps(),BIRTH_CERT_NUMBER,true));
            birthCertificationArrayList.add(birthCertification);
        }else if(TextUtils.isEmpty(birthCert) && birthCert.equalsIgnoreCase("No")){
            BirthCertification birthCertification=new BirthCertification();
            birthCertification.setHasBirthCert(birthCert);
            birthCertification.setBirthCertNotification(getValue(commonPersonObjectClient.getColumnmaps(),BIRTH_CERT_NOTIFIICATION,true));
            birthCertification.setInstruction(getValue(commonPersonObjectClient.getColumnmaps(),BIRTH_CERT_INSTRUC,true));
            birthCertification.setCaregiverInstruction(getValue(commonPersonObjectClient.getColumnmaps(),BIRTH_CERT_CAREGIVER,true));
            birthCertificationArrayList.add(birthCertification);
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.updateBirthCertification(birthCertificationArrayList);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
        String illnessDate=getValue(commonPersonObjectClient.getColumnmaps(), ILLNESS_DATE, true);
        String illnessDescription=getValue(commonPersonObjectClient.getColumnmaps(), ILLNESS_DESCRIPTION, true);
        String illnessAction=getValue(commonPersonObjectClient.getColumnmaps(), ILLNESS_ACTION, true);
        final ArrayList<ObsIllness> obsIllnessArrayList=new ArrayList<>();
        ObsIllness obsIllness=new ObsIllness();
        obsIllness.setIllnessDate(illnessDate);
        obsIllness.setDescription(illnessDescription);
        obsIllness.setActionTaken(illnessAction);
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.updateIllnessData(obsIllnessArrayList);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable2);

    }

    @Override
    public void setInitialVaccineList(Map<String, Date> recievedVaccines, final MedicalHistoryContract.InteractorCallBack callBack) {

        ArrayList<ReceivedVaccine> receivedVaccineArrayList = new ArrayList<>();
        final ArrayList<BaseVaccine> baseVaccineArrayList = new ArrayList<>();
        List<VaccineRepo.Vaccine> vList = Arrays.asList(VaccineRepo.Vaccine.values());
        for (String name : recievedVaccines.keySet()) {
            for (VaccineRepo.Vaccine vaccine : vList) {
                if (name.equalsIgnoreCase(vaccine.display())) {
                    String stateKey = VaccinateActionUtils.stateKey(vaccine);
                    ReceivedVaccine receivedVaccine = new ReceivedVaccine();
                    receivedVaccine.setVaccineCategory(stateKey);
                    receivedVaccine.setVaccineName(name);
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
    public void fetchGrowthNutritionData(String baseEntity, final MedicalHistoryContract.InteractorCallBack callBack) {
        final ArrayList<GrowthNutrition> growthNutritions = new ArrayList<>();
        RecurringServiceRecordRepository recurringServiceRecordRepository = ImmunizationLibrary.getInstance().recurringServiceRecordRepository();
        List<ServiceRecord> serviceRecordList = recurringServiceRecordRepository.findByEntityId(baseEntity);
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
        final ArrayList<BaseService> baseServiceArrayList = new ArrayList<>();
        String lastType = "";
        for (ServiceRecord serviceRecord : serviceRecordList) {
            if (!serviceRecord.getType().equalsIgnoreCase(lastType)) {
                ServiceHeader serviceHeader = new ServiceHeader();
                serviceHeader.setServiceHeaderName(serviceRecord.getType());
                baseServiceArrayList.add(serviceHeader);
                ServiceContent content = new ServiceContent();
                if (serviceRecord.getType().equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue())) {
                    String[] values = serviceRecord.getValue().split("_");
                    if (serviceRecord.getName().equalsIgnoreCase("exclusive breastfeeding0")) {
                        content.setServiceName("Early initiation breastfeeding: " + values[0]);
                    } else {
                        Object[] objects = ChildUtils.getStringWithNumber(serviceRecord.getName());
                        String name = (String) objects[0];
                        String number = (String) objects[1];
                        content.setServiceName(name + " (" + number + "m): " + values[0]);
                    }

                } else {
                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
                    String date = DATE_FORMAT.format(serviceRecord.getDate());
                    content.setServiceName(serviceRecord.getName() + " - " + date);
                }

                content.setType(serviceRecord.getType());
                baseServiceArrayList.add(content);
                lastType = serviceRecord.getType();
            } else {
                ServiceContent content = new ServiceContent();
                if (serviceRecord.getType().equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue())) {
                    String[] values = serviceRecord.getValue().split("_");
                    if (serviceRecord.getName().equalsIgnoreCase("exclusive breastfeeding0")) {
                        content.setServiceName("Early initiation breastfeeding: " + values[0]);
                    } else {
                        Object[] objects = ChildUtils.getStringWithNumber(serviceRecord.getName());
                        String name = (String) objects[0];
                        String number = (String) objects[1];
                        content.setServiceName(name + " (" + number + "m): " + values[0]);
                    }

                } else {
                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
                    String date = DATE_FORMAT.format(serviceRecord.getDate());
                    content.setServiceName(serviceRecord.getName() + " - " + date);
                }

                content.setType(serviceRecord.getType());
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
    public void onDestroy(boolean isChangingConfiguration) {

    }
}
