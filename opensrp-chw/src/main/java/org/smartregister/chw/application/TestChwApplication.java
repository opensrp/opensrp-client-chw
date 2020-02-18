package org.smartregister.chw.application;


import org.koin.core.context.GlobalContextKt;
import org.smartregister.chw.R;
import org.smartregister.family.util.AppExecutors;

import java.util.concurrent.Executors;


/**
 * Created by keyman on 11/03/2019.
 */

public class TestChwApplication extends ChwApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        setTheme(R.style.Theme_AppCompat);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        GlobalContextKt.stopKoin();
    }

    @Override
    public AppExecutors getAppExecutors() {
        return new AppExecutors(Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor());
    }
}
