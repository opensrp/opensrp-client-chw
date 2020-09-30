package org.smartregister.chw.fragment;

import android.widget.TextView;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.FindReportContract;
import org.smartregister.chw.util.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        ReflectionHelpers.setField(spyFragment, "checkedCommunities", checkedCommunities);
        ReflectionHelpers.setField(spyFragment, "communityList", communityList);
        ReflectionHelpers.setField(spyFragment, "selectedCommunitiesTV", selectedCommunitiesTV);

        spyFragment.updateSelectedCommunitiesView();

        Mockito.verify(spyFragment).selectedCommunitiesTV.setText("CHW 1 \n CHW 2");
    }

    @Test
    public void testRunReport() {
        FilterReportFragment spyFragment = Mockito.spy(FilterReportFragment.class);
        FindReportContract.Presenter presenter = Mockito.spy(FindReportContract.Presenter.class);
        ReflectionHelpers.setField(spyFragment, "presenter", presenter);

        LinkedHashMap<String, String> communityIDList = new LinkedHashMap<>();
        communityIDList.put("All communities", "");
        communityIDList.put("CHW 1", "456");
        boolean[] checkedCommunities = new boolean[2];
        checkedCommunities[0] = true;
        List<String> communityList = new ArrayList<>();
        communityList.add("All communities");
        communityList.add("CHW 1");
        ReflectionHelpers.setField(spyFragment, "checkedCommunities", checkedCommunities);
        ReflectionHelpers.setField(spyFragment, "communityList", communityList);
        ReflectionHelpers.setField(spyFragment, "communityIDList", communityIDList);
        spyFragment.runReport();


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        Calendar myCalendar = Calendar.getInstance();
        List<String> communityIds = new ArrayList<>();
        communityIds.add("");
        List<String> communities = new ArrayList<>();
        communities.add("All communities");
        Map<String, String> map = new HashMap<>();
        Gson gson = new Gson();
        map.put(Constants.ReportParameters.COMMUNITY, gson.toJson(communities));
        map.put(Constants.ReportParameters.COMMUNITY_ID, gson.toJson(communityIds));
        map.put(Constants.ReportParameters.REPORT_DATE, dateFormat.format(myCalendar.getTime()));
        Mockito.verify(presenter).runReport(map);
    }
}
