package com.opensrp.chw.core.activity;

import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.opensrp.chw.core.R;
import com.opensrp.chw.core.contract.FamilyProfileExtendedContract;
import com.opensrp.chw.core.custom_views.FamilyFloatingMenu;
import com.opensrp.chw.core.event.PermissionEvent;
import com.opensrp.chw.core.listener.FloatingMenuListener;
import com.opensrp.chw.core.presenter.CoreFamilyProfilePresenter;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.activity.BaseFamilyProfileActivity;
import org.smartregister.family.util.Constants;
import org.smartregister.util.PermissionUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class CoreFamilyProfileActivity extends BaseFamilyProfileActivity implements FamilyProfileExtendedContract.View {
    protected String familyBaseEntityId;
    protected String familyHead;
    protected String primaryCaregiver;
    protected String familyName;
    private FamilyFloatingMenu familyFloatingMenu;

    @Override
    protected void setupViews() {
        super.setupViews();

        // Update profile border
        CircleImageView profileView = findViewById(R.id.imageview_profile);
        profileView.setBorderWidth(2);

        // add floating menu
        familyFloatingMenu = new FamilyFloatingMenu(this);
        LinearLayout.LayoutParams linearLayoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        familyFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        addContentView(familyFloatingMenu, linearLayoutParams);
        familyFloatingMenu.setClickListener(
                FloatingMenuListener.getInstance(this, presenter().familyBaseEntityId())
        );
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        FloatingMenuListener.getInstance(this, presenter().familyBaseEntityId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem addMember = menu.findItem(R.id.add_member);
        if (addMember != null) {
            addMember.setVisible(false);
        }

        getMenuInflater().inflate(R.menu.profile_menu, menu);

        return true;
    }


    @Override
    public FamilyProfileExtendedContract.Presenter presenter() {
        return (CoreFamilyProfilePresenter) presenter;
    }

    @Override
    public void startChildForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {
        presenter().startChildForm(formName, entityId, metadata, currentLocationId);
    }

    @Override
    public void updateHasPhone(boolean hasPhone) {
        if (familyFloatingMenu != null) {
            familyFloatingMenu.reDraw(hasPhone);
        }
    }

    @Override
    protected void initializePresenter() {
        familyBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        familyHead = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_HEAD);
        primaryCaregiver = getIntent().getStringExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
        familyName = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_NAME);
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    public String getFamilyBaseEntityId() {
        return familyBaseEntityId;
    }

    public void startFormForEdit() {
        if (familyBaseEntityId != null) {
            presenter().fetchProfileData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // If request is cancelled, the result arrays are empty.
        if (requestCode == PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE) {
            boolean granted = (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED);
            if (granted) {
                PermissionEvent event = new PermissionEvent(requestCode, granted);
                EventBus.getDefault().post(event);
            } else {
                Toast.makeText(this, getText(R.string.allow_calls_denied), Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void setPrimaryCaregiver(String caregiver) {
        if (StringUtils.isNotBlank(caregiver)) {
            this.primaryCaregiver = caregiver;
            getIntent().putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, caregiver);
        }
    }

    protected void setFamilyHead(String head) {
        if (StringUtils.isNotBlank(head)) {
            this.familyHead = head;
            getIntent().putExtra(Constants.INTENT_KEY.FAMILY_HEAD, head);
        }
    }

    @Override
    public void refreshMemberList(FetchStatus fetchStatus) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            for (int i = 0; i < adapter.getCount(); i++) {
                refreshList(adapter.getItem(i));
            }
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    for (int i = 0; i < adapter.getCount(); i++) {
                        refreshList(adapter.getItem(i));
                    }
                }
            });
        }
    }

    protected abstract void refreshList(Fragment item);
}
