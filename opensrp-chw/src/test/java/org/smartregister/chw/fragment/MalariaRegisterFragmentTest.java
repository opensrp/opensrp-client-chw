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
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.family.adapter.FamilyRecyclerViewCustomAdapter;

@RunWith(RobolectricTestRunner.class)
@Config(application = ChwApplication.class, constants = BuildConfig.class, sdk = 22)
public class MalariaRegisterFragmentTest {

    @Mock
    private FamilyRecyclerViewCustomAdapter clientAdapter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testonEmptyMalariaCountIsCalled() {
        MalariaRegisterFragment spyFragment = Mockito.spy(MalariaRegisterFragment.class);
        FragmentActivity activity = Mockito.spy(FragmentActivity.class);
        Mockito.when(spyFragment.getActivity()).thenReturn(activity);

        View emptyView = Mockito.mock(View.class);
        Whitebox.setInternalState(spyFragment, "emptyView", emptyView);
        Whitebox.setInternalState(spyFragment, "clientAdapter", clientAdapter);
        spyFragment.countExecute();

        Mockito.verify(emptyView).setVisibility(Mockito.anyInt());
    }
}
