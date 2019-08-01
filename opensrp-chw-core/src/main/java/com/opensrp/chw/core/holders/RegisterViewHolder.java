package com.opensrp.chw.core.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.opensrp.chw.core.R;

public class RegisterViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewParentName;
    public TextView textViewChildName;
    public TextView textViewAddressGender;
    public Button dueButton;
    public View childColumn;

    public RegisterViewHolder(View itemView) {
        super(itemView);

        textViewParentName = itemView.findViewById(R.id.textview_parent_name);
        textViewChildName = itemView.findViewById(R.id.text_view_child_name);
        textViewAddressGender = itemView.findViewById(R.id.text_view_address_gender);
        dueButton = itemView.findViewById(R.id.due_button);

        childColumn = itemView.findViewById(R.id.child_column);
    }
}
