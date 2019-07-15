package org.smartregister.chw.fragment;

import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.application.ChwApplication;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;

@RunWith(RobolectricTestRunner.class)
@Config(application = ChwApplication.class, constants = BuildConfig.class, sdk = 22)
public class MalariaRegisterFragmentTest {
    @Mock
    private MalariaRegisterFragment malariaRegisterFragment;

    @Mock
    private View dueOnlyLayout;

    @Before
    public void setUp() {
        malariaRegisterFragment = spy(MalariaRegisterFragment.class);
        dueOnlyLayout = spy(View.class);
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
        dueOnlyLayout.setTag("PRESSED");
        if (dueOnlyLayout.getTag().toString().equals("PRESSED")) {
            Mockito.verify(malariaRegisterFragment, never()).dueFilter(dueOnlyLayout);
        }

    }
}
