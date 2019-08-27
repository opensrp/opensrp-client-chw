package org.smartregister.chw.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.smartregister.chw.core.model.RecurringServiceModel;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.ServiceType;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.service.AlertService;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class RecurringServiceUtil {

    public static Map<String, ServiceWrapper> getRecurringServices(String baseEntityID, DateTime anchorDate, String group) {
        Map<String, ServiceWrapper> serviceWrapperMap = new LinkedHashMap<>();

        RecurringServiceModel recurringServiceModel = getServiceModel(baseEntityID, anchorDate, group);
        Map<String, List<ServiceType>> foundServiceTypeMap = getServiceGroup(recurringServiceModel);

        for (String type : foundServiceTypeMap.keySet()) {
            ServiceWrapper serviceWrapper = new ServiceWrapper();
            serviceWrapper.setId(baseEntityID);
            serviceWrapper.setDefaultName(type);
            serviceWrapper.setDob(anchorDate);

            updateWrapperStatus(recurringServiceModel.getServiceRecords(), recurringServiceModel.getAlerts(), serviceWrapper, anchorDate, foundServiceTypeMap.get(type));
            updateWrapper(serviceWrapper, recurringServiceModel.getServiceRecords());
            serviceWrapperMap.put(type, serviceWrapper);
        }

        return serviceWrapperMap;
    }

    public static RecurringServiceModel getServiceModel(String baseEntityID, DateTime anchorDate, String group) {
        // get the services
        if (anchorDate != null) {
            ChwServiceSchedule.updateOfflineAlerts(baseEntityID, anchorDate, group);
        }

        RecurringServiceRecordRepository recurringServiceRecordRepository = ImmunizationLibrary.getInstance().recurringServiceRecordRepository();
        RecurringServiceTypeRepository recurringServiceTypeRepository = ImmunizationLibrary.getInstance().recurringServiceTypeRepository();
        AlertService alertService = ImmunizationLibrary.getInstance().context().alertService();

        Map<String, List<ServiceType>> serviceTypeMap = new LinkedHashMap<>();
        List<ServiceRecord> serviceRecords = new ArrayList<>();

        List<Alert> alertList = new ArrayList<>();

        if (recurringServiceRecordRepository != null) {
            serviceRecords = recurringServiceRecordRepository.findByEntityId(baseEntityID);
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
            alertList = alertService.findByEntityId(baseEntityID);
        }

        return new RecurringServiceModel(alertList, serviceTypeMap, serviceRecords);
    }

    public static Map<String, List<ServiceType>> getServiceGroup(RecurringServiceModel model) {
        Map<String, List<ServiceType>> foundServiceTypeMap = new LinkedHashMap<>();
        for (String type : model.getServiceTypes().keySet()) {
            if (foundServiceTypeMap.containsKey(type)) {
                continue;
            }

            for (ServiceRecord serviceRecord : model.getServiceRecords()) {
                if (serviceRecord.getSyncStatus().equals(RecurringServiceTypeRepository.TYPE_Unsynced) && serviceRecord.getType().equals(type)) {
                    foundServiceTypeMap.put(type, model.getServiceTypes().get(type));
                    break;
                }
            }

            if (foundServiceTypeMap.containsKey(type)) {
                continue;
            }

            for (Alert a : model.getAlerts()) {
                if (StringUtils.containsIgnoreCase(a.scheduleName(), type)
                        || StringUtils.containsIgnoreCase(a.visitCode(), type)) {
                    foundServiceTypeMap.put(type, model.getServiceTypes().get(type));
                    break;
                }
            }
        }
        return foundServiceTypeMap;
    }

    public static void updateWrapperStatus(List<ServiceRecord> serviceRecords, List<Alert> alertList, ServiceWrapper tag, DateTime anchorDate, List<ServiceType> serviceTypes) {
        List<ServiceRecord> serviceRecordList = new ArrayList<>();
        for (ServiceRecord serviceRecord : serviceRecords) {
            if (serviceRecord.getRecurringServiceId().equals(tag.getTypeId())) {
                serviceRecordList.add(serviceRecord);
            }
        }

        Map<String, Date> receivedServices = VaccinatorUtils.receivedServices(serviceRecordList);

        List<Map<String, Object>> sch = VaccinatorUtils.generateScheduleList(serviceTypes, anchorDate, receivedServices, alertList);

        Map<String, Object> nv = null;
        if (serviceRecordList.isEmpty()) {
            nv = nextServiceDueBasedOnExpire(sch, serviceTypes);
        } else {
            ServiceRecord lastServiceRecord = null;
            for (ServiceRecord serviceRecord : serviceRecordList) {
                if (serviceRecord.getSyncStatus().equalsIgnoreCase(RecurringServiceRecordRepository.TYPE_Unsynced)) {
                    lastServiceRecord = serviceRecord;
                }
            }

            if (lastServiceRecord != null) {
                nv = VaccinatorUtils.nextServiceDue(sch, lastServiceRecord);
            }
        }

        if (nv == null) {
            Date lastVaccine = null;
            if (!serviceRecordList.isEmpty()) {
                ServiceRecord serviceRecord = serviceRecordList.get(serviceRecordList.size() - 1);
                lastVaccine = serviceRecord.getDate();
            }

            nv = VaccinatorUtils.nextServiceDue(sch, lastVaccine);
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

    public static void updateWrapper(ServiceWrapper tag, List<ServiceRecord> serviceRecordList) {
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

    public static Map<String, Object> nextServiceDueBasedOnExpire(List<Map<String, Object>> schedule, List<ServiceType> serviceTypeList) {
        Map<String, Object> v = null;
        try {
            for (Map<String, Object> m : schedule) {
                if (m != null && m.get("status") != null && m.get("status").toString().equalsIgnoreCase("due")) {

                    if (v == null && m.get("service") != null && serviceTypeList.contains(m.get("service"))) {
                        try {
                            Alert mAlert = (Alert) m.get("alert");
                            if (mAlert != null && !mAlert.status().equals(AlertStatus.expired)) {
                                v = m;
                            }

                        } catch (Exception e) {
                            Timber.e(e);
                        }


                    } else if (v != null && v.get("alert") == null && m.get("alert") != null && m.get("service") != null && serviceTypeList.contains(m.get("service"))) {
                        Alert mAlert = (Alert) m.get("alert");
                        if (mAlert != null && !mAlert.status().equals(AlertStatus.expired)) {
                            v = m;
                        }


                    } else if (v != null && v.get("alert") != null && m.get("alert") != null && m.get("service") != null && serviceTypeList.contains(m.get("service"))) {
                        Alert vAlert = (Alert) v.get("alert");
                        Alert mAlert = (Alert) m.get("alert");
                        if (vAlert != null && !vAlert.status().equals(AlertStatus.urgent)) {
                            if (vAlert.status().equals(AlertStatus.upcoming) && (mAlert != null && (mAlert.status().equals(AlertStatus.normal) || mAlert.status().equals(AlertStatus.urgent)))) {
                                v = m;
                            } else if (vAlert.status().equals(AlertStatus.normal) && mAlert != null && mAlert.status().equals(AlertStatus.urgent)) {
                                v = m;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return v;
    }
}
