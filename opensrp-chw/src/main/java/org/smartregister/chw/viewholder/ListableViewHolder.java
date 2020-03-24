package org.smartregister.chw.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.contract.ListContract;

public abstract class ListableViewHolder<T extends ListContract.Identifiable> extends RecyclerView.ViewHolder implements ListContract.AdapterViewHolder<T> {
    public ListableViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}
