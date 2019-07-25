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
import org.smartregister.chw.util.WashCheck;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WashCheckAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<WashCheck> contentList;
    private Context context;
    private String familyName;
    private OnClickAdapter onClickAdapter;

    public WashCheckAdapter(Context context,String familyName,OnClickAdapter onClickAdapter)
    {
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
        contentViewHolder.patientNameAge.setText(context.getString(R.string.family,familyName)+" "+context.getString(R.string.wash_check_suffix));
        contentViewHolder.lastVisit.setText(context.getString(R.string.completed_on_prefix,new SimpleDateFormat("dd MMM yyyy").format(new Date(content.getLastVisit()))));
        contentViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickAdapter.onClick(viewHolder.getAdapterPosition(),content);
            }
        });
    }


    @Override
    public int getItemCount() {
        return contentList.size();
    }
    private class RegisterViewHolder extends RecyclerView.ViewHolder {
        public ImageView status;
        public CustomFontTextView patientNameAge;
        public TextView lastVisit;
        public ImageView nextArrow;
        public android.view.View patientColumn;
        public android.view.View nextArrowColumn;
        public android.view.View statusColumn;
        public android.view.View registerColumns;

        public RegisterViewHolder(android.view.View itemView) {
            super(itemView);
            this.status = (ImageView)itemView.findViewById(org.smartregister.family.R.id.status);
            this.patientNameAge = (CustomFontTextView)itemView.findViewById(org.smartregister.family.R.id.patient_name_age);
            this.lastVisit = (TextView)itemView.findViewById(org.smartregister.family.R.id.last_visit);
            this.nextArrow = (ImageView)itemView.findViewById(org.smartregister.family.R.id.next_arrow);
            this.patientColumn = itemView.findViewById(org.smartregister.family.R.id.patient_column);
            this.nextArrowColumn = itemView.findViewById(org.smartregister.family.R.id.next_arrow_column);
            this.statusColumn = itemView.findViewById(org.smartregister.family.R.id.status_layout);
            this.registerColumns = itemView.findViewById(org.smartregister.family.R.id.register_columns);
        }
    }
    public interface OnClickAdapter{
         void onClick(int position,WashCheck washCheck);
    }
}
