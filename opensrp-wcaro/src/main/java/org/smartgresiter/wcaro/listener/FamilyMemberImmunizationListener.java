package org.smartgresiter.wcaro.listener;

import org.smartgresiter.wcaro.util.ImmunizationState;

import java.util.Date;
import java.util.Map;

public interface FamilyMemberImmunizationListener {
    void onFamilyMemberState(ImmunizationState state);

    void onSelfStatus(Map<String, Date> vaccines, Map<String, Object> nv, ImmunizationState state);

}
