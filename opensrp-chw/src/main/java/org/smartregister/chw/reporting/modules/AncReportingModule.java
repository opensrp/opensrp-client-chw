package org.smartregister.chw.reporting.modules;

import android.view.ViewGroup;

import org.smartregister.reporting.domain.IndicatorTally;

import java.util.List;
import java.util.Map;

public class AncReportingModule implements ReportingModule {

    private static final String COUNT_WRA = "anc_report_indicator_1";
    private static final String COUNT_PREGNANT_WOMEN = "anc_report_indicator_2";
    private static final String COUNT_WOMEN_DUE_HOME_VISIT = "anc_report_indicator_3_1";
    private static final String COUNT_WOMEN_OVERDUE_HOME_VISIT = "anc_report_indicator_3_2";
    private static final String COUNT_WOMEN_DUE_HEALTH_FACILITY_VISIT = "anc_report_indicator_4_1";
    private static final String COUNT_WOMEN_OVERDUE_HEALTH_FACILITY_VISIT = "anc_report_indicator_4_2";
    private static final String COUNT_WOMEN_TESTED_HIV = "anc_report_indicator_5_1";
    private static final String COUNT_WOMEN_NOT_TESTED_HIV = "anc_report_indicator_5_2";
    private static final String COUNT_WOMEN_TESTED_SYPHILIS = "anc_report_indicator_6_1";
    private static final String COUNT_WOMEN_NOT_TESTED_SYPHILIS = "anc_report_indicator_6_2";
    private static final String COUNT_WOMEN_DUE_TT_IMMUNIZATION = "anc_report_indicator_7_1";
    private static final String COUNT_WOMEN_OVERDUE_TT_IMMUNIZATION = "anc_report_indicator_7_2";
    private static final String COUNT_WOMEN_DUE_IPTPSP = "anc_report_indicator_8_1";
    private static final String COUNT_WOMEN_OVERDUE_IPTPSP = "anc_report_indicator_8_2";

    private List<Map<String, IndicatorTally>> indicatorTallies;

    @Override
    public void generateReport(ViewGroup mainLayout) {

    }

    public List<Map<String, IndicatorTally>> getIndicatorTallies() {
        return indicatorTallies;
    }

    public void setIndicatorTallies(List<Map<String, IndicatorTally>> indicatorTallies) {
        this.indicatorTallies = indicatorTallies;
    }
}
