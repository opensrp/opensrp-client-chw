package org.smartgresiter.wcaro.application;


import org.smartgresiter.wcaro.R;

/**
 * Created by keyman on 11/03/2019.
 */

public class TestWcaroApplication extends WcaroApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        setTheme(R.style.Theme_AppCompat); //or just R.style.Theme_AppCompat
    }
}
