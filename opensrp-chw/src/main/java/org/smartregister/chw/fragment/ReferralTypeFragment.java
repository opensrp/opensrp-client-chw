package org.smartregister.chw.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.chw.R;

public class ReferralTypeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_refer_to_facility, container, false);
        setUpView(view);
        return view;
    }

    private void setUpView(View view) {
        RecyclerView referralTypesRecyclerView = view.findViewById(R.id.referralTypeRecyclerView);
       // referralTypesRecyclerView.setAdapter();
    }
}
