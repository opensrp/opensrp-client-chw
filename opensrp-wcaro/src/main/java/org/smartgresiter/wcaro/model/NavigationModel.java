package org.smartgresiter.wcaro.model;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.activity.ChildRegisterActivity;
import org.smartgresiter.wcaro.activity.FamilyRegisterActivity;
import org.smartgresiter.wcaro.contract.NavigationContract;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class NavigationModel implements NavigationContract.Model {

    private String TAG = NavigationModel.class.getCanonicalName();

    private static NavigationModel instance;
    List<NavigationOption> navigationOptions = new ArrayList<>();
    private Activity mActivity;

    public static NavigationModel getInstance() {
        if (instance == null)
            instance = new NavigationModel();

        return instance;
    }

    NavigationOption op1 = new NavigationOption(R.mipmap.sidemenu_families, R.mipmap.sidemenu_families_active, Constants.DrawerMenu.ALL_FAMILIES, 0);
    NavigationOption op2 = new NavigationOption(R.mipmap.sidemenu_children, R.mipmap.sidemenu_children_active, Constants.DrawerMenu.CHILD_CLIENTS, 0);

    private NavigationModel() {
    }

    @Override
    public List<NavigationOption> getNavigationItems(final Activity activity) {

        if (mActivity != activity || mActivity == null) {
            op1.setSelectedAction(new NavigationContract.SelectedAction() {
                @Override
                public void onSelect() {
                    Intent intent = new Intent(activity, FamilyRegisterActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }
            });
            op2.setSelectedAction(new NavigationContract.SelectedAction() {
                @Override
                public void onSelect() {
                    Intent intent = new Intent(activity, ChildRegisterActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }
            });


            navigationOptions.clear();
            navigationOptions.addAll(asList(op1, op2));
            mActivity = activity;
        }

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
