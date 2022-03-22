package org.smartregister.chw.listener;

import android.app.Activity;
import android.view.MenuItem;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.MotherChampionRegisterActivity;
import org.smartregister.chw.pmtct.listener.PmtctBottomNavigationListener;

import androidx.annotation.NonNull;

public class ChwMotherChampionBottomNavigationListener extends PmtctBottomNavigationListener {

    private final MotherChampionRegisterActivity baseRegisterActivity;

    public ChwMotherChampionBottomNavigationListener(Activity context) {
        super(context);
        this.baseRegisterActivity = (MotherChampionRegisterActivity) context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_home) {
            baseRegisterActivity.switchToFragment(0);
            return true;
        }else if (item.getItemId() == R.id.action_received_referrals) {
            baseRegisterActivity.switchToFragment(1);
            return true;
        }else if (item.getItemId() == R.id.action_followup) {
            baseRegisterActivity.switchToFragment(2);
            return true;
        } else
            return super.onNavigationItemSelected(item);
    }
}
