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

import java.util.List;

public class SickFormMedicalHistoryAdapter extends RecyclerView.Adapter<SickFormMedicalHistoryAdapter.MyViewHolder> {

    private List<String> serviceList;
    private Context context;
    private LayoutInflater layoutInflater;

    public SickFormMedicalHistoryAdapter(Context context, List<String> serviceList) {
        this.serviceList = serviceList;
        this.context = context;
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
        String service = serviceList.get(i);
        holder.tvDetails.setText(service);
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
