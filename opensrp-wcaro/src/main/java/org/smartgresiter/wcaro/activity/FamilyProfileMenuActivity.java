package org.smartgresiter.wcaro.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.fragment.FamilyProfileChangeHead;
import org.smartgresiter.wcaro.fragment.FamilyProfileChangePrimaryCG;
import org.smartregister.family.util.Constants;
import org.smartregister.view.activity.SecuredActivity;

public class FamilyProfileMenuActivity extends SecuredActivity {

    public static final String MENU = "MENU";

    public static class MenuType {
        public static final String ChangeHead = "ChangeHead";
        public static final String RemoveFamilyMember = "RemoveFamilyMember";
        public static final String ChangePrimaryCare = "ChangePrimaryCare";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_profile_menu);

        Intent intent = getIntent();
        String menuOption = intent.getStringExtra(FamilyProfileMenuActivity.MENU);
        String familyBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);

        Fragment fragment;
        switch (menuOption) {
            case MenuType.ChangeHead:
                fragment = FamilyProfileChangeHead.newInstance(familyBaseEntityId);
                break;
            case MenuType.RemoveFamilyMember:
                fragment = FamilyProfileChangeHead.newInstance(familyBaseEntityId);
                break;
            case MenuType.ChangePrimaryCare:
                fragment = FamilyProfileChangePrimaryCG.newInstance(familyBaseEntityId);
                break;
            default:
                fragment = FamilyProfileChangeHead.newInstance(familyBaseEntityId);
                break;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, fragment);
        ft.commit();
    }

    @Override
    protected void onCreation() {

    }

    @Override
    protected void onResumption() {

    }
}
