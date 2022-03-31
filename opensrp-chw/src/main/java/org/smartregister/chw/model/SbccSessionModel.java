package org.smartregister.chw.model;

public class SbccSessionModel {
    private String sessionDate;
    private String sessionLocation;
    private String sessionId;

    public String getSessionParticipants() {
        return sessionParticipants;
    }

    public void setSessionParticipants(String sessionParticipants) {
        this.sessionParticipants = sessionParticipants;
    }

    private String sessionParticipants;

    public SbccSessionModel() {
    }

    public SbccSessionModel(String sessionDate, String sessionLocation, String sessionId) {
        this.sessionDate = sessionDate;
        this.sessionLocation = sessionLocation;
        this.sessionId = sessionId;
    }

    public String getSessionDate() {
        return sessionDate;
    }


    public void setSessionDate(String sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getSessionLocation() {
        return sessionLocation;
    }

    public void setSessionLocation(String sessionLocation) {
        this.sessionLocation = sessionLocation;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
