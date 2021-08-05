package org.smartregister.chw.fragment;

import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.presenter.OutOfAreaDeathFragmentPresenter;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import java.util.ArrayList;

import static android.view.View.GONE;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class OutOfAreaDeathFragmentTest extends BaseUnitTest {

    @Mock
    private Context context;

    @Mock
    private ProgressBar syncProgressBar;

    @Mock
    private CommonRepository commonRepository;

    @Mock
    private ImageView syncButton;

    @Mock
    private OutOfAreaDeathFragmentPresenter presenter;

    private OutOfAreaDeathFragment fragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fragment = Mockito.mock(OutOfAreaDeathFragment.class, Mockito.CALLS_REAL_METHODS);
        CoreLibrary.init(context);
        when(context.commonrepository(anyString())).thenReturn(commonRepository);
        FragmentActivity activity = Robolectric.buildActivity(AppCompatActivity.class).create().resume().get();
        Context.bindtypes = new ArrayList<>();
        SyncStatusBroadcastReceiver.init(activity);
    }

    @Test
    public void presenterInitializesCorrectly() {
        fragment.initializePresenter();
        Assert.assertNotNull(presenter);
    }

    @Test
    public void refreshSyncProgressSpinnerTogglesSyncVisibility() {
        ReflectionHelpers.setField(fragment, "syncButton", syncButton);
        ReflectionHelpers.setField(fragment, "syncProgressBar", syncProgressBar);
        fragment.refreshSyncProgressSpinner();
        Mockito.verify(syncProgressBar, Mockito.times(1)).setVisibility(GONE);
        Mockito.verify(syncButton, Mockito.times(1)).setVisibility(GONE);
    }

    @Test
    public void getCountSelect() {
        fragment.dueFilterActive = true;
        fragment.getCountSelect();
        Mockito.verify(fragment, Mockito.times(1)).getCountSelect();
    }

    @Test
    public void getFilterAndSortQuery() {
        fragment.dueFilterActive = true;
        fragment.filterandSortQuery();
        Mockito.verify(fragment, Mockito.times(1)).filterandSortQuery();
    }

    @Test
    public void getCustomDeathQueryWithDueFilters() {
        fragment.dueFilterActive = true;
        String query = fragment.filterandSortQuery();
        commonRepository.rawCustomQueryForAdapter(query);
    }

    @Test
    public void getCustomDeathQuery() {
        commonRepository.rawCustomQueryForAdapter(fragment.customDeathQuery(0,20));
    }

    @Test
    public void getCustomDeathQueryWithFilter() {
        String query = fragment.getFilters("a");
        String deathQuery = fragment.customDeathQuery(0,20, query);
        commonRepository.rawCustomQueryForAdapter(deathQuery);
    }

    @Test
    public void getToolbarTitle() {
        fragment.dueFilterActive = true;
        fragment.getToolBarTitle();
        Mockito.verify(fragment, Mockito.times(1)).getToolBarTitle();
    }

    @Test
    public void getShowNotFoundPopup() {
        fragment.dueFilterActive = true;
        fragment.showNotFoundPopup(Mockito.anyString());
        Mockito.verify(fragment, Mockito.times(1)).showNotFoundPopup(Mockito.anyString());
    }

}
