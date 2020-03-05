package org.smartregister.chw.activity.utils;



import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
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
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Français"))
                .perform(click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("English"))
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
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Kiswahili"))
                .perform(click());
        Thread.sleep(500);
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("English"))
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

    public void logOut() throws InterruptedException{
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Log out as " + Constants.WcaroConfigs.wCaro_userName))
                .perform(click());

    }

    public void logOutBA() throws InterruptedException{
        onView(androidx.test.espresso.matcher.ViewMatchers.withSubstring("Log out as " + Constants.BoreshaAfyaConfigs.ba_userName))
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
