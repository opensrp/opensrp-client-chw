package org.smartregister.chw.interactor;

import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.interactor.CoreFamilyChangeContractInteractor;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FamilyChangeContractInteractor extends CoreFamilyChangeContractInteractor {

    public FamilyChangeContractInteractor() {
        setCoreChwApplication();
        setFlavour();
    }

    @Override
    protected void setCoreChwApplication() {
        this.coreChwApplication = ChwApplication.getInstance();
    }

    @Override
    protected void setFlavour() {
        this.flavor = new FamilyChangeContractInteractorFlv();
    }

    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }
}
