package org.smartregister.chw.util;

import android.content.ComponentName;
import android.content.pm.PackageManager;

import org.smartregister.AllConstants;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.repository.AllSharedPreferences;

import java.util.ArrayList;

public class CountryUtils {

    private static final ArrayList<String> LIBERIA_ALLOWED_LEVELS;
    private static final ArrayList<String> TANZANIA_ALLOWED_LEVELS;

    private static final String CHA = "CHA";
    private static final String CHSS = "CHSS";
    private static final String CLINIC = "Clinic";
    private static final String VILLAGE = "Village";
    private static final String FACILITY = "MOH Jhpiego Facility Name";

    static {
        LIBERIA_ALLOWED_LEVELS = new ArrayList<>();
        LIBERIA_ALLOWED_LEVELS.add(CLINIC);
        LIBERIA_ALLOWED_LEVELS.add(CHSS);
        LIBERIA_ALLOWED_LEVELS.add(CHA);

        TANZANIA_ALLOWED_LEVELS = new ArrayList<>();
        TANZANIA_ALLOWED_LEVELS.add(FACILITY);
        TANZANIA_ALLOWED_LEVELS.add(VILLAGE);
    }

    public static boolean hideNavigationQRCode() {
        return Country.LIBERIA.equals(BuildConfig.BUILD_COUNTRY);
    }

    public static int loginTitle() {
        if (Country.LIBERIA.equals(BuildConfig.BUILD_COUNTRY)) {
            return R.string.liberia_login_title;
        } else if (Country.TANZANIA.equals(BuildConfig.BUILD_COUNTRY)) {
            return R.string.tanzania_login_title;
        }
        return R.string.login_title;
    }

    public static int loginLogo() {
        if (Country.LIBERIA.equals(BuildConfig.BUILD_COUNTRY)) {
            return R.drawable.ic_liberia_logo;
        } else if (Country.TANZANIA.equals(BuildConfig.BUILD_COUNTRY)) {
            return R.drawable.tanzania_logo;
        }
        return R.drawable.opensrp_logo;
    }

    public static int launcher() {
        if (Country.LIBERIA.equals(BuildConfig.BUILD_COUNTRY)) {
            return R.mipmap.ic_liberia_launcher;
        } else if (Country.TANZANIA.equals(BuildConfig.BUILD_COUNTRY)) {
            return R.mipmap.ic_launcher;
        }
        return R.mipmap.ic_launcher;
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
        }
        return R.string.nav_logo;
    }

    public static ArrayList<String> allowedLevels() {
        if (Country.LIBERIA.equals(BuildConfig.BUILD_COUNTRY)) {
            return LIBERIA_ALLOWED_LEVELS;
        } else if (Country.TANZANIA.equals(BuildConfig.BUILD_COUNTRY)) {
            return TANZANIA_ALLOWED_LEVELS;
        }
        return LIBERIA_ALLOWED_LEVELS;
    }

    public static String defaultLevel() {
        if (Country.LIBERIA.equals(BuildConfig.BUILD_COUNTRY)) {
            return CHA;
        } else if (Country.TANZANIA.equals(BuildConfig.BUILD_COUNTRY)) {
            return VILLAGE;
        }
        return CHA;
    }

    public static void setOpenSRPUrl() {
        AllSharedPreferences preferences = Utils.getAllSharedPreferences();

        String opensrpUrl = "";
        if (BuildConfig.DEBUG) {
            if (Country.LIBERIA.equals(BuildConfig.BUILD_COUNTRY)) {
                opensrpUrl = "https://unicefwcaro-stage.smartregister.org/opensrp/";
            } else if (Country.TANZANIA.equals(BuildConfig.BUILD_COUNTRY)) {
                opensrpUrl = "https://boresha-afya-stage.smartregister.org/opensrp/";
            }
        } else {
            if (Country.LIBERIA.equals(BuildConfig.BUILD_COUNTRY)) {
                opensrpUrl = "https://unicefwcaro.smartregister.org/opensrp/";
            } else if (Country.TANZANIA.equals(BuildConfig.BUILD_COUNTRY)) {
                opensrpUrl = "https://boresha-afya.smartregister.org/opensrp/";
            }
        }

        preferences.savePreference(AllConstants.DRISHTI_BASE_URL, opensrpUrl);
    }

}
