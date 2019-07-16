package org.smartregister.chw.model;

import org.smartregister.chw.contract.NavigationContract;
import org.smartregister.util.Utils;

import java.util.List;

import timber.log.Timber;

public class NavigationModel implements NavigationContract.Model {

    private static NavigationModel instance;
    private String TAG = NavigationModel.class.getCanonicalName();
    private static Flavor flavor = new NavigationModelFlv();

    public static NavigationModel getInstance() {
        if (instance == null)
            instance = new NavigationModel();

        return instance;
    }

    @Override
    public List<NavigationOption> getNavigationItems() {
        return flavor.getNavigationItems();
    }

    @Override
    public String getCurrentUser() {
        String res = "";
        try {
            res = Utils.getPrefferedName().split(" ")[0];
        } catch (Exception e) {
            Timber.e(e);
        }

        return res;
    }

    interface Flavor {
        List<NavigationOption> getNavigationItems();
    }
}
