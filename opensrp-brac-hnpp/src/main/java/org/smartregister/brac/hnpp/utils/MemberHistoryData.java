package org.smartregister.brac.hnpp.utils;

public class MemberHistoryData {

    private int imageSource;
    private String title;
    private String visitType;
    private long visitDate;
    private String visitDetails;

    public int getImageSource() {
        return imageSource;
    }

    public void setImageSource(int imageSource) {
        this.imageSource = imageSource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public long getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(long visitDate) {
        this.visitDate = visitDate;
    }

    public String getVisitDetails() {
        return visitDetails;
    }

    public void setVisitDetails(String visitDetails) {
        this.visitDetails = visitDetails;
    }
}
