package org.smartregister.chw.listener;

import org.smartregister.chw.core.enums.ImmunizationState;

import java.util.Date;
import java.util.Map;

public interface FamilyMemberImmunizationListener {
    void onFamilyMemberState(ImmunizationState state);

    void onSelfStatus(Map<String, Date> vaccines, Map<String, Object> nv, ImmunizationState state);

}
