package org.smartregister.chw.application;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.dao.ChwChildDao;

public class ChwApplicationFlv extends DefaultChwApplicationFlv {

    @Override
    public boolean hasANC() {
        return false;
    }

    @Override
    public boolean syncUsingPost() {
        return true;
    }

    @Override
    public boolean hasPNC() {
        return false;
    }

    @Override
    public boolean hasChildSickForm() {
        return false;
    }

    @Override
    public boolean hasWashCheck() {
        return false;
    }

    @Override
    public boolean hasRoutineVisit() {
        return false;
    }

    @Override
    public boolean hasPinLogin() {
        return true;
    }

    @Override
    public boolean hasReports() {
        return true;
    }

    @Override
    public boolean hasJobAids() {
        return false;
    }

    @Override
    public boolean hasDefaultDueFilterForChildClient() {
        return false;
    }

    @Override
    public boolean hasJobAidsVitaminAGraph() {
        return false;
    }

    @Override
    public boolean hasJobAidsDewormingGraph() {
        return false;
    }

    @Override
    public boolean hasChildrenMNPSupplementationGraph() {
        return false;
    }

    @Override
    public boolean hasJobAidsBreastfeedingGraph() {
        return false;
    }

    @Override
    public boolean hasJobAidsBirthCertificationGraph() {
        return false;
    }

    @Override
    public boolean hasSurname() {
        return false;
    }

    @Override
    public boolean showMyCommunityActivityReport() {
        return true;
    }

    @Override
    public boolean showChildrenUnder5() {
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
    public boolean splitUpcomingServicesView() {
        return true;
    }

    @Override
    public boolean showNoDueVaccineView() {
        return true;
    }

    @Override
    public boolean prioritizeChildNameOnChildRegister() {
        return true;
    }

    @Override
    public boolean showChildrenUnderFiveAndGirlsAgeNineToEleven() {
        return true;
    }

    @Override
    public boolean dueVaccinesFilterInChildRegister() {
        return true;
    }

    @Override
    public boolean includeCurrentChild() {
        return false;
    }

    @Override
    public boolean saveOnSubmission() {
        return true;
    }

    @Override
    public boolean relaxVisitDateRestrictions() {
        return true;
    }

    @Override
    public boolean showLastNameOnChildProfile() {
        return true;
    }

    @Override
    public boolean showChildrenAboveTwoDueStatus() {
        return false;
    }

    @Override
    public boolean showFamilyServicesScheduleWithChildrenAboveTwo() {
        return false;
    }

    @Override
    public boolean hasForeignData() {
        return true;
    }

    @Override
    public boolean showIconsForChildrenUnderTwoAndGirlsAgeNineToEleven() {
        return true;
    }

    @Override
    public boolean useAllChildrenTitle() {
        return true;
    }

    @Override
    public boolean showBottomNavigation() {
        return false;
    }

    @Override
    public boolean disableTitleClickGoBack() {
        return true;
    }

    @Override
    public boolean showReportsDescription() {
        return true;
    }

    @Override
    public boolean showDueFilterToggle() {
        return false;
    }

    @Override
    public boolean showReportsDivider() {
        return true;
    }

    @Override
    public boolean hideChildRegisterPreviousNextIcons() {
        return true;
    }

    @Override
    public boolean hideFamilyRegisterPreviousNextIcons() {
        return true;
    }

    @Override
    public boolean showFamilyRegisterNextInToolbar() {
        return true;
    }

    @Override
    public boolean onFamilySaveGoToProfile() {
        return true;
    }

    @Override
    public boolean onChildProfileHomeGoToChildRegister() {
        return false;
    }

    @Override
    public boolean greyOutFormActionsIfInvalid() {
        return true;
    }

    @Override
    public boolean checkExtraForDueInFamily() {
        return true;
    }

    @Override
    public boolean hideCaregiverAndFamilyHeadWhenOnlyOneAdult() {
        return true;
    }

    @Override
    public boolean showsPhysicallyDisabledView() {
        return false;
    }

    @Override
    public boolean vaccinesDefaultChecked() {
        return false;
    }

    @Override
    public boolean checkDueStatusFromUpcomingServices() {
        return true;
    }

    @Override
    public int immunizationCeilingMonths(MemberObject memberObject) {
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
}

