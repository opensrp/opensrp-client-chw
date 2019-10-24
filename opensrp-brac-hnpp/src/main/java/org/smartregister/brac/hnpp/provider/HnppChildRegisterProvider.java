package org.smartregister.brac.hnpp.provider;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import org.apache.commons.lang3.text.WordUtils;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.utils.HnppChildUtils;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.chw.core.holders.RegisterViewHolder;
import org.smartregister.chw.core.provider.CoreChildRegisterProvider;
import org.smartregister.chw.core.task.UpdateLastAsyncTask;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Set;

/**
 * Created by keyman on 13/11/2018.
 */

public class HnppChildRegisterProvider extends CoreChildRegisterProvider {

    private Set<org.smartregister.configurableviews.model.View> visibleColumns;
    private View.OnClickListener onClickListener;
    private Context context;
    private CommonRepository commonRepository;

    public HnppChildRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, visibleColumns, onClickListener, paginationClickListener);
        this.visibleColumns = visibleColumns;
        this.onClickListener = onClickListener;
        this.context = context;
        this.commonRepository = commonRepository;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (visibleColumns.isEmpty()) {
            populatePatientColumn(pc, client, viewHolder);
            populateLastColumn(pc, viewHolder);

            return;
        }
    }
    public void populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient client, RegisterViewHolder viewHolder) {

        String motherEntityId = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.MOTHER_ENTITY_ID, true);
        String relationId = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.RELATIONAL_ID, true);
        String motherName = Utils.getValue(pc.getColumnmaps(), HnppConstants.KEY.CHILD_MOTHER_NAME, true);

        motherName = HnppChildUtils.getMotherName(motherEntityId,relationId,motherName);
        String parentName = context.getResources().getString(org.smartregister.chw.core.R.string.care_giver_initials, motherName);
        String firstName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
        String middleName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true);
        String lastName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
        String childName = org.smartregister.util.Utils.getName(firstName, middleName + " " + lastName);
        String houseHoldHead = Utils.getValue(pc.getColumnmaps(), HnppConstants.KEY.HOUSE_HOLD_NAME, true);

        if(TextUtils.isEmpty(motherName)){
            viewHolder.textViewChildName.setText(context.getString(R.string.house_hold_head_name,houseHoldHead));

        }else{
            fillValue(viewHolder.textViewChildName, WordUtils.capitalize(parentName));
        }
        String dobString = Utils.getDuration(Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOB, false));
        //dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;
        fillValue(viewHolder.textViewParentName, WordUtils.capitalize(childName) + " " + context.getResources().getString(org.smartregister.chw.core.R.string.age, WordUtils.capitalize(Utils.getTranslatedDate(dobString, context))));
        viewHolder.profileImage.setVisibility(View.VISIBLE);
        viewHolder.profileImage.setImageResource(org.smartregister.family.R.mipmap.ic_child);
        viewHolder.textViewAddressGender.setTextColor(ContextCompat.getColor(context, android.R.color.black));

        setAddressAndGender(pc, viewHolder);

        addButtonClickListeners(client, viewHolder);

    }

    private void populateLastColumn(CommonPersonObjectClient pc, RegisterViewHolder viewHolder) {
        Utils.startAsyncTask(new UpdateLastAsyncTask(context, commonRepository, viewHolder, pc.entityId(), onClickListener), null);
    }

    @Override
    public void setAddressAndGender(CommonPersonObjectClient pc, RegisterViewHolder viewHolder) {
        String address = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.FAMILY_HOME_ADDRESS, true);
        String gender = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.GENDER, true);
        fillValue(viewHolder.textViewAddressGender, gender + " \u00B7 " + address);
    }

    @Override
    public void addButtonClickListeners(SmartRegisterClient client, RegisterViewHolder viewHolder) {
        viewHolder.dueButtonLayout.setVisibility(View.GONE);
        viewHolder.goToProfileLayout.setVisibility(View.GONE);

        View patient = viewHolder.childColumn;
        attachPatientOnclickListener(patient, client);

        View goToProfileImage = viewHolder.goToProfileImage;
        attachPatientOnclickListener(goToProfileImage, client);

        View goToProfileLayout = viewHolder.goToProfileLayout;
        attachPatientOnclickListener(goToProfileLayout, client);

    }

}
