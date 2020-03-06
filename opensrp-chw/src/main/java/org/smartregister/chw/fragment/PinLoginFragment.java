package org.smartregister.chw.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import org.joda.time.DateTime;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.PinLoginActivity;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.PinViewContract;
import org.smartregister.chw.pinlogin.PinLogger;
import org.smartregister.chw.presenter.LoginPresenter;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.BaseLoginContract;

import timber.log.Timber;

import static org.smartregister.util.Log.logError;

public class PinLoginFragment extends Fragment implements View.OnClickListener, BaseLoginContract.View {

    public static final String TAG = "PinLoginFragment";

    private BaseLoginContract.Presenter mLoginPresenter;
    private ProgressDialog progressDialog;
    private EditText passwordEditText;
    private boolean showPasswordChecked = false;
    private TextView showPinText;
    private CheckBox showPasswordCheck;
    private Button btnLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pin_login_fragment, container, false);
        initializeBuildDetails(view);

        mLoginPresenter = new LoginPresenter(this);

        showPasswordCheck = view.findViewById(R.id.login_show_password_checkbox);
        showPinText = view.findViewById(R.id.login_show_password_text_view);
        btnLogin = view.findViewById(R.id.login_login_btn);
        passwordEditText = view.findViewById(R.id.login_password_edit_text);

        passwordEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == org.smartregister.R.integer.login || actionId == EditorInfo.IME_NULL || actionId == EditorInfo.IME_ACTION_DONE) {
                enableLoginButton(true);
                hideKeyboard();
                return true;
            }
            return false;
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Timber.v("beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Timber.v("onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                enableLoginButton(true);
            }
        });


        TextView enterPinTextView = view.findViewById(R.id.pin_title_text_view);
        enterPinTextView.setText(getString(R.string.enter_pin_for_user, getController().getPinLogger().loggedInUser()));

        setListenerOnShowPasswordCheckbox();
        initializeProgressDialog();

        view.findViewById(R.id.forgot_pin).setOnClickListener(this);
        view.findViewById(R.id.use_your_password).setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        return view;
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(org.smartregister.R.string.loggin_in_dialog_title));
        progressDialog.setMessage(getString(org.smartregister.R.string.loggin_in_dialog_message));
    }

    private void setListenerOnShowPasswordCheckbox() {
        showPinText.setOnClickListener(v -> {
            if (showPasswordChecked) {
                showPasswordCheck.setChecked(true);
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showPasswordChecked = false;
            } else {
                showPasswordCheck.setChecked(false);
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showPasswordChecked = true;
            }
        });

        showPasswordCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showPasswordChecked = false;
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                showPasswordChecked = true;
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
    }

    private void initializeBuildDetails(View view) {
        TextView buildDetailsTextView = view.findViewById(org.smartregister.R.id.login_build_text_view);
        try {
            buildDetailsTextView.setText(String.format(getString(org.smartregister.R.string.app_version), Utils.getVersion(ChwApplication.getInstance()
                    .getApplicationContext()), Utils.getBuildDate(true)));
        } catch (Exception e) {
            logError("Error fetching build details: " + e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_login_btn:
                attemptLogin();
                break;
            case R.id.forgot_pin:
            case R.id.use_your_password:
                revertToPassword();
                break;
            default:
                break;
        }
    }

    private void revertToPassword() {
        getController().getPinLogger().resetPinLogin();
        getController().startPasswordLogin();
    }

    private void attemptLogin() {
        showProgress(true);
        hideKeyboard();
        enableLoginButton(false);
        PinLogger logger = getController().getPinLogger();
        mLoginPresenter.attemptLogin(logger.getLoggedInUserName(), logger.getPassword(passwordEditText.getText().toString()));
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

    @Override
    public void setUsernameError(int resourceId) {
        Timber.v("setUsernameError attempted");
    }

    @Override
    public void resetUsernameError() {
        Timber.v("resetUsernameError attempted");
    }

    @Override
    public void setPasswordError(int resourceId) {
        passwordEditText.setError(getString(resourceId));
        passwordEditText.requestFocus();
        showErrorDialog(getResources().getString(org.smartregister.R.string.unauthorized));
    }

    @Override
    public void resetPaswordError() {
        passwordEditText.setError(null);
    }

    @Override
    public void showProgress(final boolean show) {
        try{
            if (show) {
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
        }catch (Exception e){
            Timber.v(e);
        }
    }

    @Override
    public void updateProgressMessage(String message) {
        progressDialog.setTitle(message);
    }

    @Override
    public void hideKeyboard() {
        try {
            Timber.i("Hiding Keyboard %s", DateTime.now().toString());
            Utils.hideKeyboard(getActivity());
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void showErrorDialog(String message) {
        showProgress(false);
        showErrorDialog(org.smartregister.R.string.login_failed_dialog_title, message);
    }

    public void showErrorDialog(@StringRes int title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

    @Override
    public void enableLoginButton(boolean isClickable) {
        btnLogin.setClickable(isClickable);
    }

    @Override
    public void goToHome(boolean b) {
        getController().startHomeActivity();
    }

    @Override
    public Activity getActivityContext() {
        return getActivity();
    }
}
