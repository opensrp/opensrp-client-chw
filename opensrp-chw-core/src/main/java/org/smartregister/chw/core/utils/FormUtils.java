package org.smartregister.chw.core.utils;

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
                baseProfileActivity.getClass(), CoreConstants.IDENTIFIER.UNIQUE_IDENTIFIER_KEY, false);

        metadata.updateFamilyRegister(CoreConstants.JSON_FORM.getFamilyRegister(), CoreConstants.TABLE_NAME.FAMILY,
                CoreConstants.EventType.FAMILY_REGISTRATION, CoreConstants.EventType.UPDATE_FAMILY_REGISTRATION,
                CoreConstants.CONFIGURATION.FAMILY_REGISTER, CoreConstants.RELATIONSHIP.FAMILY_HEAD, CoreConstants.RELATIONSHIP.PRIMARY_CAREGIVER);

        metadata.updateFamilyMemberRegister(CoreConstants.JSON_FORM.getFamilyMemberRegister(),
                CoreConstants.TABLE_NAME.FAMILY_MEMBER, CoreConstants.EventType.FAMILY_MEMBER_REGISTRATION,
                CoreConstants.EventType.UPDATE_FAMILY_MEMBER_REGISTRATION, CoreConstants.CONFIGURATION.FAMILY_MEMBER_REGISTER, CoreConstants.RELATIONSHIP.FAMILY);

        metadata.updateFamilyDueRegister(CoreConstants.TABLE_NAME.CHILD, Integer.MAX_VALUE, false);

        metadata.updateFamilyActivityRegister(CoreConstants.TABLE_NAME.CHILD_ACTIVITY, Integer.MAX_VALUE, false);

        metadata.updateFamilyOtherMemberRegister(CoreConstants.TABLE_NAME.FAMILY_MEMBER, Integer.MAX_VALUE, false);
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
