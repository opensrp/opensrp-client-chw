package org.smartregister.chw.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.PinLoginActivity;
import org.smartregister.chw.contract.PinViewContract;
import org.smartregister.chw.pinlogin.PinLogger;

import timber.log.Timber;

public class SetPinFragment extends Fragment {

    public static final String TAG = "SetPinFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.set_pin_fragment, container, false);

        EditText editTextPin = view.findViewById(R.id.editTextPin);
        editTextPin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    setPin(editTextPin);
                    handled = true;
                }
                return handled;
            }
        });

        return view;
    }

    private void setPin(EditText editTextPin){
        String newPin = editTextPin.getText().toString();
        if(StringUtils.isBlank(newPin) || newPin.length() < 4){
            Toast.makeText(getContext(), "Pin too short", Toast.LENGTH_SHORT).show();
            editTextPin.setError("Pin too short");
        }else{
            getController().getPinLogger().setPin(newPin, new PinLogger.EventListener() {
                @Override
                public void OnError(Exception ex) {
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    editTextPin.setError("Pin was not set");
                }

                @Override
                public void OnSuccess() {
                    getController().navigateToFragment(PinLoginFragment.TAG);
                }

                @Override
                public void OnEvent(String event) {
                    Timber.v(event);
                }
            });
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
