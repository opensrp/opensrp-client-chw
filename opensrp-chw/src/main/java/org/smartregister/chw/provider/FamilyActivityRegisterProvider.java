package org.smartregister.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.R;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.customcontrols.FontVariant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import timber.log.Timber;

public class FamilyActivityRegisterProvider extends org.smartregister.family.provider.FamilyActivityRegisterProvider {
    public FamilyActivityRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (visibleColumns.isEmpty()) {
            populatePatientColumn(pc, client, viewHolder);
            populateIdentifierColumn(pc, viewHolder);

            return;
        }
    }

    protected void populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient client, final RegisterViewHolder viewHolder) {

        String firstName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
        String middleName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true);
        String lastName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
        String eventType = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.EVENT_TYPE, true);

        eventType = (eventType.equalsIgnoreCase(Constants.EventType.CHILD_HOME_VISIT) ? context.getString(R.string.interpunct) + " " + context.getString(R.string.home_visit) : "");

        long dateNotVisited = parseLong(Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DATE_VISIT_NOT_DONE, false));
        long dateVisited = parseLong(Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DATE_LAST_HOME_VISIT, false));

        if (dateNotVisited > 0) {
            viewHolder.status.setImageResource(Utils.getActivityProfileImageResourceNotVistedIDentifier());
            fillValue(viewHolder.lastVisit, String.format(context.getString(R.string.profile_activity_not_visited), new SimpleDateFormat("dd MMM yyyy").format(new Date(dateNotVisited))));
        }

        if (dateVisited > 0) {
            viewHolder.status.setImageResource(Utils.getActivityProfileImageResourceVistedIDentifier());
            fillValue(viewHolder.lastVisit, String.format(context.getString(R.string.profile_activity_completed), new SimpleDateFormat("dd MMM yyyy").format(new Date(dateVisited))));
        }

        String patientName = Utils.getName(firstName, middleName, lastName);

        String dob = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOB, false);
        String dobString = Utils.getDuration(dob);
        dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;

        String dod = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOD, false);
        if (StringUtils.isNotBlank(dod)) {

            dobString = Utils.getDuration(dod, dob);
            dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;

            patientName = patientName + ", " + dobString + " " + eventType + context.getString(org.smartregister.family.R.string.deceased_brackets);
            viewHolder.patientNameAge.setFontVariant(FontVariant.REGULAR);
            viewHolder.patientNameAge.setTextColor(Color.GRAY);
            viewHolder.patientNameAge.setTypeface(viewHolder.patientNameAge.getTypeface(), Typeface.ITALIC);
        } else {
            patientName = patientName + ", " + dobString + " " + eventType;
            viewHolder.patientNameAge.setFontVariant(FontVariant.REGULAR);
            viewHolder.patientNameAge.setTextColor(Color.BLACK);
            viewHolder.patientNameAge.setTypeface(viewHolder.patientNameAge.getTypeface(), Typeface.NORMAL);
        }

        fillValue(viewHolder.patientNameAge, patientName);

        // Update UI cutoffs
        viewHolder.patientNameAge.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.member_due_list_title_size));

        String gender = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.GENDER, true);
        fillValue(viewHolder.gender, gender);

        viewHolder.status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.patientColumn.performClick();
            }
        });

        View patient = viewHolder.patientColumn;
        attachPatientOnclickListener(patient, client);
    }

    private void populateIdentifierColumn(CommonPersonObjectClient pc, RegisterViewHolder viewHolder) {
        String uniqueId = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false);
        //fillValue(viewHolder.ancId, String.format(context.getString(R.string.unique_id_text), uniqueId));
    }

    private long parseLong(String string) {
        long res = 0l;
        try {
            res = Long.valueOf(string);
        } catch (Exception e) {
            Timber.e(e);
        }
        return res;
    }

    private void attachPatientOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(org.smartregister.family.R.id.VIEW_ID, BaseFamilyProfileMemberFragment.CLICK_VIEW_NORMAL);
    }

}
