package org.smartregister.chw.util;

import android.content.Context;

import org.smartregister.chw.core.enums.ImmunizationState;
import org.smartregister.chw.interactor.FamilyInteractor;
import org.smartregister.domain.AlertStatus;

public class ScheduleUtil {

    public static AlertStatus getFamilyAlertStatus(Context context, String childID, String familyBaseEntityID) {
        FamilyInteractor interactor = new FamilyInteractor();
        ImmunizationState immunizationState = interactor.getFamilyImmunizationState(context, childID, familyBaseEntityID);

        if (immunizationState == ImmunizationState.DUE) {
            return AlertStatus.normal;
        } else if (immunizationState == ImmunizationState.OVERDUE) {
            return AlertStatus.urgent;
        }
        return AlertStatus.complete;
    }
}
