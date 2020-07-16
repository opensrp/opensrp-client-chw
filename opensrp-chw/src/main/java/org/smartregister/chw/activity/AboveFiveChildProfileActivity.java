package org.smartregister.chw.activity;

import android.view.Menu;
import android.view.View;

import org.smartregister.chw.R;
import org.smartregister.chw.core.activity.CoreAboveFiveChildProfileActivity;

public class AboveFiveChildProfileActivity extends CoreAboveFiveChildProfileActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_malaria_followup_visit).setVisible(false);
        return true;
    }

    @Override
    protected void onCreation() {
        super.onCreation();
        initializePresenter();
        setupViews();
        setUpToolbar();
        registerReceiver(mDateTimeChangedReceiver, sIntentFilter);
        invisibleRecordVisitPanel();
    }

    private void invisibleRecordVisitPanel() {
        if (recordVisitPanel != null) {
            recordVisitPanel.setVisibility(View.GONE);
        }
    }
}
