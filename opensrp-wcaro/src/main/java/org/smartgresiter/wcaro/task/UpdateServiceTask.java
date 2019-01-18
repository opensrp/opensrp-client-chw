package org.smartgresiter.wcaro.task;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.smartgresiter.wcaro.listener.UpdateServiceListener;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.domain.Photo;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.ServiceSchedule;
import org.smartregister.immunization.domain.ServiceType;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.ImageUtils;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.service.AlertService;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.smartregister.immunization.util.VaccinatorUtils.generateScheduleList;
import static org.smartregister.immunization.util.VaccinatorUtils.nextServiceDue;
import static org.smartregister.util.Utils.getName;
import static org.smartregister.util.Utils.getValue;

public class UpdateServiceTask extends AsyncTask<Void, Void, Map<String, UpdateServiceTask.NamedObject<?>>> {
    private CommonPersonObjectClient childDetails;
    private RecurringServiceTypeRepository recurringServiceTypeRepository;
    private RecurringServiceRecordRepository recurringServiceRecordRepository;
    private AlertService alertService;
    private Map<String, ServiceWrapper> displayServiceWrapper;
    private UpdateServiceListener listener;

    public UpdateServiceTask(CommonPersonObjectClient childDetails, UpdateServiceListener listener) {
        this.childDetails = childDetails;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        recurringServiceTypeRepository = ImmunizationLibrary.getInstance().recurringServiceTypeRepository();
        recurringServiceRecordRepository = ImmunizationLibrary.getInstance().recurringServiceRecordRepository();
        alertService = ImmunizationLibrary.getInstance().context().alertService();
        displayServiceWrapper = new LinkedHashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPostExecute(Map<String, NamedObject<?>> map) {


        Map<String, List<ServiceType>> serviceTypeMap = new LinkedHashMap<>();
        List<ServiceRecord> serviceRecords = new ArrayList<>();

        List<Alert> alertList = new ArrayList<>();

        if (map.containsKey(ServiceType.class.getName())) {
            NamedObject<?> namedObject = map.get(ServiceType.class.getName());
            if (namedObject != null) {
                serviceTypeMap = (Map<String, List<ServiceType>>) namedObject.object;
            }

        }

        if (map.containsKey(ServiceRecord.class.getName())) {
            NamedObject<?> namedObject = map.get(ServiceRecord.class.getName());
            if (namedObject != null) {
                serviceRecords = (List<ServiceRecord>) namedObject.object;
            }

        }

        if (map.containsKey(Alert.class.getName())) {
            NamedObject<?> namedObject = map.get(Alert.class.getName());
            if (namedObject != null) {
                alertList = (List<Alert>) namedObject.object;
            }

        }

        Map<String, List<ServiceType>> foundServiceTypeMap = new LinkedHashMap<>();
        for (String type : serviceTypeMap.keySet()) {
            if (foundServiceTypeMap.containsKey(type)) {
                continue;
            }

            for (ServiceRecord serviceRecord : serviceRecords) {
                if (serviceRecord.getSyncStatus().equals(RecurringServiceTypeRepository.TYPE_Unsynced)) {
                    if (serviceRecord.getType().equals(type)) {
                        foundServiceTypeMap.put(type, serviceTypeMap.get(type));
                        break;
                    }
                }
            }

            if (foundServiceTypeMap.containsKey(type)) {
                continue;
            }

            for (Alert a : alertList) {
                if (StringUtils.containsIgnoreCase(a.scheduleName(), type)
                        || StringUtils.containsIgnoreCase(a.visitCode(), type)) {
                    foundServiceTypeMap.put(type, serviceTypeMap.get(type));
                    break;
                }
            }

        }

        if (foundServiceTypeMap.isEmpty()) {
            return;
        }
        for (String type : foundServiceTypeMap.keySet()) {
            ServiceWrapper serviceWrapper = new ServiceWrapper();
            serviceWrapper.setId(childDetails.entityId());
            serviceWrapper.setGender(childDetails.getDetails().get("gender"));
            serviceWrapper.setDefaultName(type);

            String dobString = getValue(childDetails.getColumnmaps(), "dob", false);
            if (StringUtils.isNotBlank(dobString)) {
                Calendar dobCalender = Calendar.getInstance();
                DateTime dateTime = new DateTime(dobString);
                dobCalender.setTime(dateTime.toDate());
                serviceWrapper.setDob(new DateTime(dobCalender.getTime()));
            }

            Photo photo = ImageUtils.profilePhotoByClient(childDetails);
            serviceWrapper.setPhoto(photo);

            String zeirId = getValue(childDetails.getColumnmaps(), "zeir_id", false);
            serviceWrapper.setPatientNumber(zeirId);

            String firstName = getValue(childDetails.getColumnmaps(), "first_name", true);
            String lastName = getValue(childDetails.getColumnmaps(), "last_name", true);
            String childName = getName(firstName, lastName);
            serviceWrapper.setPatientName(childName.trim());

            updateWrapperStatus(serviceRecords, alertList, serviceWrapper, childDetails, foundServiceTypeMap.get(type));
            updateWrapper(serviceWrapper, serviceRecords);
            displayServiceWrapper.put(type, serviceWrapper);
        }
        Log.v("Service_wrapper", "service wrapper" + displayServiceWrapper);
        listener.onUpdateServiceList(displayServiceWrapper);
    }

    @Override
    protected Map<String, NamedObject<?>> doInBackground(Void... voids) {
        String dobString = Utils.getValue(childDetails.getColumnmaps(), "dob", false);
        if (!TextUtils.isEmpty(dobString)) {
            DateTime dateTime = new DateTime(dobString);
            VaccineSchedule.updateOfflineAlerts(childDetails.entityId(), dateTime, "child");
            ServiceSchedule.updateOfflineAlerts(childDetails.entityId(), dateTime);
        }


        Map<String, List<ServiceType>> serviceTypeMap = new LinkedHashMap<>();
        List<ServiceRecord> serviceRecords = new ArrayList<>();

        List<Alert> alertList = new ArrayList<>();

        if (recurringServiceRecordRepository != null) {
            serviceRecords = recurringServiceRecordRepository.findByEntityId(childDetails.entityId());
        }

        if (recurringServiceTypeRepository != null) {
            List<ServiceType> serviceTypes = recurringServiceTypeRepository.fetchAll();
            for (ServiceType serviceType : serviceTypes) {
                String type = serviceType.getType();
                List<ServiceType> serviceTypeList = serviceTypeMap.get(type);
                if (serviceTypeList == null) {
                    serviceTypeList = new ArrayList<>();
                }
                serviceTypeList.add(serviceType);
                serviceTypeMap.put(type, serviceTypeList);
            }
        }

        if (alertService != null) {
            alertList = alertService.findByEntityId(childDetails.entityId());
        }

        Map<String, NamedObject<?>> map = new HashMap<>();

        NamedObject<Map<String, List<ServiceType>>> serviceTypeNamedObject = new NamedObject<>(ServiceType.class.getName(), serviceTypeMap);
        map.put(serviceTypeNamedObject.name, serviceTypeNamedObject);

        NamedObject<List<ServiceRecord>> serviceRecordNamedObject = new NamedObject<>(ServiceRecord.class.getName(), serviceRecords);
        map.put(serviceRecordNamedObject.name, serviceRecordNamedObject);

        NamedObject<List<Alert>> alertsNamedObject = new NamedObject<>(Alert.class.getName(), alertList);
        map.put(alertsNamedObject.name, alertsNamedObject);

        return map;
    }

    public void updateWrapperStatus(List<ServiceRecord> serviceRecords, List<Alert> alertList, ServiceWrapper tag, CommonPersonObjectClient childDetails, List<ServiceType> serviceTypes) {


        List<ServiceRecord> serviceRecordList = new ArrayList<>();
        for (ServiceRecord serviceRecord : serviceRecords) {
            if (serviceRecord.getRecurringServiceId().equals(tag.getTypeId())) {
                serviceRecordList.add(serviceRecord);
            }
        }


        Map<String, Date> receivedServices = VaccinatorUtils.receivedServices(serviceRecordList);

        String dobString = getValue(childDetails.getColumnmaps(), "dob", false);
        List<Map<String, Object>> sch = generateScheduleList(serviceTypes, new DateTime(dobString), receivedServices, alertList);


        Map<String, Object> nv = null;
        if (serviceRecordList.isEmpty()) {
            nv = nextServiceDue(sch, serviceTypes);
        } else {
            ServiceRecord lastServiceRecord = null;
            for (ServiceRecord serviceRecord : serviceRecordList) {
                if (serviceRecord.getSyncStatus().equalsIgnoreCase(RecurringServiceRecordRepository.TYPE_Unsynced)) {
                    lastServiceRecord = serviceRecord;
                }
            }

            if (lastServiceRecord != null) {
                nv = nextServiceDue(sch, lastServiceRecord);
            }
        }

        if (nv == null) {
            Date lastVaccine = null;
            if (!serviceRecordList.isEmpty()) {
                ServiceRecord serviceRecord = serviceRecordList.get(serviceRecordList.size() - 1);
                lastVaccine = serviceRecord.getDate();
            }

            nv = nextServiceDue(sch, lastVaccine);
        }

        if (nv != null) {
            ServiceType nextServiceType = (ServiceType) nv.get("service");
            tag.setStatus(nv.get("status").toString());
            tag.setAlert((Alert) nv.get("alert"));
            if (nv.get("date") != null && nv.get("date") instanceof DateTime) {
                tag.setVaccineDate((DateTime) nv.get("date"));
            }
            tag.setServiceType(nextServiceType);
        }
    }

    public void updateWrapper(ServiceWrapper tag, List<ServiceRecord> serviceRecordList) {

        if (!serviceRecordList.isEmpty()) {
            for (ServiceRecord serviceRecord : serviceRecordList) {
                if (tag.getName().toLowerCase().contains(serviceRecord.getName().toLowerCase()) && serviceRecord.getDate() != null) {
                    long diff = serviceRecord.getUpdatedAt() - serviceRecord.getDate().getTime();
                    if (diff > 0 && TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) > 1) {
                        tag.setUpdatedVaccineDate(new DateTime(serviceRecord.getDate()), false);
                    } else {
                        tag.setUpdatedVaccineDate(new DateTime(serviceRecord.getDate()), true);
                    }
                    tag.setDbKey(serviceRecord.getId());
                    tag.setSynced(serviceRecord.getSyncStatus() != null && serviceRecord.getSyncStatus().equals(VaccineRepository.TYPE_Synced));
                }
            }
        }

    }

    public class NamedObject<T> {
        public final String name;
        public final T object;

        public NamedObject(String name, T object) {
            this.name = name;
            this.object = object;
        }
    }
}


