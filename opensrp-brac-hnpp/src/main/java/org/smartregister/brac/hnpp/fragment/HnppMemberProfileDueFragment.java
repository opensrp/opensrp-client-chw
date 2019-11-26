package org.smartregister.brac.hnpp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.activity.FamilyProfileActivity;
import org.smartregister.brac.hnpp.activity.HnppFamilyOtherMemberProfileActivity;
import org.smartregister.brac.hnpp.model.MemberProfileDueModel;
import org.smartregister.brac.hnpp.presenter.HnppMemberProfileDuePresenter;
import org.smartregister.brac.hnpp.provider.MemberDueRegisterProvider;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.utils.WashCheck;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.adapter.FamilyRecyclerViewCustomAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileDueFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.Utils;

import java.util.HashMap;
import java.util.Set;

import timber.log.Timber;

public class HnppMemberProfileDueFragment extends BaseFamilyProfileDueFragment implements View.OnClickListener {
    private static final int TAG_OPEN_ANC1 = 101;
    private static final int TAG_OPEN_ANC2 = 102;
    private static final int TAG_OPEN_ANC3 = 103;

    private static final int TAG_OPEN_FAMILY = 111;
    private static final int TAG_OPEN_REFEREAL = 222;


    private int dueCount = 0;
    private View emptyView;
    private String familyName;
    private long dateFamilyCreated;
    private String familyBaseEntityId;
    private String baseEntityId;
    private LinearLayout otherServiceView;


    public static BaseFamilyProfileDueFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        BaseFamilyProfileDueFragment fragment = new HnppMemberProfileDueFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initializePresenter() {
        familyBaseEntityId = getArguments().getString(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        baseEntityId = getArguments().getString(Constants.INTENT_KEY.BASE_ENTITY_ID);
        familyName = getArguments().getString(Constants.INTENT_KEY.FAMILY_NAME);
        presenter = new HnppMemberProfileDuePresenter(this, new MemberProfileDueModel(), null, familyBaseEntityId);
        //TODO need to pass this value as this value using at homevisit rule
        dateFamilyCreated = getArguments().getLong("");

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        //TODO
        Timber.d("setAdvancedSearchFormData");
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        emptyView = view.findViewById(R.id.empty_view);
        otherServiceView = view.findViewById(R.id.other_option);
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        MemberDueRegisterProvider chwDueRegisterProvider = new MemberDueRegisterProvider(this.getActivity(), this.commonRepository(), visibleColumns, this.registerActionHandler, this.paginationViewHandler);
        this.clientAdapter = new FamilyRecyclerViewCustomAdapter(null, chwDueRegisterProvider, this.context().commonrepository(this.tablename), Utils.metadata().familyDueRegister.showPagination);
        this.clientAdapter.setCurrentlimit(Utils.metadata().familyDueRegister.currentLimit);
        this.clientsView.setAdapter(this.clientAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addStaticView();
            }
        },500);


    }

    @Override
    protected void onViewClicked(View view) {
        super.onViewClicked(view);
        switch (view.getId()) {
            case R.id.patient_column:
                if (view.getTag() != null && view.getTag(org.smartregister.family.R.id.VIEW_ID) == CLICK_VIEW_NORMAL) {
                    goToChildProfileActivity(view);
                }
                break;
            case R.id.next_arrow:
                if (view.getTag() != null && view.getTag(org.smartregister.family.R.id.VIEW_ID) == CLICK_VIEW_NEXT_ARROW) {
                    goToChildProfileActivity(view);
                }
                break;
            default:
                break;
        }
    }

    public void goToChildProfileActivity(View view) {
        if (view.getTag() instanceof CommonPersonObjectClient) {
            CommonPersonObjectClient patient = (CommonPersonObjectClient) view.getTag();
        }

    }
    private void addStaticView(){
        if(otherServiceView.getVisibility() == View.VISIBLE){
            return;
        }
        otherServiceView.setVisibility(View.VISIBLE);
        View anc1View = LayoutInflater.from(getContext()).inflate(R.layout.view_member_due,null);
        ImageView imageanc1View = anc1View.findViewById(R.id.image_view);
        TextView nameanc1View =  anc1View.findViewById(R.id.title_txt);
        imageanc1View.setImageResource(R.mipmap.ic_anc_pink);
        nameanc1View.setText("গর্ভবতী পরিচর্যা-১ম ত্রিমাসিক");
        anc1View.setTag(TAG_OPEN_ANC1);
        anc1View.setOnClickListener(this);
        otherServiceView.addView(anc1View);

        View familyView = LayoutInflater.from(getContext()).inflate(R.layout.view_member_due,null);
        ImageView image = familyView.findViewById(R.id.image_view);
        TextView name =  familyView.findViewById(R.id.title_txt);
        image.setImageResource(R.drawable.childrow_family);
        name.setText("ফেমেলির অন্যান্য সেবা");
        familyView.setTag(TAG_OPEN_FAMILY);
        familyView.setOnClickListener(this);
        otherServiceView.addView(familyView);

        View referelView = LayoutInflater.from(getContext()).inflate(R.layout.view_member_due,null);
        ImageView imageReferel = referelView.findViewById(R.id.image_view);
        TextView nameReferel =  referelView.findViewById(R.id.title_txt);
        imageReferel.setImageResource(R.mipmap.ic_refer);
        nameReferel.setText("রেফেরেল");
        referelView.setTag(TAG_OPEN_REFEREAL);
        referelView.setOnClickListener(this);
        otherServiceView.addView(referelView);

    }

    @Override
    public void countExecute() {
        super.countExecute();
    }

    public void onEmptyRegisterCount(final boolean has_no_records) {
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        Integer tag = (Integer) v.getTag();
        if (tag != null) {
            switch (tag) {
                case TAG_OPEN_FAMILY:
                    if (getActivity() != null && getActivity() instanceof HnppFamilyOtherMemberProfileActivity) {
                        HnppFamilyOtherMemberProfileActivity activity = (HnppFamilyOtherMemberProfileActivity) getActivity();
                        activity.openFamilyDueTab();
                    }
                    break;
                case TAG_OPEN_REFEREAL:
                    if (getActivity() != null && getActivity() instanceof HnppFamilyOtherMemberProfileActivity) {
                        HnppFamilyOtherMemberProfileActivity activity = (HnppFamilyOtherMemberProfileActivity) getActivity();
                        activity.openRefereal();
                    }
                    break;
                case TAG_OPEN_ANC1:
                    if (getActivity() != null && getActivity() instanceof HnppFamilyOtherMemberProfileActivity) {
                        HnppFamilyOtherMemberProfileActivity activity = (HnppFamilyOtherMemberProfileActivity) getActivity();
                        activity.startMalariaRegister();
                    }
                    break;
            }
        }
    }
}