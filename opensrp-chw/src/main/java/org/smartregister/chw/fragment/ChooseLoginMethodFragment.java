package org.smartregister.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.PinLoginActivity;
import org.smartregister.chw.contract.PinViewContract;

public class ChooseLoginMethodFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "ChooseLoginMethodFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pin_login_fragment, container, false);
        view.findViewById(R.id.btnUsePin).setOnClickListener(this);
        view.findViewById(R.id.btnUsePassword).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnUsePin:
                getController().navigateToFragment(SetPinFragment.TAG);
                break;
            case R.id.btnUsePassword:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!(getActivity() instanceof PinViewContract.Controller)) {
            throw new IllegalStateException("Host activity does not implement Controller");
        }
    }

    private PinViewContract.Controller getController() {
        return (PinLoginActivity) getActivity();
    }
}
