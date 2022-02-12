package org.smartregister.chw.activity;

import android.content.Context;

import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.core.activity.DefaultChildMedicalHistoryActivityFlv;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.Vaccine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChildMedicalHistoryActivityFlv extends DefaultChildMedicalHistoryActivityFlv {

    @Override
    public void processViewData(List<Visit> visits, Map<String, List<Vaccine>> vaccineMap, List<ServiceRecord> serviceTypeListMap, Context context) {
        this.visits = visits;
        this.vaccineMap = vaccineMap;

        for (Visit v : this.visits) {
            List<Visit> type_visits = visitMap.get(v.getVisitType());
            if (type_visits == null) type_visits = new ArrayList<>();

            type_visits.add(v);
            visitMap.put(v.getVisitType(), type_visits);
        }

        evaluateLastVisitDate();
        evaluateImmunizations();
        evaluateGrowthAndNutrition();
        evaluateECD();
        evaluateLLITN();
    }

}
