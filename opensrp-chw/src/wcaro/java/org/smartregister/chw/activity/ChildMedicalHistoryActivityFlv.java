package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Context;

import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.Date;
import java.util.Map;

public class ChildMedicalHistoryActivityFlv extends DefaultChildMedicalHistoryActivity implements ChildMedicalHistoryActivity.Flavor {

    @Override
    public void renderView(Activity activity) {
        super.onViewUpdated(activity);
    }

    @Override
    public void fetchData(Context context, Map<String, Date> vaccineList, String dateOfBirth, CommonPersonObjectClient childClient) {

        //generateHomeVisitServiceList(childClient);
        setInitialVaccineList(vaccineList);
        fetchFullYImmunization(dateOfBirth);
        fetchGrowthNutrition(childClient);
        fetchBirthCertificateData(childClient);
        fetchIllnessData(childClient);
    }
}
