package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.chw.core.activity.CoreChildMedicalHistoryActivity;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.Date;
import java.util.LinkedHashMap;

public class ChildMedicalHistoryActivity extends CoreChildMedicalHistoryActivity {

    public static void startMedicalHistoryActivity(Activity activity, CommonPersonObjectClient childClient, String childName, String lastVisitDays, String dateOfirth,
                                                   LinkedHashMap<String, Date> receivedVaccine) {
        Intent intent = new Intent(activity, ChildMedicalHistoryActivity.class);
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON, childClient);
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_NAME, childName);
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_DATE_OF_BIRTH, dateOfirth);
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_LAST_VISIT_DAYS, lastVisitDays);
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_VACCINE_LIST, receivedVaccine);

        activity.startActivity(intent);
    }

    @Override
    protected void onCreation() {
        super.onCreation();
        setFlavor(new ChildMedicalHistoryActivityFlv());
    }

    @Override
    public void onViewCreated(Activity activity) {
        setFlavor(new ChildMedicalHistoryActivityFlv());
        super.onViewCreated(activity);
    }
}
