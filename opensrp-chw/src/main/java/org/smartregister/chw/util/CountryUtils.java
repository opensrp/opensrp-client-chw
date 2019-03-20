package org.smartregister.chw.util;

import android.content.ComponentName;
import android.content.pm.PackageManager;

import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;

public class CountryUtils {

    public static boolean hideNavigationQRCode() {
        return Country.LIBERIA.equals(BuildConfig.BUILD_COUNTRY);
    }

    public static int loginTitle() {
        if (Country.LIBERIA.equals(BuildConfig.BUILD_COUNTRY)) {
            return R.string.liberia_login_title;
        } else if (Country.TANZANIA.equals(BuildConfig.BUILD_COUNTRY)) {
            return R.string.tanzania_login_title;
        } else {
            return R.string.login_title;
        }
    }

    public static int loginLogo() {
        if (Country.LIBERIA.equals(BuildConfig.BUILD_COUNTRY)) {
            return R.drawable.ic_liberia_logo;
        } else if (Country.TANZANIA.equals(BuildConfig.BUILD_COUNTRY)) {
            return R.drawable.tanzania_logo;
        } else {
            return R.drawable.opensrp_logo;
        }
    }

    public static int launcher() {
        if (Country.LIBERIA.equals(BuildConfig.BUILD_COUNTRY)) {
            return R.mipmap.ic_liberia_launcher;
        } else if (Country.TANZANIA.equals(BuildConfig.BUILD_COUNTRY)) {
            return R.mipmap.ic_launcher;
        } else {
            return R.mipmap.ic_launcher;
        }
    }

    public static void switchLoginAlias(PackageManager packageManager) {
        ComponentName liberia = new ComponentName("org.smartregister.chw", "org.smartregister.chw.activity.LoginActivityLiberia");
        ComponentName tanzania = new ComponentName("org.smartregister.chw", "org.smartregister.chw.activity.LoginActivityTanzania");
        if (Country.LIBERIA.equals(BuildConfig.BUILD_COUNTRY)) {
            packageManager.setComponentEnabledSetting(liberia, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            packageManager.setComponentEnabledSetting(tanzania, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        } else if (Country.TANZANIA.equals(BuildConfig.BUILD_COUNTRY)) {
            packageManager.setComponentEnabledSetting(tanzania, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            packageManager.setComponentEnabledSetting(liberia, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        }
    }

    public static int navLogoString() {
        if (Country.LIBERIA.equals(BuildConfig.BUILD_COUNTRY)) {
            return R.string.liberia_nav_logo;
        } else if (Country.TANZANIA.equals(BuildConfig.BUILD_COUNTRY)) {
            return R.string.tanzania_nav_logo;
        } else {
            return R.string.nav_logo;
        }
    }

}
