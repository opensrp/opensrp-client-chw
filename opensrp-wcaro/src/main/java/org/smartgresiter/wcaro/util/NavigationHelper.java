package org.smartgresiter.wcaro.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.activity.ChildRegisterActivity;
import org.smartgresiter.wcaro.activity.FamilyRegisterActivity;
import org.smartgresiter.wcaro.activity.LoginActivity;
import org.smartgresiter.wcaro.adapter.NavigationAdapter;
import org.smartgresiter.wcaro.model.NavigationModel;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This class assist in sharing the Navigation adapter across fragments
 * Default behaviour and implementation is held by the class until the fragment is swapped
 */
public class NavigationHelper {

    private String TAG = NavigationHelper.class.getCanonicalName();

    private static NavigationHelper instance;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Toolbar toolbar;

    private final List<NavigationModel> navigationModels = new ArrayList<>();
    private NavigationAdapter navigationAdapter;
    private RecyclerView recyclerView;
    private TextView tvLogout;
    private View rootView = null;
    private Activity parentActivity;

    private NavigationHelper() {
        // load the navigation objects
    }

    private void init(Activity activity, View parentView, Toolbar myToolbar) {
        parentActivity = activity;
        setParentView(parentView);
        toolbar = myToolbar;
        initialize();
    }

    public static NavigationHelper getInstance(Activity activity, View parentView, Toolbar myToolbar) {
        if (instance == null) {
            instance = new NavigationHelper();
        }

        instance.init(activity, parentView, myToolbar);
        return instance;
    }

    private void setParentView(View parentView) {
        if (parentView != null) {
            rootView = parentView;
        } else {
            // get current view
            // ViewGroup current = parentActivity.getWindow().getDecorView().findViewById(android.R.id.content);
            ViewGroup current = (ViewGroup) ((ViewGroup) (parentActivity.findViewById(android.R.id.content))).getChildAt(0);
            if (current.getParent() != null) {
                ((ViewGroup) current.getParent()).removeView(current); // <- fix
            }

            // swap content view
            LayoutInflater mInflater = LayoutInflater.from(parentActivity);
            ViewGroup contentView = (ViewGroup) mInflater.inflate(R.layout.activity_base, null);
            parentActivity.setContentView(contentView);

            rootView = parentActivity.findViewById(R.id.nav_view);
            RelativeLayout rl = parentActivity.findViewById(R.id.nav_content);

            if (current.getParent() != null) {
                ((ViewGroup) current.getParent()).removeView(current); // <- fix
            }

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            current.setLayoutParams(params);
            rl.addView(current);
        }
        //
    }

    private void initialize() {
        //
        drawer = rootView.findViewById(R.id.drawer_layout);
        recyclerView = rootView.findViewById(R.id.rvOptions);
        navigationView = rootView.findViewById(R.id.nav_view);
        tvLogout = rootView.findViewById(R.id.tvLogout);
        recyclerView = rootView.findViewById(R.id.rvOptions);

        // register all objects
        registerDrawer();
        registerNavigation();
        registerLogout(parentActivity);
        registerSync();

        // update all actions
        updateLastSyncTime(navigationView);
        updateRegisterCount();
    }

    private void registerDrawer() {
        if (drawer != null) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    parentActivity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }
    }

    private void registerNavigation() {
        if (recyclerView != null) {
            if (navigationModels.size() == 0) {
                navigationModels.add(new NavigationModel(R.drawable.badge, Constants.DrawerMenu.ALL_FAMILIES, 0));
                navigationModels.add(new NavigationModel(R.drawable.badge, Constants.DrawerMenu.CHILD_CLIENTS, 0));
            }

            if (navigationAdapter == null) {
                navigationAdapter = new NavigationAdapter(navigationModels, parentActivity);
            }

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(parentActivity);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(navigationAdapter);

            navigationAdapter.addAction(Constants.DrawerMenu.ALL_FAMILIES,
                    new NavigationAdapter.SelectedAction() {
                        @Override
                        public void onSelect() {
                            Intent intent = new Intent(parentActivity, FamilyRegisterActivity.class);
                            parentActivity.startActivity(intent);
                        }
                    });

            navigationAdapter.addAction(Constants.DrawerMenu.CHILD_CLIENTS,
                    new NavigationAdapter.SelectedAction() {
                        @Override
                        public void onSelect() {
                            Intent intent = new Intent(parentActivity, ChildRegisterActivity.class);
                            parentActivity.startActivity(intent);
                        }
                    });

        }
    }

    private void registerLogout(final Activity activity) {
        updateCurrentUser();
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(activity);
            }
        });
    }

    private void registerSync() {

        TextView tvSync = rootView.findViewById(R.id.tvSync);
        ImageView ivSync = rootView.findViewById(R.id.ivSyncIcon);

        View.OnClickListener syncClicker = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manualSync(parentActivity, navigationView);
            }
        };


        tvSync.setOnClickListener(syncClicker);
        ivSync.setOnClickListener(syncClicker);
    }

    private void logout(Activity activity) {
        Toast.makeText(activity.getApplicationContext(), activity.getResources().getText(R.string.action_log_out), Toast.LENGTH_SHORT).show();
        DrishtiApplication drishtiApplication = (DrishtiApplication) activity.getApplication();
        drishtiApplication.logoutCurrentUser();

        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    private void updateLastSyncTime(View parentView) {
        // get the last sync time
        SimpleDateFormat sdf = new SimpleDateFormat("hh.mm aa , MMM d", Locale.getDefault());
        if (parentView != null) {
            Date lastSyncTime = getLastSyncTime();
            TextView tvLastSyncTime = parentView.findViewById(R.id.tvSyncTime);
            if (lastSyncTime != null) {
                tvLastSyncTime.setVisibility(View.VISIBLE);
                tvLastSyncTime.setText(sdf.format(lastSyncTime));
            } else {
                tvLastSyncTime.setVisibility(View.INVISIBLE);
            }
        }
    }

    private Date getLastSyncTime() {
        // logic
        return new Date();
    }

    private void updateRegisterCount() {
        if (navigationAdapter != null) {
            int x = 0;
            while (x < navigationModels.size()) {
                switch (navigationModels.get(x).getMenuTitle()) {
                    case Constants.DrawerMenu.ALL_FAMILIES:
                        // execute count of the
                        Integer all_count = 0;//(int) Math.floor(Math.random() * 101);
                        navigationModels.get(x).setRegisterCount(all_count);
                        break;
                    case Constants.DrawerMenu.CHILD_CLIENTS:
                        Integer children_count = 0; //(int) Math.floor(Math.random() * 101);
                        navigationModels.get(x).setRegisterCount(children_count);
                        break;
                }
                x++;
            }

            navigationAdapter.notifyDataSetChanged();
        }
    }

    private void manualSync(Context context, View parentView) {
        Toast.makeText(context, context.getResources().getText(R.string.action_start_sync), Toast.LENGTH_SHORT).show();
        updateLastSyncTime(parentView);
    }

    private void updateCurrentUser() {
        if (tvLogout != null) {
            tvLogout.setText(String.format("%s %s", parentActivity.getResources().getText(R.string.nav_logout_as), currentUser()));
        }
    }

    private String currentUser() {
        String res = "";
        try {
            res = Utils.getPrefferedName().split(" ")[0];
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return res;
    }

    public boolean onBackPressed() {
        boolean res = false;
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
                res = true;
            }
        }
        return res;
    }
}
