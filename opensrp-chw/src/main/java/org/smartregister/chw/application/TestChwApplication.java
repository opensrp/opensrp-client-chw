package org.smartregister.chw.application;


import org.smartregister.chw.R;

/**
 * Created by keyman on 11/03/2019.
 */

public class TestChwApplication extends ChwApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        setTheme(R.style.Theme_AppCompat); //or just R.style.Theme_AppCompat
    }
}
