package org.smartregister.chw.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.chw.R;


public class JobAidsGuideBooksFragment extends Fragment {

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
