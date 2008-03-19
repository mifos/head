package org.mifos.application.branchreport;

import org.mifos.framework.hibernate.IntEnumUserType;

public class LoanArrearsAgingPeriodType extends
		IntEnumUserType<LoanArrearsAgingPeriod> {
	public LoanArrearsAgingPeriodType() {
		super(LoanArrearsAgingPeriod.class, LoanArrearsAgingPeriod.values());
	}
}
