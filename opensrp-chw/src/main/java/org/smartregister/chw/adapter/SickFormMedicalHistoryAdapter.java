package org.smartregister.chw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.contract.SickFormMedicalHistoryContract;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SickFormMedicalHistoryAdapter extends RecyclerView.Adapter<SickFormMedicalHistoryAdapter.MyViewHolder> {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

    private List<Visit> serviceList;
    private Context context;
    private LayoutInflater layoutInflater;
    private SickFormMedicalHistoryContract.View view;

    public SickFormMedicalHistoryAdapter(Context context, List<Visit> serviceList, SickFormMedicalHistoryContract.View view) {
        this.serviceList = serviceList;
        this.context = context;
        this.view = view;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        this.layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View v = layoutInflater.inflate(R.layout.sick_form_medical_history, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SickFormMedicalHistoryAdapter.MyViewHolder holder, int i) {
        Visit visit = serviceList.get(i);
        holder.tvDetails.setText(sdf.format(visit.getDate()));
        holder.ivNext.setOnClickListener(v -> view.onAdapterInteraction(visit));
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDetails;
        private ImageView ivNext;

        private MyViewHolder(View view) {
            super(view);
            tvDetails = view.findViewById(R.id.tvDetails);
            ivNext = view.findViewById(R.id.ivNext);
        }
    }
}
