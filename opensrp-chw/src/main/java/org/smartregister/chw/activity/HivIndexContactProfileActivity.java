package org.smartregister.chw.activity;

import static org.smartregister.chw.hiv.util.Constants.ActivityPayload.HIV_MEMBER_OBJECT;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.core.activity.CoreHivIndexContactProfileActivity;
import org.smartregister.chw.core.contract.FamilyProfileExtendedContract;
import org.smartregister.chw.core.interactor.CoreHivIndexContactProfileInteractor;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.listener.OnRetrieveNotifications;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.custom_view.HivIndexContactFloatingMenu;
import org.smartregister.chw.hiv.activity.BaseHivFormsActivity;
import org.smartregister.chw.hiv.dao.HivDao;
import org.smartregister.chw.hiv.dao.HivIndexDao;
import org.smartregister.chw.hiv.domain.HivIndexContactObject;
import org.smartregister.chw.presenter.HivIndexContactProfilePresenter;
import org.smartregister.chw.tb.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class HivIndexContactProfileActivity extends CoreHivIndexContactProfileActivity implements FamilyProfileExtendedContract.PresenterCallBack, OnRetrieveNotifications {

    public final static String REGISTERED_TO_HIV_REGISTRY = "registered_to_hiv_registry";
    private CommonPersonObjectClient commonPersonObjectClient;

    public static void startHivIndexContactProfileActivity(Activity activity, HivIndexContactObject hivIndexContactObject) {
        Intent intent = new Intent(activity, HivIndexContactProfileActivity.class);
        intent.putExtra(HIV_MEMBER_OBJECT, hivIndexContactObject);
        activity.startActivity(intent);
    }

    public static void startHivIndexContactFollowupActivity(Activity activity, String baseEntityID) throws JSONException {

        Intent intent = new Intent(activity, BaseHivFormsActivity.class);
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.BASE_ENTITY_ID, baseEntityID);

        HivIndexContactObject hivIndexContactObject = HivIndexDao.getMember(baseEntityID);

        if (hivIndexContactObject.getHivStatus().equals("negative")) {
            if (hivIndexContactObject.getRelationship().equals("sexual_partner")) { //Changing the rule file to the rule file for index contacts who are sex partners
                JSONObject form = (new FormUtils()).getFormJsonFromRepositoryOrAssets(activity, CoreConstants.JSON_FORM.getHivIndexContactFollowupVisitForNegativeClients());
                if (form != null)
                    form.put("rules_file", "rule/hiv_index_contact_followup_for_negative_sex_partners_rules.yml");
                intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.JSON_FORM, form.toString());
            } else { //Leaving the default rule files for non sex partners index contacts
                intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.JSON_FORM, (new FormUtils()).getFormJsonFromRepositoryOrAssets(activity, CoreConstants.JSON_FORM.getHivIndexContactFollowupVisitForNegativeClients()).toString());
            }
        } else if (hivIndexContactObject.getHivStatus().equals("positive")) {
            intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.JSON_FORM, (new FormUtils()).getFormJsonFromRepositoryOrAssets(activity, CoreConstants.JSON_FORM.getHivIndexContactFollowupVisitForPositiveClients()).toString());
        } else {
            if (hivIndexContactObject.getRelationship().equals("sexual_partner")) { //Changing the rule file to the rule file for index contacts who are sex partners
                JSONObject form = (new FormUtils()).getFormJsonFromRepositoryOrAssets(activity, CoreConstants.JSON_FORM.getHivIndexContactFollowupVisit());
                if (form != null)
                    form.put("rules_file", "rule/hiv_index_contact_followup_for_unknown_status_sex_partner_rules.yml");
                intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.JSON_FORM, form.toString());
            } else { //Leaving the default rule files for non sex partners index contacts
                intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.JSON_FORM, (new FormUtils()).getFormJsonFromRepositoryOrAssets(activity, CoreConstants.JSON_FORM.getHivIndexContactFollowupVisit()).toString());
            }
        }

        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.ACTION, Constants.ActivityPayloadType.FOLLOW_UP_VISIT);
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.USE_DEFAULT_NEAT_FORM_LAYOUT, false);

        activity.startActivityForResult(intent, org.smartregister.chw.anc.util.Constants.REQUEST_CODE_HOME_VISIT);
    }

    @Override
    protected void onCreation() {
        super.onCreation();
        setCommonPersonObjectClient(getClientDetailsByBaseEntityID(getHivIndexContactObject().getBaseEntityId()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (notificationAndReferralRecyclerView != null && notificationAndReferralRecyclerView.getAdapter() != null) {
            notificationAndReferralRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    public CommonPersonObjectClient getCommonPersonObjectClient() {
        return commonPersonObjectClient;
    }

    public void setCommonPersonObjectClient(CommonPersonObjectClient commonPersonObjectClient) {
        this.commonPersonObjectClient = commonPersonObjectClient;
    }

    @Override
    protected void initializePresenter() {
        showProgressBar(true);
        setHivContactProfilePresenter(new HivIndexContactProfilePresenter(this, new CoreHivIndexContactProfileInteractor(), getHivIndexContactObject()));
        fetchProfileData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        try {
            if (itemId == R.id.action_issue_hiv_community_followup_referral) {
                HivRegisterActivity.startHIVFormActivity(this, getHivIndexContactObject().getBaseEntityId(), CoreConstants.JSON_FORM.getHivIndexContactCommunityFollowupReferral(), (new FormUtils()).getFormJsonFromRepositoryOrAssets(this, CoreConstants.JSON_FORM.getHivIndexContactCommunityFollowupReferral()).toString());
                return true;
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(org.smartregister.chw.core.R.menu.hiv_profile_menu, menu);
        menu.findItem(R.id.action_issue_hiv_community_followup_referral).setVisible(true);
        return true;
    }

    @Override
    public void openFollowUpVisitForm(boolean isEdit) {
        if (!isEdit) {
            try {
                startHivIndexContactFollowupActivity(this, getHivIndexContactObject().getBaseEntityId());
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    @Override
    protected void removeMember() {
        // Not required for HF (as seen in other profile activities)?
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.record_hiv_followup_visit) {
            openFollowUpVisitForm(false);
        }
    }

    @Override
    public void initializeCallFAB() {
        setHivFloatingMenu(new HivIndexContactFloatingMenu(this, getHivIndexContactObject()));

        OnClickFloatingMenu onClickFloatingMenu = viewId -> {
            switch (viewId) {
                case R.id.hiv_fab:
                    checkPhoneNumberProvided();
                    ((HivIndexContactFloatingMenu) getHivFloatingMenu()).animateFAB();
                    break;
                case R.id.call_layout:
                    ((HivIndexContactFloatingMenu) getHivFloatingMenu()).launchCallWidget();
                    ((HivIndexContactFloatingMenu) getHivFloatingMenu()).animateFAB();
                    break;
                default:
                    Timber.d("Unknown fab action");
                    break;
            }
        };

        ((HivIndexContactFloatingMenu) getHivFloatingMenu()).setFloatMenuClickListener(onClickFloatingMenu);
        getHivFloatingMenu().setGravity(Gravity.BOTTOM | Gravity.END);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(getHivFloatingMenu(), linearLayoutParams);
    }

    private void checkPhoneNumberProvided() {
        boolean phoneNumberAvailable = (StringUtils.isNotBlank(getHivIndexContactObject().getPhoneNumber()));
        ((HivIndexContactFloatingMenu) getHivFloatingMenu()).redraw(phoneNumberAvailable);
    }

    @Override
    public Context getContext() {
        return HivIndexContactProfileActivity.this;
    }

    @Override
    public void verifyHasPhone() {
        // TODO -> Implement for HF
    }

    @Override
    public void notifyHasPhone(boolean b) {
        // TODO -> Implement for HF
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        try {
            boolean savedToHivRegistry = data.getBooleanExtra(REGISTERED_TO_HIV_REGISTRY, false);
            if (savedToHivRegistry) {
                HivProfileActivity.startHivProfileActivity(this, Objects.requireNonNull(HivDao.getMember(getHivIndexContactObject().getBaseEntityId())));
                finish();
            } else {
                setHivIndexContactObject(HivIndexDao.getMember(getHivIndexContactObject().getBaseEntityId()));
                initializePresenter();
                fetchProfileData();
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void setFollowUpButtonDue() {
        super.setFollowUpButtonDue();
        showFollowUpVisitButton(!getHivIndexContactObject().getContactClientNotificationMethod().equals("na"));
    }

    @Override
    public void onReceivedNotifications(List<Pair<String, String>> list) {

    }
}

