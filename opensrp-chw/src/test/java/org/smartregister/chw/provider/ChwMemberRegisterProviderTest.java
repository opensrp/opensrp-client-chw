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
import org.smartregister.family.provider.FamilyMemberRegisterProvider;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;

public class ChwMemberRegisterProviderTest extends BaseUnitTest {

    private Context context = RuntimeEnvironment.application;
    @Mock
    private CommonRepository commonRepository;
    @Mock
    private View.OnClickListener onClickListener;
    @Mock
    private View.OnClickListener onPageClickListener;
    private Set<View> visibleColumns = new HashSet<>();

    private ChwMemberRegisterProvider chwMemberRegisterProvider;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        chwMemberRegisterProvider = Mockito.spy(new ChwMemberRegisterProvider(context, commonRepository, visibleColumns, onClickListener, onPageClickListener, null, null));
    }

    @Test
    public void testGetView() {
        Cursor cursor = Mockito.mock(Cursor.class);
        CommonPersonObjectClient client = Mockito.mock(CommonPersonObjectClient.class);
        FamilyMemberRegisterProvider.RegisterViewHolder registerViewHolder = chwMemberRegisterProvider.createViewHolder(null);
        chwMemberRegisterProvider.getView(cursor, client, registerViewHolder);

        Mockito.verify(chwMemberRegisterProvider).populateIdentifierColumn(eq(client), eq(registerViewHolder));
    }
}