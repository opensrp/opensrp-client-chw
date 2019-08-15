package org.smartregister.chw.adapter;

import org.smartregister.chw.core.adapter.MemberAdapter;
import org.smartregister.chw.core.domain.FamilyMember;
import org.smartregister.chw.core.listener.MemberAdapterListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.chw.BaseUnitTest;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;

public class MemberAdapterTest extends BaseUnitTest {

    @Mock
    private MemberAdapter.MyViewHolder myViewHolder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSetSelected() {
        MemberAdapterListener listener = Mockito.spy(MemberAdapterListener.class);
        ArrayList<FamilyMember> myDataset = new ArrayList<>();

        MemberAdapter memberAdapter = new MemberAdapter(RuntimeEnvironment.application, myDataset, listener);

        memberAdapter.setSelected(myViewHolder, "12345");

        verify(listener).onMenuChoiceChange();
    }
}
