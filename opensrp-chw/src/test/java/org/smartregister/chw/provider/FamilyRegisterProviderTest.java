package org.smartregister.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class FamilyRegisterProviderTest extends BaseUnitTest {
    private Context context = RuntimeEnvironment.application;
    @Mock
    private CommonRepository commonRepository;
    @Mock
    private View.OnClickListener onClickListener;
    @Mock
    private View.OnClickListener onPageClickListener;
    private Set<View> visibleColumns = new HashSet<>();

    private FamilyRegisterProvider familyRegisterProvider;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        org.smartregister.Context srpContext = Mockito.mock(org.smartregister.Context.class);
        CoreLibrary.init(srpContext);
        when(srpContext.commonrepository(anyString())).thenReturn(commonRepository);
        org.smartregister.Context.bindtypes = new ArrayList<>();

        familyRegisterProvider = Mockito.spy(new FamilyRegisterProvider(context, commonRepository, visibleColumns, onClickListener, onPageClickListener));
    }

    @Test
    public void testGetView() {
        Cursor cursor = Mockito.mock(Cursor.class);
        CommonPersonObjectClient client = Mockito.mock(CommonPersonObjectClient.class);
        FamilyRegisterProvider.RegisterViewHolder registerViewHolder = familyRegisterProvider.createViewHolder(null);
        familyRegisterProvider.getView(cursor, client, registerViewHolder);

        Assert.assertEquals(View.GONE, registerViewHolder.dueButton.getVisibility());
    }

    @Test
    public void testGetChildAgeLimitFilter(){
        if (ChwApplication.getApplicationFlavor().showIconsForChildrenUnderTwoAndGirlsAgeNineToEleven()){
            Assert.assertEquals(ChildDBConstants.childDueVaccinesFilterForChildrenBelowTwoAndGirlsAgeNineToEleven(), familyRegisterProvider.getChildAgeLimitFilter());
        }else {
            Assert.assertEquals(org.smartregister.chw.core.utils.ChildDBConstants.childAgeLimitFilter(), familyRegisterProvider.getChildAgeLimitFilter());
        }
    }
}