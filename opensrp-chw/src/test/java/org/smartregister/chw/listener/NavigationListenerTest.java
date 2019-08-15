package org.smartregister.chw.listener;

import android.app.Activity;
import android.view.View;

import com.opensrp.chw.core.adapter.NavigationAdapter;
import com.opensrp.chw.core.listener.NavigationListener;
import com.opensrp.chw.core.utils.CoreConstants;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.smartregister.chw.activity.AncRegisterActivity;
import org.smartregister.chw.activity.ChildRegisterActivity;
import org.smartregister.chw.activity.FamilyRegisterActivity;
import org.smartregister.chw.activity.MalariaRegisterActivity;
import org.smartregister.chw.activity.PncRegisterActivity;
import org.smartregister.chw.util.Constants;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        Map<String, Class> registeredActivities = new HashMap<>();
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.ANC_REGISTER_ACTIVITY, AncRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.FAMILY_REGISTER_ACTIVITY, FamilyRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.CHILD_REGISTER_ACTIVITY, ChildRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.PNC_REGISTER_ACTIVITY, PncRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.MALARIA_REGISTER_ACTIVITY, MalariaRegisterActivity.class);

        Mockito.doReturn(registeredActivities).when(navigationAdapter).getRegisteredActivities();
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
        navigationListenerSpy.startRegisterActivity(MalariaRegisterActivity.class);

        verify(activity).overridePendingTransition(anyInt(), anyInt());
        verify(activity).finish();
    }

}
