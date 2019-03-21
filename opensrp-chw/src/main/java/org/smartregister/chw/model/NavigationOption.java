package org.smartregister.chw.model;

public class NavigationOption {

    private int ResourceID;
    private int ResourceActiveID;
    private String MenuTitle;
    private long RegisterCount;

    public NavigationOption(int resourceID, int resourceActiveID, String menuTitle, long registerCount) {
        ResourceID = resourceID;
        ResourceActiveID = resourceActiveID;
        MenuTitle = menuTitle;
        RegisterCount = registerCount;
    }

    public int getResourceID() {
        return ResourceID;
    }

    public void setResourceID(int resourceID) {
        ResourceID = resourceID;
    }

    public int getResourceActiveID() {
        return ResourceActiveID;
    }

    public void setResourceActiveID(int resourceActiveID) {
        ResourceActiveID = resourceActiveID;
    }

    public String getMenuTitle() {
        return MenuTitle;
    }

    public void setMenuTitle(String menuTitle) {
        MenuTitle = menuTitle;
    }

    public long getRegisterCount() {
        return RegisterCount;
    }

    public void setRegisterCount(long registerCount) {
        RegisterCount = registerCount;
    }

}
