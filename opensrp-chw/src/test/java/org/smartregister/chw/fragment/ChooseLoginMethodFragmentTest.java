package org.smartregister.chw.fragment;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import junit.framework.TestCase;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;

import java.util.ArrayList;

public class ChooseLoginMethodFragmentTest extends TestCase {
    @Mock
    private CommonRepository commonRepository;
    @Mock
    private Context context;
    private ChooseLoginMethodFragment fragment;
    @Mock
    private View view;

    private FragmentActivity activity;
    private ActivityController<AppCompatActivity> controller;

    @Mock
    private LayoutInflater layoutInflater;

    @Mock
    private ViewGroup viewGroup;

    @Mock
    private Bundle bundle;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fragment = Mockito.mock(ChooseLoginMethodFragment.class, Mockito.CALLS_REAL_METHODS);
       // ReflectionHelpers.setField(fragment, "presenter", presenter);
        //ReflectionHelpers.setField(fragment, "view", view);
       // ReflectionHelpers.setField(fragment, "dueOnlyLayout", view);
       // ReflectionHelpers.setField(fragment, "dueFilterActive", true);

        CoreLibrary.init(context);
        when(context.commonrepository(anyString())).thenReturn(commonRepository);
        controller = Robolectric.buildActivity(AppCompatActivity.class).create().resume();
        activity = controller.get();
        Context.bindtypes = new ArrayList<>();
        //SyncStatusBroadcastReceiver.init(activity);


    }
    public void testOnCreateView() {
    }

    public void testOnClick() {
    }

    public void testOnResume() {
    }
}