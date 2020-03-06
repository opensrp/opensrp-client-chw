package org.smartregister.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.smartregister.chw.R;

public class PinLoginFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "PinLoginFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pin_login_fragment, container, false);
        //view.findViewById(R.id.btnUsePin).setOnClickListener(this);
        //view.findViewById(R.id.btnUsePassword).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

    }
}
