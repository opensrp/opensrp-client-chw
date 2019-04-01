package org.smartregister.chw.listener;

import org.smartregister.chw.util.ImmunizationState;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.domain.Vaccine;

import java.util.List;
import java.util.Map;

public interface ImmunizationStateChangeListener {
    void onImmunicationStateChange(List<Alert> alerts, List<Vaccine> vaccines, String stateKey, List<Map<String, Object>> sch,Map<String, Object> nv, ImmunizationState state);
}
