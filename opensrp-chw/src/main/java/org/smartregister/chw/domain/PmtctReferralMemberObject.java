package org.smartregister.chw.domain;

import org.smartregister.chw.pmtct.domain.MemberObject;

import java.util.Date;

public class PmtctReferralMemberObject extends MemberObject {
    private Date pmtctCommunityReferralDate;
    private Date lastFacilityVisitDate;
    private String reasonsForIssuingCommunityFollowupReferral;
    private String comments;

    public String getReasonsForIssuingCommunityFollowupReferral() {
        return reasonsForIssuingCommunityFollowupReferral;
    }

    public void setReasonsForIssuingCommunityFollowupReferral(String reasonsForIssuingCommunityFollowupReferral) {
        this.reasonsForIssuingCommunityFollowupReferral = reasonsForIssuingCommunityFollowupReferral;
    }

    public Date getPmtctCommunityReferralDate() {
        return pmtctCommunityReferralDate;
    }

    public void setPmtctCommunityReferralDate(Date pmtctCommunityReferralDate) {
        this.pmtctCommunityReferralDate = pmtctCommunityReferralDate;
    }

    public Date getLastFacilityVisitDate() {
        return lastFacilityVisitDate;
    }

    public void setLastFacilityVisitDate(Date lastFacilityVisitDate) {
        this.lastFacilityVisitDate = lastFacilityVisitDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
