package org.mifos.clientportfolio.loan.ui.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.application.servicefacade.GroupLoanAccountServiceFacade;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.clientportfolio.loan.ui.BackdatedPaymentable;
import org.mifos.clientportfolio.loan.ui.LoanAccountFormBean;
import org.mifos.dto.domain.FeeDto;
import org.mifos.dto.domain.LoanCreationInstallmentDto;
import org.mifos.dto.domain.PenaltyDto;
import org.mifos.dto.screen.LoanCreationLoanDetailsDto;
import org.mifos.dto.screen.LoanScheduleDto;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("PMD")
public class LoanControllerHelper {

    private final LoanAccountServiceFacade loanAccountServiceFacade;
    private final GroupLoanAccountServiceFacade groupLoanAccountServiceFacade;

    @Autowired
    public LoanControllerHelper(LoanAccountServiceFacade loanAccountServiceFacade,
            GroupLoanAccountServiceFacade groupLoanAccountServiceFacade) {
        this.loanAccountServiceFacade = loanAccountServiceFacade;
        this.groupLoanAccountServiceFacade = groupLoanAccountServiceFacade;
    }
    
    public void populateFormBeanFromDto(int customerId, int productId, LoanAccountFormBean formBean,
            BackdatedPaymentable loanScheduleFormBean, LocalDate disbursementDate, LoanScheduleDto loanSchedule,
            boolean resetActualPaymentDatesAndAmountsForRedoLoan) {
        List<DateTime> installments = new ArrayList<DateTime>();
        List<DateTime> actualPaymentDates = new ArrayList<DateTime>();
        List<Number> installmentAmounts = new ArrayList<Number>();
        List<Number> actualPaymentAmounts = new ArrayList<Number>();
        List<Short> actualPaymentTypes = new ArrayList<Short>();

        BigDecimal totalLoanInterest = BigDecimal.ZERO;
        BigDecimal totalLoanFees = BigDecimal.ZERO;
        for (LoanCreationInstallmentDto installment : loanSchedule.getInstallments()) {

            totalLoanInterest = totalLoanInterest.add(BigDecimal.valueOf(installment.getInterest()));
            totalLoanFees = totalLoanFees.add(BigDecimal.valueOf(installment.getFees()));

            installments.add(new DateTime(installment.getDueDate()));
            actualPaymentDates.add(new DateTime(installment.getDueDate()));
            installmentAmounts.add(installment.getTotal());
            if (new LocalDate(installment.getDueDate()).isBefore(new LocalDate().plusDays(1))) {
                actualPaymentAmounts.add(installment.getTotal());
            } else {
                actualPaymentAmounts.add(Double.valueOf("0.0"));
            }
            actualPaymentTypes.add(null);
        }
        loanScheduleFormBean.setInstallments(installments);
        loanScheduleFormBean.setVariableInstallments(loanSchedule.getInstallments());
        loanScheduleFormBean.setInstallmentAmounts(installmentAmounts);
        if (resetActualPaymentDatesAndAmountsForRedoLoan) {
            loanScheduleFormBean.setActualPaymentDates(actualPaymentDates);
            loanScheduleFormBean.setActualPaymentAmounts(actualPaymentAmounts);
            loanScheduleFormBean.setActualPaymentTypes(actualPaymentTypes);
        }

        loanScheduleFormBean.setLoanPrincipal(BigDecimal.valueOf(formBean.getAmount().doubleValue()));
        loanScheduleFormBean.setTotalLoanInterest(totalLoanInterest);
        loanScheduleFormBean.setTotalLoanFees(totalLoanFees);
        loanScheduleFormBean.setRepaymentInstallments(loanSchedule.getInstallments());
        if (disbursementDate != null) {
            loanScheduleFormBean.setDisbursementDate(disbursementDate.toDateMidnight().toDate());
        }

        // variable installments related
        loanScheduleFormBean.setVariableInstallmentsAllowed(formBean.isVariableInstallmentsAllowed());
        if (loanScheduleFormBean.isVariableInstallmentsAllowed()) {
            loanScheduleFormBean.setMinGapInDays(formBean.getMinGapInDays());
            loanScheduleFormBean.setMaxGapInDays(formBean.getMaxGapInDays());
            loanScheduleFormBean.setMinInstallmentAmount(formBean.getMinInstallmentAmount());

            loanScheduleFormBean.setCustomerId(formBean.getCustomerId());
            loanScheduleFormBean.setLoanAccountFormBean(formBean);
        }

        List<FeeDto> applicableFees = new ArrayList<FeeDto>();
        LoanCreationLoanDetailsDto dto = this.loanAccountServiceFacade.retrieveLoanDetailsForLoanAccountCreation(
                customerId, Integer.valueOf(productId).shortValue(), formBean.isRedoLoanAccount());
        int feeIndex = 0;
        for (Boolean defaultFeeSelectedForRemoval : formBean.getDefaultFeeSelected()) {
            if (defaultFeeSelectedForRemoval == null || !defaultFeeSelectedForRemoval) {
                Integer feeId = formBean.getDefaultFeeId()[feeIndex].intValue();
                BigDecimal amountOrRate = BigDecimal.valueOf(formBean.getDefaultFeeAmountOrRate()[feeIndex]
                        .doubleValue());
                applicableFees.add(findFeeById(dto.getDefaultFees(), feeId, amountOrRate));
            }
            feeIndex++;
        }

        List<PenaltyDto> applicablePenalties = new ArrayList<PenaltyDto>();
        int penaltyIndex = 0;
        for (Boolean defaultPenaltySelectedForRemoval : formBean.getDefaultPenaltySelected()) {
            if (defaultPenaltySelectedForRemoval == null || !defaultPenaltySelectedForRemoval) {
                Integer penaltyId = formBean.getDefaultPenaltyId()[penaltyIndex].intValue();
                BigDecimal amountOrRate = BigDecimal.valueOf(formBean.getDefaultPenaltyAmountOrRate()[penaltyIndex]
                        .doubleValue());
                applicablePenalties.add(findPenaltyById(dto.getDefaultPenalties(), penaltyId, amountOrRate));
            }
            penaltyIndex++;
        }

        feeIndex = 0;
        Number[] additionalFeesSelected = formBean.getSelectedFeeId();
        if (additionalFeesSelected != null) {
            for (Number additionalFee : additionalFeesSelected) {
                if (additionalFee != null) {
                    BigDecimal amountOrRate = BigDecimal.valueOf(formBean.getSelectedFeeAmount()[feeIndex]
                            .doubleValue());
                    applicableFees.add(findFeeById(dto.getAdditionalFees(), additionalFee.intValue(), amountOrRate));
                }
                feeIndex++;
            }
        }

        loanScheduleFormBean.setApplicableFees(applicableFees);
        loanScheduleFormBean.setApplicablePenalties(applicablePenalties);
    }

    private List<Integer> getSelectedIndices(LoanAccountFormBean formBean) {
        int index = 0;
        List<Integer> indices = new ArrayList<Integer>();
        for (Boolean clientSelected : formBean.getClientSelectForGroup()) {
            if (clientSelected != null && clientSelected.booleanValue()) {
                indices.add(index);
            }
            index++;
        }
        return indices;
    }
    
    public List<Integer> getSelectedMemberIdsFromFormBean(LoanAccountFormBean formBean) {
        List<Integer> idsList = new ArrayList<Integer>();
        for (Integer index : getSelectedIndices(formBean)) {
            Integer memberId = groupLoanAccountServiceFacade.getMemberClientId(formBean.getClientGlobalId()[index]);
            idsList.add(memberId);
        }
        
        return idsList;
    }

    public List<String> getSelectedMemberGlobalIds(LoanAccountFormBean formBean) {
        List<String> idsList = new ArrayList<String>();
        for (Integer index : getSelectedIndices(formBean)) {
            idsList.add(formBean.getClientGlobalId()[index]);
        }
        
        return idsList;
    }
    
    public List<BigDecimal> getSelectedMemberAmounts(LoanAccountFormBean formBean) {
        List<BigDecimal> amountList = new ArrayList<BigDecimal>();
        for (Integer index : getSelectedIndices(formBean)) {
            amountList.add(BigDecimal.valueOf(formBean.getClientAmount()[index].doubleValue()));
        }
        
        return amountList;
    }
    
    public FeeDto findFeeById(final List<FeeDto> defaultFees, final Integer feeId, final BigDecimal amountOrRate) {
        FeeDto found = null;

        for (FeeDto feeDto : defaultFees) {
            if (Integer.valueOf(feeDto.getId()).equals(feeId)) {
                feeDto.setAmount(amountOrRate.toPlainString());
                if (feeDto.isRateBasedFee()) {
                    feeDto.setRate(amountOrRate.doubleValue());
                }
                found = feeDto;
            }
        }

        return found;
    }

    public PenaltyDto findPenaltyById(final List<PenaltyDto> defaultPenalties, final Integer penaltyId,
            final BigDecimal amountOrRate) {
        PenaltyDto found = null;

        for (PenaltyDto penaltyDto : defaultPenalties) {
            if (Integer.valueOf(penaltyDto.getPenaltyId()).equals(penaltyId)) {
                penaltyDto.setAmount(amountOrRate.toPlainString());
                if (penaltyDto.isRateBasedPenalty()) {
                    penaltyDto.setRate(amountOrRate.doubleValue());
                }
                found = penaltyDto;
            }
        }

        return found;
    }

}
