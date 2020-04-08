package org.smartregister.chw.activity.utils;



import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.smartregister.chw.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;


public class Utils  {

    View view;

    public void logIn(String username, String password ) throws InterruptedException {
        onView(withId(R.id.login_user_name_edit_text))
                .perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.login_password_edit_text))
                .perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.login_login_btn))
                .perform(click());
        Thread.sleep(10000);
    }

/*
    public void logOut() throws InterruptedException{
        ViewInteraction viewInteraction = onView(withId(R.id.drawer_layout));
        viewInteraction.check(matches(isClosed(Gravity.LEFT)));
        viewInteraction.perform(DrawerActions.open());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Log out as CHA"))
                .perform(scrollTo(), click());
    }
*/

    Activity getCurrentActivity() throws Throwable {
        getInstrumentation().waitForIdleSync();
        final Activity[] activity = new Activity[1];
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                java.util.Collection<Activity> activities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                activity[0] = Iterables.getOnlyElement(activities);
            }
        });
        return activity[0];
    }

    public void revertLanguage() throws InterruptedException{
        openDrawerFrench();
        onView(ViewMatchers.withSubstring("Français"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("English"))
                .perform(click());
    }

    public ViewInteraction locateLayout(int position1, int position2) {
        ViewInteraction layout =  onView(
                allOf(withId(R.id.patient_column),
                        childAtPosition(
                                allOf(withId(R.id.register_columns),
                                        childAtPosition(
                                                withId(R.id.recycler_view),
                                                position1)),
                                position2)));

        return layout;
    }

    public void addTestFamilyMemberBa(String firstName, String secondName, String age) throws Throwable{
        onView(withId(R.id.fab))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Add new family member"))
                .perform(click());
        Thread.sleep(100);
        onView(ViewMatchers.withSubstring("Other family member"))
                .perform(click());
        Thread.sleep(100);
        Activity activity = getCurrentActivity();
        onView(withId(getViewId((JsonFormActivity) activity, "step1:same_as_fam_name")))
                .perform(click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:first_name")))
                .perform(typeText(firstName), closeSoftKeyboard());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:middle_name")))
                .perform(click())
                .perform(typeText(secondName), closeSoftKeyboard());
        Thread.sleep(100);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:dob_unknown")))
                .perform(scrollTo(), click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:age")))
                .perform(scrollTo(), typeText(age));
        Thread.sleep(100);
       /* onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("National ID"))
                .perform(scrollTo(), click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:national_id")))
                .perform(scrollTo(), typeText(Configs.TestConfigs.nationalID));

        */
        onView(withId(getViewId((JsonFormActivity) activity, "step1:insurance_provider")))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Community Health Fund (CHF)"))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:insurance_provider_number")))
                .perform(scrollTo())
                .perform(typeText(Configs.TestConfigs.nationalID), closeSoftKeyboard());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:sex")))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Female"))
                .perform(click());
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:preg_1yr")))
                .perform(scrollTo(), click());
        onView(ViewMatchers.withSubstring("No"))
                .perform(click());
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:disabilities")))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("No"))
                .perform(click());
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:is_primary_caregiver")))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("No"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Nurse"))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Religious leader"))
                .perform(scrollTo(), click());
        onView(ViewMatchers.withSubstring("Save"))
                .perform(click());
        Thread.sleep(500);
    }

    public void addTestFamilyMember(String firstName, String secondName, String age) throws Throwable {
        onView(withId(R.id.fab))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Add new family member"))
                .perform(click());
        Thread.sleep(100);
        onView(ViewMatchers.withSubstring("Other family member"))
                .perform(click());
        Activity activity = getCurrentActivity();
        Thread.sleep(500);
        onView(withId(getViewId((JsonFormActivity) activity, "step1:national_id")))
                .perform(typeText(Configs.TestConfigs.nationalID));
        //onView(withId(getViewId((JsonFormActivity) activity, "step1:surname"))).perform(scrollTo(), typeText("JinaLaFamilia"));
        onView(withId(getViewId((JsonFormActivity) activity, "step1:same_as_fam_name")))
                .perform(scrollTo(), click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:first_name")))
                .perform(scrollTo(), typeText(firstName));
        onView(withId(getViewId((JsonFormActivity) activity, "step1:middle_name")))
                .perform(scrollTo(), typeText(secondName));
        onView(withId(getViewId((JsonFormActivity) activity, "step1:dob_unknown")))
                .perform(scrollTo(), click());
        onView(withId(getViewId((JsonFormActivity) activity, "step1:age")))
                .perform(scrollTo(), typeText(age));
        onView(ViewMatchers.withSubstring("Sex"))
                .perform(scrollTo(), click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Female"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("Save"))
                .perform(click());
    }

    public void openFamilyMenu(){
        ViewInteraction overflowMenuButton = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.family_toolbar),
                                        2),
                                0),
                        isDisplayed()));
        overflowMenuButton.perform(click());
    }

    public void revertLanguageSwahili() throws InterruptedException{
        openDrawerSwahili();
        onView(ViewMatchers.withSubstring("Kiswahili"))
                .perform(click());
        Thread.sleep(500);
        onView(ViewMatchers.withSubstring("English"))
                .perform(click());
    }

    public void openDrawer(){
        onView(
                allOf(withContentDescription("Open"),
                        childAtPosition(
                                allOf(withId(R.id.register_toolbar),
                                        childAtPosition(
                                                withClassName(is("com.google.android.material.appbar.AppBarLayout")),
                                                0)),
                                1),
                        isDisplayed())).perform(click());
    }

    public void openDrawerFrench(){
        onView(
                allOf(withContentDescription("Ouvrir"),
                        childAtPosition(
                                allOf(withId(R.id.register_toolbar),
                                        childAtPosition(
                                                withClassName(is("com.google.android.material.appbar.AppBarLayout")),
                                                0)),
                                1),
                        isDisplayed())).perform(click());
    }

    public void openDrawerSwahili(){
        onView(
                allOf(withContentDescription("Fungua"),
                        childAtPosition(
                                allOf(withId(R.id.register_toolbar),
                                        childAtPosition(
                                                withClassName(is("com.google.android.material.appbar.AppBarLayout")),
                                                0)),
                                1),
                        isDisplayed())).perform(click());
    }
    public ViewInteraction floatingButton()  {
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.btn_repeating_group_done),
                        childAtPosition(
                                allOf(withId(R.id.reference_layout),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        return floatingActionButton;
    }
    public void logOut() throws InterruptedException{
        onView(ViewMatchers.withSubstring("Log out as "
                + Constants.WcaroConfigs.wCaro_userName))
                .perform(click());

    }

    public void logOutBA() throws InterruptedException{
        onView(ViewMatchers.withSubstring("Log out as " + Constants.BoreshaAfyaConfigs.ba_userName))
                .perform(scrollTo(), click());

    }


    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    public static int getViewId(JsonFormActivity jsonFormActivity, String key){
        return jsonFormActivity.getFormDataView(key).getId();
    }

}
