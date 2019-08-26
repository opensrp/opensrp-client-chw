package org.smartregister.chw.fragment;

import android.view.View;
import android.widget.LinearLayout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.core.model.FamilyCallDialogModel;

import timber.log.Timber;

@RunWith(RobolectricTestRunner.class)
@Config(application = ChwApplication.class, sdk = 22)
public class FamilyCallDialogFragmentTest {

    @Mock
    FamilyCallDialogFragment familyCallDialogFragment;

    String phoneNumber = "12345Test";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRefreshHeadOfFamilyView() {
        FamilyCallDialogFragment spyFragment = Mockito.spy(FamilyCallDialogFragment.class);
        LinearLayout llFamilyHead = Mockito.spy(LinearLayout.class);
        Whitebox.setInternalState(spyFragment, "llFamilyHead", llFamilyHead);
        Mockito.doNothing().when(llFamilyHead).setVisibility(ArgumentMatchers.anyInt());


        // verify that an invalid model does not show on screen

        FamilyCallDialogModel model = null;
        try {
            spyFragment.refreshHeadOfFamilyView(model);
        } catch (Exception e) {
            Timber.e(e);
        }
        Mockito.verify(llFamilyHead).setVisibility(View.GONE);


        model = new FamilyCallDialogModel();
        try {
            spyFragment.refreshHeadOfFamilyView(model);
        } catch (Exception e) {
            Timber.e(e);
        }
        Mockito.verify(llFamilyHead, Mockito.times(2)).setVisibility(View.GONE);


        // verify a valid model is displayed on screen
        model.setPhoneNumber(phoneNumber);
        Assert.assertTrue(model.getPhoneNumber().equalsIgnoreCase(phoneNumber));
        try {
            spyFragment.refreshHeadOfFamilyView(model);
        } catch (Exception e) {
            Timber.e(e);
        }
        Mockito.verify(llFamilyHead).setVisibility(View.VISIBLE);
    }

    @Test
    public void testRefreshCareGiverView() {
        FamilyCallDialogFragment spyFragment = Mockito.spy(FamilyCallDialogFragment.class);
        LinearLayout llCareGiver = Mockito.spy(LinearLayout.class);
        Whitebox.setInternalState(spyFragment, "llCareGiver", llCareGiver);
        Mockito.doNothing().when(llCareGiver).setVisibility(ArgumentMatchers.anyInt());


        // verify that an invalid model does not show on screen

        FamilyCallDialogModel model = null;
        try {
            spyFragment.refreshCareGiverView(model);
        } catch (Exception e) {
            Timber.e(e);
        }
        Mockito.verify(llCareGiver).setVisibility(View.GONE);


        model = new FamilyCallDialogModel();
        try {
            spyFragment.refreshCareGiverView(model);
        } catch (Exception e) {
            Timber.e(e);
        }
        Mockito.verify(llCareGiver, Mockito.times(2)).setVisibility(View.GONE);


        // verify a valid model is displayed on screen
        model.setPhoneNumber(phoneNumber);
        Assert.assertTrue(model.getPhoneNumber().equalsIgnoreCase(phoneNumber));
        try {
            spyFragment.refreshCareGiverView(model);
        } catch (Exception e) {
            Timber.e(e);
        }
        Mockito.verify(llCareGiver).setVisibility(View.VISIBLE);
    }
}
