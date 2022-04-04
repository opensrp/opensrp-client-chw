package org.smartregister.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.provider.FamilyDueRegisterProvider;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;

public class ChwDueRegisterProviderTest extends BaseUnitTest {
    private Context context = RuntimeEnvironment.application;
    @Mock
    private CommonRepository commonRepository;
    @Mock
    private View.OnClickListener onClickListener;
    @Mock
    private View.OnClickListener onPageClickListener;
    private Set<View> visibleColumns = new HashSet<>();

    private ChwDueRegisterProvider chwDueRegisterProvider;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        chwDueRegisterProvider = Mockito.spy(new ChwDueRegisterProvider(context, commonRepository, visibleColumns, onClickListener, onPageClickListener));
    }

    @Test
    public void testGetView() {
        Cursor cursor = Mockito.mock(Cursor.class);
        CommonPersonObjectClient client = Mockito.mock(CommonPersonObjectClient.class);
        FamilyDueRegisterProvider.RegisterViewHolder registerViewHolder = chwDueRegisterProvider.createViewHolder(null);
        chwDueRegisterProvider.getView(cursor, client, registerViewHolder);

        Mockito.verify(chwDueRegisterProvider).populatePatientColumn(eq(client), eq(client), eq(registerViewHolder));
    }
}