package org.smartregister.chw.presenter;


import android.support.v7.app.AppCompatActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.chw.core.contract.CoreApplication;
import org.smartregister.chw.core.contract.NavigationContract;
import org.smartregister.chw.core.interactor.NavigationInteractor;
import org.smartregister.chw.core.model.NavigationModel;
import org.smartregister.chw.core.presenter.NavigationPresenter;

@RunWith(PowerMockRunner.class)
@PrepareForTest({NavigationModel.class, NavigationInteractor.class})
public class NavigationPresenterTest {

    @Mock
    NavigationModel model;
    @Mock
    NavigationInteractor interactor;
    @Mock
    NavigationModel.Flavor modelFlavor;
    @Mock
    private NavigationContract.View view;
    @Mock
    private NavigationPresenter presenter;
    @Mock
    private CoreApplication application;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(NavigationModel.class);
        PowerMockito.when(NavigationModel.getInstance()).thenReturn(model);

        PowerMockito.mockStatic(NavigationInteractor.class);
        PowerMockito.when(NavigationInteractor.getInstance()).thenReturn(interactor);
    }

    @Test
    public void test_SetUpIsWorking() {
        Assert.assertNotNull(view);
        Assert.assertNotNull(model);
        Assert.assertNotNull(interactor);
    }

    @Test
    public void test_getNavigationView() {
        NavigationContract.Presenter presenter = PowerMockito.spy(new NavigationPresenter(application, view, modelFlavor));
        Assert.assertNotNull(presenter.getNavigationView());
        Assert.assertEquals(view, presenter.getNavigationView());
    }

    @Test
    public void test_refreshLastSyncModifiesView() {
        NavigationContract.Presenter presenter = PowerMockito.spy(new NavigationPresenter(application, view, modelFlavor));
        presenter.refreshLastSync();
        Mockito.verify(view).refreshLastSync(interactor.getLastSync());
    }

    @Test
    public void test_displayCurrentUserUpdatesView() {
        NavigationContract.Presenter presenter = PowerMockito.spy(new NavigationPresenter(application, view, modelFlavor));
        presenter.displayCurrentUser();
        Mockito.verify(view).refreshCurrentUser(model.getCurrentUser());
    }

    @Test
    public void test_getOptionsRequestModel() {
        NavigationContract.Presenter presenter = PowerMockito.spy(new NavigationPresenter(application, view, modelFlavor));

        presenter.getOptions();
        Mockito.verify(model).getNavigationItems();
    }

    @Test
    @Ignore // Needs a better implementation
    public void test_Sync() {
        NavigationContract.Presenter presenter = PowerMockito.spy(new NavigationPresenter(application, view, modelFlavor));
        AppCompatActivity activity = Mockito.mock(AppCompatActivity.class);

        presenter.sync(activity);
        Mockito.verify(interactor).sync();
    }


    @Test
    public void test_refreshNavigationCount() {
        NavigationContract.Presenter presenter = PowerMockito.spy(new NavigationPresenter(application, view, modelFlavor));
        AppCompatActivity activity = Mockito.mock(AppCompatActivity.class);

        presenter.refreshNavigationCount(activity);

        // that the model is requested
        Mockito.verify(model).getNavigationItems();
    }
}
