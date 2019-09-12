package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.contract.FamilyOtherMemberProfileExtendedContract;
import org.smartregister.chw.core.custom_views.CoreFamilyMemberFloatingMenu;
import org.smartregister.chw.core.fragment.CoreFamilyOtherMemberProfileFragment;
import org.smartregister.chw.core.interactor.CoreChildProfileInteractor;
import org.smartregister.chw.core.listener.FloatingMenuListener;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.presenter.CoreFamilyOtherMemberActivityPresenter;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.activity.BaseFamilyOtherMemberProfileActivity;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.fragment.BaseFamilyOtherMemberProfileFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.view.contract.BaseProfileContract;

import timber.log.Timber;

public abstract class CoreFamilyOtherMemberProfileActivity extends BaseFamilyOtherMemberProfileActivity
        implements FamilyOtherMemberProfileExtendedContract.View {

    protected CoreFamilyMemberFloatingMenu familyFloatingMenu;
    protected String familyBaseEntityId;
    protected String baseEntityId;
    protected String familyHead;
    protected String primaryCaregiver;
    protected String familyName;
    protected String PhoneNumber;
    protected CommonPersonObjectClient commonPersonObject;
    protected OnClickFloatingMenu onClickFloatingMenu;
    private TextView textViewFamilyHas;
    private RelativeLayout layoutFamilyHasRow;

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_family_other_member_profile_chw);

        Toolbar toolbar = findViewById(org.smartregister.family.R.id.family_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        appBarLayout = findViewById(org.smartregister.family.R.id.toolbar_appbarlayout);

        imageRenderHelper = new ImageRenderHelper(this);

        initializePresenter();

        setupViews();
    }

    @Override
    protected void setupViews() {
        super.setupViews();

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(String.format(getString(R.string.return_to_family_name), presenter().getFamilyName()));

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorHeight(0);

        findViewById(R.id.viewpager).setVisibility(View.GONE);

        // add floating menu
        familyFloatingMenu = getFamilyMemberFloatingMenu();
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        familyFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        addContentView(familyFloatingMenu, linearLayoutParams);

        familyFloatingMenu.setClickListener(onClickFloatingMenu);
        textViewFamilyHas = findViewById(R.id.textview_family_has);
        layoutFamilyHasRow = findViewById(R.id.family_has_row);

        layoutFamilyHasRow.setOnClickListener(this);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        FloatingMenuListener.getInstance(this, presenter().getFamilyBaseEntityId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem addMember = menu.findItem(R.id.add_member);
        if (addMember != null) {
            addMember.setVisible(false);
        }

        getMenuInflater().inflate(R.menu.other_member_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (i == R.id.action_anc_registration) {
            startAncRegister();
            return true;
        } else if (i == R.id.action_malaria_registration) {
            startMalariaRegister();
            return true;
        } else if (i == R.id.action_registration) {
            startFormForEdit(R.string.edit_member_form_title);
            return true;
        } else if (i == R.id.action_remove_member) {
            removeIndividualProfile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public CoreFamilyOtherMemberActivityPresenter presenter() {
        return (CoreFamilyOtherMemberActivityPresenter) presenter;
    }

    protected abstract void startAncRegister();

    protected abstract void startMalariaRegister();

    public void startFormForEdit(Integer title_resource) {

        CommonRepository commonRepository = Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName);

        final CommonPersonObject personObject = commonRepository.findByBaseEntityId(commonPersonObject.getCaseId());
        final CommonPersonObjectClient client =
                new CommonPersonObjectClient(personObject.getCaseId(), personObject.getDetails(), "");
        client.setColumnmaps(personObject.getColumnmaps());

        startEditMemberJsonForm(title_resource, client);
    }

    protected abstract void removeIndividualProfile();

    protected abstract void startEditMemberJsonForm(Integer title_resource, CommonPersonObjectClient client);

    protected abstract BaseProfileContract.Presenter getFamilyOtherMemberActivityPresenter(
            String familyBaseEntityId, String baseEntityId, String familyHead, String primaryCaregiver, String villageTown, String familyName);

    protected abstract CoreFamilyMemberFloatingMenu getFamilyMemberFloatingMenu();

    public void startFormActivity(JSONObject jsonForm) {

        Intent intent = new Intent(this, Utils.metadata().familyMemberFormActivity);
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());


        Form form = new Form();
        form.setActionBarBackground(R.color.family_actionbar);
        form.setWizard(false);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case CoreConstants.ProfileActivityResults.CHANGE_COMPLETED:
                Intent intent = new Intent(getFamilyOtherMemberProfileActivity(), getFamilyProfileActivity());
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
                finish();
                break;
            case JsonFormUtils.REQUEST_CODE_GET_JSON:
                try {
                    String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                    JSONObject form = new JSONObject(jsonString);
                    if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyMemberRegister.updateEventType)) {
                        presenter().updateFamilyMember(jsonString);
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }
                break;
            default:
                break;
        }
    }

    protected abstract Context getFamilyOtherMemberProfileActivity();

    protected abstract Class<? extends CoreFamilyProfileActivity> getFamilyProfileActivity();

    @Override
    public void refreshList() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            for (int i = 0; i < adapter.getCount(); i++) {
                refreshList(adapter.getItem(i));
            }
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                for (int i = 0; i < adapter.getCount(); i++) {
                    refreshList(adapter.getItem(i));
                }
            });
        }
    }

    @Override
    public void updateHasPhone(boolean hasPhone) {
        if (familyFloatingMenu != null) {
            familyFloatingMenu.reDraw(hasPhone);
        }
    }

    @Override
    public void setFamilyServiceStatus(String status) {
        layoutFamilyHasRow.setVisibility(View.VISIBLE);
        if (status.equalsIgnoreCase(CoreChildProfileInteractor.FamilyServiceType.DUE.name())) {
            textViewFamilyHas.setText(getString(R.string.family_has_services_due));
        } else if (status.equalsIgnoreCase(CoreChildProfileInteractor.FamilyServiceType.OVERDUE.name())) {
            textViewFamilyHas.setText(CoreChildUtils.fromHtml(getString(R.string.family_has_service_overdue)));
        } else {
            textViewFamilyHas.setText(getString(R.string.family_has_nothing_due));
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    protected void refreshList(Fragment fragment) {
        if (fragment instanceof CoreFamilyOtherMemberProfileFragment) {
            CoreFamilyOtherMemberProfileFragment familyOtherMemberProfileFragment = ((CoreFamilyOtherMemberProfileFragment) fragment);
            if (familyOtherMemberProfileFragment.presenter() != null) {
                familyOtherMemberProfileFragment.refreshListView();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.family_has_row) {
            openFamilyDueTab();
        } else {
            super.onClick(view);
        }
    }

    @Override
    protected void initializePresenter() {
        commonPersonObject = (CommonPersonObjectClient) getIntent().getSerializableExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON);
        familyBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        familyHead = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_HEAD);
        primaryCaregiver = getIntent().getStringExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
        String villageTown = getIntent().getStringExtra(Constants.INTENT_KEY.VILLAGE_TOWN);
        familyName = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_NAME);
        PhoneNumber = commonPersonObject.getColumnmaps().get(CoreConstants.JsonAssets.FAMILY_MEMBER.PHONE_NUMBER);
        presenter = getFamilyOtherMemberActivityPresenter(familyBaseEntityId, baseEntityId, familyHead, primaryCaregiver, villageTown, familyName);
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        BaseFamilyOtherMemberProfileFragment profileOtherMemberFragment = getFamilyOtherMemberProfileFragment();
        adapter.addFragment(profileOtherMemberFragment, "");

        viewPager.setAdapter(adapter);

        return viewPager;
    }

    protected abstract BaseFamilyOtherMemberProfileFragment getFamilyOtherMemberProfileFragment();

    private void openFamilyDueTab() {
        Intent intent = new Intent(this, getFamilyProfileActivity());

        intent.putExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, familyBaseEntityId);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, familyHead);
        intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, primaryCaregiver);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_NAME, familyName);

        intent.putExtra(CoreConstants.INTENT_KEY.SERVICE_DUE, true);
        startActivity(intent);
    }

}
