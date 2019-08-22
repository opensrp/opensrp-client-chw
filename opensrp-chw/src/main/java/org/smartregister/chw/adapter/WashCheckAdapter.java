package org.smartregister.chw.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.smartregister.chw.R;
import org.smartregister.chw.core.utils.WashCheck;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WashCheckAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<WashCheck> contentList;
    private Context context;
    private String familyName;
    private OnClickAdapter onClickAdapter;

    public WashCheckAdapter(Context context, String familyName, OnClickAdapter onClickAdapter) {
        this.context = context;
        this.familyName = familyName;
        this.onClickAdapter = onClickAdapter;
        contentList = new ArrayList<>();
    }

    public void setData(ArrayList<WashCheck> contentList) {
        this.contentList.clear();
        this.contentList.addAll(contentList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RegisterViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_wash_check_family_activity, null));

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {
        final WashCheck content = contentList.get(position);
        RegisterViewHolder contentViewHolder = (RegisterViewHolder) viewHolder;
        contentViewHolder.patientNameAge.setText(context.getString(R.string.family, familyName) + " " + context.getString(R.string.wash_check_suffix));
        contentViewHolder.lastVisit.setText(context.getString(R.string.completed_on_prefix, new SimpleDateFormat("dd MMM yyyy").format(new Date(content.getLastVisit()))));
        contentViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickAdapter.onClick(viewHolder.getAdapterPosition(), content);
            }
        });
    }


    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public interface OnClickAdapter {
        void onClick(int position, WashCheck washCheck);
    }

    private class RegisterViewHolder extends RecyclerView.ViewHolder {
        public ImageView status;
        public TextView lastVisit;
        private CustomFontTextView patientNameAge;

        private RegisterViewHolder(View itemView) {
            super(itemView);
            this.status = itemView.findViewById(org.smartregister.family.R.id.status);
            this.patientNameAge = itemView.findViewById(org.smartregister.family.R.id.patient_name_age);
            this.lastVisit = itemView.findViewById(org.smartregister.family.R.id.last_visit);
        }
    }
}
