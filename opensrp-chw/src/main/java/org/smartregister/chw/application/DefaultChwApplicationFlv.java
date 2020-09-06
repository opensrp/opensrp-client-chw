package org.smartregister.chw.application;

public abstract class DefaultChwApplicationFlv implements ChwApplication.Flavor {
    @Override
    public boolean hasP2P() {
        return true;
    }

    @Override
    public boolean hasReferrals() {
        return false;
    }

    @Override
    public boolean hasANC() {
        return true;
    }

    @Override
    public boolean hasPNC() {
        return true;
    }

    @Override
    public boolean hasChildSickForm() {
        return false;
    }

    @Override
    public boolean hasFamilyPlanning() {
        return false;
    }

    @Override
    public boolean hasMalaria() {
        return false;
    }

    @Override
    public boolean hasWashCheck() {
        return true;
    }

    @Override
    public boolean hasRoutineVisit() {
        return false;
    }

    @Override
    public boolean hasServiceReport() {
        return false;
    }

    @Override
    public boolean hasStockUsageReport() {
        return false;
    }

    @Override
    public boolean hasPinLogin() {
        return false;
    }

    @Override
    public boolean hasReports() {
        return false;
    }

    @Override
    public boolean hasJobAids() {
        return true;
    }

    @Override
    public boolean hasQR() {
        return false;
    }

    @Override
    public boolean hasTasks() {
        return false;
    }

    @Override
    public boolean hasDefaultDueFilterForChildClient() {
        return false;
    }

    public boolean hasJobAidsVitaminAGraph() {
        return true;
    }

    @Override
    public boolean hasJobAidsDewormingGraph() {
        return true;
    }

    @Override
    public boolean hasChildrenMNPSupplementationGraph() {
        return true;
    }

    @Override
    public boolean hasJobAidsBreastfeedingGraph() {
        return true;
    }

    @Override
    public boolean hasJobAidsBirthCertificationGraph() {
        return true;
    }

    @Override
    public boolean hasSurname() {
        return true;
    }

    public boolean showMyCommunityActivityReport() {
        return false;
    }

    @Override
    public boolean launchChildClientsAtLogin() {
        return false;
    }

    @Override
    public boolean useThinkMd() {
        return false;
    }

    @Override
    public boolean hasFamilyLocationRow() {
        return false;
    }

    @Override
    public boolean usesPregnancyRiskProfileLayout() {
        return false;
    }

    @Override
    public boolean splitUpcomingServicesView() {
        return false;
    }
}
