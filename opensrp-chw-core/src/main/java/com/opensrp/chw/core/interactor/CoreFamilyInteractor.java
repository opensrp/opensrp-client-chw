package com.opensrp.chw.core.interactor;

import android.content.Context;
import android.database.Cursor;

import com.opensrp.chw.core.enums.ImmunizationState;

import com.opensrp.chw.core.utils.CoreChildUtils;
import com.opensrp.chw.core.utils.CoreConstants;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import timber.log.Timber;

import static com.opensrp.chw.core.enums.ImmunizationState.DUE;
import static com.opensrp.chw.core.enums.ImmunizationState.OVERDUE;

public abstract class CoreFamilyInteractor {

    public Observable<String> updateFamilyDueStatus(final Context context, final String childId, final String familyId) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                ImmunizationState familyImmunizationState = ImmunizationState.NO_ALERT;
                String query = CoreChildUtils.getChildListByFamilyId(CoreConstants.TABLE_NAME.CHILD, familyId);
                Cursor cursor = null;
                try {
                    cursor = org.smartregister.family.util.Utils.context().commonrepository(CoreConstants.TABLE_NAME.CHILD).queryTable(query);
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            familyImmunizationState = getImmunizationState(familyImmunizationState, cursor, context, childId);
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

    private ImmunizationState getImmunizationState(ImmunizationState familyImmunizationState, Cursor cursor, Context context, String childId){
        switch (this.getChildStatus(context, childId, cursor)) {
            case DUE:
                if (familyImmunizationState != OVERDUE) {
                    familyImmunizationState = DUE;
                }
                break;
            case OVERDUE:
                familyImmunizationState = OVERDUE;
                break;
            default:
                break;
        }
        return familyImmunizationState;
    };

    public abstract ImmunizationState getChildStatus(Context context, final String childId, Cursor cursor);

    private static String toStringFamilyState(ImmunizationState state) {
        if (state.equals(ImmunizationState.DUE)) {
            return CoreChildProfileInteractor.FamilyServiceType.DUE.name();
        } else if (state.equals(ImmunizationState.OVERDUE)) {
            return CoreChildProfileInteractor.FamilyServiceType.OVERDUE.name();
        } else {
            return CoreChildProfileInteractor.FamilyServiceType.NOTHING.name();
        }
    }

    public static ImmunizationState getImmunizationStatus(String visitStatus) {
        if (visitStatus.equalsIgnoreCase(CoreChildProfileInteractor.VisitType.OVERDUE.name())) {
            return ImmunizationState.OVERDUE;
        }
        if (visitStatus.equalsIgnoreCase(CoreChildProfileInteractor.VisitType.DUE.name())) {
            return ImmunizationState.DUE;
        }
        return ImmunizationState.NO_ALERT;
    }
}
