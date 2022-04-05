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

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;

public class FamilyActivityRegisterProviderTest extends BaseUnitTest {

    private Context context = RuntimeEnvironment.application;
    @Mock
    private CommonRepository commonRepository;
    @Mock
    private View.OnClickListener onClickListener;
    @Mock
    private View.OnClickListener onPageClickListener;
    private Set<View> visibleColumns = new HashSet<>();

    private FamilyActivityRegisterProvider familyActivityRegisterProvider;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        familyActivityRegisterProvider = Mockito.spy(new FamilyActivityRegisterProvider(context, commonRepository, visibleColumns, onClickListener, onPageClickListener));
    }

    @Test
    public void testGetView() {
        Cursor cursor = Mockito.mock(Cursor.class);
        CommonPersonObjectClient client = Mockito.mock(CommonPersonObjectClient.class);
        FamilyActivityRegisterProvider.RegisterViewHolder registerViewHolder = familyActivityRegisterProvider.createViewHolder(null);
        familyActivityRegisterProvider.getView(cursor, client, registerViewHolder);

        Mockito.verify(familyActivityRegisterProvider).populatePatientColumn(eq(client), eq(client), eq(registerViewHolder));
    }
}