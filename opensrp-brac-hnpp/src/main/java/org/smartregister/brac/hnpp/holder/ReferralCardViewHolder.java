package org.smartregister.brac.hnpp.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.smartregister.brac.hnpp.R;
import org.smartregister.view.customcontrols.CustomFontTextView;

/**
 * Created by wizard on 06/08/19.
 */
public class ReferralCardViewHolder extends RecyclerView.ViewHolder {
    public RelativeLayout referralRow;
    public ImageView referralRowImage;
    public ImageView referralArrowImage;
    public CustomFontTextView textViewReferralHeader;
    public CustomFontTextView textViewReferralInfo;

    public ReferralCardViewHolder(@NonNull View itemView) {
        super(itemView);
        referralRow = itemView.findViewById(R.id.referal_row);
        referralRowImage = itemView.findViewById(R.id.referal_row_image);
        referralArrowImage = itemView.findViewById(R.id.referal_arrow_image);
        textViewReferralHeader = itemView.findViewById(R.id.textview_referal_header);
        textViewReferralInfo = itemView.findViewById(R.id.text_view_referal_info);
    }
}
