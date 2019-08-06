package com.opensrp.chw.hf.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.opensrp.hf.R;

import org.smartregister.view.customcontrols.CustomFontTextView;

public class ReferralsCardHolder extends RecyclerView.ViewHolder {
    private RelativeLayout referalRow;
    private ImageView referalRowImage;
    private ImageView referalArrowImage;
    private CustomFontTextView textviewReferalHeader;
    private CustomFontTextView textviewReferalInfo;
    private View viewReferalRow;

    public ReferralsCardHolder(@NonNull View itemView) {
        super(itemView);

        referalRow = itemView.findViewById(R.id.referal_row);
        referalRowImage = itemView.findViewById(R.id.referal_row_image);
        referalArrowImage = itemView.findViewById(R.id.referal_arrow_image);
        textviewReferalHeader = itemView.findViewById(R.id.textview_referal_header);
        textviewReferalInfo = itemView.findViewById(R.id.text_view_referal_info);
        viewReferalRow = itemView.findViewById(R.id.view_referal_row);
    }
}
