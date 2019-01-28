package org.smartgresiter.wcaro.listener;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import org.smartgresiter.wcaro.activity.FamilyProfileActivity;
import org.smartgresiter.wcaro.activity.FamilyRegisterActivity;
import org.smartgresiter.wcaro.activity.JobAidsActivity;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.AllConstants;
import org.smartregister.listener.BottomNavigationListener;
import org.smartregister.util.PermissionUtils;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BarcodeScanActivity;

public class JobsAidsBottomNavigationListener extends BottomNavigationListener {
    private Activity context;

    public JobsAidsBottomNavigationListener(Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        super.onNavigationItemSelected(item);

        if (item.getItemId() == org.smartregister.family.R.id.action_family) {
            Intent intent = new Intent(context, FamilyRegisterActivity.class);
            context.startActivity(intent);

        } else if (item.getItemId() == org.smartregister.family.R.id.action_scan_qr) {

            if (PermissionUtils.isPermissionGranted(context, Manifest.permission.CAMERA, PermissionUtils.CAMERA_PERMISSION_REQUEST_CODE)) {
                try {
                    Intent intent = new Intent(context, BarcodeScanActivity.class);
                    context.startActivityForResult(intent, AllConstants.BARCODE.BARCODE_REQUEST_CODE);
                } catch (SecurityException e) {
                    Utils.showToast(context, context.getString(org.smartregister.R.string.allow_camera_management));
                }
            }

        } else if (item.getItemId() == org.smartregister.family.R.id.action_register) {

            try {
                ((FamilyRegisterActivity) context).startRegistration();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return true;
    }
}
