package org.smartgresiter.wcaro.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartgresiter.wcaro.R;


public class JobAidsGuideBooksFragment extends Fragment {


    public JobAidsGuideBooksFragment() {
        // Required empty public constructor
    }

    public static JobAidsGuideBooksFragment newInstance() {
        JobAidsGuideBooksFragment fragment = new JobAidsGuideBooksFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job_aids_guide_books, container, false);
    }

}
