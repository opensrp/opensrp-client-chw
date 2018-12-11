package org.smartgresiter.wcaro.model;

public class NavigationModel {

    private int ResourceID;
    private String MenuTitle;
    private long RegisterCount;

    public NavigationModel(int resourceID, String menuTitle, long registerCount) {
        ResourceID = resourceID;
        MenuTitle = menuTitle;
        RegisterCount = registerCount;
    }

    public int getResourceID() {
        return ResourceID;
    }

    public void setResourceID(int resourceID) {
        ResourceID = resourceID;
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
