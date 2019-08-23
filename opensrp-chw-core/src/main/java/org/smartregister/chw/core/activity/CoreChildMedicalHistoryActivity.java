package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class CoreChildMedicalHistoryActivity extends BaseChildMedicalHistory {

    private Flavor flavor;

    public static void startMedicalHistoryActivity(Activity activity, CommonPersonObjectClient childClient, String childName, String lastVisitDays, String dateOfirth,
                                                   LinkedHashMap<String, Date> receivedVaccine, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON, childClient);
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_NAME, childName);
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_DATE_OF_BIRTH, dateOfirth);
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_LAST_VISIT_DAYS, lastVisitDays);
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_VACCINE_LIST, receivedVaccine);

        activity.startActivity(intent);
    }


    @Override
    public void onViewCreated(Activity activity) {
        if (flavor != null) {
            flavor.renderView(this);
            flavor.fetchData(activity, vaccineList, dateOfBirth, childClient);
        }
    }

    public Flavor getFlavor() {
        return flavor;
    }

    public void setFlavor(Flavor flavor) {
        this.flavor = flavor;
    }

    public interface Flavor {
        void renderView(Activity activity);

        void fetchData(Context context, Map<String, Date> vaccineList, String dateOfBirth, CommonPersonObjectClient childClient);
    }
}
