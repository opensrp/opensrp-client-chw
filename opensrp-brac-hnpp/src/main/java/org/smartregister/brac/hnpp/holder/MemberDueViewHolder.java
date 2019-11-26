package org.smartregister.brac.hnpp.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.smartregister.brac.hnpp.R;
import org.smartregister.view.customcontrols.CustomFontTextView;

/**
 * Created by wizard on 06/08/19.
 */
public class MemberDueViewHolder extends RecyclerView.ViewHolder {
    public RelativeLayout itemBg;
    public ImageView statusImage,imageView;
    public CustomFontTextView textViewTitle;
    public CustomFontTextView textViewLastVisit;

    public MemberDueViewHolder(@NonNull View itemView) {
        super(itemView);
        itemBg = itemView.findViewById(R.id.member_due_bar);
        statusImage = itemView.findViewById(R.id.status);
        imageView = itemView.findViewById(R.id.image_view);
        textViewTitle = itemView.findViewById(R.id.title_txt);
        textViewLastVisit = itemView.findViewById(R.id.last_visit);
    }
}
