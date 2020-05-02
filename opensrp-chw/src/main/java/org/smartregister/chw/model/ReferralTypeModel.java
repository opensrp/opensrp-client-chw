package org.smartregister.chw.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ReferralTypeModel implements Parcelable {

    private String referralType;
    private String formName;

    public ReferralTypeModel(String referralType, String formName) {
        this.referralType = referralType;
        this.formName = formName;
    }

    private ReferralTypeModel(Parcel in) {
        referralType = in.readString();
        formName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(referralType);
        dest.writeString(formName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReferralTypeModel> CREATOR = new Creator<ReferralTypeModel>() {
        @Override
        public ReferralTypeModel createFromParcel(Parcel in) {
            return new ReferralTypeModel(in);
        }

        @Override
        public ReferralTypeModel[] newArray(int size) {
            return new ReferralTypeModel[size];
        }
    };

    public String getReferralType() {
        return referralType;
    }

    public String getFormName() {
        return formName;
    }

}
