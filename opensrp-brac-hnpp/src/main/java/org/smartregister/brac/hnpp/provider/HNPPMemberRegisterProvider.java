package org.smartregister.brac.hnpp.provider;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.chw.core.provider.CoreMemberRegisterProvider;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.fragment.BaseFamilyRegisterFragment;
import org.smartregister.family.provider.FamilyMemberRegisterProvider;
import org.smartregister.family.util.DBConstants;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.customcontrols.FontVariant;

import java.util.Set;

public class HNPPMemberRegisterProvider extends CoreMemberRegisterProvider {
    private final LayoutInflater inflater;
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;

    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationClickListener;
    private String familyHead;
    private String primaryCaregiver;
    private Context context;
    private CommonRepository commonRepository;
    private ImageRenderHelper imageRenderHelper;

    public HNPPMemberRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener, String familyHead, String primaryCaregiver) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener, familyHead, primaryCaregiver);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.visibleColumns = visibleColumns;
        this.familyHead = familyHead;
        this.primaryCaregiver = primaryCaregiver;
        this.onClickListener = onClickListener;
        this.paginationClickListener = paginationClickListener;
        this.imageRenderHelper = new ImageRenderHelper(context);
        this.context = context;
        this.commonRepository = commonRepository;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {

        CommonPersonObjectClient pc = (CommonPersonObjectClient)client;
        if (this.visibleColumns.isEmpty()) {
            this.populatePatientColumn(pc, client, viewHolder);
            this.populateIdentifierColumn(pc, viewHolder);
        }
       // super.getView(cursor, client, viewHolder);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.profile.getLayoutParams();
        layoutParams.width = context.getResources().getDimensionPixelSize(org.smartregister.chw.core.R.dimen.member_profile_pic_width);
        layoutParams.height = context.getResources().getDimensionPixelSize(org.smartregister.chw.core.R.dimen.member_profile_pic_width);
        viewHolder.profile.setLayoutParams(layoutParams);
        viewHolder.patientNameAge.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(org.smartregister.chw.core.R.dimen.member_profile_list_title_size));

        viewHolder.statusLayout.setVisibility(View.GONE);
        viewHolder.status.setVisibility(View.GONE);

       // String entityType = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.ENTITY_TYPE, false);
//        if (CoreConstants.TABLE_NAME.CHILD.equals(entityType)) {
////            Utils.startAsyncTask(new UpdateAsyncTask(viewHolder, pc), null);
//        }
    }
    private void populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient client, final FamilyMemberRegisterProvider.RegisterViewHolder viewHolder) {
        String firstName = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
        String middleName = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true);
        String lastName = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
        String patientName = org.smartregister.family.util.Utils.getName(firstName, middleName, lastName);
        String entityType = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.ENTITY_TYPE, false);
        String relation_with_household_head = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), HnppConstants.KEY.RELATION_WITH_HOUSEHOLD, false);
        String dob = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOB, false);
        String guId = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(),  HnppConstants.KEY.GU_ID, false);
        String dobString = org.smartregister.family.util.Utils.getDuration(dob);
        dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;
        String dod = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOD, false);
        if (StringUtils.isNotBlank(dod)) {
            dobString = org.smartregister.family.util.Utils.getDuration(dod, dob);
            dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;
            patientName = patientName + "\n" + org.smartregister.family.util.Utils.getTranslatedDate(dobString, this.context) + " " + this.context.getString(org.smartregister.family.R.string.deceased_brackets);
            viewHolder.patientNameAge.setFontVariant(FontVariant.REGULAR);
            viewHolder.patientNameAge.setTextColor(-7829368);
            viewHolder.patientNameAge.setTypeface(viewHolder.patientNameAge.getTypeface(), Typeface.ITALIC);
            viewHolder.profile.setImageResource(org.smartregister.family.util.Utils.getMemberProfileImageResourceIDentifier(entityType));
            viewHolder.nextArrow.setVisibility(View.GONE);
        } else {
//            patientName = patientName + "\n" + org.smartregister.family.util.Utils.getTranslatedDate(dobString, this.context);

            viewHolder.patientNameAge.setFontVariant(FontVariant.REGULAR);
            viewHolder.patientNameAge.setTextColor(-16777216);
            viewHolder.patientNameAge.setTypeface(viewHolder.patientNameAge.getTypeface(), View.VISIBLE);
            this.imageRenderHelper.refreshProfileImage(pc.getCaseId(), viewHolder.profile, org.smartregister.family.util.Utils.getMemberProfileImageResourceIDentifier(entityType));
            viewHolder.nextArrow.setVisibility(View.VISIBLE);
        }
        ((TextView)viewHolder.patientNameAge).setSingleLine(true);
        ((TextView)viewHolder.gender).setSingleLine(false);
        fillValue(viewHolder.patientNameAge, patientName);
//        String gender_key = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), "gender", true);
//        String gender = "";
//        if (gender_key.equalsIgnoreCase("Male")) {
//            gender = this.context.getString(org.smartregister.family.R.string.male);
//        } else if (gender_key.equalsIgnoreCase("Female")) {
//            gender = this.context.getString(org.smartregister.family.R.string.female);
//        }
        String relationAge = context.getString(R.string.relation_with_member_and_head,relation_with_household_head) + "<br>বয়সঃ " + org.smartregister.family.util.Utils.getTranslatedDate(dobString, this.context);

        if(!TextUtils.isEmpty(guId))relationAge = relationAge.concat("<br>"+this.context.getString(R.string.finger_print_added));
        viewHolder.gender.setText(Html.fromHtml(relationAge));


        viewHolder.nextArrowColumn.setOnClickListener(new View.OnClickListener() {
            public void onClick(android.view.View v) {
                viewHolder.nextArrow.performClick();
            }
        });
        viewHolder.profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(android.view.View v) {
                viewHolder.patientColumn.performClick();
            }
        });
        viewHolder.registerColumns.setOnClickListener(new View.OnClickListener() {
            public void onClick(android.view.View v) {
                viewHolder.patientColumn.performClick();
            }
        });
        if (StringUtils.isBlank(dod)) {
            android.view.View patient = viewHolder.patientColumn;
           attachPatientOnclickListener(patient, client);
            android.view.View nextArrow = viewHolder.nextArrow;
            attachNextArrowOnclickListener(nextArrow, client);
        }

    }
    private void attachNextArrowOnclickListener(android.view.View view, SmartRegisterClient client) {
        view.setOnClickListener(this.onClickListener);
        view.setTag(client);
        view.setTag(org.smartregister.family.R.id.VIEW_ID, "click_next_arrow");
    }
    private void attachPatientOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(org.smartregister.family.R.id.VIEW_ID, BaseFamilyRegisterFragment.CLICK_VIEW_NORMAL);
    }

    private void attachDosageOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(org.smartregister.family.R.id.VIEW_ID, BaseFamilyRegisterFragment.CLICK_VIEW_DOSAGE_STATUS);
    }
    private void populateIdentifierColumn(CommonPersonObjectClient pc, FamilyMemberRegisterProvider.RegisterViewHolder viewHolder) {
        String uniqueId = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), "unique_id", false);
        String baseEntityId = pc.getCaseId();
        if (StringUtils.isNotBlank(baseEntityId)) {
            if (baseEntityId.equals(familyHead)) {
                viewHolder.familyHead.setVisibility(View.VISIBLE);
            } else {
                viewHolder.familyHead.setVisibility(View.GONE);
            }

            if (baseEntityId.equals(primaryCaregiver)) {
                viewHolder.primaryCaregiver.setVisibility(View.VISIBLE);
            } else {
                viewHolder.primaryCaregiver.setVisibility(View.GONE);
            }
        }

    }
}
