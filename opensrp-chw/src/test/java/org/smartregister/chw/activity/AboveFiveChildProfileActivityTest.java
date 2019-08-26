package org.smartregister.chw.activity;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.smartregister.chw.BaseActivityTest;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.helper.ImageRenderHelper;

import de.hdodenhof.circleimageview.CircleImageView;

@RunWith(RobolectricTestRunner.class)
@Config(application = ChwApplication.class, sdk = 22)
public class AboveFiveChildProfileActivityTest extends BaseActivityTest<AboveFiveChildProfileActivity> {
    @Mock
    RelativeLayout layoutLastVisitRow;
    @Mock
    View viewLastVisitRow;
    @Mock
    ImageRenderHelper imageRenderHelper;

    @Override
    protected Class<AboveFiveChildProfileActivity> getActivityClass() {
        return AboveFiveChildProfileActivity.class;
    }

    @Test
    public void setProfileImageBorderTest() {
        AboveFiveChildProfileActivity spyActivity = Mockito.spy(getActivity());
        CircleImageView imageView = Mockito.spy(new CircleImageView(RuntimeEnvironment.application));
        Whitebox.setInternalState(spyActivity, "imageViewProfile", imageView);
        Whitebox.setInternalState(spyActivity, "imageRenderHelper", imageRenderHelper);
        spyActivity.setProfileImage("1234");
        Assert.assertEquals(2, imageView.getBorderWidth());

    }

    @Test
    public void setParentNameViewGone() {
        AboveFiveChildProfileActivity spyActivity = Mockito.spy(getActivity());
        TextView textView = Mockito.spy(new TextView(RuntimeEnvironment.application));
        Whitebox.setInternalState(spyActivity, "textViewParentName", textView);
        spyActivity.setParentName("sdfs");
        Assert.assertEquals(View.GONE, textView.getVisibility());
    }

    @Test
    public void setLastVisitRowGone() {
        AboveFiveChildProfileActivity spyActivity = Mockito.spy(getActivity());
        TextView textViewLastVisit = Mockito.spy(new TextView(RuntimeEnvironment.application));
        TextView textViewMedicalHistory = Mockito.spy(new TextView(RuntimeEnvironment.application));
        Whitebox.setInternalState(spyActivity, "layoutLastVisitRow", layoutLastVisitRow);
        Whitebox.setInternalState(spyActivity, "viewLastVisitRow", viewLastVisitRow);
        Whitebox.setInternalState(spyActivity, "textViewLastVisit", textViewLastVisit);
        Whitebox.setInternalState(spyActivity, "textViewMedicalHistory", textViewMedicalHistory);
        spyActivity.setLastVisitRowView("10");
        Assert.assertEquals(View.GONE, textViewLastVisit.getVisibility());
    }

    @Test
    public void goneRecordVisitPanel() throws Exception {
        AboveFiveChildProfileActivity spyActivity = Mockito.spy(getActivity());
        View recordVisitPanel = Mockito.spy(new View(RuntimeEnvironment.application));
        Whitebox.setInternalState(spyActivity, "recordVisitPanel", recordVisitPanel);
        Whitebox.invokeMethod(spyActivity, "invisibleRecordVisitPanel");
        Assert.assertEquals(View.GONE, recordVisitPanel.getVisibility());
    }

}
