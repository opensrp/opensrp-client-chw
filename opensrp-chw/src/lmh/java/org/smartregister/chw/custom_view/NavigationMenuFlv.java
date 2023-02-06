package org.smartregister.chw.custom_view;

import android.app.Activity;
import android.graphics.Color;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import org.apache.commons.lang3.tuple.Pair;
import org.smartregister.chw.pinlogin.PinLoginUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class NavigationMenuFlv extends DefaultNavigationMenuFlv {

    @Override
    public List<Pair<String, Locale>> getSupportedLanguages() {
        return Arrays.asList(Pair.of("English", Locale.ENGLISH));
    }

    @Override
    public boolean hasMultipleLanguages() {
        return false;
    }

    @Override
    public boolean hasCommunityResponders() {
        return false;
    }

    @Override
    public String childNavigationMenuCountString() {
        return "Select count(*)\n" +
                " FROM ec_child\n" +
                " LEFT JOIN ec_family ON  ec_child.relational_id = ec_family.id COLLATE NOCASE  \n" +
                " LEFT JOIN ec_family_member ON  ec_family_member.base_entity_id = ec_child.base_entity_id COLLATE NOCASE  \n" +
                " WHERE  ec_child.date_removed is null\n" +
                "AND ec_family_member.is_closed = 0" +
                " AND  (( ifnull(ec_child.entry_point,'') <> 'PNC' )  or (ifnull(ec_child.entry_point,'') = 'PNC' and ( date(ec_child.dob, '+28 days') <= date() \n" +
                " and ((SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 0))) \n" +
                " or (ifnull(ec_child.entry_point,'') = 'PNC'  \n" +
                " and (SELECT is_closed FROM ec_family_member WHERE base_entity_id = ec_child.mother_entity_id ) = 1)) \n" +
                " and (((julianday('now') - julianday(ec_child.dob))/365.25) < 2 or (ec_child.gender = 'Female' and (((julianday('now') - julianday(ec_child.dob))/365.25) BETWEEN 9 AND 11)))\n";
    }

    @Override
    public AlertDialog doLogOutDialog(Activity activity) {
        AlertDialog dialog =  new AlertDialog.Builder(activity)
                .setTitle("Log Out?")
                .setMessage("You will be returned to the " + (PinLoginUtil.getPinLogger().enabledPin() ? "PIN entry screen." : "Log in screen."))
                .create();
        dialog.setOnShowListener(dialog1 -> {
            Button positiveButton = ((AlertDialog) dialog1)
                    .getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setTextColor(Color.RED);

            Button negativeButton = ((AlertDialog) dialog1)
                    .getButton(AlertDialog.BUTTON_NEGATIVE);
            negativeButton.setTextColor(Color.GRAY);
        });

        return dialog;
    }

     @Override
    public boolean hasSyncStatusProgressBar() {
        return false;
    }
}
