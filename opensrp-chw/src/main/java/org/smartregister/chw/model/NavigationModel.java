package org.smartregister.chw.model;

import android.util.Log;

import org.smartregister.chw.contract.NavigationContract;
import org.smartregister.util.Utils;

import java.util.List;

public class NavigationModel implements NavigationContract.Model {

    private static NavigationModel instance;
    private String TAG = NavigationModel.class.getCanonicalName();

    public static NavigationModel getInstance() {
        if (instance == null)
            instance = new NavigationModel();

        return instance;
    }

    @Override
    public List<NavigationOption> getNavigationItems() {
        return NavigationModelFlv.getNavigationItems();
    }

    @Override
    public String getCurrentUser() {
        String res = "";
        try {
            res = Utils.getPrefferedName().split(" ")[0];
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return res;
    }

}
