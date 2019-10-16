package org.smartregister.chw.model;

public class ReferralTypeModel {

    private String referralType;
    private String client;
    private String formName;

    public ReferralTypeModel(String client, String referralType, String formName) {
        this.client = client;
        this.referralType = referralType;
        this.formName = formName;
    }

    public String getReferralType() {
        return referralType;
    }

    public String getFormName() {
        return formName;
    }

    public String getClient() {
        return client;
    }
}
