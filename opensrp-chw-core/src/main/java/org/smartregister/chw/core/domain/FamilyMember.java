package org.smartregister.chw.core.domain;

public class FamilyMember {

    private String firstName;
    private String lastName;
    private String middleName;
    private String gender;
    private String dob;
    private String dod;
    private String familyID;
    private String memberID;
    private String phone;
    private String otherPhone;
    private String eduLevel;
    private boolean isPrimaryCareGiver = false;
    private boolean isFamilyHead = false;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDod() {
        return dod;
    }

    public void setDod(String dod) {
        this.dod = dod;
    }

    public String getFamilyID() {
        return familyID;
    }

    public void setFamilyID(String familyID) {
        this.familyID = familyID;
    }

    public String getMemberID() {
        return memberID;
    }

    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOtherPhone() {
        return otherPhone;
    }

    public void setOtherPhone(String otherPhone) {
        this.otherPhone = otherPhone;
    }

    public String getEduLevel() {
        return eduLevel;
    }

    public void setEduLevel(String eduLevel) {
        this.eduLevel = eduLevel;
    }

    public boolean getPrimaryCareGiver() {
        return isPrimaryCareGiver;
    }

    public void setPrimaryCareGiver(boolean primaryCareGiver) {
        isPrimaryCareGiver = primaryCareGiver;
    }

    public boolean getFamilyHead() {
        return isFamilyHead;
    }

    public void setFamilyHead(boolean familyHead) {
        isFamilyHead = familyHead;
    }

    public String getFullNames() {
        return String.format("%s %s %s", isNull(getFirstName()), isNull(getMiddleName()), isNull(getLastName()));
    }

    private String isNull(String string) {
        if (string == null) {
            return "";
        } else {
            return string.trim();
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
}
