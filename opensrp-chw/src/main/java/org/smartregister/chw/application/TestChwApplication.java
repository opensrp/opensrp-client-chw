package org.smartregister.chw.application;


import org.koin.core.context.GlobalContextKt;
import org.smartregister.chw.R;
import org.smartregister.chw.repository.ChwRepository;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.repository.Repository;

import java.util.concurrent.Executors;

import timber.log.Timber;


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
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new ChwRepository(getInstance().getApplicationContext(), context);
            }
        } catch (UnsatisfiedLinkError e) {
            Timber.e(e);
        }
        return repository;
    }

    @Override
    public AppExecutors getAppExecutors() {
        return new AppExecutors(Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor());
    }
}
