package org.smartregister.chw.activity;

import android.app.Activity;
import org.smartregister.chw.core.activity.CoreChildMedicalHistoryActivity;

public class ChildMedicalHistoryActivity extends CoreChildMedicalHistoryActivity {

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
