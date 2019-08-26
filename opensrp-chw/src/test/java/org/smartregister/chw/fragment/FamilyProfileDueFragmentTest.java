package org.smartregister.chw.fragment;

import android.support.v4.app.FragmentActivity;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.family.adapter.FamilyRecyclerViewCustomAdapter;

@RunWith(RobolectricTestRunner.class)
@Config(application = ChwApplication.class, sdk = 22)
public class FamilyProfileDueFragmentTest {

    @Mock
    private FamilyRecyclerViewCustomAdapter clientAdapter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testonEmptyRegisterCountIsCalled() {
        FamilyProfileDueFragment spyFragment = Mockito.spy(FamilyProfileDueFragment.class);
        FragmentActivity activity = Mockito.spy(FragmentActivity.class);
        Mockito.when(spyFragment.getActivity()).thenReturn(activity);

        View emptyView = Mockito.mock(View.class);
        Whitebox.setInternalState(spyFragment, "emptyView", emptyView);
        Whitebox.setInternalState(spyFragment, "clientAdapter", clientAdapter);
        spyFragment.countExecute();

        Mockito.verify(emptyView).setVisibility(Mockito.anyInt());
    }

    @Test
    public void testonEmptyRegisterCountHidesViewHideView() {
        FamilyProfileDueFragment spyFragment = Mockito.spy(FamilyProfileDueFragment.class);
        FragmentActivity activity = Mockito.spy(FragmentActivity.class);
        Mockito.when(spyFragment.getActivity()).thenReturn(activity);
        View emptyView = Mockito.mock(View.class);
        Whitebox.setInternalState(spyFragment, "emptyView", emptyView);
        spyFragment.onEmptyRegisterCount(false);

        Mockito.verify(emptyView).setVisibility(View.GONE);
    }

    @Test
    public void testonEmptyRegisterCountHidesViewShowsView() {
        FamilyProfileDueFragment spyFragment = Mockito.spy(FamilyProfileDueFragment.class);
        FragmentActivity activity = Mockito.spy(FragmentActivity.class);
        Mockito.when(spyFragment.getActivity()).thenReturn(activity);
        View emptyView = Mockito.mock(View.class);
        Whitebox.setInternalState(spyFragment, "emptyView", emptyView);
        spyFragment.onEmptyRegisterCount(true);

        Mockito.verify(emptyView).setVisibility(View.VISIBLE);
    }
}
