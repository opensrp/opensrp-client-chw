package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.presenter.AboveFiveChildProfilePresenter;
import org.smartregister.helper.ImageRenderHelper;

import de.hdodenhof.circleimageview.CircleImageView;

@RunWith(RobolectricTestRunner.class)
@Config(application = ChwApplication.class, sdk = 22)
public class AboveFiveChildProfileActivityTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private AboveFiveChildProfileActivity activity;

    @Mock
    private RelativeLayout layoutLastVisitRow;
    @Mock
    private View viewLastVisitRow;
    @Mock
    private ImageRenderHelper imageRenderHelper;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ActivityController<AboveFiveChildProfileActivity> controller = Robolectric.buildActivity(AboveFiveChildProfileActivity.class)
                .create()
                .start();
        activity = controller.get();


        Context context = Context.getInstance();
        CoreLibrary.init(context);

        //Auto login by default
        String password = "pwd";
        context.session().start(context.session().lengthInMilliseconds());
        context.configuration().getDrishtiApplication().setPassword(password.getBytes());
        context.session().setPassword(password.getBytes());

        MockitoAnnotations.initMocks(this);
        controller = Robolectric.buildActivity(AboveFiveChildProfileActivity.class);

        activity = controller.get();
    }

    @Test
    public void setProfileImageBorderTest() {
        AboveFiveChildProfileActivity spyActivity = Mockito.spy(AboveFiveChildProfileActivity.class);
        CircleImageView imageView = Mockito.spy(new CircleImageView(RuntimeEnvironment.application));
        ReflectionHelpers.setField(spyActivity, "imageViewProfile", imageView);
        ReflectionHelpers.setField(spyActivity, "imageRenderHelper", imageRenderHelper);
        spyActivity.setProfileImage("1234");
        Assert.assertEquals(0, imageView.getBorderWidth());

    }

    @Test
    public void setParentNameViewGone() {
        AboveFiveChildProfileActivity spyActivity = Mockito.spy(AboveFiveChildProfileActivity.class);
        TextView textView = Mockito.spy(new TextView(RuntimeEnvironment.application));
        ReflectionHelpers.setField(spyActivity, "textViewParentName", textView);
        spyActivity.setParentName("sdfs");
        Assert.assertEquals(View.GONE, textView.getVisibility());
    }

    @Test
    public void setLastVisitRowGone() {
        AboveFiveChildProfileActivity spyActivity = Mockito.spy(AboveFiveChildProfileActivity.class);
        TextView textViewLastVisit = Mockito.spy(new TextView(RuntimeEnvironment.application));
        TextView textViewMedicalHistory = Mockito.spy(new TextView(RuntimeEnvironment.application));
        ReflectionHelpers.setField(spyActivity, "layoutLastVisitRow", layoutLastVisitRow);
        ReflectionHelpers.setField(spyActivity, "viewLastVisitRow", viewLastVisitRow);
        ReflectionHelpers.setField(spyActivity, "textViewLastVisit", textViewLastVisit);
        ReflectionHelpers.setField(spyActivity, "textViewMedicalHistory", textViewMedicalHistory);
        spyActivity.setLastVisitRowView("10");
        Assert.assertEquals(View.GONE, textViewLastVisit.getVisibility());
    }

    @Test
    public void goneRecordVisitPanel() throws Exception {
        AboveFiveChildProfileActivity spyActivity = Mockito.spy(AboveFiveChildProfileActivity.class);
        View recordVisitPanel = Mockito.spy(new View(RuntimeEnvironment.application));
        ReflectionHelpers.setField(spyActivity, "recordVisitPanel", recordVisitPanel);
        ReflectionHelpers.callInstanceMethod(spyActivity, "invisibleRecordVisitPanel");
        Assert.assertEquals(View.GONE, recordVisitPanel.getVisibility());
    }

    @Test
    public void testOnClick() {
        View view = Mockito.mock(View.class);
        ReflectionHelpers.setField(activity, "progressBar", Mockito.mock(ProgressBar.class));
        ReflectionHelpers.setField(activity, "tvEdit", Mockito.mock(TextView.class));

        activity = Mockito.spy(activity);
        AboveFiveChildProfilePresenter presenter = Mockito.mock(AboveFiveChildProfilePresenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();

        // visit not done
        Mockito.doReturn(org.smartregister.chw.core.R.id.textview_visit_not).when(view).getId();
        activity.onClick(view);
        Mockito.verify(presenter).updateVisitNotDone(Mockito.anyLong());

        // visit done
        Mockito.doReturn(org.smartregister.chw.core.R.id.textview_undo).when(view).getId();
        activity.onClick(view);
        Mockito.verify(presenter).updateVisitNotDone(0);

        Mockito.doReturn(org.smartregister.chw.core.R.id.family_has_row).when(view).getId();
        activity.onClick(view);

        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(activity).startActivity(intentArgumentCaptor.capture());
    }


    @Test
    public void testOnActivityResult() throws Exception {
        activity = Mockito.spy(activity);
        AboveFiveChildProfilePresenter presenter = Mockito.mock(AboveFiveChildProfilePresenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();

        int resultCode = Activity.RESULT_OK;
        Intent data = Mockito.mock(Intent.class);
        Bundle bundle = new Bundle();
        data.putExtras(data.getExtras());
        Mockito.doReturn(data).when(activity).getIntent();
        Mockito.doReturn(bundle).when(data).getExtras();

        Mockito.doNothing().when(activity).execute();


        activity.onActivityResult(CoreConstants.ProfileActivityResults.CHANGE_COMPLETED, resultCode, data);
        Mockito.verify(activity, Mockito.times(3)).finish();
    }

    @Test
    public void testOnOptionsItemSelected() {
        activity = Mockito.spy(activity);
        AboveFiveChildProfilePresenter presenter = Mockito.mock(AboveFiveChildProfilePresenter.class);
        Mockito.doReturn(presenter).when(activity).presenter();
        ReflectionHelpers.setField(activity, "memberObject", Mockito.mock(MemberObject.class));


        MenuItem item = Mockito.mock(MenuItem.class);
        Mockito.doReturn(R.id.action_registration).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(presenter).startFormForEdit(Mockito.any(), Mockito.any());

        Mockito.doReturn(android.R.id.home).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(activity).onBackPressed();

        Mockito.doReturn(org.smartregister.chw.core.R.id.action_sick_child_form).when(item).getItemId();
        activity.onOptionsItemSelected(item);
        Mockito.verify(presenter).startSickChildForm(Mockito.any());
    }


    @Test
    public void testInitializePresenter() {
        activity.initializePresenter();
        Assert.assertNotNull(ReflectionHelpers.getField(activity, "presenter"));
    }

    @Test
    public void testSetDueTodayServices() {
        activity = Mockito.spy(activity);
        RelativeLayout relativeLayout = Mockito.spy(new RelativeLayout(RuntimeEnvironment.application));
        TextView textView = Mockito.spy(new TextView(RuntimeEnvironment.application));
        ReflectionHelpers.setField(activity, "layoutServiceDueRow", relativeLayout);
        ReflectionHelpers.setField(activity, "textViewDueToday", textView);
        activity.setDueTodayServices();
        Mockito.verify(relativeLayout).setVisibility(View.GONE);
        Mockito.verify(textView).setVisibility(View.GONE);
    }

}
