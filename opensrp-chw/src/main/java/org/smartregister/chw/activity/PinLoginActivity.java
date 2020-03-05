package org.smartregister.chw.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import org.smartregister.chw.R;
import org.smartregister.chw.contract.PinViewContract;
import org.smartregister.chw.fragment.ChooseLoginMethodFragment;
import org.smartregister.chw.fragment.SetPinFragment;
import org.smartregister.view.activity.SecuredActivity;

import timber.log.Timber;

public class PinLoginActivity extends SecuredActivity implements PinViewContract.Controller {
    public static final String DESTINATION_FRAGMENT = "DESTINATION_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_login);

        switchToFragment(new ChooseLoginMethodFragment());

    }

    private void switchToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, fragment)
                .commit();
    }

    @Override
    protected void onCreation() {
        Timber.v("onCreation");
    }

    @Override
    protected void onResumption() {
        Timber.v("onResumption");
    }

    @Override
    public void navigateToFragment(String destinationFragment) {
        switch (destinationFragment) {
            case ChooseLoginMethodFragment.TAG:
                switchToFragment(new ChooseLoginMethodFragment());
                break;
            case SetPinFragment
                    .TAG:
                switchToFragment(new SetPinFragment());
                break;
            default:
                break;
        }
    }

    @Override
    public void startPasswordLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
