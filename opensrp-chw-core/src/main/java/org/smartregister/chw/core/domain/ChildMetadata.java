package org.smartregister.chw.core.domain;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.smartregister.view.activity.BaseProfileActivity;

public class ChildMetadata {

    public final Class nativeFormActivity;
    public final Class profileActivity;

    public ChildRegister childRegister;
    public ChildMemberRegister childMemberRegister;

    public ChildMetadata(Class<? extends JsonFormActivity> nativeFormActivity, Class<? extends BaseProfileActivity> profileActivity) {
        this.nativeFormActivity = nativeFormActivity;
        this.profileActivity = profileActivity;
    }

    public void updateChildRegister(String formName, String tableName, String registerEventType, String updateEventType, String config) {
        this.childRegister = new ChildRegister(formName, tableName, registerEventType, updateEventType, config);
    }

    public void updateChildMemberRegister(String formName, String tableName, String registerEventType, String updateEventType, String config, String familyRelationKey) {
        this.childMemberRegister = new ChildMemberRegister(formName, tableName, registerEventType, updateEventType, config, familyRelationKey);
    }

    public class ChildRegister {

        public final String formName;
        public final String tableName;
        public final String registerEventType;
        public final String updateEventType;
        public final String config;

        public ChildRegister(String formName, String tableName, String registerEventType, String updateEventType, String config) {
            this.formName = formName;
            this.tableName = tableName;
            this.registerEventType = registerEventType;
            this.updateEventType = updateEventType;
            this.config = config;
        }

        public String getFormName() {
            return formName;
        }
    }

    public class ChildMemberRegister {

        public final String formName;

        public final String tableName;

        public final String registerEventType;

        public final String updateEventType;

        public final String config;

        public final String childRelationKey;


        public ChildMemberRegister(String formName, String tableName, String registerEventType, String updateEventType, String config, String familyRelationKey) {
            this.formName = formName;
            this.tableName = tableName;
            this.registerEventType = registerEventType;
            this.updateEventType = updateEventType;
            this.config = config;
            this.childRelationKey = familyRelationKey;
        }

    }
}
