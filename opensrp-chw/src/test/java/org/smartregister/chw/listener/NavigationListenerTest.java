package org.smartregister.chw.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.smartregister.chw.activity.*;
import org.smartregister.chw.adapter.NavigationAdapter;
import org.smartregister.chw.util.Constants;

import static org.mockito.Mockito.*;

public class NavigationListenerTest {
    @Mock
    protected Activity activity = mock(Activity.class);

    @Mock
    protected View view = mock(View.class);
    @Spy
    protected NavigationListener navigationListenerSpy;
    @Mock
    private NavigationAdapter navigationAdapter = mock(NavigationAdapter.class);

    @Before
    public void setUp() {
        NavigationListener navigationListener = new NavigationListener(activity, navigationAdapter);
        navigationListenerSpy = spy(navigationListener);
    }

    @Test
    public void testOnClickWithChildClients() {
        when(view.getTag()).thenReturn(Constants.DrawerMenu.CHILD_CLIENTS);
        doNothing().when(navigationListenerSpy).startRegisterActivity(ChildRegisterActivity.class);
        navigationListenerSpy.onClick(view);
        verify(navigationListenerSpy).startRegisterActivity(ChildRegisterActivity.class);
    }

    @Test
    public void testOnClickWithAllFamilies() {
        when(view.getTag()).thenReturn(Constants.DrawerMenu.ALL_FAMILIES);
        doNothing().when(navigationListenerSpy).startRegisterActivity(FamilyRegisterActivity.class);
        navigationListenerSpy.onClick(view);
        verify(navigationListenerSpy).startRegisterActivity(FamilyRegisterActivity.class);
    }

    @Test
    public void testOnClickWithAnc() {
        when(view.getTag()).thenReturn(Constants.DrawerMenu.ANC);
        doNothing().when(navigationListenerSpy).startRegisterActivity(AncRegisterActivity.class);
        navigationListenerSpy.onClick(view);
        verify(navigationListenerSpy).startRegisterActivity(AncRegisterActivity.class);
    }

    @Test
    public void testOnClickWithPnc() {
        when(view.getTag()).thenReturn(Constants.DrawerMenu.PNC);
        doNothing().when(navigationListenerSpy).startRegisterActivity(PncRegisterActivity.class);
        navigationListenerSpy.onClick(view);
        verify(navigationListenerSpy).startRegisterActivity(PncRegisterActivity.class);
    }

    @Test
    public void testOnClickWithMalaria() {
        when(view.getTag()).thenReturn(Constants.DrawerMenu.MALARIA);
        doNothing().when(navigationListenerSpy).startRegisterActivity(MalariaRegisterActivity.class);
        navigationListenerSpy.onClick(view);
        verify(navigationListenerSpy).startRegisterActivity(MalariaRegisterActivity.class);
    }

    @Test
    public void testStartRegisterActivity() {
        Intent intent = new Intent(activity, MalariaRegisterActivity.class);
        navigationListenerSpy.startRegisterActivity(MalariaRegisterActivity.class);

//        verify(activity).startActivity(intent);
        verify(activity).overridePendingTransition(anyInt(), anyInt());
        verify(activity).finish();
    }

}
