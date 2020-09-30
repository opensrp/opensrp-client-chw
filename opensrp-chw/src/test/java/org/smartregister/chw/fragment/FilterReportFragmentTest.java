package org.smartregister.chw.fragment;

import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.chw.application.ChwApplication;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(application = ChwApplication.class, sdk = 22)
public class FilterReportFragmentTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdateSelectedCommunitiesView() {
        FilterReportFragment spyFragment = Mockito.spy(FilterReportFragment.class);

        TextView selectedCommunitiesTV = Mockito.mock(TextView.class);
        boolean[] checkedCommunities = new boolean[2];
        checkedCommunities[0] = true;
        checkedCommunities[1] = true;
        List<String> communityList = new ArrayList<>();
        communityList.add("CHW 1");
        communityList.add("CHW 2");
        Whitebox.setInternalState(spyFragment, "checkedCommunities", checkedCommunities);
        Whitebox.setInternalState(spyFragment, "communityList", communityList);
        Whitebox.setInternalState(spyFragment, "selectedCommunitiesTV", selectedCommunitiesTV);

        spyFragment.updateSelectedCommunitiesView();

        Mockito.verify(spyFragment).selectedCommunitiesTV.setText("CHW 1 \n CHW 2");
    }
}
