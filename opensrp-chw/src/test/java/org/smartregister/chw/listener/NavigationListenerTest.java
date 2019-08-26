package org.smartregister.chw.listener;

import android.app.Activity;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.smartregister.chw.activity.AncRegisterActivity;
import org.smartregister.chw.activity.ChildRegisterActivity;
import org.smartregister.chw.activity.FamilyRegisterActivity;
import org.smartregister.chw.activity.MalariaRegisterActivity;
import org.smartregister.chw.activity.PncRegisterActivity;
import org.smartregister.chw.core.activity.BaseReferralRegister;
import org.smartregister.chw.core.adapter.NavigationAdapter;
import org.smartregister.chw.core.listener.NavigationListener;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class NavigationListenerTest {

    @Mock
    protected Activity activity = Mockito.mock(Activity.class);
    @Mock
    protected View view = Mockito.mock(View.class);
    @Spy
    protected NavigationListener navigationListenerSpy;
    @Mock
    private NavigationAdapter navigationAdapter = Mockito.mock(NavigationAdapter.class);

    @Before
    public void setUp() {
        NavigationListener navigationListener = new NavigationListener(activity, navigationAdapter);
        navigationListenerSpy = Mockito.spy(navigationListener);

        Map<String, Class> registeredActivities = new HashMap<>();
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.ANC_REGISTER_ACTIVITY, AncRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.FAMILY_REGISTER_ACTIVITY, FamilyRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.CHILD_REGISTER_ACTIVITY, ChildRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.PNC_REGISTER_ACTIVITY, PncRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.MALARIA_REGISTER_ACTIVITY, MalariaRegisterActivity.class);
        registeredActivities.put(CoreConstants.REGISTERED_ACTIVITIES.REFERRALS_REGISTER_ACTIVITY, BaseReferralRegister.class);

        Mockito.doReturn(registeredActivities).when(navigationAdapter).getRegisteredActivities();
    }

    @Test
    public void testOnClickWithChildClients() {
        Mockito.when(view.getTag()).thenReturn(Constants.DrawerMenu.CHILD_CLIENTS);
        Mockito.doNothing().when(navigationListenerSpy).startRegisterActivity(ChildRegisterActivity.class);
        navigationListenerSpy.onClick(view);
        Mockito.verify(navigationListenerSpy).startRegisterActivity(ChildRegisterActivity.class);
    }

    @Test
    public void testOnClickWithAllFamilies() {
        Mockito.when(view.getTag()).thenReturn(Constants.DrawerMenu.ALL_FAMILIES);
        Mockito.doNothing().when(navigationListenerSpy).startRegisterActivity(FamilyRegisterActivity.class);
        navigationListenerSpy.onClick(view);
        Mockito.verify(navigationListenerSpy).startRegisterActivity(FamilyRegisterActivity.class);
    }

    @Test
    public void testOnClickWithAnc() {
        Mockito.when(view.getTag()).thenReturn(Constants.DrawerMenu.ANC);
        Mockito.doNothing().when(navigationListenerSpy).startRegisterActivity(AncRegisterActivity.class);
        navigationListenerSpy.onClick(view);
        Mockito.verify(navigationListenerSpy).startRegisterActivity(AncRegisterActivity.class);
    }

    @Test
    public void testOnClickWithPnc() {
        Mockito.when(view.getTag()).thenReturn(Constants.DrawerMenu.PNC);
        Mockito.doNothing().when(navigationListenerSpy).startRegisterActivity(PncRegisterActivity.class);
        navigationListenerSpy.onClick(view);
        Mockito.verify(navigationListenerSpy).startRegisterActivity(PncRegisterActivity.class);
    }

    @Test
    public void testOnClickWithMalaria() {
        Mockito.when(view.getTag()).thenReturn(Constants.DrawerMenu.MALARIA);
        Mockito.doNothing().when(navigationListenerSpy).startRegisterActivity(MalariaRegisterActivity.class);
        navigationListenerSpy.onClick(view);
        Mockito.verify(navigationListenerSpy).startRegisterActivity(MalariaRegisterActivity.class);
    }

    @Test
    public void testStartRegisterActivity() {
        navigationListenerSpy.startRegisterActivity(MalariaRegisterActivity.class);

        Mockito.verify(activity).overridePendingTransition(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.verify(activity).finish();
    }

}
