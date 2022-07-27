package org.smartregister.chw.interactor;


import org.joda.time.LocalDate;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.dao.ChwChildDao;
import org.smartregister.immunization.domain.ServiceWrapper;

import java.util.Map;

import timber.log.Timber;

public class ChildHomeVisitInteractorFlv extends DefaultChildHomeVisitInteractorFlv {

    @Override
    protected int immunizationCeiling() {
        String gender = ChwChildDao.getChildGender(memberObject.getBaseEntityId());

        if (gender != null && gender.equalsIgnoreCase("Female")) {
            if (memberObject.getAge() >= 9 && memberObject.getAge() <= 11) {
                return 132;
            } else {
                return 60;
            }
        }

        return 60;
    }

    @Override
    protected void bindEvents(Map<String, ServiceWrapper> serviceWrapperMap) throws BaseAncHomeVisitAction.ValidationException {
        try {
            evaluateChildVaccineCard();
            evaluateImmunization();
        } catch (BaseAncHomeVisitAction.ValidationException e) {
            throw (e);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    protected int vaccineCardCeiling() {
        return 60;
    }

    @Override
    protected void evaluateChildVaccineCard() throws Exception {
        // expires on 5 years. verify that vaccine card is not received
        if (new LocalDate().isBefore(new LocalDate(dob).plusYears(5)) && !vaccineCardReceived) {
            addChildVaccineCardCardAction();
        }
    }
}