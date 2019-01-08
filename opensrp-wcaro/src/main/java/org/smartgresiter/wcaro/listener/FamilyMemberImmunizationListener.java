package org.smartgresiter.wcaro.listener;

import org.smartgresiter.wcaro.util.ImmunizationState;
import org.smartregister.immunization.domain.Vaccine;

import java.util.List;
import java.util.Map;

public interface FamilyMemberImmunizationListener {
    void onFamilyMemberState(ImmunizationState state);
    void onSelfStatus(List<Vaccine> vaccines,Map<String, Object> nv, ImmunizationState state);

}
