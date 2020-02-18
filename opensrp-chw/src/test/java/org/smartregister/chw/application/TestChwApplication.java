package org.smartregister.chw.application;


import net.sqlcipher.database.SQLiteDatabase;

import org.koin.core.context.GlobalContextKt;
import org.robolectric.TestLifecycleApplication;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.repository.Repository;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by keyman on 11/03/2019.
 */

public class TestChwApplication extends ChwApplication implements TestLifecycleApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        CoreLibrary.init(context);
        ConfigurableViewsLibrary.init(context);
        FamilyLibrary.init(context, getMetadata(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        setTheme(R.style.Theme_AppCompat);
    }

    @Override
    public AppExecutors getAppExecutors() {
        return new AppExecutors(Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor());
    }

   /* @Override
    public Repository getRepository() {
        repository = mock(Repository.class);
        SQLiteDatabase sqLiteDatabase = mock(SQLiteDatabase.class);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);

        return repository;
    }*/

    @Override
    public void beforeTest(Method method) {
        //Overridden method to execute before test
    }

    @Override
    public void prepareTest(Object test) {
        //Overridden method for preparing tests
    }

    @Override
    public void afterTest(Method method) {
        GlobalContextKt.stopKoin();
    }
}
