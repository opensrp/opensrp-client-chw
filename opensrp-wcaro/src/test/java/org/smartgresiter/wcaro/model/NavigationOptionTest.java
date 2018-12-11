package org.smartgresiter.wcaro.model;

import org.junit.Test;
import org.smartgresiter.wcaro.R;

import static org.junit.Assert.assertEquals;

public class NavigationOptionTest {

    @Test
    public void navigationModel_TestConstructor() {
        NavigationOption navigationOption = new NavigationOption(R.drawable.child_boy_infant, "Child Registry", 0);

        assertEquals(navigationOption.getMenuTitle(), "Child Registry");
        assertEquals(navigationOption.getResourceID(), R.drawable.child_boy_infant);
        assertEquals(navigationOption.getRegisterCount(), 0);
    }

    @Test
    public void navigationModel_TestSettersAndGetters() {
        NavigationOption model = new NavigationOption(R.drawable.child_boy_infant, "Child Registry", 0);

        model.setRegisterCount(2);
        model.setMenuTitle("Test Menu");
        model.setResourceID(R.drawable.child_girl_infant);


        assertEquals(model.getRegisterCount(), 2);
        assertEquals(model.getResourceID(), R.drawable.child_girl_infant);
        assertEquals(model.getMenuTitle(), "Test Menu");

    }
}