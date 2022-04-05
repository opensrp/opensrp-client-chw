package org.smartregister.chw.fragment;

import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

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
import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.presenter.FamilyRegisterFragmentPresenter;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class FamilyRegisterFragmentTest extends BaseUnitTest {
    @Mock
    private CommonRepository commonRepository;
    @Mock
    private Context context;

    private FamilyRegisterFragment fragment;
    @Mock
    private FamilyRegisterFragmentPresenter presenter;
    @Mock
    private View view;

    private FragmentActivity activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fragment = Mockito.mock(FamilyRegisterFragment.class, Mockito.CALLS_REAL_METHODS);
        ReflectionHelpers.setField(fragment, "presenter", presenter);
        ReflectionHelpers.setField(fragment, "view", view);
        ReflectionHelpers.setField(fragment, "dueOnlyLayout", view);
        ReflectionHelpers.setField(fragment, "dueFilterActive", true);

        CoreLibrary.init(context);
        when(context.commonrepository(anyString())).thenReturn(commonRepository);
        activity = Robolectric.buildActivity(AppCompatActivity.class).create().resume().get();
        Context.bindtypes = new ArrayList<>();
        SyncStatusBroadcastReceiver.init(activity);
    }

    @Test
    public void testSetUpViewsWithDisableChildRegistrationTitleGoBack() {
        when(fragment.getActivity()).thenReturn(activity);
        when(fragment.getContext()).thenReturn(activity);
        View view = LayoutInflater.from(activity).inflate(org.smartregister.chw.core.R.layout.fragment_base_register, null);
        fragment.setupViews(view);

        View titleLayout = view.findViewById(R.id.title_layout);
        boolean disableGoBack = ChwApplication.getApplicationFlavor().disableTitleClickGoBack();
        assertTrue(!disableGoBack || (disableGoBack && !titleLayout.hasOnClickListeners()));
    }
}
