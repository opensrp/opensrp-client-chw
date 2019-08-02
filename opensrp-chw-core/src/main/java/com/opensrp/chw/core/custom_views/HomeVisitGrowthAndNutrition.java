package com.opensrp.chw.core.custom_views;

import android.app.FragmentManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.opensrp.chw.core.R;
import com.opensrp.chw.core.contract.HomeVisitGrowthNutritionContract;
import com.opensrp.chw.core.fragment.CoreChildHomeVisitFragment;
import com.opensrp.chw.core.fragment.GrowthNutritionInputFragment;
import com.opensrp.chw.core.presenter.CoreHomeVisitGrowthNutritionPresenter;
import com.opensrp.chw.core.utils.CoreChildUtils;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.immunization.domain.ServiceWrapper;

import java.text.MessageFormat;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;

public class HomeVisitGrowthAndNutrition extends LinearLayout implements View.OnClickListener, HomeVisitGrowthNutritionContract.View {
    public static final String TAG = "HomeVisitGrowthAndNutrition";
    private LinearLayout layoutExclusiveBar, layoutMnpBar, layoutVitaminBar, layoutDewormingBar;
    private TextView textViewExclusiveFeedingName, textViewMnpName, textViewVitaminName, textViewDewormingName;
    private TextView textViewExclusiveFeedingTitle, textViewMnpTitle, textViewVitaminTitle, textViewDewormingTitle;
    private CircleImageView imageViewExclusiveStatus, imageViewMnpStatus, imageViewVitaminStatus, imageViewDewormingStatus;
    private HomeVisitGrowthNutritionContract.Presenter presenter;
    private CommonPersonObjectClient commonPersonObjectClient;
    private FragmentManager fragmentManager;
    private CoreChildHomeVisitFragment childHomeVisitFragment;
    private String feedingText;
    private boolean isEditMode;
    private Context context;

    public HomeVisitGrowthAndNutrition(Context context) {
        super(context);
        this.context = context;
        initUi();
    }

    public HomeVisitGrowthAndNutrition(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initUi();
    }

    public HomeVisitGrowthAndNutrition(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initUi();
    }

    private void initUi() {
        inflate(getContext(), R.layout.view_growth_nutrition, this);
        layoutExclusiveBar = findViewById(R.id.exclusive_feeding_bar);
        layoutMnpBar = findViewById(R.id.mnp_bar);
        layoutVitaminBar = findViewById(R.id.vitamin_a_bar);
        layoutDewormingBar = findViewById(R.id.deworming_bar);
        textViewExclusiveFeedingTitle = findViewById(R.id.textview_exclusive_feeding);
        textViewMnpTitle = findViewById(R.id.textview_mnp_bar);
        textViewVitaminTitle = findViewById(R.id.textview_vitamin_a);
        textViewDewormingTitle = findViewById(R.id.textview_deworming);
        textViewExclusiveFeedingName = findViewById(R.id.textview_exclusive_feeding_name);
        textViewMnpName = findViewById(R.id.textview_mnp_bar_name);
        textViewVitaminName = findViewById(R.id.textview_vitamin_a_name);
        textViewDewormingName = findViewById(R.id.textview_deworming_name);
        imageViewExclusiveStatus = findViewById(R.id.exclusive_feeding_status_circle);
        imageViewMnpStatus = findViewById(R.id.mnp_bar_status_circle);
        imageViewVitaminStatus = findViewById(R.id.vitamin_a_status_circle);
        imageViewDewormingStatus = findViewById(R.id.deworming_status_circle);
        layoutExclusiveBar.setOnClickListener(this);
        layoutMnpBar.setOnClickListener(this);
        layoutVitaminBar.setOnClickListener(this);
        layoutDewormingBar.setOnClickListener(this);
//        imageViewExclusiveStatus.setOnClickListener(this);
//        imageViewVitaminStatus.setOnClickListener(this);
//        imageViewDewormingStatus.setOnClickListener(this);
        initializePresenter();
    }

    public void setData(CoreChildHomeVisitFragment childHomeVisitFragment, FragmentManager fragmentManager, CommonPersonObjectClient commonPersonObjectClient, boolean isEditMode) {
        this.childHomeVisitFragment = childHomeVisitFragment;
        this.fragmentManager = fragmentManager;
        this.commonPersonObjectClient = commonPersonObjectClient;
        this.isEditMode = isEditMode;
        presenter.parseRecordServiceData(commonPersonObjectClient, isEditMode);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();//            case R.id.exclusive_feeding_status_circle:
//                ServiceWrapper ExServiceWrapper=((HomeVisitGrowthNutritionPresenter)presenter).getServiceWrapperExclusive();
//                notVisitSetState(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue(),ExServiceWrapper);
//                break;
//            case R.id.vitamin_a_status_circle:
//                ServiceWrapper vitaminServiceWrapper=((HomeVisitGrowthNutritionPresenter)presenter).getServiceWrapperVitamin();
//                notVisitSetState(GrowthNutritionInputFragment.GROWTH_TYPE.VITAMIN.getValue(),vitaminServiceWrapper);
//                break;
//            case R.id.deworming_status_circle:
//                ServiceWrapper deServiceWrapper=((HomeVisitGrowthNutritionPresenter)presenter).getServiceWrapperDeworming();
//                notVisitSetState(GrowthNutritionInputFragment.GROWTH_TYPE.DEWORMING.getValue(),deServiceWrapper);
//                break;
        if (i == R.id.exclusive_feeding_bar) {//if (!presenter.isSelected(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue())) {
            ServiceWrapper serviceWrapper = ((CoreHomeVisitGrowthNutritionPresenter) presenter).getServiceWrapperExclusive();
            showGrowthNutritionDialog(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue(), getContext().getString(R.string.visit_exclusive_breastfeeding),
                    textViewExclusiveFeedingTitle.getText().toString(), serviceWrapper);
            // }
        } else if (i == R.id.mnp_bar) {//if (!presenter.isSelected(GrowthNutritionInputFragment.GROWTH_TYPE.MNP.getValue())) {

            ServiceWrapper serviceWrapperMnp = ((CoreHomeVisitGrowthNutritionPresenter) presenter).getServiceWrapperMnp();
            showGrowthNutritionDialog(GrowthNutritionInputFragment.GROWTH_TYPE.MNP.getValue(), getContext().getString(R.string.visit_mnp_dose),
                    textViewMnpTitle.getText().toString(), serviceWrapperMnp);
            //}
        } else if (i == R.id.vitamin_a_bar) {//if (!presenter.isSelected(GrowthNutritionInputFragment.GROWTH_TYPE.VITAMIN.getValue())) {
            ServiceWrapper serviceWrapperVit = ((CoreHomeVisitGrowthNutritionPresenter) presenter).getServiceWrapperVitamin();
            showGrowthNutritionDialog(GrowthNutritionInputFragment.GROWTH_TYPE.VITAMIN.getValue(), getContext().getString(R.string.visit_vitamin_a_dose),
                    textViewVitaminTitle.getText().toString(), serviceWrapperVit);
            //}
        } else if (i == R.id.deworming_bar) {//if (!presenter.isSelected(GrowthNutritionInputFragment.GROWTH_TYPE.DEWORMING.getValue())) {
            ServiceWrapper serviceWrapperDorm = ((CoreHomeVisitGrowthNutritionPresenter) presenter).getServiceWrapperDeworming();
            showGrowthNutritionDialog(GrowthNutritionInputFragment.GROWTH_TYPE.DEWORMING.getValue(), getContext().getString(R.string.visit_deworming_dose),
                    textViewDewormingTitle.getText().toString(), serviceWrapperDorm);
            // }
        }
    }

    private void showGrowthNutritionDialog(String type, String title, String question, ServiceWrapper serviceWrapper) {
        GrowthNutritionInputFragment growthNutritionInputFragment = GrowthNutritionInputFragment.getInstance(title, question, type, commonPersonObjectClient);
        growthNutritionInputFragment.setContext(HomeVisitGrowthAndNutrition.this);
        growthNutritionInputFragment.setServiceWrapper(serviceWrapper);
        growthNutritionInputFragment.show(fragmentManager, TAG);
    }

    @Override
    public HomeVisitGrowthNutritionContract.Presenter initializePresenter() {
        presenter = new CoreHomeVisitGrowthNutritionPresenter(this);
        return presenter;
    }

    @Override
    public void updateExclusiveFeedingData(String name, String dueDate) {
        if (!TextUtils.isEmpty(name)) {
            layoutExclusiveBar.setVisibility(VISIBLE);
            findViewById(R.id.view_exclusive_feeding_bar).setVisibility(VISIBLE);
            Object[] displayName = CoreChildUtils.getStringWithNumber(name);
            String str = context.getString(R.string.exclusive_breastfeeding);
            String no = (String) displayName[1];
            feedingText = MessageFormat.format("{0} {1} {2}", str, no, getContext().getString(R.string.visit_months));
            textViewExclusiveFeedingTitle.setText(feedingText);
            String status = CoreChildUtils.getServiceDueStatus(dueDate);
            textViewExclusiveFeedingName.setText(CoreChildUtils.dueOverdueCalculation(getContext(), status, dueDate));
        }

    }

    @Override
    public void updateMnpData(String name, String dueDate) {
        if (!TextUtils.isEmpty(name)) {
            layoutMnpBar.setVisibility(VISIBLE);
            findViewById(R.id.view_mnp_bar).setVisibility(VISIBLE);
            Object[] displayName = CoreChildUtils.getStringWithNumber(name);
            String str = (String) displayName[0];
            String no = (String) displayName[1];
            String mnpText = MessageFormat.format("{0} {1} {2}", str, CoreChildUtils.getFirstSecondAsNumber(no), getContext().getString(R.string.visit_pack));
            textViewMnpTitle.setText(mnpText);
            String status = CoreChildUtils.getServiceDueStatus(dueDate);
            textViewMnpName.setText(CoreChildUtils.dueOverdueCalculation(getContext(), status, dueDate));
        }
    }

    @Override
    public void updateVitaminAData(String name, String dueDate) {
        if (!TextUtils.isEmpty(name)) {
            layoutVitaminBar.setVisibility(VISIBLE);
            findViewById(R.id.view_vitamin_a_bar).setVisibility(VISIBLE);
            Object[] displayName = CoreChildUtils.getStringWithNumber(name);
            String str = context.getString(R.string.vitamin_a);
            String no = (String) displayName[1];
            String vitaminText = MessageFormat.format("{0} {1} {2}", str, CoreChildUtils.getFirstSecondAsNumber(no), getContext().getString(R.string.visit_dose));
            textViewVitaminTitle.setText(vitaminText);
            String status = CoreChildUtils.getServiceDueStatus(dueDate);
            textViewVitaminName.setText(CoreChildUtils.dueOverdueCalculation(getContext(), status, dueDate));
        }
    }

    @Override
    public void updateDewormingData(String name, String dueDate) {
        if (!TextUtils.isEmpty(name)) {
            layoutDewormingBar.setVisibility(VISIBLE);
            findViewById(R.id.view_deworming_bar).setVisibility(VISIBLE);
            Object[] displayName = CoreChildUtils.getStringWithNumber(name);
            String str = context.getString(R.string.deworming);
            String no = (String) displayName[1];
            String dewormingText = MessageFormat.format("{0} {1} {2}", str, CoreChildUtils.getFirstSecondAsNumber(no), getContext().getString(R.string.visit_dose));
            textViewDewormingTitle.setText(dewormingText);
            String status = CoreChildUtils.getServiceDueStatus(dueDate);
            textViewDewormingName.setText(CoreChildUtils.dueOverdueCalculation(getContext(), status, dueDate));
        }
    }

    @Override
    public void statusImageViewUpdate(String type, boolean value, String message, String yesNoValue) {
        if (type.equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue())) {
            updateStatusTick(imageViewExclusiveStatus, (value && yesNoValue.toLowerCase().equalsIgnoreCase("yes")));
            textViewExclusiveFeedingTitle.setText(feedingText);
            String translated = "";
            if (yesNoValue.equalsIgnoreCase("Yes")) {
                translated = context.getString(R.string.yes);
            } else if (yesNoValue.equalsIgnoreCase("No")) {
                translated = context.getString(R.string.no);
            }
            textViewExclusiveFeedingName.setText(StringUtils.capitalize(translated));
        } else if (type.equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.MNP.getValue())) {
            updateStatusTick(imageViewMnpStatus, value);
            textViewMnpName.setText(message);
        } else if (type.equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.VITAMIN.getValue())) {
            updateStatusTick(imageViewVitaminStatus, value);
            textViewVitaminName.setText(message);
        } else if (type.equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.DEWORMING.getValue())) {
            updateStatusTick(imageViewDewormingStatus, value);
            textViewDewormingName.setText(message);
        }
    }


    private void updateStatusTick(CircleImageView imageView, boolean isCheck) {

        imageView.setImageResource(R.drawable.ic_checked);
        imageView.setColorFilter(getResources().getColor(R.color.white));
        imageView.setCircleBackgroundColor(getResources().getColor(
                ((isCheck) ? R.color.alert_complete_green : R.color.pnc_circle_yellow))
        );
        imageView.setBorderColor(getResources().getColor(
                ((isCheck) ? R.color.alert_complete_green : R.color.pnc_circle_yellow))
        );

        if (childHomeVisitFragment != null) {
            childHomeVisitFragment.checkIfSubmitIsToBeEnabled();
        }

    }

    @Override
    public void allDataLoaded() {
        if (childHomeVisitFragment != null) {
            childHomeVisitFragment.allServicesDataLoaded = true;
            if (isEditMode) childHomeVisitFragment.forcfullyProgressBarInvisible();
            else childHomeVisitFragment.progressBarInvisible();
        }
    }

    @Override
    public Context getViewContext() {
        return getContext();
    }

    public void setState(String type, ServiceWrapper serviceWrapper) {
        presenter.setSaveState(type, serviceWrapper);

    }

    public void notVisitSetState(String type, ServiceWrapper serviceWrapper) {
        presenter.setNotVisitState(type, serviceWrapper);
    }

    public Observable undoGrowthData() {
        return ((CoreHomeVisitGrowthNutritionPresenter) presenter).undoGrowthData();
    }

    public boolean isAllSelected() {
        return ((CoreHomeVisitGrowthNutritionPresenter) presenter).isAllSelected();
    }

    public Map<String, ServiceWrapper> returnSaveStateMap() {
        return presenter.getSaveStateMap();
    }

    public Map<String, ServiceWrapper> returnNotSaveStateMap() {
        return presenter.getNotSaveStateMap();
    }

}