package org.smartregister.chw.model;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.chw.R;
import org.smartregister.chw.core.model.NavigationOption;
import org.smartregister.chw.util.Constants;

public class NavigationOptionTest {

    @Test
    public void navigationModel_TestConstructor() {
        NavigationOption navigationOption = new NavigationOption(R.mipmap.sidemenu_families, R.mipmap.sidemenu_families_active, R.string.menu_all_families, Constants.DrawerMenu.ALL_FAMILIES, 0);

        Assert.assertEquals(navigationOption.getMenuTitle(), Constants.DrawerMenu.ALL_FAMILIES);
        Assert.assertEquals(navigationOption.getResourceID(), R.mipmap.sidemenu_families);
        Assert.assertEquals(navigationOption.getRegisterCount(), 0);
    }

    @Test
    public void navigationModel_TestSettersAndGetters() {
        NavigationOption model = new NavigationOption(R.mipmap.sidemenu_families, R.mipmap.sidemenu_families_active, R.string.menu_all_families, Constants.DrawerMenu.ALL_FAMILIES, 0);

        model.setRegisterCount(2);
        model.setMenuTitle("Test Menu");
        model.setResourceID(R.mipmap.sidemenu_families);


        Assert.assertEquals(model.getRegisterCount(), 2);
        Assert.assertEquals(model.getResourceID(), R.mipmap.sidemenu_families);
        Assert.assertEquals(model.getMenuTitle(), "Test Menu");

    }
}