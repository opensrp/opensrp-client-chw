package org.smartregister.chw.interactor;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseUpcomingService;
import org.smartregister.chw.core.model.VaccineTaskModel;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.RecurringServiceUtil;
import org.smartregister.chw.core.utils.VaccineScheduleUtil;
import org.smartregister.chw.dao.PersonDao;
import org.smartregister.chw.util.ContactUtil;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.immunization.domain.VaccineWrapper;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class DefaultAncUpcomingServicesInteractorFlv implements AncUpcomingServicesInteractor.Flavor {

    @Override
    public List<BaseUpcomingService> getMemberServices(Context context, MemberObject memberObject) {
        List<BaseUpcomingService> services = new ArrayList<>();

        Date createDate = null;
        try {
            PersonDao dao = new PersonDao();
            String x = dao.getAncCreatedDate(memberObject.getBaseEntityId());
            createDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(x);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (createDate == null) {
            return services;
        }

        // anc card
        evaluateANCCard(services, memberObject, context, createDate);
        evaluateHealthFacility(services, memberObject, context);
        evaluateTT(services, memberObject, context);
        evaluateIPTP(services, memberObject, context);

        return services;
    }

    protected void evaluateANCCard(List<BaseUpcomingService> services, MemberObject memberObject, Context context, Date createDate) {
        if (memberObject.getHasAncCard() != null && !memberObject.getHasAncCard().equalsIgnoreCase("Yes")) {
            BaseUpcomingService cardService = new BaseUpcomingService();
            cardService.setServiceName(context.getString(R.string.anc_card));
            cardService.setServiceDate(createDate);
            services.add(cardService);
        }
    }

    protected void evaluateHealthFacility(List<BaseUpcomingService> services, MemberObject memberObject, Context context) {
        // hfv
        LocalDate dateCreated = new DateTime(memberObject.getDateCreated()).toLocalDate();
        List<LocalDate> dateList = new ArrayList<>(ContactUtil.getContactSchedule(memberObject, dateCreated).values());
        if (dateList.size() > 0) {
            BaseUpcomingService healthFacilityService = new BaseUpcomingService();
            healthFacilityService.setServiceName(MessageFormat.format(context.getString(R.string.health_facility_visit_num), (memberObject.getConfirmedContacts() + 1)));
            healthFacilityService.setServiceDate(dateList.get(0).toDate());
            services.add(healthFacilityService);
        }
    }

    protected void evaluateTT(List<BaseUpcomingService> services, MemberObject memberObject, Context context) {
        Triple<DateTime, VaccineRepo.Vaccine, String> ttIteration = getNextTT(memberObject);
        if (ttIteration != null && StringUtils.isNotBlank(ttIteration.getRight())) {
            BaseUpcomingService ttService = new BaseUpcomingService();
            ttService.setServiceName(MessageFormat.format(context.getString(R.string.tt_dose), ttIteration.getRight()));
            ttService.setServiceDate(ttIteration.getLeft().toDate());
            services.add(ttService);
        }
    }

    protected void evaluateIPTP(List<BaseUpcomingService> services, MemberObject memberObject, Context context) {
        Pair<String, Date> iptp = getNextIPTP(memberObject);
        if (iptp != null) {
            BaseUpcomingService iptpService = new BaseUpcomingService();
            iptpService.setServiceName(MessageFormat.format(context.getString(R.string.iptp_sp_dose), iptp.getLeft()));
            iptpService.setServiceDate(iptp.getRight());
            services.add(iptpService);
        }
    }

    private Triple<DateTime, VaccineRepo.Vaccine, String> getNextTT(MemberObject memberObject) {
        VaccineTaskModel vaccineTaskModel = null;

        DateTime lastMenstrualPeriod = DateTimeFormat.forPattern("dd-MM-yyyy").parseDateTime(memberObject.getLastMenstrualPeriod());
        int ga = Days.daysBetween(lastMenstrualPeriod, new DateTime()).getDays() / 7;

        if (ga >= 13) {
            vaccineTaskModel = VaccineScheduleUtil.getWomanVaccine(memberObject.getBaseEntityId(), lastMenstrualPeriod, new ArrayList<VaccineWrapper>());
        }

        if (vaccineTaskModel == null || vaccineTaskModel.getScheduleList().size() < 1) {
            return null;
        }
        // compute the due date

        return VaccineScheduleUtil.getIndividualVaccine(vaccineTaskModel, "TT");
    }

    private Pair<String, Date> getNextIPTP(MemberObject memberObject) {
        DateTime lmp = DateTimeFormat.forPattern("dd-MM-yyyy").parseDateTime(memberObject.getLastMenstrualPeriod());
        Map<String, ServiceWrapper> serviceWrapperMap = RecurringServiceUtil.getRecurringServices(memberObject.getBaseEntityId(), lmp, CoreConstants.SERVICE_GROUPS.WOMAN);
        ServiceWrapper serviceWrapper = serviceWrapperMap.get("IPTp-SP");


        if (serviceWrapper == null) {
            return null;
        }

        String iteration = serviceWrapper.getName().substring(serviceWrapper.getName().length() - 1);
        return Pair.of(iteration, serviceWrapper.getVaccineDate().toDate());
    }
}