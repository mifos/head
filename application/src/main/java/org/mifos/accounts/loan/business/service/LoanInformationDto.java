package org.mifos.accounts.loan.business.service;

import java.util.Date;
import java.util.Set;

import org.mifos.accounts.business.AccountFlagMapping;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.framework.business.service.DataTransferObject;

public class LoanInformationDto implements DataTransferObject {

    private final String prdOfferingName;
    private final String globalAccountNum;
    private final AccountStateEntity accountState;
    private final Set<AccountFlagMapping> accountFlags;
    private final Date disbursementDate;
    private final boolean redone;
    private final Integer businessActivityId;

    public LoanInformationDto(final String prdOfferingName, final String globalAccountNum, final AccountStateEntity accountState,
            final Set<AccountFlagMapping> accountFlags, final Date disbursementDate, final boolean redone, final Integer businessActivityId) {

        super();
        this.prdOfferingName = prdOfferingName;
        this.globalAccountNum = globalAccountNum;
        this.accountState = accountState;
        this.accountFlags = accountFlags;
        this.disbursementDate = disbursementDate;
        this.redone = redone;
        this.businessActivityId = businessActivityId;
    }

    public String getPrdOfferingName() {
        return this.prdOfferingName;
    }

    public String getGlobalAccountNum() {
        return this.globalAccountNum;
    }

    public AccountStateEntity getAccountState() {
        return this.accountState;
    }

    public Set<AccountFlagMapping> getAccountFlags() {
        return this.accountFlags;
    }

    public Date getDisbursementDate() {
        return this.disbursementDate;
    }

    public boolean isRedone() {
        return this.redone;
    }

    public Integer getBusinessActivityId() {
        return this.businessActivityId;
    }
}
