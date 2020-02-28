package org.smartregister.chw.fragment;

import android.database.MatrixCursor;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.activity.FamilyProfileActivity;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.adapter.FamilyRecyclerViewCustomAdapter;

@RunWith(RobolectricTestRunner.class)
@Config(application = ChwApplication.class, sdk = 22)
public class FamilyProfileDueFragmentTest {

    @Mock
    private FamilyRecyclerViewCustomAdapter clientAdapter;

    @Mock
    private CommonRepository commonRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testonEmptyRegisterCountIsCalled() {
        FamilyProfileDueFragment spyFragment = Mockito.spy(FamilyProfileDueFragment.class);
        FamilyProfileActivity activity = Mockito.spy(FamilyProfileActivity.class);

        Mockito.doReturn(activity).when(spyFragment).getActivity();
        Mockito.doReturn(commonRepository).when(spyFragment).commonRepository();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"count(*)"});
        matrixCursor.addRow(new Object[]{67F});
        Mockito.doReturn(matrixCursor).when(commonRepository).rawCustomQueryForAdapter(Mockito.anyString());


        ReflectionHelpers.setField(spyFragment,"countSelect","select * from ec_family");
        ReflectionHelpers.setField(spyFragment, "clientAdapter", clientAdapter);
        spyFragment.countExecute();

        Mockito.verify(spyFragment).onEmptyRegisterCount(Mockito.anyBoolean());
    }

    @Test
    public void testonEmptyRegisterCountHidesViewHideView() {
        FamilyProfileDueFragment spyFragment = Mockito.spy(FamilyProfileDueFragment.class);
        FragmentActivity activity = Mockito.spy(FragmentActivity.class);
        Mockito.when(spyFragment.getActivity()).thenReturn(activity);
        View emptyView = Mockito.mock(View.class);
        ReflectionHelpers.setField(spyFragment, "emptyView", emptyView);
        spyFragment.onEmptyRegisterCount(false);

        Mockito.verify(emptyView).setVisibility(View.GONE);
    }

    @Test
    public void testonEmptyRegisterCountHidesViewShowsView() {
        FamilyProfileDueFragment spyFragment = Mockito.spy(FamilyProfileDueFragment.class);
        FragmentActivity activity = Mockito.spy(FragmentActivity.class);
        Mockito.when(spyFragment.getActivity()).thenReturn(activity);
        View emptyView = Mockito.mock(View.class);
        ReflectionHelpers.setField(spyFragment, "emptyView", emptyView);
        spyFragment.onEmptyRegisterCount(true);

        Mockito.verify(emptyView).setVisibility(View.VISIBLE);
    }
}
