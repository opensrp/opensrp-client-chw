package org.smartregister.chw.fragment;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.PinLoginActivity;
import org.smartregister.chw.contract.PinViewContract;

public class PinLoginFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "PinLoginFragment";

    private EditText editTextPassword;
    private boolean showPasswordChecked = false;
    private TextView showPinText;
    private TextView forgotPin;
    private CheckBox showPasswordCheck;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pin_login_fragment, container, false);

        showPasswordCheck = view.findViewById(R.id.login_show_password_checkbox);
        forgotPin = view.findViewById(R.id.forgot_pin);
        showPinText = view.findViewById(R.id.login_show_password_text_view);
        Button btnLogin = view.findViewById(R.id.login_login_btn);
        editTextPassword = view.findViewById(R.id.login_password_edit_text);

        setListenerOnShowPasswordCheckbox();

        forgotPin.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        return view;
    }

    private void setListenerOnShowPasswordCheckbox() {
        showPinText.setOnClickListener(v -> {
            if (showPasswordChecked) {
                showPasswordCheck.setChecked(true);
                editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showPasswordChecked = false;
            } else {
                showPasswordCheck.setChecked(false);
                editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showPasswordChecked = true;
            }
        });

        showPasswordCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showPasswordChecked = false;
                editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                showPasswordChecked = true;
                editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_login_btn:
                attemptLogin();
                break;
            case R.id.forgot_pin:
                revertToPassword();
                break;
            default:
                break;
        }
    }

    private void revertToPassword() {

    }

    private void attemptLogin() {

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
