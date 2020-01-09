package org.smartregister.chw.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.R;
import org.smartregister.chw.model.ReferralTypeModel;

import java.util.ArrayList;
import java.util.List;

public class ReferralTypeAdapter extends RecyclerView.Adapter<ReferralTypeAdapter.ReferralTypeViewHolder> {

    private List<ReferralTypeModel> referralTypes = new ArrayList<>();
    private View.OnClickListener onClickListener;
    public boolean canStart = false;

    public List<ReferralTypeModel> getReferralTypes() {
        return referralTypes;
    }

    public void setReferralTypes(List<ReferralTypeModel> referralTypes) {
        if (referralTypes != null) {
            this.referralTypes.addAll(referralTypes);
        }
    }

    @NonNull
    @Override
    public ReferralTypeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.referral_type_list_row, viewGroup, false);
        return new ReferralTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReferralTypeViewHolder referralTypeViewHolder, int position) {
        ReferralTypeModel referralTypeModel = referralTypes.get(position);
        referralTypeViewHolder.referralType.setText(referralTypeModel.getReferralType());
        referralTypeViewHolder.referralType.setTag(R.id.referral_type_form_name, referralTypeModel.getFormName());
    }

    @Override
    public int getItemCount() {
        return referralTypes.size();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class ReferralTypeViewHolder extends RecyclerView.ViewHolder {
        private TextView referralType;

        private ReferralTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setTag(this);
            referralType = itemView.findViewById(R.id.referralType);
            itemView.setOnClickListener(v -> onClickListener.onClick(v));
        }
    }
}
