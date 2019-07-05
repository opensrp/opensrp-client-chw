package org.smartregister.chw.model;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.family.domain.FamilyEventClient;

import java.util.ArrayList;
import java.util.List;

public abstract class DefaultFamilyProfileModelFlv implements FamilyProfileModel.Flavor {
    @Override
    public void updateWra(FamilyEventClient familyEventClient) {
        Client client = familyEventClient.getClient();
        Event event = familyEventClient.getEvent();
        if (client != null && event != null && client.getGender().equalsIgnoreCase("female") && client.getBirthdate() != null) {
            DateTime date = new DateTime(client.getBirthdate());
            Years years = Years.yearsBetween(date.toLocalDate(), LocalDate.now());
            int age = years.getYears();
            if (age >= 10 && age <= 49) {
                List<Object> list = new ArrayList<>();
                list.add("true");
                event.addObs(new Obs("concept", "text", "162849AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "",
                        list, new ArrayList<>(), null, "wra"));
            }

        }
    }
}