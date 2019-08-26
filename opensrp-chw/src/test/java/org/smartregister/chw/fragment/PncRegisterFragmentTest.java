package org.smartregister.chw.fragment;

import android.support.v4.app.FragmentActivity;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.smartregister.chw.BaseUnitTest;

@PrepareForTest(PncRegisterFragment.class)
public class PncRegisterFragmentTest extends BaseUnitTest {

    @Mock
    private FragmentActivity activity;

    private PncRegisterFragment pncRegisterFragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PncRegisterFragment objct = new PncRegisterFragment();
        pncRegisterFragment = PowerMockito.spy(objct);
    }


    @Test
    public void testInitializePresenter() {
        Mockito.doReturn(activity).when(pncRegisterFragment).getActivity();

        pncRegisterFragment.initializePresenter();
        TestCase.assertNotNull(pncRegisterFragment.presenter());
    }


}


