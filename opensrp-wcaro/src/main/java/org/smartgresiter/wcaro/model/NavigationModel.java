package org.smartgresiter.wcaro.model;

import android.app.Activity;
import android.util.Log;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.contract.NavigationContract;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class NavigationModel implements NavigationContract.Model {

    private static NavigationModel instance;
    List<NavigationOption> navigationOptions = new ArrayList<>();
    NavigationOption op1 = new NavigationOption(R.mipmap.sidemenu_families, R.mipmap.sidemenu_families_active, Constants.DrawerMenu.ALL_FAMILIES, 0);
    NavigationOption op2 = new NavigationOption(R.mipmap.sidemenu_children, R.mipmap.sidemenu_children_active, Constants.DrawerMenu.CHILD_CLIENTS, 0);
    private String TAG = NavigationModel.class.getCanonicalName();
    private Activity mActivity;

    private NavigationModel() {
        navigationOptions.clear();
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
