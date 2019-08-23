package org.smartregister.chw.core.utils;

import org.joda.time.DateTime;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.ServiceType;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.service.AlertService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Keyman on 26/05/2017.
 */

public class ChwServiceSchedule {

    public static void updateOfflineAlerts(String baseEntityId, DateTime dob, String group) {
        RecurringServiceTypeRepository recurringServiceTypeRepository = ImmunizationLibrary.getInstance().recurringServiceTypeRepository();
        List<String> types = recurringServiceTypeRepository.fetchTypes(group);
        for (String type : types) {
            updateOfflineAlerts(type, baseEntityId, dob);
        }
    }

    public static void updateOfflineAlerts(String type, String baseEntityId, DateTime dob) {
        try {
            if (baseEntityId == null || dob == null) {
                return;
            }

            RecurringServiceTypeRepository recurringServiceTypeRepository = ImmunizationLibrary.getInstance().recurringServiceTypeRepository();
            RecurringServiceRecordRepository recurringServiceRecordRepository = ImmunizationLibrary.getInstance().recurringServiceRecordRepository();
            AlertService alertService = ImmunizationLibrary.getInstance().context().alertService();

            List<ServiceType> serviceTypes = recurringServiceTypeRepository.findByType(type);

            String[] alertArray = VaccinateActionUtils.allAlertNames(serviceTypes);

            // Get all the administered services
            List<ServiceRecord> issuedServices = recurringServiceRecordRepository.findByEntityId(baseEntityId);
            alertService.deleteOfflineAlerts(baseEntityId, alertArray);

            List<Alert> existingAlerts = alertService.findByEntityIdAndAlertNames(baseEntityId, alertArray);

            for (ServiceType serviceType : serviceTypes) {
                Alert curAlert = getOfflineAlert(serviceType, issuedServices, baseEntityId, dob);

//                if (curAlert == null ) {
//                    break;
//                } else {
                // Check if the current alert already exists for the entityId
                boolean exists = false;
                for (Alert curExistingAlert : existingAlerts) {
                    if (curAlert != null && curExistingAlert.scheduleName().equalsIgnoreCase(curAlert.scheduleName())
                            && curExistingAlert.caseId().equalsIgnoreCase(curAlert.caseId())) {
                        exists = true;
                        break;
                    }
                }

                // Check if service is already given
                if (!exists) {
                    for (ServiceRecord serviceRecord : issuedServices) {
                        if (curAlert != null && curAlert.scheduleName().equalsIgnoreCase(serviceRecord.getName())
                                || curAlert != null && curAlert.visitCode().equalsIgnoreCase(serviceRecord.getName())) {
                            exists = true;
                            break;
                        }
                    }
                }

                // Insert alert into table
                if (!exists && curAlert != null && !curAlert.status().value().equalsIgnoreCase(AlertStatus.expired.value())) {
                    alertService.create(curAlert);
                }
                //}
            }

        } catch (Exception e) {
            Timber.e(e);
        }

    }


    public static Alert getOfflineAlert(final ServiceType serviceType, final List<ServiceRecord> issuedServices, final String baseEntityId, final DateTime dateOfBirth) {

        try {
            DateTime dueDateTime = VaccinatorUtils.getServiceDueDate(serviceType, dateOfBirth, issuedServices);
            DateTime expiryDateTime = VaccinatorUtils.getServiceExpiryDate(serviceType, dateOfBirth);
            // Use the trigger date as a reference, since that is what is mostly used
            AlertStatus alertStatus = calculateAlertStatus(dueDateTime, expiryDateTime);

            if (alertStatus != null) {
                Date startDate = dueDateTime == null ? dateOfBirth.toDate() : dueDateTime.toDate();
                Date expiryDate = expiryDateTime == null ? null : expiryDateTime.toDate();
                return new Alert(baseEntityId, serviceType.getName(), serviceType.getName().toLowerCase().replace(" ", ""),
                        alertStatus, startDate == null ? null : DateUtil.yyyyMMdd.format(startDate), expiryDate == null ? null : DateUtil.yyyyMMdd.format(expiryDate), true);
            }
            return null;
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    private static AlertStatus calculateAlertStatus(DateTime dueDateTime, DateTime expiryDateTime) {

        try {
            Calendar expiredCal = Calendar.getInstance();
            expiredCal.setTime(expiryDateTime.toDate());
            standardiseCalendarDate(expiredCal);

            Calendar dueCal = Calendar.getInstance();
            dueCal.setTime(dueDateTime.toDate());
            standardiseCalendarDate(dueCal);

            Calendar today = Calendar.getInstance();
            standardiseCalendarDate(today);

            if (expiredCal.getTimeInMillis() < today.getTimeInMillis()) {// expired
                return AlertStatus.expired;
            } else if (dueCal.getTimeInMillis() <= today.getTimeInMillis()) {// Due
                return AlertStatus.normal;
            } else if (dueCal.getTimeInMillis() == expiredCal.getTimeInMillis()) {
                return AlertStatus.normal;
            }
        } catch (Exception e) {
            Timber.e(e, ChwServiceSchedule.class.getName(), e.toString());
        }
        return null;

    }

    public static void standardiseCalendarDate(Calendar calendarDate) {
        calendarDate.set(Calendar.HOUR_OF_DAY, 0);
        calendarDate.set(Calendar.MINUTE, 0);
        calendarDate.set(Calendar.SECOND, 0);
        calendarDate.set(Calendar.MILLISECOND, 0);
    }

}

