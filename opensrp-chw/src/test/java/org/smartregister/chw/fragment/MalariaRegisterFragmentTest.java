package org.smartregister.chw.fragment;

import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.view.fragment.BaseRegisterFragment;

import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;

//import static org.mockito.ArgumentMatchers.anyBoolean;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.verify;

//@RunWith(RobolectricTestRunner.class)
//@Config(application = ChwApplication.class, constants = BuildConfig.class, sdk = 22)
public class MalariaRegisterFragmentTest {
    @Mock
    private BaseRegisterFragment baseRegisterFragment;
    @Mock
    private MalariaRegisterFragment malariaRegisterFragment;

    @Mock
    private View dueOnlyLayout;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testDueFilter() throws Exception {
        malariaRegisterFragment.dueFilter(dueOnlyLayout);
//        verify(baseRegisterFragment).filter(anyString(), anyString(), anyString(), anyBoolean());
//        verify(dueOnlyLayout).setTag(anyString());
//        PowerMockito.verifyPrivate(malariaRegisterFragment).invoke("switchViews", dueOnlyLayout, anyBoolean());
    }

    @Test
    public void testToggleFilterSelection() {
        dueOnlyLayout.setTag(isNull());
//        malariaRegisterFragment.toggleFilterSelection(dueOnlyLayout);
//        Mockito.verify(malariaRegisterFragment).dueFilter(dueOnlyLayout);
    }

    @Test
    public void testToggleFilterSelectionNeverCallsNormalFilter() {
        Whitebox.setInternalState(malariaRegisterFragment, "dueOnlyLayout", dueOnlyLayout);
        if (dueOnlyLayout.getTag() == null) {
            Mockito.verify(malariaRegisterFragment, never()).normalFilter(dueOnlyLayout);
        }
    }

    @Test
    public void testToggleFilterSelectionWhenDueOnlyLayoutHasTagNeverCallsDueFilter() {
        Whitebox.setInternalState(malariaRegisterFragment, "dueOnlyLayout", dueOnlyLayout);
//        dueOnlyLayout.setTag("PRESSED");
//        if (dueOnlyLayout.getTag().toString().equals("PRESSED")) {
//            Mockito.verify(malariaRegisterFragment, never()).dueFilter(dueOnlyLayout);
//        }

    }
}
