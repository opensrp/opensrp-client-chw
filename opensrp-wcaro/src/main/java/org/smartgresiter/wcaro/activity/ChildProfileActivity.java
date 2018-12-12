package org.smartgresiter.wcaro.activity;

import android.app.AppComponentFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.custom_view.IndividualMemberFloatingMenu;
import org.smartregister.view.activity.BaseProfileActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChildProfileActivity extends AppCompatActivity {
    private IndividualMemberFloatingMenu individualMemberFloatingMenu;
    @BindView(R.id.imageview_profile)
    ImageView profileImage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_profile);
        ButterKnife.bind(this);
    }
}
