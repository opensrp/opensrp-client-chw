package org.smartregister.chw.fragment;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.activity.AncHomeVisitActivity;
import org.smartregister.chw.activity.AncMemberProfileActivity;
import org.smartregister.chw.core.presenter.AncRegisterFragmentPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AncRegisterFragmentTest extends BaseUnitTest {
    @Mock
    private Context context;

    @Mock
    private CommonRepository commonRepository;

    @Mock
    private AncRegisterFragmentPresenter presenter;

    private AncRegisterFragment fragment;

    private FragmentActivity activity;

    private ActivityController controller;

    @Mock
    private CommonPersonObjectClient client;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fragment = Mockito.mock(AncRegisterFragment.class, Mockito.CALLS_REAL_METHODS);
        ReflectionHelpers.setField(fragment, "presenter", presenter);

        CoreLibrary.init(context);
        when(context.commonrepository(anyString())).thenReturn(commonRepository);
        controller = Robolectric.buildActivity(AppCompatActivity.class).create().resume();
        activity = (FragmentActivity) controller.get();
    }

    @Test
    public void presenterInitializesWhenActivityIsNull() {
        fragment.initializePresenter();
        Assert.assertNotNull(presenter);
    }

    @Test
    public void presenterInitializesCorrectly() {
        when(fragment.getActivity()).thenReturn(activity);
        fragment.initializePresenter();
        Assert.assertNotNull(presenter);
    }

    @Test
    public void openProfileOpensCorrectly() {
        when(fragment.getActivity()).thenReturn(activity);
        fragment.openProfile(client);
        Activity activity = Mockito.mock(Activity.class);
        AncMemberProfileActivity.startMe(activity, client.getCaseId());
        Mockito.verify(activity).startActivity(Mockito.any());
    }

    @Test
    public void openHomeVisitTestWhenActivityIsNull() {
        fragment.openHomeVisit(client);
    }

    @Test
    public void openHomeVisitTest() {
        when(fragment.getActivity()).thenReturn(activity);
        fragment.openHomeVisit(client);
        Activity activity = Mockito.mock(Activity.class);
        AncHomeVisitActivity.startMe(activity, client.getCaseId(), false);
    }

}
