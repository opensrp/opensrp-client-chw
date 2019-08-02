package com.opensrp.chw.core.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

import static org.smartregister.util.Utils.getName;

public class Person {
    private String baseEntityID;
    private String firstName = "";
    private String lastName = "";
    private String middleName = "";
    private Date dob;

    public Person(String baseEntityID, String firstName, String lastName, String middleName, Date dob) {
        this.baseEntityID = baseEntityID;
        if (StringUtils.isNotBlank(firstName))
            this.firstName = firstName;
        if (StringUtils.isNotBlank(lastName))
            this.lastName = lastName;
        if (StringUtils.isNotBlank(middleName))
            this.middleName = middleName;
        this.dob = dob;
    }

    public String getBaseEntityID() {
        return baseEntityID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public Date getDob() {
        return dob;
    }

    public String getFullName() {
        return getName(getName(firstName, middleName), lastName);
    }
}
