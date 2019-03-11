package org.smartgresiter.wcaro.domain;

public class FamilyMember {

    private String familyID;
    private String memberID;
    private String phone;
    private String otherPhone;
    private String eduLevel;
    private boolean isPrimaryCareGiver = false;
    private boolean isFamilyHead = false;

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
}
