package org.smartregister.chw.core.event;

public class PermissionEvent {

    private int PermissionType;
    private boolean isGranted;

    public PermissionEvent(int permissionType, boolean isGranted) {
        PermissionType = permissionType;
        this.isGranted = isGranted;
    }

    public PermissionEvent() {
    }

    public int getPermissionType() {
        return PermissionType;
    }

    public void setPermissionType(int permissionType) {
        PermissionType = permissionType;
    }

    public boolean isGranted() {
        return isGranted;
    }

    public void setGranted(boolean granted) {
        isGranted = granted;
    }
}
