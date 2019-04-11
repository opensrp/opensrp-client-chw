package org.smartregister.chw.model;

import android.util.Log;

import org.smartregister.chw.R;
import org.smartregister.chw.contract.NavigationContract;
import org.smartregister.chw.util.Constants;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class NavigationModel implements NavigationContract.Model {

    private static NavigationModel instance;
    private List<NavigationOption> navigationOptions = new ArrayList<>();
    private String TAG = NavigationModel.class.getCanonicalName();

    private NavigationModel() {
        navigationOptions.clear();
        NavigationOption op1 = new NavigationOption(R.mipmap.sidemenu_families, R.mipmap.sidemenu_families_active, R.string.menu_all_families, Constants.DrawerMenu.ALL_FAMILIES, 0);
        NavigationOption op2 = new NavigationOption(R.mipmap.sidemenu_children, R.mipmap.sidemenu_children_active, R.string.menu_child_clients, Constants.DrawerMenu.CHILD_CLIENTS, 0);
        navigationOptions.addAll(asList(op1, op2));
    }

    public static NavigationModel getInstance() {
        if (instance == null)
            instance = new NavigationModel();

        return instance;
    }

    @Override
    public List<NavigationOption> getNavigationItems() {
        return navigationOptions;
    }

    @Override
    public void setNavigationOptions(List<NavigationOption> navigationOptions) {
        this.navigationOptions = navigationOptions;
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
