package org.smartregister.chw.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import org.smartregister.chw.R;


public class JobAidsGuideBooksFragment1 extends Fragment {

    public static JobAidsGuideBooksFragment1 newInstance() {
        JobAidsGuideBooksFragment1 fragment = new JobAidsGuideBooksFragment1();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_job_aids_guide_books1, container, false);
        ConstraintLayout layoutTutorials = rootView.findViewById(R.id.layoutTutorials);
        ConstraintLayout layoutCounseling = rootView.findViewById(R.id.layoutCounseling);
        layoutCounseling.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), JobAidsPDFActivity.class);
            getActivity().startActivity(intent);
        });

        layoutTutorials.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), JobAidsGuideBooksTutorialsActivity.class);
            getActivity().startActivity(intent);
        });

        return rootView;
    }
}
