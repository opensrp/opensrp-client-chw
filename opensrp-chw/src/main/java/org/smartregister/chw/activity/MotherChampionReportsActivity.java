package org.smartregister.chw.activity;

import android.view.View;
import android.widget.TextView;

import org.smartregister.chw.R;

public class MotherChampionReportsActivity extends CBHSReportsActivity {
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cbhs_monthly_summary) {
            MotherChampionReportsViewActivity.startMe(this, "mother-champion-report", reportPeriod);
        }
    }

    @Override
    public void setUpToolbar() {
        super.setUpToolbar();
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(R.string.mother_champion_reports);
    }
}