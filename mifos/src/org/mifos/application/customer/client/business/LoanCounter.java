package org.mifos.application.customer.client.business;

import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.PersistentObject;

public class LoanCounter extends PersistentObject {

	private final Integer loanCounterId;

	private final ClientPerformanceHistoryEntity clientPerfHistory;

	private Integer loanCycleCounter = 0;

	private final LoanOfferingBO loanOffering;

	protected LoanCounter() {
		this.loanCounterId = null;
		this.clientPerfHistory = null;
		this.loanOffering = null;
		this.loanCycleCounter = 0;
	}

	public LoanCounter(ClientPerformanceHistoryEntity clientPerfHistory,
			LoanOfferingBO loanOffering, YesNoFlag counterFlag) {
		this.loanCounterId = null;
		this.clientPerfHistory = clientPerfHistory;
		this.loanOffering = loanOffering;
		if (counterFlag.equals(YesNoFlag.YES))
			this.loanCycleCounter++;
		else
			this.loanCycleCounter--;
	}

	public ClientPerformanceHistoryEntity getClientPerfHistory() {
		return clientPerfHistory;
	}

	public LoanOfferingBO getLoanOffering() {
		return loanOffering;
	}

	public Integer getLoanCounterId() {
		return loanCounterId;
	}

	public Integer getLoanCycleCounter() {
		return loanCycleCounter;
	}

	void setLoanCycleCounter(Integer loanCycleCounter) {
		this.loanCycleCounter = loanCycleCounter;
	}

	void updateLoanCounter(YesNoFlag counterFlag) {
		if (counterFlag.equals(YesNoFlag.YES))
			this.loanCycleCounter++;
		else
			this.loanCycleCounter--;
	}
}
