package org.smartregister.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.smartregister.chw.R;

public class FindReportFragment extends Fragment {
    public static final String TAG = "FindReportFragment";
    public static final String REPORT_NAME = "REPORT_NAME";

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.find_report_fragment, container, false);

        return view;
    }
}
