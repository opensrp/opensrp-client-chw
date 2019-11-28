package org.smartregister.brac.hnpp.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.brac.hnpp.R;
import org.smartregister.brac.hnpp.adapter.MemberHistoryAdapter;
import org.smartregister.brac.hnpp.adapter.OtherServiceAdapter;
import org.smartregister.brac.hnpp.contract.MemberHistoryContract;
import org.smartregister.brac.hnpp.contract.OtherServiceContract;
import org.smartregister.brac.hnpp.presenter.MemberHistoryPresenter;
import org.smartregister.brac.hnpp.presenter.MemberOtherServicePresenter;
import org.smartregister.brac.hnpp.utils.HnppConstants;
import org.smartregister.brac.hnpp.utils.MemberHistoryData;
import org.smartregister.brac.hnpp.utils.OtherServiceData;
import org.smartregister.family.util.Constants;

public class MemberHistoryFragment extends Fragment implements MemberHistoryContract.View {

    private MemberHistoryPresenter presenter;
    private RecyclerView clientsView;
    private String baseEntityId;

    public static MemberHistoryFragment getInstance(Bundle bundle){
        MemberHistoryFragment memberHistoryFragment = new MemberHistoryFragment();
        Bundle args = bundle;
        if(args == null){
            args = new Bundle();
        }
        memberHistoryFragment.setArguments(args);
        return memberHistoryFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view,null);
        clientsView = view.findViewById(R.id.recycler_view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        baseEntityId = getArguments().getString(Constants.INTENT_KEY.BASE_ENTITY_ID);
        initializePresenter();
    }

    @Override
    public void initializePresenter() {
        presenter = new MemberHistoryPresenter(this);
        presenter.fetchData(baseEntityId);
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void updateAdapter() {

        MemberHistoryAdapter adapter = new MemberHistoryAdapter(getActivity(),onClickAdapter);
        adapter.setData(presenter.getMemberHistory());
        this.clientsView.setAdapter(adapter);
    }


    @Override
    public MemberHistoryContract.Presenter getPresenter() {
        return presenter;
    }

    private MemberHistoryAdapter.OnClickAdapter onClickAdapter = (position, content) -> startFormActivity(content);
    private void startFormActivity(MemberHistoryData content){
        switch (content.getVisitType()){

        }
    }
}