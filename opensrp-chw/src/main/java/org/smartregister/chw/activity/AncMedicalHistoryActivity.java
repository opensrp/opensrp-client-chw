package org.smartregister.chw.activity;

import org.smartregister.chw.core.activity.CoreAncMedicalHistoryActivity;

public class AncMedicalHistoryActivity extends CoreAncMedicalHistoryActivity {
    @Override
    public void setFlavor(Flavor flavor) {
        super.setFlavor(new AncMedicalHistoryActivityFlv());
    }
}
