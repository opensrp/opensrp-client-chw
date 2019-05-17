package org.smartregister.chw.interactor;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import org.smartregister.chw.util.ChildDBConstants;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.ChildVisit;
import org.smartregister.chw.util.ImmunizationState;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.family.util.DBConstants;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import timber.log.Timber;

public class FamilyInteractor {

    public static Observable<String> updateFamilyDueStatus(final Context context, final String childId, final String familyId) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                ImmunizationState familyImmunizationState = ImmunizationState.NO_ALERT;
                String query = ChildUtils.getChildListByFamilyId(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD, familyId);
                Cursor cursor = null;
                try {
                    cursor = org.smartregister.family.util.Utils.context().commonrepository(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD).queryTable(query);
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            switch (getChildStatus(context, childId, cursor)) {
                                case DUE:
                                    if (familyImmunizationState != ImmunizationState.OVERDUE) {
                                        familyImmunizationState = ImmunizationState.DUE;
                                    }
                                    break;
                                case OVERDUE:
                                    familyImmunizationState = ImmunizationState.OVERDUE;
                                    break;
                                default:
                                    break;
                            }
                        } while (cursor.moveToNext());
                    }
                } catch (Exception ex) {
                    Timber.e(ex.toString());
                } finally {
                    if (cursor != null)
                        cursor.close();
                }

                e.onNext(toStringFamilyState(familyImmunizationState));
            }
        });

    }

    private static String toStringFamilyState(ImmunizationState state) {
        if (state.equals(ImmunizationState.DUE)) {
            return ChildProfileInteractor.FamilyServiceType.DUE.name();
        } else if (state.equals(ImmunizationState.OVERDUE)) {
            return ChildProfileInteractor.FamilyServiceType.OVERDUE.name();
        } else {
            return ChildProfileInteractor.FamilyServiceType.NOTHING.name();
        }
    }

    private static ImmunizationState getChildStatus(Context context, final String childId, Cursor cursor) {
        CommonPersonObject personObject = org.smartregister.family.util.Utils.context().commonrepository(org.smartregister.chw.util.Constants.TABLE_NAME.CHILD).findByBaseEntityId(cursor.getString(1));
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
        return ImmunizationState.NO_ALERT;
    }

    private static ImmunizationState getImmunizationStatus(String visitStatus) {
        if (visitStatus.equalsIgnoreCase(ChildProfileInteractor.VisitType.OVERDUE.name())) {
            return ImmunizationState.OVERDUE;
        }
        if (visitStatus.equalsIgnoreCase(ChildProfileInteractor.VisitType.DUE.name())) {
            return ImmunizationState.DUE;
        }
        return ImmunizationState.NO_ALERT;
    }
}
