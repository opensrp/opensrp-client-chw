package org.smartregister.chw.custom_view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.smartregister.chw.R;
import org.smartregister.chw.adapter.HomeVisitImmunizationAdapter;
import org.smartregister.chw.contract.ImmunizationEditContract;
import org.smartregister.chw.presenter.ImmunizationEditViewPresenter;

public class ImmunizationEditView extends LinearLayout implements ImmunizationEditContract.View {

    private RecyclerView recyclerView;
    private HomeVisitImmunizationAdapter adapter;
    private ImmunizationEditViewPresenter presenter;
    public ImmunizationEditView(Context context) {
        super(context);
        initUi();
    }

    public ImmunizationEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    public ImmunizationEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();
    }
    private void initUi(){
        inflate(getContext(), R.layout.custom_vaccine_edit,this);
        recyclerView = findViewById(R.id.immunization_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public ImmunizationEditContract.Presenter initializePresenter() {
        presenter = new ImmunizationEditViewPresenter(this);
        return presenter;
    }

    @Override
    public void updateAdapter() {
        if(adapter==null){
            adapter = new HomeVisitImmunizationAdapter(getContext());
            adapter.addItem(presenter.getHomeVisitVaccineGroupDetails());
            recyclerView.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }

    }
}
