package org.smartregister.chw.fragment;

import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

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
    public void assertViewLayoutNotNull() {
        Assert.assertNotNull(dueOnlyLayout);
    }

    @Test
    public void testToggleFilterSelectionCallsDueFilterWhenTagIsNull() {
        dueOnlyLayout.setTag(null);
        Mockito.doNothing().when(spy).dueFilter(dueOnlyLayout);
        spy.toggleFilterSelection(dueOnlyLayout);
        Mockito.verify(spy).dueFilter(dueOnlyLayout);
    }

    @Test
    public void testToggleFilterSelectionNeverCallsNormalFilterWhenTagIsNull() {
        dueOnlyLayout.setTag(null);
        Mockito.doNothing().when(spy).dueFilter(dueOnlyLayout);
        spy.toggleFilterSelection(dueOnlyLayout);
        Mockito.verify(spy, Mockito.never()).normalFilter(dueOnlyLayout);
    }

    @Test
    public void testToggleFilterSelectionCallsNormalFilterWhenTagIsPressed() {
        Mockito.when(dueOnlyLayout.getTag()).thenReturn("PRESSED");
        Mockito.doNothing().when(spy).normalFilter(dueOnlyLayout);
        spy.toggleFilterSelection(dueOnlyLayout);
        Mockito.verify(spy).normalFilter(dueOnlyLayout);
    }

    @Test
    public void testToggleFilterSelectionNeverCallsDueFilterWhenTagIsPressed() {
        Mockito.when(dueOnlyLayout.getTag()).thenReturn("PRESSED");
        Mockito.doNothing().when(spy).normalFilter(dueOnlyLayout);
        spy.toggleFilterSelection(dueOnlyLayout);
        Mockito.verify(spy, Mockito.never()).dueFilter(dueOnlyLayout);
    }

    @Test
    public void testSearchTextNull() {
        Assert.assertEquals("", spy.searchText());
    }

}
