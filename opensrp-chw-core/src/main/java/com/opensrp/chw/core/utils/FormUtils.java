package com.opensrp.chw.core.utils;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.family.activity.FamilyWizardFormActivity;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.domain.FamilyMetadata;
import org.smartregister.view.activity.BaseProfileActivity;

import java.util.ArrayList;
import java.util.List;

public class FormUtils {
    public static FamilyMetadata getFamilyMetadata(BaseProfileActivity baseProfileActivity) {
        FamilyMetadata metadata = new FamilyMetadata(FamilyWizardFormActivity.class, FamilyWizardFormActivity.class,
                baseProfileActivity.getClass(), Constants.IDENTIFIER.UNIQUE_IDENTIFIER_KEY, false);

        metadata.updateFamilyRegister(Constants.JSON_FORM.getFamilyRegister(), Constants.TABLE_NAME.FAMILY,
                Constants.EventType.FAMILY_REGISTRATION, Constants.EventType.UPDATE_FAMILY_REGISTRATION,
                Constants.CONFIGURATION.FAMILY_REGISTER, Constants.RELATIONSHIP.FAMILY_HEAD, Constants.RELATIONSHIP.PRIMARY_CAREGIVER);

        metadata.updateFamilyMemberRegister(Constants.JSON_FORM.getFamilyMemberRegister(),
                Constants.TABLE_NAME.FAMILY_MEMBER, Constants.EventType.FAMILY_MEMBER_REGISTRATION,
                Constants.EventType.UPDATE_FAMILY_MEMBER_REGISTRATION, Constants.CONFIGURATION.FAMILY_MEMBER_REGISTER, Constants.RELATIONSHIP.FAMILY);

        metadata.updateFamilyDueRegister(Constants.TABLE_NAME.CHILD, Integer.MAX_VALUE, false);

        metadata.updateFamilyActivityRegister(Constants.TABLE_NAME.CHILD_ACTIVITY, Integer.MAX_VALUE, false);

        metadata.updateFamilyOtherMemberRegister(Constants.TABLE_NAME.FAMILY_MEMBER, Integer.MAX_VALUE, false);
        return metadata;
    }

    public static void updateWraForBA(FamilyEventClient familyEventClient) {
        Client client = familyEventClient.getClient();
        Event event = familyEventClient.getEvent();
        if (client != null && event != null && client.getGender().equalsIgnoreCase("female") && client.getBirthdate() != null) {
            DateTime date = new DateTime(client.getBirthdate());
            Years years = Years.yearsBetween(date.toLocalDate(), LocalDate.now());
            int age = years.getYears();
            if (age >= 15 && age <= 49) {
                List<Object> list = new ArrayList<>();
                list.add("true");
                event.addObs(new Obs("concept", "text", "162849AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "",
                        list, new ArrayList<>(), null, "wra"));
            }

        }
    }

}
