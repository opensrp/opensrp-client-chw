package org.smartregister.brac.hnpp.provider;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jeasy.rules.api.Rules;
import org.mvel2.sh.text.TextUtil;
import org.smartregister.brac.hnpp.HnppApplication;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.chw.core.model.ChildVisit;
import org.smartregister.chw.core.provider.CoreRegisterProvider;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.family.fragment.BaseFamilyRegisterFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mahmud on 1/9/2019.
 */

public class HHRegisterProvider extends CoreRegisterProvider  {

    private final LayoutInflater inflater;
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;

    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationClickListener;

    private Context context;
    private CommonRepository commonRepository;
    private ImageRenderHelper imageRenderHelper;

    public HHRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.visibleColumns = visibleColumns;

        this.onClickListener = onClickListener;
        this.paginationClickListener = paginationClickListener;
        this.imageRenderHelper = new ImageRenderHelper(context);
        this.context = context;
        this.commonRepository = commonRepository;
    }

    @Override
    public void updateDueColumn(Context context, RegisterViewHolder viewHolder, ChildVisit childVisit) {

    }

    @Override
    public List<ChildVisit> retrieveChildVisitList(Rules rules, List<Map<String, String>> list) {
        return null;
    }

    @Override
    public ChildVisit mergeChildVisits(List<ChildVisit> childVisitList) {
        return null;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder1) {
        HouseHoldRegisterProvider viewHolder = (HouseHoldRegisterProvider)viewHolder1;
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (visibleColumns.isEmpty()) {

            String familyHeadId = pc.getColumnmaps().get(DBConstants.KEY.FAMILY_HEAD);

            final CommonPersonObject familyHeadObject =  Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName).findByBaseEntityId(familyHeadId);

            String familyHeadName = "";
            if (familyHeadObject != null && familyHeadObject.getColumnmaps() != null)
                familyHeadName = familyHeadObject.getColumnmaps().get(DBConstants.KEY.FIRST_NAME);

            pc.getColumnmaps().put(Constants.KEY.FAMILY_HEAD_NAME, familyHeadName);
            if(!TextUtils.isEmpty(familyHeadId)){
                this.imageRenderHelper.refreshProfileImage(pc.getCaseId(), viewHolder.profileImage, Utils.getMemberProfileImageResourceIDentifier(CoreConstants.TABLE_NAME.FAMILY_MEMBER));

            }
            populatePatientColumn(pc, client, viewHolder);
            populateMemberIconView(pc, viewHolder);

            return;
        }
    }

    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder, int currentPageCount, int totalPageCount, boolean hasNext, boolean hasPrevious) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
        footerViewHolder.pageInfoView.setText(
                MessageFormat.format(context.getString(org.smartregister.R.string.str_page_info), currentPageCount,
                        totalPageCount));

        footerViewHolder.nextPageView.setVisibility(hasNext ? View.VISIBLE : View.INVISIBLE);
        footerViewHolder.previousPageView.setVisibility(hasPrevious ? View.VISIBLE : View.INVISIBLE);

        footerViewHolder.nextPageView.setOnClickListener(paginationClickListener);
        footerViewHolder.previousPageView.setOnClickListener(paginationClickListener);
    }

    private void populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient client, final HouseHoldRegisterProvider viewHolder) {

        String firstName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
        setText(viewHolder.houseHoldName, context.getString(R.string.name,firstName));
        String houseHoldId = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, true);
        if(!TextUtils.isEmpty(houseHoldId)){
            houseHoldId = houseHoldId.replace(Constants.IDENTIFIER.FAMILY_SUFFIX,"")
                    .replace(HnppConstants.IDENTIFIER.FAMILY_TEXT,"");
        }
        setText(viewHolder.houseHoldId,context.getString(R.string.house_hold_id,houseHoldId));

        String phoneNumber = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.PHONE_NUMBER, true);
        if(!TextUtils.isEmpty(phoneNumber) && phoneNumber.length() > 1){
            setText(viewHolder.mobileNumber,phoneNumber);
        }else{
            setText(viewHolder.mobileNumber,context.getString(R.string.phone_no,phoneNumber));
        }

        String totalMember = Utils.getValue(pc.getColumnmaps(), HnppConstants.KEY.TOTAL_MEMBER, true);
        setText(viewHolder.totalMember,context.getString(R.string.member_count,TextUtils.isEmpty(totalMember)?"0":totalMember));

        View patient = viewHolder.patientColumn;
        attachPatientOnclickListener(patient, client);

        View addMemberBtn = viewHolder.addMemberBtn;
        attachDosageOnclickListener(addMemberBtn, client);

        viewHolder.registerColumns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.patientColumn.performClick();
            }
        });
    }
    private void setText(TextView textView,String value){
        if(textView == null) return;
        if(!TextUtils.isEmpty(value)){
            textView.setVisibility(View.VISIBLE);
            textView.setText(value);
        }else{
            textView.setVisibility(View.GONE);
        }
    }

    private void populateMemberIconView(CommonPersonObjectClient pc, HouseHoldRegisterProvider viewHolder) {
        String familyBaseEntityId = pc.getCaseId();
        if (updateAsyncTask == null) { //Ensure this task is only called once
            Utils.startAsyncTask(new UpdateAsyncTask(context, viewHolder, familyBaseEntityId), null);
        }
    }
    protected void updateChildIcons(HouseHoldRegisterProvider viewHolder, List<Map<String, String>> list, int ancWomanCount) {
        if (ancWomanCount > 0) {
            viewHolder.memberIcon.setVisibility(View.VISIBLE);
            View view = LayoutInflater.from(context).inflate(R.layout.member_with_count, null);
            ImageView ancImage = view.findViewById(R.id.member_image);
            TextView textViewCount = view.findViewById(R.id.count_tv);
            ancImage.setImageResource(org.smartregister.chw.core.R.mipmap.ic_anc_pink);
            textViewCount.setText(ancWomanCount+"");
            viewHolder.memberIcon.addView(view);

        }
        int maleChildCount = 0,femaleChildCount = 0;
        if (list != null && !list.isEmpty()) {
            viewHolder.memberIcon.setVisibility(View.VISIBLE);
            for (Map<String, String> map : list) {
                String gender = map.get(DBConstants.KEY.GENDER);
                if ("Male".equalsIgnoreCase(gender)) {
                    maleChildCount++;
                } else {
                    femaleChildCount++;
                }
            }
            if(maleChildCount>0){
                View view = LayoutInflater.from(context).inflate(R.layout.member_with_count, null);
                ImageView ancImage = view.findViewById(R.id.member_image);
                TextView textViewCount = view.findViewById(R.id.count_tv);
                ancImage.setImageResource(org.smartregister.chw.core.R.mipmap.ic_boy_child);
                textViewCount.setText(maleChildCount+"");
                viewHolder.memberIcon.addView(view);
            }
            if(femaleChildCount>0){
                View view = LayoutInflater.from(context).inflate(R.layout.member_with_count, null);
                ImageView ancImage = view.findViewById(R.id.member_image);
                TextView textViewCount = view.findViewById(R.id.count_tv);
                ancImage.setImageResource(org.smartregister.chw.core.R.mipmap.ic_girl_child);
                textViewCount.setText(femaleChildCount+"");
                viewHolder.memberIcon.addView(view);
            }
        }

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

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return null;
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {//Implement Abstract Method
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    @Override
    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public HouseHoldRegisterProvider createViewHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.house_hold_list_row, parent, false);

        return new HouseHoldRegisterProvider(view);
    }

    @Override
    public RecyclerView.ViewHolder createFooterHolder(ViewGroup parent) {
        View view = inflater.inflate(org.smartregister.family.R.layout.smart_register_pagination, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public boolean isFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        return FooterViewHolder.class.isInstance(viewHolder);
    }


    public static void fillValue(TextView v, String value) {
        if (v != null)
            v.setText(value);

    }

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    public class HouseHoldRegisterProvider extends RegisterViewHolder {
        public TextView houseHoldName;
        public TextView houseHoldId;
        public TextView mobileNumber;
        public TextView totalMember;
        public TextView lastVisitDate;
        public CircleImageView addMemberBtn;
        public CircleImageView profileImage;
        public View patientColumn;
        public LinearLayout memberIcon;

        public View registerColumns;

        public HouseHoldRegisterProvider(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.hh_profile_img);

            houseHoldId = itemView.findViewById(R.id.house_hold_id);

            houseHoldName = itemView.findViewById(R.id.house_hold_name);

            mobileNumber = itemView.findViewById(R.id.house_hold_mobile_number);

            totalMember = itemView.findViewById(R.id.house_hold_member_count);

            lastVisitDate = itemView.findViewById(R.id.next_visit_date);

            addMemberBtn = itemView.findViewById(R.id.add_member_btn);

            patientColumn = itemView.findViewById(R.id.patient_column);

            memberIcon = itemView.findViewById(R.id.member_icon_layout);
            memberIcon.setVisibility(View.GONE);

            registerColumns = itemView.findViewById(org.smartregister.family.R.id.register_columns);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public TextView pageInfoView;
        public Button nextPageView;
        public Button previousPageView;

        public FooterViewHolder(View view) {
            super(view);

            nextPageView = view.findViewById(org.smartregister.R.id.btn_next_page);
            previousPageView = view.findViewById(org.smartregister.R.id.btn_previous_page);
            pageInfoView = view.findViewById(org.smartregister.R.id.txt_page_info);
        }
    }
    private class UpdateAsyncTask extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final HouseHoldRegisterProvider viewHolder;
        private final String familyBaseEntityId;

        private List<Map<String, String>> list;
        private int ancWomanCount;

        private UpdateAsyncTask(Context context, HouseHoldRegisterProvider viewHolder, String familyBaseEntityId) {
            this.context = context;
            this.viewHolder = viewHolder;
            this.familyBaseEntityId = familyBaseEntityId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            list = getChildren(familyBaseEntityId);
            ancWomanCount = HnppApplication.ancRegisterRepository().getAncWomenCount(familyBaseEntityId);

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            // Update child Icon
            updateChildIcons(viewHolder, list, ancWomanCount);
        }
    }

}
