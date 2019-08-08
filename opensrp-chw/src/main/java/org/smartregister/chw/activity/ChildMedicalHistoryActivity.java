package org.smartregister.chw.activity;

import com.opensrp.chw.core.activity.CoreChildMedicalHistoryActivity;

public class ChildMedicalHistoryActivity extends CoreChildMedicalHistoryActivity {

    @Override
    protected void onCreation() {
        super.onCreation();
        setFlavor(new ChildMedicalHistoryActivityFlv());
    }
}
