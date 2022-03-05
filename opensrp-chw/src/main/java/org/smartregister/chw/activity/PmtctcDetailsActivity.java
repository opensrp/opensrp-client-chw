package org.smartregister.chw.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.R;
import org.smartregister.chw.dao.PmtctDao;
import org.smartregister.chw.domain.PmtctReferralMemberObject;
import org.smartregister.view.activity.SecuredActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PmtctcDetailsActivity extends SecuredActivity implements View.OnClickListener {
    private CustomFontTextView clientName;
    private CustomFontTextView careGiverName;
    private CustomFontTextView careGiverPhone;
    private CustomFontTextView comments;
    private CustomFontTextView referralDate;
    private CustomFontTextView lastFacilityVisitDate;
    private CustomFontTextView referralType;
    private CustomFontTextView locationName;
    private CustomFontTextView markAsDone;
    private String baseEntityId;
    private PmtctReferralMemberObject memberObject;
    public static final String PMTCT_MEMBER_OBJECT = "PMTCT_MEMBER_OBJECT";

    public static void startPmtctDetailsActivity(Activity activity, String baseEntityId) {
        Intent intent = new Intent(activity, PmtctcDetailsActivity.class);
        intent.putExtra(PMTCT_MEMBER_OBJECT, baseEntityId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_pmtctc_details);
        inflateToolbar();

        baseEntityId = getIntent().getStringExtra(PMTCT_MEMBER_OBJECT);
        memberObject = PmtctDao.getPmtctReferralMemberObject(baseEntityId);
        setUpViews();
    }

    @Override
    protected void onResumption() {

    }

    protected AppBarLayout appBarLayout;

    private void inflateToolbar() {
        Toolbar toolbar = findViewById(R.id.back_pmtct_toolbar);
        CustomFontTextView toolBarTextView = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);

        upArrow.setColorFilter(getResources().getColor(R.color.text_blue), PorterDuff.Mode.SRC_ATOP);
        supportActionBar.setHomeAsUpIndicator(upArrow);
        supportActionBar.setElevation(0f);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationOnClickListener(view -> finish());
        }

        toolBarTextView.setText(R.string.back_to_pmtct);
        appBarLayout = findViewById(R.id.app_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            appBarLayout.setOutlineProvider(null);
    }

    private void setUpViews() {
        clientName = findViewById(R.id.client_name);
        careGiverName = findViewById(R.id.care_giver_name);
        careGiverPhone = findViewById(R.id.care_giver_phone);
        comments = findViewById(R.id.comments);
        referralDate = findViewById(R.id.referral_date);
        lastFacilityVisitDate = findViewById(R.id.last_facility_visit_date);
        referralType = findViewById(R.id.referral_type);
        locationName = findViewById(R.id.location_name);
        markAsDone = findViewById(R.id.mark_ask_done);
        markAsDone.setOnClickListener(this);
        obtainReferralDetails();
    }


    @SuppressLint("SetTextI18n")
    private void obtainReferralDetails() {
        clientName.setText(memberObject.getFirstName() + " " + memberObject.getMiddleName() + " " + memberObject.getLastName() + ", " + memberObject.getAge());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Calendar referralDateCalendar = Calendar.getInstance();
        referralDateCalendar.setTimeInMillis(memberObject.getPmtctCommunityReferralDate().getTime());
        referralDate.setText(dateFormatter.format(referralDateCalendar.getTime()));

        referralDateCalendar.setTimeInMillis(memberObject.getLastFacilityVisitDate().getTime());
        lastFacilityVisitDate.setText(dateFormatter.format(referralDateCalendar.getTime()));
        locationName.setText(memberObject.getAddress());

        referralType.setText(memberObject.getReasonsForIssuingCommunityFollowupReferral());
        if (memberObject.getPrimaryCareGiver() != null)
            careGiverName.setText(String.format("CG : %s", memberObject.getPrimaryCareGiverName()));
        else
            careGiverName.setVisibility(View.GONE);


        if (StringUtils.isBlank(getFamilyMemberContacts()) && StringUtils.isEmpty(getFamilyMemberContacts())) {
            careGiverPhone.setText(getString(R.string.phone_not_provided));
        } else {
            careGiverPhone.setText(getFamilyMemberContacts());
        }

        if (StringUtils.isNotBlank(memberObject.getComments()) && StringUtils.isNotEmpty(memberObject.getComments())) {
            comments.setText(memberObject.getComments());
        }
    }


    private String getFamilyMemberContacts() {
        String phoneNumber = "";
        String familyPhoneNumber = memberObject.getPhoneNumber();
        String familyHeadPhoneNumber = memberObject.getFamilyHeadPhoneNumber();
        String primaryCareGiverPhoneNumber = memberObject.getFamilyHeadPhoneNumber();

        if (StringUtils.isNoneEmpty(familyPhoneNumber)) {
            phoneNumber = familyPhoneNumber;
        } else if (StringUtils.isEmpty(familyHeadPhoneNumber) && StringUtils.isNoneEmpty()) {
            phoneNumber = familyHeadPhoneNumber;
        } else if (StringUtils.isEmpty(primaryCareGiverPhoneNumber) && StringUtils.isNoneEmpty()) {
            phoneNumber = primaryCareGiverPhoneNumber;
        }
        return phoneNumber;
    }

    @Override
    public void onClick(View view) {

    }
}