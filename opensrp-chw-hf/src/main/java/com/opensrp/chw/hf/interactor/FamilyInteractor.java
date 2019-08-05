package com.opensrp.chw.hf.interactor;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.opensrp.chw.core.enums.ImmunizationState;
import com.opensrp.chw.core.interactor.CoreFamilyInteractor;
import com.opensrp.chw.core.model.ChildVisit;
import com.opensrp.chw.core.utils.ChildDBConstants;
import com.opensrp.chw.core.utils.CoreConstants;
import com.opensrp.chw.hf.utils.ChildUtils;

import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.family.util.DBConstants;

import static com.opensrp.chw.core.enums.ImmunizationState.NO_ALERT;

public class FamilyInteractor extends CoreFamilyInteractor {

    @Override
    public ImmunizationState getChildStatus(Context context, final String childId, Cursor cursor) {
        CommonPersonObject personObject = org.smartregister.family.util.Utils.context().commonrepository(CoreConstants.TABLE_NAME.CHILD).findByBaseEntityId(cursor.getString(1));
        if (!personObject.getCaseId().equalsIgnoreCase(childId)) {

            String dobString = org.smartregister.util.Utils.getValue(personObject.getColumnmaps(), DBConstants.KEY.DOB, false);
            String visitNotDoneStr = org.smartregister.util.Utils.getValue(personObject.getColumnmaps(), ChildDBConstants.KEY.VISIT_NOT_DONE, false);
            String lastHomeVisitStr = org.smartregister.util.Utils.getValue(personObject.getColumnmaps(), ChildDBConstants.KEY.LAST_HOME_VISIT, false);
            String strDateCreated = org.smartregister.family.util.Utils.getValue(personObject.getColumnmaps(), ChildDBConstants.KEY.DATE_CREATED, false);
            long lastHomeVisit = TextUtils.isEmpty(lastHomeVisitStr) ? 0 : Long.parseLong(lastHomeVisitStr);
            long visitNotDone = TextUtils.isEmpty(visitNotDoneStr) ? 0 : Long.parseLong(visitNotDoneStr);

            long dateCreated = 0;
            if (!TextUtils.isEmpty(strDateCreated)) {
                dateCreated = org.smartregister.family.util.Utils.dobStringToDateTime(strDateCreated).getMillis();
            }

            final ChildVisit childVisit = ChildUtils.getChildVisitStatus(context, dobString, lastHomeVisit, visitNotDone, dateCreated);
            return getImmunizationStatus(childVisit.getVisitStatus());
        }
        return NO_ALERT;
    }
}
