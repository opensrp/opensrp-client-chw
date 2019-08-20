package org.smartregister.chw.core.model;

import org.smartregister.chw.core.contract.NavigationContract;
import org.smartregister.util.Utils;

import java.util.List;

import timber.log.Timber;

public class NavigationModel implements NavigationContract.Model {

    private static NavigationModel instance;
    private Flavor flavor;

    public static NavigationModel getInstance() {
        if (instance == null)
            instance = new NavigationModel();
        return instance;
    }

    @Override
    public void setNavigationFlavor(Flavor flavor) {
        this.flavor = flavor;
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

    public interface Flavor {
        List<NavigationOption> getNavigationItems();
    }
}
