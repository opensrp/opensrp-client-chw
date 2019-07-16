package org.smartregister.chw.fragment;

import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

public class MalariaRegisterFragmentTest {
    private MalariaRegisterFragment malariaRegisterFragment = new MalariaRegisterFragment();

    @Spy
    private MalariaRegisterFragment spy;

    @Mock
    private View dueOnlyLayout;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        spy = Mockito.spy(malariaRegisterFragment);
    }

    @Test
    public void testToggleFilterSelectionCallsDueFilterWhenTagIsNull() {
        dueOnlyLayout.setTag(null);
        doNothing().when(spy).dueFilter(dueOnlyLayout);
        spy.toggleFilterSelection(dueOnlyLayout);
        Mockito.verify(spy).dueFilter(dueOnlyLayout);
    }

    @Test
    public void testToggleFilterSelectionNeverCallsNormalFilterWhenTagIsNull() {
        dueOnlyLayout.setTag(null);
        doNothing().when(spy).dueFilter(dueOnlyLayout);
        spy.toggleFilterSelection(dueOnlyLayout);
        Mockito.verify(spy, never()).normalFilter(dueOnlyLayout);
    }

    @Test
    public void testToggleFilterSelectionCallsNormalFilterWhenTagIsPressed() {
        when(dueOnlyLayout.getTag()).thenReturn("PRESSED");
        doNothing().when(spy).normalFilter(dueOnlyLayout);
        spy.toggleFilterSelection(dueOnlyLayout);
        Mockito.verify(spy).normalFilter(dueOnlyLayout);
    }

    @Test
    public void testToggleFilterSelectionCallsDueFilterWhenTagIsPressed() {
        when(dueOnlyLayout.getTag()).thenReturn("PRESSED");
        doNothing().when(spy).normalFilter(dueOnlyLayout);
        spy.toggleFilterSelection(dueOnlyLayout);
        Mockito.verify(spy, never()).dueFilter(dueOnlyLayout);
    }
}
