package org.smartregister.chw.core.domain;

import java.util.Date;

public class Child {

    private String baseEntityID;
    private String firstName;
    private String lastName;
    private String middleName;
    private String motherBaseEntityID;
    private String familyBaseEntityID;
    private Date dateOfBirth;
    private Date dateCreated;
    private Date lastVisitDate;
    private Date lastVisitNotDoneDate;

    public String getBaseEntityID() {
        return baseEntityID;
    }

    public void setBaseEntityID(String baseEntityID) {
        this.baseEntityID = baseEntityID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getMotherBaseEntityID() {
        return motherBaseEntityID;
    }

    public void setMotherBaseEntityID(String motherBaseEntityID) {
        this.motherBaseEntityID = motherBaseEntityID;
    }

    public String getFamilyBaseEntityID() {
        return familyBaseEntityID;
    }

    public void setFamilyBaseEntityID(String familyBaseEntityID) {
        this.familyBaseEntityID = familyBaseEntityID;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getLastVisitDate() {
        return lastVisitDate;
    }

    public void setLastVisitDate(Date lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    public Date getLastVisitNotDoneDate() {
        return lastVisitNotDoneDate;
    }

    public void setLastVisitNotDoneDate(Date lastVisitNotDoneDate) {
        this.lastVisitNotDoneDate = lastVisitNotDoneDate;
    }

    public Date getLastVisitOrNotVisitedDate() {
        if (getLastVisitDate() == null && getLastVisitNotDoneDate() == null)
            return getDateCreated();

        if (getLastVisitDate() == null) {
            return getLastVisitNotDoneDate();
        } else if (getLastVisitNotDoneDate() == null) {
            return getLastVisitDate();
        } else if (getLastVisitDate().getTime() > getLastVisitNotDoneDate().getTime()) {
            return getLastVisitDate();
        } else {
            return getLastVisitDate();
        }
    }
}
