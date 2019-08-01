package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.opensrp.chw.core.utils.Constants;

import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChildMedicalHistoryActivity extends BaseChildMedicalHistory {

    private Flavor flavor = new ChildMedicalHistoryActivityFlv();

    public static void startMedicalHistoryActivity(Activity activity, CommonPersonObjectClient childClient, String childName, String lastVisitDays, String dateOfirth,
                                                   LinkedHashMap<String, Date> receivedVaccine) {
        Intent intent = new Intent(activity, ChildMedicalHistoryActivity.class);
        intent.putExtra(Constants.INTENT_KEY.CHILD_COMMON_PERSON, childClient);
        intent.putExtra(Constants.INTENT_KEY.CHILD_NAME, childName);
        intent.putExtra(Constants.INTENT_KEY.CHILD_DATE_OF_BIRTH, dateOfirth);
        intent.putExtra(Constants.INTENT_KEY.CHILD_LAST_VISIT_DAYS, lastVisitDays);
        intent.putExtra(Constants.INTENT_KEY.CHILD_VACCINE_LIST, receivedVaccine);

        activity.startActivity(intent);
    }


    @Override
    public void onViewCreated(Activity activity) {
        flavor.renderView(this);
        flavor.fetchData(activity, vaccineList, dateOfBirth, childClient);

    }

    public interface Flavor {
        void renderView(Activity activity);

        void fetchData(Context context, Map<String, Date> vaccineList, String dateOfBirth, CommonPersonObjectClient childClient);
    }
}
