package org.smartregister.chw.listener;

import org.smartregister.domain.Alert;
import org.smartregister.immunization.domain.Vaccine;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ImmunizationStateChangeListener {
    void onImmunicationStateChange(List<Alert> alerts, List<Vaccine> vaccines, Map<String, Date> receivedVaccine, List<Map<String, Object>> sch);
}
