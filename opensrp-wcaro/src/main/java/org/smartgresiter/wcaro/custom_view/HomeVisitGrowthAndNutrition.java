package org.smartgresiter.wcaro.custom_view;

import android.app.FragmentManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.contract.HomeVisitGrowthNutritionContract;
import org.smartgresiter.wcaro.fragment.ChildHomeVisitFragment;
import org.smartgresiter.wcaro.fragment.GrowthNutritionInputFragment;
import org.smartgresiter.wcaro.presenter.HomeVisitGrowthNutritionPresenter;
import org.smartgresiter.wcaro.util.ChildUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.immunization.domain.ServiceWrapper;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeVisitGrowthAndNutrition extends LinearLayout implements View.OnClickListener, HomeVisitGrowthNutritionContract.View {
    public static final String TAG = "HomeVisitGrowthAndNutrition";
    private LinearLayout layoutExclusiveBar, layoutMnpBar, layoutVitaminBar, layoutDewormingBar;
    private TextView textViewExclusiveFeedingName, textViewMnpName, textViewVitaminName, textViewDewormingName;
    private CircleImageView imageViewExclusiveStatus, imageViewMnpStatus, imageViewVitaminStatus, imageViewDewormingStatus;
    private HomeVisitGrowthNutritionContract.Presenter presenter;
    private CommonPersonObjectClient commonPersonObjectClient;
    private FragmentManager fragmentManager;
    private ChildHomeVisitFragment childHomeVisitFragment;

    public HomeVisitGrowthAndNutrition(Context context) {
        super(context);
        initUi();
    }

    public HomeVisitGrowthAndNutrition(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    public HomeVisitGrowthAndNutrition(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();
    }

    private void initUi() {
        inflate(getContext(), R.layout.view_growth_nutrition, this);
        layoutExclusiveBar = findViewById(R.id.exclusive_feeding_bar);
        layoutMnpBar = findViewById(R.id.mnp_bar);
        layoutVitaminBar = findViewById(R.id.vitamin_a_bar);
        layoutDewormingBar = findViewById(R.id.deworming_bar);
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

    public void setData(ChildHomeVisitFragment childHomeVisitFragment, FragmentManager fragmentManager, CommonPersonObjectClient commonPersonObjectClient) {
        this.childHomeVisitFragment = childHomeVisitFragment;
        this.fragmentManager = fragmentManager;
        this.commonPersonObjectClient = commonPersonObjectClient;
        presenter.parseRecordServiceData(commonPersonObjectClient);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.exclusive_feeding_status_circle:
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
            case R.id.exclusive_feeding_bar:
                if (!presenter.isSelected(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue())) {
                    ServiceWrapper serviceWrapper = ((HomeVisitGrowthNutritionPresenter) presenter).getServiceWrapperExclusive();
                    showGrowthNutritionDialog(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue(), "Exclusive breastfeeding",
                            textViewExclusiveFeedingName.getText().toString(), serviceWrapper);
                }

                break;
            case R.id.mnp_bar:
                if (!presenter.isSelected(GrowthNutritionInputFragment.GROWTH_TYPE.MNP.getValue())) {

                    ServiceWrapper serviceWrapperMnp = ((HomeVisitGrowthNutritionPresenter) presenter).getServiceWrapperMnp();
                    showGrowthNutritionDialog(GrowthNutritionInputFragment.GROWTH_TYPE.MNP.getValue(), "Record MNP dose",
                            textViewMnpName.getText().toString(), serviceWrapperMnp);
                }
                break;
            case R.id.vitamin_a_bar:
                if (!presenter.isSelected(GrowthNutritionInputFragment.GROWTH_TYPE.VITAMIN.getValue())) {
                    ServiceWrapper serviceWrapperVit = ((HomeVisitGrowthNutritionPresenter) presenter).getServiceWrapperVitamin();
                    showGrowthNutritionDialog(GrowthNutritionInputFragment.GROWTH_TYPE.VITAMIN.getValue(), "Record Vitamin A dose",
                            textViewVitaminName.getText().toString(), serviceWrapperVit);
                }
                break;
            case R.id.deworming_bar:
                if (!presenter.isSelected(GrowthNutritionInputFragment.GROWTH_TYPE.DEWORMING.getValue())) {
                    ServiceWrapper serviceWrapperDorm = ((HomeVisitGrowthNutritionPresenter) presenter).getServiceWrapperDeworming();
                    showGrowthNutritionDialog(GrowthNutritionInputFragment.GROWTH_TYPE.DEWORMING.getValue(), "Record Deworming dose",
                            textViewDewormingName.getText().toString(), serviceWrapperDorm);
                }
                break;
        }
    }

    private void showGrowthNutritionDialog(String type, String title, String question, ServiceWrapper serviceWrapper) {
        GrowthNutritionInputFragment growthNutritionInputFragment = GrowthNutritionInputFragment.getInstance(title, question, type, serviceWrapper, commonPersonObjectClient);
        growthNutritionInputFragment.setContext(HomeVisitGrowthAndNutrition.this);
        growthNutritionInputFragment.show(fragmentManager, TAG);
    }

    @Override
    public HomeVisitGrowthNutritionContract.Presenter initializePresenter() {
        presenter = new HomeVisitGrowthNutritionPresenter(this);
        return presenter;
    }

    @Override
    public void updateExclusiveFeedingData(String name) {
        if (!TextUtils.isEmpty(name)) {
            layoutExclusiveBar.setVisibility(VISIBLE);
            ((View) findViewById(R.id.view_exclusive_feeding_bar)).setVisibility(VISIBLE);
            Object[] displayName = ChildUtils.getStringWithNumber(name);
            String str = (String) displayName[0];
            String no = (String) displayName[1];

            textViewExclusiveFeedingName.setText(str + " " + no + " month");
        }

    }

    @Override
    public void updateMnpData(String name) {
        if (!TextUtils.isEmpty(name)) {
            layoutMnpBar.setVisibility(VISIBLE);
            ((View) findViewById(R.id.view_mnp_bar)).setVisibility(VISIBLE);
            Object[] displayName = ChildUtils.getStringWithNumber(name);
            String str = (String) displayName[0];
            String no = (String) displayName[1];
            name = str + " " + ChildUtils.getFirstSecondAsNumber(no) + " pack";
            textViewMnpName.setText(name);
        }
    }

    @Override
    public void updateVitaminAData(String name) {
        if (!TextUtils.isEmpty(name)) {
            layoutVitaminBar.setVisibility(VISIBLE);
            ((View) findViewById(R.id.view_vitamin_a_bar)).setVisibility(VISIBLE);
            Object[] displayName = ChildUtils.getStringWithNumber(name);
            String str = (String) displayName[0];
            String no = (String) displayName[1];
            name = str + " " + ChildUtils.getFirstSecondAsNumber(no) + " dose";
            textViewVitaminName.setText(name);
        }
    }

    @Override
    public void updateDewormingData(String name) {
        if (!TextUtils.isEmpty(name)) {
            layoutDewormingBar.setVisibility(VISIBLE);
            Object[] displayName = ChildUtils.getStringWithNumber(name);
            String str = (String) displayName[0];
            String no = (String) displayName[1];
            name = str + " " + ChildUtils.getFirstSecondAsNumber(no) + " dose";
            textViewDewormingName.setText(name);
        }
    }

    @Override
    public void statusImageViewUpdate(String type, boolean value) {
        if (type.equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.EXCLUSIVE.getValue())) {
            updateStatusTick(imageViewExclusiveStatus, value);
        } else if (type.equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.MNP.getValue())) {
            updateStatusTick(imageViewMnpStatus, value);
        } else if (type.equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.VITAMIN.getValue())) {
            updateStatusTick(imageViewVitaminStatus, value);
        } else if (type.equalsIgnoreCase(GrowthNutritionInputFragment.GROWTH_TYPE.DEWORMING.getValue())) {
            updateStatusTick(imageViewDewormingStatus, value);
        }
    }

    @Override
    public void updateUpcomingService() {

    }


    private void updateStatusTick(CircleImageView imageView, boolean isCheck) {
        if (isCheck) {
            imageView.setImageResource(R.drawable.ic_checked);
            imageView.setColorFilter(getResources().getColor(R.color.white));
            imageView.setCircleBackgroundColor(getResources().getColor(R.color.alert_complete_green));
            imageView.setBorderColor(getResources().getColor(R.color.alert_complete_green));

        } else {
            imageView.setImageResource(R.drawable.ic_checked);
            imageView.setColorFilter(getResources().getColor(R.color.white));
            imageView.setCircleBackgroundColor(getResources().getColor(R.color.pnc_circle_yellow));
            imageView.setBorderColor(getResources().getColor(R.color.pnc_circle_yellow));
        }
        if (childHomeVisitFragment != null) {
            childHomeVisitFragment.checkIfSubmitIsToBeEnabled();
        }

    }

    public void setState(String type, ServiceWrapper serviceWrapper) {
        presenter.setSaveState(type, serviceWrapper);

    }

    public void notVisitSetState(String type, ServiceWrapper serviceWrapper) {
        presenter.serNotVisitState(type, serviceWrapper);
    }

    public void resetAll() {
        presenter.resetAllSaveState();
    }

    public boolean isAllSelected() {
        return presenter.isAllSelected();
    }

    public Map<String, String> returnSaveStateMap() {
        return presenter.getSaveStateMap();
    }
}
