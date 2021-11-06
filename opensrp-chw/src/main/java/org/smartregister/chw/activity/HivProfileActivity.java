package org.smartregister.chw.activity;

import static org.smartregister.chw.util.NotificationsUtil.handleNotificationRowClick;
import static org.smartregister.chw.util.NotificationsUtil.handleReceivedNotifications;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.activity.CoreHivProfileActivity;
import org.smartregister.chw.core.activity.CoreHivUpcomingServicesActivity;
import org.smartregister.chw.core.adapter.NotificationListAdapter;
import org.smartregister.chw.core.contract.FamilyProfileExtendedContract;
import org.smartregister.chw.core.interactor.CoreHivProfileInteractor;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.listener.OnRetrieveNotifications;
import org.smartregister.chw.core.task.RunnableTask;
import org.smartregister.chw.core.utils.ChwNotificationUtil;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.custom_view.HivFloatingMenu;
import org.smartregister.chw.hiv.activity.BaseHivFormsActivity;
import org.smartregister.chw.hiv.dao.HivDao;
import org.smartregister.chw.hiv.domain.HivMemberObject;
import org.smartregister.chw.hiv.util.Constants;
import org.smartregister.chw.hiv.util.HivUtil;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.presenter.HivProfilePresenter;
import org.smartregister.chw.schedulers.ChwScheduleTaskExecutor;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.annotations.Nullable;
import timber.log.Timber;

public class HivProfileActivity extends CoreHivProfileActivity
        implements FamilyProfileExtendedContract.PresenterCallBack, OnRetrieveNotifications {

    private static final String FOLLOWUP_STATUS_DECEASED_EN_VALUE = "Deceased";
    private static final String FOLLOWUP_STATUS_QUALIFIED_FROM_SERVICE_EN_VALUE = "Client has completed and qualified from the services";
    private static final String FOLLOWUP_STATUS_DECEASED_SW_VALUE = "Amefariki";
    private static final String FOLLOWUP_STATUS_QUALIFIED_FROM_SERVICE_SW_VALUE = "Amefuzu huduma";

    private List<ReferralTypeModel> referralTypeModels = new ArrayList<>();
    private NotificationListAdapter notificationListAdapter = new NotificationListAdapter();
    private Flavor flavor = new HivProfileActivityFlv();

    public static void startHivProfileActivity(Activity activity, HivMemberObject memberObject) {
        Intent intent = new Intent(activity, HivProfileActivity.class);
        intent.putExtra(Constants.ActivityPayload.HIV_MEMBER_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    public static void startHivFollowupActivity(Activity activity, String baseEntityID) throws JSONException {
        Intent intent = new Intent(activity, BaseHivFormsActivity.class);
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.BASE_ENTITY_ID, baseEntityID);

        HivMemberObject hivMemberObject = HivDao.getMember(baseEntityID);
        JSONObject formJsonObject;

        if (hivMemberObject.getCtcNumber().isEmpty()) {
            formJsonObject = (new FormUtils()).getFormJsonFromRepositoryOrAssets(activity, org.smartregister.chw.util.Constants.JSON_FORM.getHivFollowupVisit());
        } else {
            formJsonObject = (new FormUtils()).getFormJsonFromRepositoryOrAssets(activity, org.smartregister.chw.util.Constants.JSON_FORM.getHivFollowupVisitForClientsWithCtcNumbers());
        }
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.JSON_FORM, formJsonObject.toString());
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.ACTION, Constants.ActivityPayloadType.FOLLOW_UP_VISIT);
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.USE_DEFAULT_NEAT_FORM_LAYOUT, false);

        activity.startActivityForResult(intent, org.smartregister.chw.anc.util.Constants.REQUEST_CODE_HOME_VISIT);
    }

    @Override
    public void setProfileViewDetails(@androidx.annotation.Nullable HivMemberObject hivMemberObject) {
        super.setProfileViewDetails(hivMemberObject);

        if (!getHivMemberObject().getClientFollowupStatus().equals("")) {
            int labelTextColor;
            int background;
            String labelText;

            getTvStatus().setVisibility(View.VISIBLE);
            switch (getHivMemberObject().getClientFollowupStatus()) {
                case FOLLOWUP_STATUS_DECEASED_EN_VALUE:
                case FOLLOWUP_STATUS_DECEASED_SW_VALUE:
                    labelTextColor = context().getColorResource(org.smartregister.chw.opensrp_chw_anc.R.color.high_risk_text_red);
                    background = org.smartregister.chw.opensrp_chw_anc.R.drawable.high_risk_label;
                    labelText = getResources().getString(R.string.client_followup_status_deceased);
                    hideFollowUpVisitButton();
                    break;
                case FOLLOWUP_STATUS_QUALIFIED_FROM_SERVICE_EN_VALUE:
                case FOLLOWUP_STATUS_QUALIFIED_FROM_SERVICE_SW_VALUE:
                    labelTextColor = context().getColorResource(org.smartregister.chw.opensrp_chw_anc.R.color.low_risk_text_green);
                    background = org.smartregister.chw.opensrp_chw_anc.R.drawable.low_risk_label;
                    labelText = getResources().getString(R.string.client_followup_status_qualified_from_service);
                    hideFollowUpVisitButton();
                    break;
                default:
                    labelTextColor = context().getColorResource(org.smartregister.chw.opensrp_chw_anc.R.color.default_risk_text_black);
                    background = org.smartregister.chw.opensrp_chw_anc.R.drawable.risk_label;
                    labelText = "";
                    getTvStatus().setVisibility(View.GONE);
                    break;
            }

            getTvStatus().setText(labelText);
            getTvStatus().setTextColor(labelTextColor);
            getTvStatus().setBackgroundResource(background);
        }

    }

    @Override
    protected void onCreation() {
        super.onCreation();
        addHivReferralTypes();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationAndReferralRecyclerView.setAdapter(notificationListAdapter);
        notificationListAdapter.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        notificationListAdapter.canOpen = true;
        ChwNotificationUtil.retrieveNotifications(ChwApplication.getApplicationFlavor().hasReferrals(),
                getHivMemberObject().getBaseEntityId(), this);
    }

    @Override
    public void setupViews() {
        super.setupViews();

    }

    @Override
    protected void removeMember() {
        IndividualProfileRemoveActivity.startIndividualProfileActivity(HivProfileActivity.this,
                getClientDetailsByBaseEntityID(getHivMemberObject().getBaseEntityId()),
                getHivMemberObject().getFamilyBaseEntityId(), getHivMemberObject().getFamilyHead(),
                getHivMemberObject().getPrimaryCareGiver(), FpRegisterActivity.class.getCanonicalName());
    }

    @Override
    protected void initializePresenter() {
        showProgressBar(true);
        setHivProfilePresenter(new HivProfilePresenter(this, new CoreHivProfileInteractor(this), getHivMemberObject()));
        fetchProfileData();
    }

    private void checkPhoneNumberProvided() {
        boolean phoneNumberAvailable = (StringUtils.isNotBlank(getHivMemberObject().getPhoneNumber())
                || StringUtils.isNotBlank(getHivMemberObject().getPrimaryCareGiverPhoneNumber()));

        ((HivFloatingMenu) getHivFloatingMenu()).redraw(phoneNumberAvailable);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.record_hiv_followup_visit) {
            openFollowUpVisitForm(false);
        } else if (id == R.id.rlHivRegistrationDate) {
            startHivRegistrationDetailsActivity();
        }
        handleNotificationRowClick(this, view, notificationListAdapter, getHivMemberObject().getBaseEntityId());
    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public void verifyHasPhone() {
        // Implement
    }

    @Override
    public void notifyHasPhone(boolean b) {
        // Implement
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // recompute schedule
        Runnable runnable = () -> ChwScheduleTaskExecutor.getInstance().execute(getHivMemberObject().getBaseEntityId(), org.smartregister.chw.hiv.util.Constants.EventType.FOLLOW_UP_VISIT, new Date());
        org.smartregister.chw.util.Utils.startAsyncTask(new RunnableTask(runnable), null);

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CoreConstants.ProfileActivityResults.CHANGE_COMPLETED && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent(this, HivRegisterActivity.class);
            intent.putExtras(getIntent().getExtras());
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void openMedicalHistory() {
        //TODO implement
    }

    @Override
    public void openHivRegistrationForm() {
        try {
            String formName;
            if (getHivMemberObject().getGender().equalsIgnoreCase("male")) {
                formName = CoreConstants.JSON_FORM.getMaleHivRegistration();
            } else {
                formName = CoreConstants.JSON_FORM.getFemaleHivRegistration();
            }
            HivRegisterActivity.startHIVFormActivity(this, getHivMemberObject().getBaseEntityId(), formName, (new FormUtils()).getFormJsonFromRepositoryOrAssets(this, formName).toString());
        } catch (JSONException e) {
            Timber.e(e);
        }

    }

    @Override
    public void openUpcomingServices() {
        CoreHivUpcomingServicesActivity.startMe(this, HivUtil.toMember(getHivMemberObject()));
    }

    @Override
    public void openFamilyDueServices() {
        Intent intent = new Intent(this, FamilyProfileActivity.class);

        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, getHivMemberObject().getFamilyBaseEntityId());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_HEAD, getHivMemberObject().getFamilyHead());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.PRIMARY_CAREGIVER, getHivMemberObject().getPrimaryCareGiver());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_NAME, getHivMemberObject().getFamilyName());

        intent.putExtra(CoreConstants.INTENT_KEY.SERVICE_DUE, true);
        startActivity(intent);
    }

    @Override
    public void openFollowUpVisitForm(boolean isEdit) {
        if (!isEdit) {
            try {
                startHivFollowupActivity(this, getHivMemberObject().getBaseEntityId());
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    private void addHivReferralTypes() {
        if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH) {

            //HIV Testing referrals will only be issued to non positive clients
            if (getHivMemberObject().getCtcNumber().isEmpty()) {
                referralTypeModels.add(new ReferralTypeModel(getString(R.string.hts_referral),
                        CoreConstants.JSON_FORM.getHtsReferralForm(), CoreConstants.TASKS_FOCUS.SUSPECTED_HIV));
            } else { //HIV Treatment and care referrals will be issued to HIV Positive clients
                referralTypeModels.add(new ReferralTypeModel(getString(R.string.hiv_referral),
                        CoreConstants.JSON_FORM.getHivReferralForm(), CoreConstants.TASKS_FOCUS.SICK_HIV));
            }

            referralTypeModels.add(new ReferralTypeModel(getString(R.string.tb_referral),
                    CoreConstants.JSON_FORM.getTbReferralForm(), CoreConstants.TASKS_FOCUS.SUSPECTED_TB));


            if (getHivMemberObject().getGender().equalsIgnoreCase("Female")) {
                //Obtaining the clients CommonPersonObjectClient used for checking is the client is Of Reproductive Age
                CommonRepository commonRepository = Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName);

                final CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(getHivMemberObject().getBaseEntityId());
                final CommonPersonObjectClient client = new CommonPersonObjectClient(commonPersonObject.getCaseId(), commonPersonObject.getDetails(), "");
                client.setColumnmaps(commonPersonObject.getColumnmaps());

                if (org.smartregister.chw.core.utils.Utils.isMemberOfReproductiveAge(client, 15, 49)) {
                    referralTypeModels.add(new ReferralTypeModel(getString(R.string.anc_danger_signs),
                            CoreConstants.JSON_FORM.getAncReferralForm(), CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS));

                    referralTypeModels.add(new ReferralTypeModel(getString(R.string.pnc_danger_signs),
                            CoreConstants.JSON_FORM.getPncReferralForm(), CoreConstants.TASKS_FOCUS.PNC_DANGER_SIGNS));
                }
            }

            referralTypeModels.add(new ReferralTypeModel(getString(R.string.gbv_referral),
                    CoreConstants.JSON_FORM.getGbvReferralForm(), CoreConstants.TASKS_FOCUS.SUSPECTED_GBV));
        }

    }

    public List<ReferralTypeModel> getReferralTypeModels() {
        return referralTypeModels;
    }

    @Override
    public void initializeCallFAB() {
        setHivFloatingMenu(new HivFloatingMenu(this, getHivMemberObject()));

        OnClickFloatingMenu onClickFloatingMenu = viewId -> {
            switch (viewId) {
                case R.id.hiv_fab:
                    checkPhoneNumberProvided();
                    ((HivFloatingMenu) getHivFloatingMenu()).animateFAB();
                    break;
                case R.id.call_layout:
                    ((HivFloatingMenu) getHivFloatingMenu()).launchCallWidget();
                    ((HivFloatingMenu) getHivFloatingMenu()).animateFAB();
                    break;
                case R.id.refer_to_facility_layout:
                    ((HivProfilePresenter) getHivProfilePresenter()).referToFacility();
                    break;
                default:
                    Timber.d("Unknown fab action");
                    break;
            }

        };

        ((HivFloatingMenu) getHivFloatingMenu()).setFloatMenuClickListener(onClickFloatingMenu);
        getHivFloatingMenu().setGravity(Gravity.BOTTOM | Gravity.END);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(getHivFloatingMenu(), linearLayoutParams);
    }

    @Override
    public void onReceivedNotifications(List<Pair<String, String>> notifications) {
        handleReceivedNotifications(this, notifications, notificationListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(org.smartregister.chw.core.R.menu.hiv_profile_menu, menu);

        flavor.updateTbMenuItems(getHivMemberObject().getBaseEntityId(), menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == org.smartregister.chw.core.R.id.action_tb_registration) {
            startTbRegister();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void startTbRegister() {
        try {
            TbRegisterActivity.startTbFormActivity(HivProfileActivity.this, getHivMemberObject().getBaseEntityId(), CoreConstants.JSON_FORM.getTbRegistration(), (new FormUtils()).getFormJsonFromRepositoryOrAssets(this, CoreConstants.JSON_FORM.getTbRegistration()).toString());
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    /**
     * Pre-populating the registration form before opening it
     */
    public void startHivRegistrationDetailsActivity() {
        Intent intent = new Intent(this, BaseHivFormsActivity.class);
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.BASE_ENTITY_ID, getHivMemberObject().getBaseEntityId());

        String formName;
        if (getHivMemberObject().getGender().equalsIgnoreCase("male")) {
            formName = CoreConstants.JSON_FORM.getMaleHivRegistration();
        } else {
            formName = CoreConstants.JSON_FORM.getFemaleHivRegistration();
        }

        JSONObject formJsonObject = null;
        try {
            formJsonObject = (new FormUtils()).getFormJsonFromRepositoryOrAssets(this, formName);
            JSONArray fields = formJsonObject.getJSONArray("steps").getJSONObject(0).getJSONArray("fields");

            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.getJSONObject(i);
                if (field.getString("name").equals("cbhs_number")) {
                    field.getJSONObject("properties").put("text", getHivMemberObject().getCbhsNumber());
                } else if (field.getString("name").equals("client_hiv_status_during_registration")) {
                    if (!getHivMemberObject().getCtcNumber().isEmpty())
                        field.getJSONObject("properties").put("selection", "1");
                    else
                        field.getJSONObject("properties").put("selection", "0");
                } else if (field.getString("name").equals("ctc_number") && !getHivMemberObject().getCtcNumber().isEmpty()) {
                    field.getJSONObject("properties").put("text", getHivMemberObject().getCtcNumber());
                }
            }

        } catch (JSONException e) {
            Timber.e(e);
        }

        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.JSON_FORM, formJsonObject.toString());
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.ACTION, Constants.ActivityPayloadType.REGISTRATION);
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.USE_DEFAULT_NEAT_FORM_LAYOUT, false);

        this.startActivityForResult(intent, org.smartregister.chw.anc.util.Constants.REQUEST_CODE_HOME_VISIT);
    }

    public interface Flavor {
        void updateTbMenuItems(@Nullable String baseEntityId, @Nullable Menu menu);
    }
}

