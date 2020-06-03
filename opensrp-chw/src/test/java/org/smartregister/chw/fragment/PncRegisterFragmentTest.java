package org.smartregister.chw.fragment;

import androidx.fragment.app.FragmentActivity;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.BaseUnitTest;

public class PncRegisterFragmentTest extends BaseUnitTest {

    @Mock
    private FragmentActivity activity;

    private PncRegisterFragment pncRegisterFragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PncRegisterFragment objct = new PncRegisterFragment();
        pncRegisterFragment = Mockito.spy(objct);
    }


    @Test
    public void testInitializePresenter() {
        Mockito.doReturn(activity).when(pncRegisterFragment).getActivity();

        pncRegisterFragment.initializePresenter();
        TestCase.assertNotNull(pncRegisterFragment.presenter());
    }


}


