package org.smartgresiter.wcaro.model;

import org.smartgresiter.wcaro.contract.NavigationContract;

public class NavigationOption {

    private int ResourceID;
    private String MenuTitle;
    private long RegisterCount;
    private NavigationContract.SelectedAction selectedAction;

    public NavigationOption(int resourceID, String menuTitle, long registerCount) {
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

    public NavigationContract.SelectedAction getSelectedAction() {
        return selectedAction;
    }

    public void setSelectedAction(NavigationContract.SelectedAction selectedAction) {
        this.selectedAction = selectedAction;
    }
}
