package org.smartregister.chw.core.model;

import org.smartregister.domain.Alert;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.ServiceType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RecurringServiceModel {
    private List<Alert> alerts = new ArrayList<>();
    private Map<String, List<ServiceType>> serviceTypes = new LinkedHashMap<>();
    private List<ServiceRecord> serviceRecords = new ArrayList<>();

    public RecurringServiceModel(List<Alert> alerts, Map<String, List<ServiceType>> serviceTypes, List<ServiceRecord> serviceRecords) {
        if (alerts != null)
            this.alerts = alerts;
        if (serviceTypes != null)
            this.serviceTypes = serviceTypes;
        if (serviceRecords != null)
            this.serviceRecords = serviceRecords;
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public Map<String, List<ServiceType>> getServiceTypes() {
        return serviceTypes;
    }

    public List<ServiceRecord> getServiceRecords() {
        return serviceRecords;
    }
}
