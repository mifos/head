package org.mifos.clientportfolio.loan.ui;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.mifos.dto.domain.FeeDto;
import org.mifos.dto.domain.LoanCreationInstallmentDto;
import org.mifos.dto.domain.PenaltyDto;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2", "DLS_DEAD_LOCAL_STORE"}, justification="should disable at filter level and also for pmd - not important for us")
public interface BackdatedPaymentable extends Serializable {

    List<DateTime> getInstallments();
    
    void setInstallments(List<DateTime> installments);

    List<Number> getInstallmentAmounts();
    
    void setInstallmentAmounts(List<Number> installmentAmounts);

    void setActualPaymentDates(List<DateTime> actualPaymentDates);

    void setActualPaymentAmounts(List<Number> actualPaymentAmounts);

    void setLoanPrincipal(BigDecimal valueOf);

    void setTotalLoanInterest(BigDecimal totalLoanInterest);

    void setTotalLoanFees(BigDecimal totalLoanFees);

    void setRepaymentInstallments(List<LoanCreationInstallmentDto> installments);

    void setDisbursementDate(Date date);

    void setVariableInstallmentsAllowed(boolean variableInstallmentsAllowed);

    boolean isVariableInstallmentsAllowed();

    void setMinGapInDays(Integer minGapInDays);

    void setMaxGapInDays(Integer maxGapInDays);

    void setMinInstallmentAmount(BigDecimal minInstallmentAmount);

    void setCustomerId(Integer customerId);

    void setVariableInstallments(List<LoanCreationInstallmentDto> installments);

    void setApplicableFees(List<FeeDto> applicableFees);
    
    void setApplicablePenalties(List<PenaltyDto> applicablePenalties);

    void setLoanAccountFormBean(LoanAccountFormBean loanAccountFormBean);
}