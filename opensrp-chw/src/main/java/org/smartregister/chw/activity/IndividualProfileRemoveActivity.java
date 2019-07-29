package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.View;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.fragment.IndividualProfileRemoveFragment;
import org.smartregister.chw.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.view.activity.SecuredActivity;
import timber.log.Timber;

public class IndividualProfileRemoveActivity extends SecuredActivity {
    private static final String TAG = "IndividualProfile";
    private IndividualProfileRemoveFragment individualProfileRemoveFragment;

    public static void startIndividualProfileActivity(Activity activity, CommonPersonObjectClient commonPersonObjectClient, String familyBaseEntityId,
                                                      String familyHead, String primaryCareGiver, String className) {
        Intent intent = new Intent(activity, IndividualProfileRemoveActivity.class);
        intent.putExtra(Constants.INTENT_KEY.CHILD_COMMON_PERSON, commonPersonObjectClient);
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, familyBaseEntityId);
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_HEAD, familyHead);
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.PRIMARY_CAREGIVER, primaryCareGiver);
        intent.putExtra(Constants.INTENT_KEY.CLASS, className);
        activity.startActivityForResult(intent, org.smartregister.chw.util.Constants.ProfileActivityResults.CHANGE_COMPLETED);
    }

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_family_remove_member);
        findViewById(R.id.detail_toolbar).setVisibility(View.GONE);
        findViewById(R.id.close).setVisibility(View.GONE);
        findViewById(R.id.tvDetails).setVisibility(View.GONE);
        individualProfileRemoveFragment = IndividualProfileRemoveFragment.newInstance(getIntent().getExtras());
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.flFrame, individualProfileRemoveFragment)
                .commit();
    }


    @Override
    protected void onResumption() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);

                JSONObject form = new JSONObject(jsonString);
                individualProfileRemoveFragment.confirmRemove(form);
            } catch (Exception e) {
                Timber.e(e);
            }
        } else {
            finish();
        }
    }

    public void onRemoveMember() {
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
