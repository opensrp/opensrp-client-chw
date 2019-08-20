package org.smartregister.chw.activity;

import org.smartregister.chw.core.activity.CoreChildMedicalHistoryActivity;

public class ChildMedicalHistoryActivity extends CoreChildMedicalHistoryActivity {

    @Override
    protected void onCreation() {
        super.onCreation();
        setFlavor(new ChildMedicalHistoryActivityFlv());
    }
}
