package org.smartregister.chw.custom_view;

import android.app.Activity;
import android.content.Intent;

import org.apache.commons.lang3.tuple.Pair;
import org.smartregister.chw.core.activity.CoreStockInventoryReportActivity;
import org.smartregister.chw.core.activity.HIA2ReportsActivity;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.referral.util.Constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class NavigationMenuFlv extends DefaultNavigationMenuFlv {

    @Override
    public List<Pair<String, Locale>> getSupportedLanguages() {
        return Arrays.asList(Pair.of("English", Locale.ENGLISH), Pair.of("Kiswahili", new Locale("sw")));
    }

    @Override
    public HashMap<String, String> getTableMapValues() {
        HashMap<String, String> tableMap = new HashMap<>();
        tableMap.put(CoreConstants.DrawerMenu.REFERRALS, Constants.Tables.REFERRAL);
        return tableMap;
    }

    @Override
    public boolean hasServiceReport() {
        return true;
    }

    @Override
    public boolean hasStockReport() {
        return true;
    }

    @Override
    public boolean hasCommunityResponders() {
        return true;
    }

    @Override
    public boolean hasSyncStatusProgressBar() {
        return false;
    }

    @Override
    public Intent getStockReportIntent(Activity activity) {
        return new Intent(activity, CoreStockInventoryReportActivity.class);
    }

    @Override
    public Intent getServiceReportIntent(Activity activity) {
        return new Intent(activity, HIA2ReportsActivity.class);
    }

    @Override
    public String childNavigationMenuCountString() {
        return null;
    }

    @Override
    public Intent getHIA2ReportActivityIntent(Activity activity) {
        return new Intent(activity, HIA2ReportsActivity.class);
    }
}
