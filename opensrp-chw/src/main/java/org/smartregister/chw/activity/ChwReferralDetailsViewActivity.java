package org.smartregister.chw.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.dao.ChwHivOutcomeDao;
import org.smartregister.chw.dao.ReferralDao;
import org.smartregister.chw.referral.activity.ReferralDetailsViewActivity;
import org.smartregister.chw.referral.domain.MemberObject;
import org.smartregister.chw.referral.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Task;
import org.smartregister.view.customcontrols.CustomFontTextView;

import timber.log.Timber;

public class ChwReferralDetailsViewActivity extends ReferralDetailsViewActivity {


    @Override
    protected void onCreation() {
        super.onCreation();
        String taskId = ReferralDao.getTaskIdByReasonReference(getMemberObject().getBaseEntityId());
        Task task = ChwApplication.getInstance().getTaskRepository().getTaskByIdentifier(taskId);
        if (!task.getBusinessStatus().equalsIgnoreCase("Complete")) {
            createCancelReferral(task);
        } else {
            //TODO this needs refactoring. This is a draft implementation.
            if (getMemberObject().getChwReferralService().equals("Suspected HIV")) {
                String servicesProvided = ChwHivOutcomeDao.servicesProvided(task.getForEntity(), task.getLastModified().getMillis());
                String hivStatus = ChwHivOutcomeDao.hivStatus(task.getForEntity(), task.getLastModified().getMillis());

                if (servicesProvided != null) {
                    ((CustomFontTextView) findViewById(R.id.chw_details_phone)).setText(servicesProvided + " " + hivStatus);
                    ((CustomFontTextView) findViewById(R.id.chw_details_phone_label)).setText("Services Provided");
                    findViewById(R.id.chw_details_phone_layout).setVisibility(View.VISIBLE);
                }
            }
        }

        if (getMemberObject().getProblem().equals("anc_male_engagement"))
            ((CustomFontTextView) findViewById(R.id.client_referral_problem)).setText(getResources().getString(R.string.anc_male_engagement));
        else if (getMemberObject().getProblem().equalsIgnoreCase("None"))
            ((CustomFontTextView) findViewById(R.id.client_referral_problem)).setText(getResources().getString(R.string.none));

        if (getMemberObject().getServicesBeforeReferral() != null && getMemberObject().getServicesBeforeReferral().equalsIgnoreCase("None"))
            ((CustomFontTextView) findViewById(R.id.pre_referral_management)).setText(getResources().getString(R.string.none));
    }

    public static void startChwReferralDetailsViewActivity(Activity activity, MemberObject memberObject, CommonPersonObjectClient client) {
        Intent intent = new Intent(activity, ChwReferralDetailsViewActivity.class);
        intent.putExtra(Constants.ReferralMemberObject.MEMBER_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    private void createCancelReferral(Task task) {
        LinearLayout referralVisitBar = findViewById(R.id.record_visit_bar);
        referralVisitBar.setVisibility(View.VISIBLE);

        CustomFontTextView markAsDone = findViewById(R.id.mark_ask_done);
        markAsDone.setText(R.string.cancel_referral);

        View viewReferralRow = findViewById(R.id.view_referal_row);
        viewReferralRow.setVisibility(View.GONE);

        markAsDone.setOnClickListener(view -> {
            closeReferralDialog(task);
        });

    }

    private void cancelReferral(Task task) {
        MemberObject memberObject = getMemberObject();
        assert memberObject != null;
        task.setForEntity(memberObject.getBaseEntityId());

        CoreReferralUtils.cancelTask(task);
    }

    private void closeReferralDialog(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.cancel_referral_title));
        builder.setMessage(getString(R.string.cancel_referral_message));
        builder.setCancelable(true);

        builder.setPositiveButton(this.getString(R.string.cancel_referral), (dialog, id) -> {
            try {
                cancelReferral(task);
                finish();
            } catch (Exception e) {
                Timber.e(e, "ReferralTaskViewActivity --> closeReferralDialog");
            }
        });
        builder.setNegativeButton(this.getString(R.string.exit), ((dialog, id) -> dialog.cancel()));

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private String getTranslatedHivServicesProvided(String serviceProvided) {
        //TODO implement this
        return null;
    }
}
