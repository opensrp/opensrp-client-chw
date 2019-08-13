package org.smartregister.chw.domain;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PNCHealthFacilityVisitSummary {
    private Date deliveryDate;
    private Date lastVisitDate;
    private int previousVisitCount = 0;

    public PNCHealthFacilityVisitSummary(String deliveryDate, String lastVisitDate, String previousVisitCount) throws ParseException {

        if (StringUtils.isNotBlank(deliveryDate))
            this.deliveryDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(deliveryDate);

        if (StringUtils.isNotBlank(lastVisitDate))
            this.lastVisitDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(lastVisitDate);

        if (StringUtils.isNotBlank(previousVisitCount))
            this.previousVisitCount = Integer.parseInt(previousVisitCount);
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public Date getLastVisitDate() {
        return lastVisitDate;
    }

    public int getPreviousVisitCount() {
        return previousVisitCount;
    }

    public int getCurrentVisitCount() {
        return previousVisitCount + 1;
    }
}
