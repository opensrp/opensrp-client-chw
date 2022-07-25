package org.smartregister.chw.domain.mother_champion_report;

import org.smartregister.chw.domain.ReportObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MotherChampionReportObject extends ReportObject {

    private final List<String> indicatorCodes = new ArrayList<>();

    public MotherChampionReportObject(Date reportDate) {
        super(reportDate);

    }


}
