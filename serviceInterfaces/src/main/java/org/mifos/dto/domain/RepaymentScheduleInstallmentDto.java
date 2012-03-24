package org.mifos.dto.domain;

import java.io.Serializable;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "SE_NO_SERIALVERSIONID", justification = "should disable at filter level and also for pmd - not important for us")
public class RepaymentScheduleInstallmentDto implements Serializable {
    
	private final Integer installment;

    private final String principal;

    private final String interest;

    private final String fees;

    private final String miscFees;
    
    private final String feesWithMiscFee;

    private final String miscPenalty;

    private final String total;

    private final String dueDate;

	public RepaymentScheduleInstallmentDto(Integer installment,
			String principal, String interest, String fees, String miscFees,
			String feesWithMiscFee, String miscPenalty, String total,
			String dueDate) {
		super();
		this.installment = installment;
		this.principal = principal;
		this.interest = interest;
		this.fees = fees;
		this.miscFees = miscFees;
		this.feesWithMiscFee = feesWithMiscFee;
		this.miscPenalty = miscPenalty;
		this.total = total;
		this.dueDate = dueDate;
	}

	public Integer getInstallment() {
		return installment;
	}

	public String getPrincipal() {
		return principal;
	}

	public String getInterest() {
		return interest;
	}

	public String getFees() {
		return fees;
	}

	public String getMiscFees() {
		return miscFees;
	}

	public String getMiscPenalty() {
		return miscPenalty;
	}

	public String getTotal() {
		return total;
	}

	public String getDueDate() {
		return dueDate;
	}

	public String getFeesWithMiscFee() {
		return feesWithMiscFee;
	}
    
}
