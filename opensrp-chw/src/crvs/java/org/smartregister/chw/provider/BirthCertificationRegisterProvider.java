package org.smartregister.chw.provider;

import static org.smartregister.chw.core.utils.ChildDBConstants.KEY.BIRTH_CERT;
import static org.smartregister.chw.util.ChildDBConstants.KEY.BIRTH_REGISTRATION;
import static org.smartregister.chw.util.CrvsConstants.NO;
import static org.smartregister.chw.util.CrvsConstants.YES;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;

import org.smartregister.chw.R;
import org.smartregister.chw.core.holders.RegisterViewHolder;
import org.smartregister.chw.core.provider.CoreCertificationRegisterProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Set;

/**
 * Created by Allan
 */

public class BirthCertificationRegisterProvider extends CoreCertificationRegisterProvider {

    private Set<org.smartregister.configurableviews.model.View> visibleColumns;

    private View.OnClickListener onClickListener;

    private Context context;

    public BirthCertificationRegisterProvider(Context context, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, visibleColumns, onClickListener, paginationClickListener);
        this.visibleColumns = visibleColumns;
        this.onClickListener = onClickListener;
        this.context = context;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (visibleColumns.isEmpty()) {
            populatePatientColumn(pc, client, viewHolder);
            populateLastColumn(pc, viewHolder);
        }
    }

    private void populateLastColumn(CommonPersonObjectClient pc, RegisterViewHolder viewHolder) {
        Button viewHolderDueBtn = viewHolder.dueButton;
        if (pc != null) {
            viewHolder.dueButton.setVisibility(View.VISIBLE);
            String certificateReceived = Utils.getValue(pc.getColumnmaps(), BIRTH_CERT, true);
            String birthRegistrationDone = Utils.getValue(pc.getColumnmaps(), BIRTH_REGISTRATION, true);
            if (YES.equalsIgnoreCase(certificateReceived) || YES.equalsIgnoreCase(birthRegistrationDone)) {
                setReceivedButtonColor(context, viewHolderDueBtn);
            } else if (NO.equalsIgnoreCase(certificateReceived) || NO.equalsIgnoreCase(birthRegistrationDone)) {
                setNotReceivedButtonColor(context, viewHolderDueBtn);
            } else {
                setUpdateStatusButtonColor(context, viewHolderDueBtn);
            }
        } else {
            viewHolderDueBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void setReceivedButtonColor(Context context, Button dueButton) {
        updateButton(dueButton, context.getString(R.string.birth_certificate_received),
                context.getResources().getColor(R.color.certificate_received_green), 0);
    }

    @Override
    public void setNotReceivedButtonColor(Context context, Button dueButton) {
        updateButton(dueButton, context.getString(org.smartregister.chw.core.R.string.birth_certificate_not_received),
                context.getResources().getColor(org.smartregister.chw.core.R.color.black), 0);
    }

}
