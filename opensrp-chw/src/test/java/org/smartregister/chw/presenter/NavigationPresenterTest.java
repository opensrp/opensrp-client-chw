package org.smartregister.chw.presenter;


import android.support.v7.app.AppCompatActivity;

import org.smartregister.chw.core.contract.CoreApplication;
import org.smartregister.chw.core.contract.NavigationContract;
import org.smartregister.chw.core.interactor.NavigationInteractor;
import org.smartregister.chw.core.model.NavigationModel;
import org.smartregister.chw.core.presenter.NavigationPresenter;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

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
        when(NavigationModel.getInstance()).thenReturn(model);

        PowerMockito.mockStatic(NavigationInteractor.class);
        when(NavigationInteractor.getInstance()).thenReturn(interactor);
    }

    @Test
    public void test_SetUpIsWorking() {
        assertNotNull(view);
        assertNotNull(model);
        assertNotNull(interactor);
    }

    @Test
    public void test_getNavigationView() {
        NavigationContract.Presenter presenter = PowerMockito.spy(new NavigationPresenter(application, view, modelFlavor));
        assertNotNull(presenter.getNavigationView());
        assertEquals(view, presenter.getNavigationView());
    }

    @Test
    public void test_refreshLastSyncModifiesView() {
        NavigationContract.Presenter presenter = PowerMockito.spy(new NavigationPresenter(application, view, modelFlavor));
        presenter.refreshLastSync();
        verify(view).refreshLastSync(interactor.getLastSync());
    }

    @Test
    public void test_displayCurrentUserUpdatesView() {
        NavigationContract.Presenter presenter = PowerMockito.spy(new NavigationPresenter(application, view, modelFlavor));
        presenter.displayCurrentUser();
        verify(view).refreshCurrentUser(model.getCurrentUser());
    }

    @Test
    public void test_getOptionsRequestModel() {
        NavigationContract.Presenter presenter = PowerMockito.spy(new NavigationPresenter(application, view, modelFlavor));

        presenter.getOptions();
        verify(model).getNavigationItems();
    }

    @Test
    @Ignore // Needs a better implementation
    public void test_Sync() {
        NavigationContract.Presenter presenter = PowerMockito.spy(new NavigationPresenter(application, view, modelFlavor));
        AppCompatActivity activity = Mockito.mock(AppCompatActivity.class);

        presenter.sync(activity);
        verify(interactor).Sync();
    }


    @Test
    public void test_refreshNavigationCount() {
        NavigationContract.Presenter presenter = PowerMockito.spy(new NavigationPresenter(application, view, modelFlavor));
        AppCompatActivity activity = Mockito.mock(AppCompatActivity.class);

        presenter.refreshNavigationCount(activity);

        // that the model is requested
        verify(model).getNavigationItems();
    }
}
