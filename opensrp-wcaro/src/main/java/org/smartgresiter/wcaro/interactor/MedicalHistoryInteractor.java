package org.smartgresiter.wcaro.interactor;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.contract.MedicalHistoryContract;
import org.smartgresiter.wcaro.fragment.GrowthNutritionInputFragment;
import org.smartgresiter.wcaro.util.BaseService;
import org.smartgresiter.wcaro.util.BaseVaccine;
import org.smartgresiter.wcaro.util.ChildUtils;
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
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.BIRTH_CERT_ISSUE_DATE;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.BIRTH_CERT_NOTIFIICATION;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.BIRTH_CERT_NUMBER;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.ILLNESS_ACTION;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.ILLNESS_DATE;
import static org.smartgresiter.wcaro.util.ChildDBConstants.KEY.ILLNESS_DESCRIPTION;
import static org.smartgresiter.wcaro.util.ChildUtils.fixVaccineCasing;
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
    public void fetchBirthAndIllnessData(CommonPersonObjectClient commonPersonObjectClient, final MedicalHistoryContract.InteractorCallBack callBack) {
        String birthCert = getValue(commonPersonObjectClient.getColumnmaps(), BIRTH_CERT, true);
        Context context= WcaroApplication.getInstance().getApplicationContext();
        final ArrayList<String> birthCertificationContent = new ArrayList<>();
        if (!TextUtils.isEmpty(birthCert) && birthCert.equalsIgnoreCase("Yes")) {
            birthCertificationContent.add(context.getString(R.string.birth_cert_value,birthCert));
            birthCertificationContent.add(context.getString(R.string.birth_cert_date,getValue(commonPersonObjectClient.getColumnmaps(), BIRTH_CERT_ISSUE_DATE, true)));
            birthCertificationContent.add(context.getString(R.string.birth_cert_number,getValue(commonPersonObjectClient.getColumnmaps(), BIRTH_CERT_NUMBER, true)));

        } else if (!TextUtils.isEmpty(birthCert) && birthCert.equalsIgnoreCase("No")) {
            birthCertificationContent.add(context.getString(R.string.birth_cert_value,birthCert));
            String notification = getValue(commonPersonObjectClient.getColumnmaps(), BIRTH_CERT_NOTIFIICATION, true);
            birthCertificationContent.add(context.getString(R.string.birth_cert_notification,notification));
            if (!TextUtils.isEmpty(notification) && notification.equalsIgnoreCase("Yes")) {
                birthCertificationContent.add(context.getString(R.string.birth_cert_note_1));
            } else if (!TextUtils.isEmpty(notification) && notification.equalsIgnoreCase("No")) {
                birthCertificationContent.add(context.getString(R.string.birth_cert_note_2));
            }
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
        final ArrayList<String> illnessContent = new ArrayList<>();

        String illnessDate = getValue(commonPersonObjectClient.getColumnmaps(), ILLNESS_DATE, true);
        if(!TextUtils.isEmpty(illnessDate)){
            String illnessDescription = getValue(commonPersonObjectClient.getColumnmaps(), ILLNESS_DESCRIPTION, true);
            String illnessAction = getValue(commonPersonObjectClient.getColumnmaps(), ILLNESS_ACTION, true);
            illnessContent.add(context.getString(R.string.illness_date_with_value, illnessDate));
            illnessContent.add(context.getString(R.string.illness_des_with_value, illnessDescription));
            illnessContent.add(context.getString(R.string.illness_action_value,illnessAction));

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
                    receivedVaccine.setVaccineName(fixVaccineCasing(name).replace("MEASLES","MCV"));
                    if(receivedVaccine.getVaccineName().contains("MEASLES")){
                        receivedVaccine.setVaccineName(receivedVaccine.getVaccineName().replace("MEASLES","MCV"));
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
    public void fetchGrowthNutritionData(String baseEntity, final MedicalHistoryContract.InteractorCallBack callBack) {
        RecurringServiceRecordRepository recurringServiceRecordRepository = ImmunizationLibrary.getInstance().recurringServiceRecordRepository();
        Context context= WcaroApplication.getInstance().getApplicationContext();
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
                    //String[] values = serviceRecord.getValue().split("_");
                    if (serviceRecord.getName().equalsIgnoreCase("exclusive breastfeeding0")) {
                        content.setServiceName(context.getString(R.string.initial_breastfeed_value,serviceRecord.getValue()));
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

                content.setType(serviceRecord.getType());
                baseServiceArrayList.add(content);
                lastType = serviceRecord.getType();
            } else {
                ServiceContent content = new ServiceContent();
                if (serviceRecord.getType().equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue())) {
                    //String[] values = serviceRecord.getValue().split("_");
                    if (serviceRecord.getName().equalsIgnoreCase("exclusive breastfeeding0")) {
                        content.setServiceName(context.getString(R.string.initial_breastfeed_value,serviceRecord.getValue()));
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
