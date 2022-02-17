package org.smartregister.chw.task;

import static org.smartregister.chw.util.CrvsConstants.BASE_ENTITY_ID;
import static org.smartregister.chw.util.CrvsConstants.CLIENT_TYPE;
import static org.smartregister.chw.util.CrvsConstants.DEATH_CERTIFICATE_ISSUE_DATE;
import static org.smartregister.chw.util.CrvsConstants.DEATH_CERTIFICATE_NUMBER;
import static org.smartregister.chw.util.CrvsConstants.DEATH_NOTIFICATION_DONE;
import static org.smartregister.chw.util.CrvsConstants.DOB;
import static org.smartregister.chw.util.CrvsConstants.INFORMANT_ADDRESS;
import static org.smartregister.chw.util.CrvsConstants.INFORMANT_NAME;
import static org.smartregister.chw.util.CrvsConstants.INFORMANT_PHONE;
import static org.smartregister.chw.util.CrvsConstants.INFORMANT_RELATIONSHIP;
import static org.smartregister.chw.util.CrvsConstants.NO;
import static org.smartregister.chw.util.CrvsConstants.OFFICIAL_ADDRESS;
import static org.smartregister.chw.util.CrvsConstants.OFFICIAL_ID;
import static org.smartregister.chw.util.CrvsConstants.OFFICIAL_NAME;
import static org.smartregister.chw.util.CrvsConstants.OFFICIAL_NUMBER;
import static org.smartregister.chw.util.CrvsConstants.OFFICIAL_POSITION;
import static org.smartregister.chw.util.CrvsConstants.RECEIVED_DEATH_CERTIFICATE;
import static org.smartregister.chw.util.CrvsConstants.YES;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;

import org.jeasy.rules.api.Rules;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.DeadClientsUpdateActivity;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.holders.RegisterViewHolder;
import org.smartregister.chw.core.model.ChildVisit;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.util.Constants;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DeadUpdateLastAsyncTask extends AsyncTask<Void, Void, Void> {
    public final Context context;
    public final RegisterViewHolder viewHolder;
    public final CommonPersonObjectClient baseEntityId;
    private final Rules rules;
    public CommonPersonObject commonPersonObject;
    public ChildVisit childVisit;
    public View.OnClickListener onClickListener;
    private SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    public DeadUpdateLastAsyncTask(Context context, RegisterViewHolder viewHolder, CommonPersonObjectClient baseEntityId, View.OnClickListener onClickListener) {
        this.context = context;
        this.viewHolder = viewHolder;
        this.baseEntityId = baseEntityId;
        this.onClickListener = onClickListener;
        this.rules = CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.HOME_VISIT);
    }

    @Override
    public Void doInBackground(Void... params) {
        /*if (baseEntityId != null) {
//            commonPersonObject = commonRepository.findByBaseEntityId(Utils.getValue(baseEntityId.getColumnmaps(), BASE_ENTITY_ID, false));

            Map<String, VisitSummary> map = VisitDao.getVisitSummary(Utils.getValue(baseEntityId.getColumnmaps(), BASE_ENTITY_ID, false));
            if (map != null) {
                VisitSummary notDoneSummary = map.get(CoreConstants.EventType.CHILD_VISIT_NOT_DONE);
                VisitSummary lastVisitSummary = map.get(CoreConstants.EventType.CHILD_HOME_VISIT);

                long lastVisit = 0;
                long visitNot = 0;
                long dateCreated = 0;
                try {
                    String createVal = Utils.getValue(baseEntityId.getColumnmaps(), ChwDBConstants.DATE_CREATED, false);
                    if (StringUtils.isNotBlank(createVal))
                        dateCreated = ISO8601DATEFORMAT.parse(createVal).getTime();

                } catch (Exception e) {
                    Timber.e(e);
                }
                if (lastVisitSummary != null)
                    lastVisit = lastVisitSummary.getVisitDate().getTime();

                if (notDoneSummary != null)
                    visitNot = notDoneSummary.getVisitDate().getTime();

                try {
                    String dobString = getDuration(Utils.getValue(baseEntityId.getColumnmaps(), DOB, false));
                    childVisit = CoreChildUtils.getChildVisitStatus(context, rules, dobString, lastVisit, visitNot, dateCreated);
                } catch (Exception e) {
                    childVisit = null;
                    e.printStackTrace();
                }
            }
            return null;
        }*/
        return null;
    }

    @Override
    protected void onPostExecute(Void param) {
        viewHolder.dueButtonLayout.setVisibility(View.VISIBLE);
        viewHolder.dueButton.setVisibility(View.VISIBLE);
        String received_death_certificate;
        try {
            received_death_certificate = Utils.getValue(baseEntityId.getColumnmaps(), RECEIVED_DEATH_CERTIFICATE, false);
            if (received_death_certificate.trim().equalsIgnoreCase(YES)) {
                setReceivedButtonColor(context, viewHolder.dueButton);
            } else if (received_death_certificate.trim().equalsIgnoreCase(NO)) {
                setNotReceivedButtonColor(context, viewHolder.dueButton);
            } else {
                setUpdateStatusButtonColor(context, viewHolder.dueButton);
            }
        } catch (Exception e) {
            viewHolder.dueButton.setText(context.getResources().getString(R.string.update_status));
            e.printStackTrace();
        }
        viewHolder.dueButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, DeadClientsUpdateActivity.class);
            intent.putExtra(Constants.ACTIVITY_PAYLOAD.ACTION, Constants.ACTION.START_REGISTRATION);
            intent.putExtra(BASE_ENTITY_ID, Utils.getValue(baseEntityId.getColumnmaps(), BASE_ENTITY_ID, false));
            intent.putExtra(CLIENT_TYPE, Utils.getValue(baseEntityId.getColumnmaps(), CLIENT_TYPE, false));
            intent.putExtra(DOB, Utils.getValue(baseEntityId.getColumnmaps(), DOB, false));
            intent.putExtra(RECEIVED_DEATH_CERTIFICATE, Utils.getValue(baseEntityId.getColumnmaps(), RECEIVED_DEATH_CERTIFICATE, false));
            intent.putExtra(DEATH_CERTIFICATE_ISSUE_DATE, Utils.getValue(baseEntityId.getColumnmaps(), DEATH_CERTIFICATE_ISSUE_DATE, false));
            intent.putExtra(DEATH_CERTIFICATE_NUMBER, Utils.getValue(baseEntityId.getColumnmaps(), DEATH_CERTIFICATE_NUMBER, false));
            intent.putExtra(DEATH_NOTIFICATION_DONE, Utils.getValue(baseEntityId.getColumnmaps(), DEATH_NOTIFICATION_DONE, false));
            intent.putExtra(INFORMANT_NAME, Utils.getValue(baseEntityId.getColumnmaps(), INFORMANT_NAME, false));
            intent.putExtra(INFORMANT_RELATIONSHIP, Utils.getValue(baseEntityId.getColumnmaps(), INFORMANT_RELATIONSHIP, false));
            intent.putExtra(INFORMANT_ADDRESS, Utils.getValue(baseEntityId.getColumnmaps(), INFORMANT_ADDRESS, false));
            intent.putExtra(INFORMANT_PHONE, Utils.getValue(baseEntityId.getColumnmaps(), INFORMANT_PHONE, false));
            intent.putExtra(OFFICIAL_NAME, Utils.getValue(baseEntityId.getColumnmaps(), OFFICIAL_NAME, false));
            intent.putExtra(OFFICIAL_ID, Utils.getValue(baseEntityId.getColumnmaps(), OFFICIAL_ID, false));
            intent.putExtra(OFFICIAL_POSITION, Utils.getValue(baseEntityId.getColumnmaps(), OFFICIAL_POSITION, false));
            intent.putExtra(OFFICIAL_ADDRESS, Utils.getValue(baseEntityId.getColumnmaps(), OFFICIAL_ADDRESS, false));
            intent.putExtra(OFFICIAL_NUMBER, Utils.getValue(baseEntityId.getColumnmaps(), OFFICIAL_NUMBER, false));
            context.startActivity(intent);
        });

    }

    public void setNotReceivedButtonColor(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.black));
        dueButton.setText(context.getString(R.string.certificate_not_received));
        dueButton.setBackgroundResource(0);
    }

    public void setReceivedButtonColor(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.certificate_received));
        dueButton.setText(context.getString(R.string.death_certificate_received));
        dueButton.setBackgroundResource(0);
    }

    public void setUpdateStatusButtonColor(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.pie_chart_yellow));
        dueButton.setText(context.getString(R.string.update_status));
        dueButton.setBackgroundResource(R.drawable.update_cert_status_btn);
    }
}
