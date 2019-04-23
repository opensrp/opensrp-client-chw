package org.smartregister.chw.model;

import android.util.Log;

import org.smartregister.chw.BuildConfig;
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

        NavigationOption op3 = new NavigationOption(R.mipmap.sidemenu_anc, R.mipmap.sidemenu_anc_active, R.string.menu_anc, Constants.DrawerMenu.ANC, 0);
        NavigationOption op4 = new NavigationOption(R.mipmap.sidemenu_landd, R.mipmap.sidemenu_landd_active, R.string.menu_labour_and_delivery, Constants.DrawerMenu.LD,0);
        NavigationOption op5 = new NavigationOption(R.mipmap.sidemenu_pnc, R.mipmap.sidemenu_pnc_active, R.string.menu_pnc, Constants.DrawerMenu.PNC, 0);
        NavigationOption op6 = new NavigationOption(R.mipmap.sidemenu_fp, R.mipmap.sidemenu_fp_active, R.string.menu_family_planing, Constants.DrawerMenu.FP, 0);
        NavigationOption op7 = new NavigationOption(R.mipmap.sidemenu_malaria, R.mipmap.sidemenu_malaria_active, R.string.menu_malaria, Constants.DrawerMenu.MALARIA, 0);
        switch (BuildConfig.BUILD_COUNTRY) {
            case TANZANIA:
                op2.setTitleID(R.string.menu_child);
                op2.setMenuTitle(Constants.DrawerMenu.CH);
                navigationOptions.addAll(asList(op1, op3, op4, op5, op2, op6, op7));
                break;
            default:
                navigationOptions.addAll(asList(op1, op2));
                break;

        }
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
