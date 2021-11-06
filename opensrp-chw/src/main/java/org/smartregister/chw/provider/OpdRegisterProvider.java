package org.smartregister.chw.provider;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import org.smartregister.chw.R;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.opd.holders.OpdRegisterViewHolder;
import org.smartregister.opd.utils.OpdDbConstants;

public class OpdRegisterProvider extends org.smartregister.opd.provider.OpdRegisterProvider {
    private final Context context;

    public OpdRegisterProvider(@NonNull Context context, @NonNull View.OnClickListener onClickListener, @NonNull View.OnClickListener paginationClickListener) {
        super(context, onClickListener, paginationClickListener);
        this.context = context;
    }

    @Override
    public void populatePatientColumn(CommonPersonObjectClient commonPersonObjectClient, OpdRegisterViewHolder viewHolder) {
        super.populatePatientColumn(commonPersonObjectClient, viewHolder);
        String registerType = org.smartregister.util.Utils.getValue(commonPersonObjectClient.getColumnmaps(),
                OpdDbConstants.KEY.REGISTER_TYPE, true);

        if (!TextUtils.isEmpty(registerType)) {
            viewHolder.showRegisterType();
            String type = getTranslatedRegisterType(registerType);
            fillValue(viewHolder.tvRegisterType, type);
        } else {
            viewHolder.hideRegisterType();
        }
    }

    private String getTranslatedRegisterType(String registerType) {
        if (registerType.equalsIgnoreCase(CoreConstants.REGISTER_TYPE.CHILD)) {
            return context.getString(R.string.menu_child);
        } else if (registerType.equalsIgnoreCase(CoreConstants.REGISTER_TYPE.ANC)) {
            return context.getString(R.string.menu_anc);
        } else if (registerType.equalsIgnoreCase(CoreConstants.REGISTER_TYPE.PNC)) {
            return context.getString(R.string.menu_pnc);
        } else if (registerType.equalsIgnoreCase(CoreConstants.REGISTER_TYPE.FAMILY_PLANNING)) {
            return context.getString(R.string.menu_family_planning);
        } else if (registerType.equalsIgnoreCase(CoreConstants.REGISTER_TYPE.MALARIA)) {
            return context.getString(R.string.menu_malaria);
        }
        return registerType;
    }

    @Override
    public void setAddressAndGender(CommonPersonObjectClient pc, OpdRegisterViewHolder viewHolder) {
        super.setAddressAndGender(pc, viewHolder);
        String gender_key = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.GENDER, true);
        String gender = "";
        if (gender_key.equalsIgnoreCase("Male")) {
            gender = context.getString(org.smartregister.chw.core.R.string.male);
        } else if (gender_key.equalsIgnoreCase("Female")) {
            gender = context.getString(org.smartregister.chw.core.R.string.female);
        }
        fillValue(viewHolder.textViewGender, gender);
    }
}
