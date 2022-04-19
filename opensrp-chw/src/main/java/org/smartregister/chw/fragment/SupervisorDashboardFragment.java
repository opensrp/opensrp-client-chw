package org.smartregister.chw.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.smartregister.chw.R;
import org.smartregister.chw.presenter.SuperVisorDashboardFragmentPresenter;
import org.smartregister.chw.reporting.ChwReport;
import org.smartregister.reporting.domain.IndicatorTally;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SupervisorDashboardFragment extends Fragment {
    public static final String TAG = "SupervisorIndicatorsFragment";
    private SuperVisorDashboardFragmentPresenter presenter;

    private ViewGroup visualizationsViewGroup;
    private ProgressBar progressBar;

    public SupervisorDashboardFragment() {
        // Required empty public constructor
    }

    public static SupervisorDashboardFragment newInstance() {
        SupervisorDashboardFragment fragment = new SupervisorDashboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new SuperVisorDashboardFragmentPresenter();
        loadIndicatorTallies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_indicators_dashboard, container, false);
        progressBar = rootView.findViewById(R.id.progress_bar);
        visualizationsViewGroup = rootView.findViewById(R.id.dashboard_content);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void loadIndicatorTallies() {
        Observable<List<Map<String, IndicatorTally>>> observable = Observable.create(e -> {
            List<Map<String, IndicatorTally>> indicatorTallies = presenter.getLatestIndicatorTallies();
            e.onNext(indicatorTallies);
            e.onComplete();
        });

        final Disposable[] disposable = new Disposable[1];
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Map<String, IndicatorTally>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable[0] = d;
                    }

                    @Override
                    public void onNext(List<Map<String, IndicatorTally>> indicatorTallies) {
                        showIndicatorVisualizations(indicatorTallies);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {
                        disposable[0].dispose();
                        disposable[0] = null;
                    }
                });
    }

    public void showIndicatorVisualizations(List<Map<String, IndicatorTally>> indicatorTallies) {
        visualizationsViewGroup.removeAllViews();
        ChwReport.showSupervisorIndicatorVisualisations(visualizationsViewGroup, indicatorTallies, getActivity());
        progressBar.setVisibility(View.GONE);
    }
}