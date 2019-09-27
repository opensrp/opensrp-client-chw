package org.smartregister.chw.core.model;

public class ChildModel {

    private String childFullName;
    private String dateOfBirth;

    public ChildModel(String childFullName, String dateOfBirth) {
        this.childFullName = childFullName;
        this.dateOfBirth = dateOfBirth;
    }

    public String getChildFullName() {
        return childFullName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
}
