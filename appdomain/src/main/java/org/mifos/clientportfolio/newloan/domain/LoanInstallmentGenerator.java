package org.mifos.clientportfolio.newloan.domain;

import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.util.helpers.InstallmentDate;

public interface LoanInstallmentGenerator {

	List<InstallmentDate> generate(LocalDate actualDisbursementDate, int numberOfInstallments, GraceType graceType, int graceDuration, Short officeId);

}
