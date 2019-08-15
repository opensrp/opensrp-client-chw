package org.smartregister.chw.core.domain;

import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

/**
 * Created by raihan on 6/11/18.
 */

public class HomeVisit {
    private Long id;
    private String baseEntityId;
    private String name;
    private Date date;
    private String anmId;
    private String locationId;
    private String syncStatus;
    private Long updatedAt;
    private String eventId;
    private String formSubmissionId;
    private Date createdAt;
    private Map<String, String> formfields;
    private JSONObject VaccineGroupsGiven = new JSONObject();
    private JSONObject singleVaccinesGiven = new JSONObject();
    private JSONObject vaccineNotGiven = new JSONObject();
    private JSONObject ServicesGiven = new JSONObject();
    private JSONObject serviceNotGiven = new JSONObject();
    private JSONObject birthCertificationState = new JSONObject();
    private JSONObject illness_information = new JSONObject();
    private String homeVisitId;

    public String getHomeVisitId() {
        return homeVisitId;
    }

    public void setHomeVisitId(String homeVisitId) {
        this.homeVisitId = homeVisitId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAnmId() {
        return anmId;
    }

    public void setAnmId(String anmId) {
        this.anmId = anmId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getFormSubmissionId() {
        return formSubmissionId;
    }

    public void setFormSubmissionId(String formSubmissionId) {
        this.formSubmissionId = formSubmissionId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Map<String, String> getFormFields() {
        return formfields;
    }

    public void setFormFields(Map<String, String> formfields) {
        this.formfields = formfields;
    }

    public JSONObject getVaccineGroupsGiven() {
        return VaccineGroupsGiven;
    }

    public void setVaccineGroupsGiven(JSONObject vaccineGroupsGiven) {
        VaccineGroupsGiven = vaccineGroupsGiven;
    }

    public JSONObject getSingleVaccinesGiven() {
        return singleVaccinesGiven;
    }

    public void setSingleVaccinesGiven(JSONObject singleVaccinesGiven) {
        this.singleVaccinesGiven = singleVaccinesGiven;
    }

    public JSONObject getServicesGiven() {
        return ServicesGiven;
    }

    public void setServicesGiven(JSONObject servicesGiven) {
        ServicesGiven = servicesGiven;
    }

    public JSONObject getBirthCertificationState() {
        return birthCertificationState;
    }

    public void setBirthCertificationState(JSONObject birthCertificationState) {
        this.birthCertificationState = birthCertificationState;
    }

    public JSONObject getIllnessInformation() {
        return illness_information;
    }

    public void setIllnessInformation(JSONObject illness_information) {
        this.illness_information = illness_information;
    }

    public JSONObject getServiceNotGiven() {
        return serviceNotGiven;
    }

    public void setServiceNotGiven(JSONObject serviceNotGiven) {
        this.serviceNotGiven = serviceNotGiven;
    }

    public JSONObject getVaccineNotGiven() {
        return vaccineNotGiven;
    }

    public void setVaccineNotGiven(JSONObject vaccineNotGiven) {
        this.vaccineNotGiven = vaccineNotGiven;
    }
}
