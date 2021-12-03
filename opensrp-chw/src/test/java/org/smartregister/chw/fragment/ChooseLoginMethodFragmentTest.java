package org.smartregister.chw.fragment;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.PinLoginActivity;

public class ChooseLoginMethodFragmentTest extends BaseUnitTest {
    private ChooseLoginMethodFragment fragment;
    @Mock
    private View view;

    private PinLoginActivity activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fragment = new ChooseLoginMethodFragment();

        ActivityController<PinLoginActivity> controller = Robolectric.buildActivity(PinLoginActivity.class)
                .create()
                .resume();
        activity = controller.get();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content,fragment, null)
                .commitNow();
        controller.visible();
    }

    @Test
    public void testOnClickUsePin() {
        PinLoginActivity spyActivity = Mockito.spy(activity);
        ChooseLoginMethodFragment spyFragment = Mockito.spy(fragment);
        Mockito.doReturn(spyActivity)
                .when(spyFragment)
                .getActivity();

        when(view.getId()).thenReturn(R.id.btnUsePin);
        spyFragment.onClick(view);
        verify(spyActivity).navigateToFragment(eq(SetPinFragment.TAG));
    }

    @Test
    public void testOnClickUsePassword() {
        PinLoginActivity spyActivity = Mockito.spy(activity);
        ChooseLoginMethodFragment spyFragment = Mockito.spy(fragment);
        Mockito.doReturn(spyActivity)
                .when(spyFragment)
                .getActivity();
        Mockito.doNothing()
                .when(spyActivity)
                .startHomeActivity();

        when(view.getId()).thenReturn(R.id.btnUsePassword);
        spyFragment.onClick(view);
        verify(spyActivity).startHomeActivity();
    }

}