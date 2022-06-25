package org.smartregister.chw.provider;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.smartregister.chw.R;
import org.smartregister.chw.core.holders.ReferralViewHolder;
import org.smartregister.chw.core.provider.BaseReferralRegisterProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Location;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.LocationRepository;

public class LTFURegisterProvider extends BaseReferralRegisterProvider {
    private final Context context;

    public LTFURegisterProvider(Context context, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, onClickListener, paginationClickListener);
        this.context = context;
    }

    @Override
    public void populatePatientColumn(CommonPersonObjectClient pc, ReferralViewHolder viewHolder) {
        super.populatePatientColumn(pc, viewHolder);
        TextView referralClinic = viewHolder.itemView.findViewById(R.id.referral_clinic);
        referralClinic.setText(Utils.getValue(pc.getColumnmaps(), "REFERRAL_CLINIC", true));

        TextView referredByTextView = viewHolder.itemView.findViewById(org.smartregister.chw.core.R.id.referred_by);
        LocationRepository locationRepository = new LocationRepository();
        String locationId = Utils.getValue(pc.getColumnmaps(), org.smartregister.chw.referral.util.DBConstants.Key.REFERRAL_HF, false);
        Location location = locationRepository.getLocationById(locationId);
        if(location != null) {
            referredByTextView.setText(context.getString(R.string.referred_by, location.getProperties().getName()));
        }else{
            referredByTextView.setText(context.getString(R.string.referred_by, locationId));
        }

    }
}
