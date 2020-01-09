package org.smartregister.chw.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.R;
import org.smartregister.chw.domain.MedicalHistory;

import java.util.List;

public class MedicalHistoryAdapter extends RecyclerView.Adapter<MedicalHistoryAdapter.MyViewHolder> {
    private List<MedicalHistory> items;
    private LayoutInflater inflater;
    private int layoutID;

    public MedicalHistoryAdapter(List<MedicalHistory> items, @LayoutRes int layoutID) {
        this.items = items;
        this.layoutID = layoutID;
    }

    @NonNull
    @Override
    public MedicalHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        inflater = LayoutInflater.from(viewGroup.getContext());
        View v = inflater.inflate(R.layout.medical_history_nested_item, viewGroup, false);
        return new MedicalHistoryAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicalHistoryAdapter.MyViewHolder myViewHolder, int position) {
        MedicalHistory item = items.get(position);

        myViewHolder.tvSubTitle.setVisibility(StringUtils.isNotBlank(item.getTitle()) ? View.VISIBLE : View.GONE);
        if (StringUtils.isNotBlank(item.getTitle()))
            myViewHolder.tvSubTitle.setText(item.getTitle());

        if (item.getText() == null) return;

        for (String content : item.getText()) {
            View view = inflater.inflate(layoutID, null);
            TextView tvContent = view.findViewById(R.id.tvContent);
            tvContent.setText(content);

            myViewHolder.llItems.addView(view);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSubTitle;
        private LinearLayout llItems;

        private MyViewHolder(View view) {
            super(view);
            tvSubTitle = view.findViewById(R.id.tvSubTitle);
            llItems = view.findViewById(R.id.llItems);
        }
    }

}
