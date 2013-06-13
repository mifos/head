package org.mifos.application.importexport.servicefacade;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.application.importexport.xls.XlsLoansAccountImporter;
import org.mifos.application.importexport.xls.XlsSavingsAccountImporter;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.clientportfolio.loan.service.DailySchedule;
import org.mifos.clientportfolio.loan.service.MonthlyOnDayOfMonthSchedule;
import org.mifos.clientportfolio.loan.service.MonthlyOnWeekOfMonthSchedule;
import org.mifos.clientportfolio.loan.service.RecurringSchedule;
import org.mifos.clientportfolio.loan.service.WeeklySchedule;
import org.mifos.clientportfolio.newloan.applicationservice.CreateLoanAccount;
import org.mifos.dto.domain.CreateAccountFeeDto;
import org.mifos.dto.domain.CreateAccountPenaltyDto;
import org.mifos.dto.domain.ImportedLoanDetail;
import org.mifos.dto.domain.ImportedSavingDetail;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.OpeningBalanceSavingsAccount;
import org.mifos.dto.domain.ParsedLoansDto;
import org.mifos.dto.domain.ParsedSavingsDto;
import org.mifos.dto.screen.LoanCreationLoanDetailsDto;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.springframework.beans.factory.annotation.Autowired;

public class ImportLoansSavingsFacadeWebTier implements
        ImportLoansSavingsFacade {
    private XlsLoansAccountImporter xlsLoansAccount;
    private PersonnelServiceFacade personelServiceFacade;
    private LoanAccountServiceFacade loanAccountServiceFacade;
    private XlsSavingsAccountImporter xlsSavingsAccount;
    private SavingsServiceFacade savingServiceFacade;
    

    @Autowired
    public ImportLoansSavingsFacadeWebTier(XlsLoansAccountImporter xlsLoansAccount, PersonnelServiceFacade personelServiceFacade,
            LoanAccountServiceFacade loanAccountServiceFacade, XlsSavingsAccountImporter xlsSavingsAccount, SavingsServiceFacade savingServiceFacade) {
        this.xlsLoansAccount=xlsLoansAccount;
        this.personelServiceFacade=personelServiceFacade;
        this.loanAccountServiceFacade=loanAccountServiceFacade;
        this.xlsSavingsAccount = xlsSavingsAccount;
        this.savingServiceFacade = savingServiceFacade;
    }

    @Override
    public ParsedLoansDto parseImportLoans(InputStream stream) {
        xlsLoansAccount.setLocale(personelServiceFacade.getUserPreferredLocale());
        ParsedLoansDto plDto=xlsLoansAccount.parse(stream);
        return plDto;
    }

    @Override
    public ParsedLoansDto saveLoans(ParsedLoansDto parsedLoansDto) {
        List<CreateAccountFeeDto> defaultAccountFees=new ArrayList<CreateAccountFeeDto>();
        List<QuestionGroupDetail> questionGroupDetails=new ArrayList<QuestionGroupDetail>();
        List<CreateAccountPenaltyDto> defaultPenalties=new ArrayList<CreateAccountPenaltyDto>();
        for (ImportedLoanDetail detail : parsedLoansDto.getSuccessfullyParsedRows()) {
            LoanCreationLoanDetailsDto lcldd=loanAccountServiceFacade.retrieveLoanDetailsForLoanAccountCreation(
                    detail.getCustomerId(), detail.getPrdOfferingId(), false);
            RecurringSchedule recurringSchedule = null;
        	MeetingDto meetingDto = lcldd.getCustomerMeetingDetail();
            int meetingEvery = (lcldd.isRepaymentIndependentOfMeetingEnabled()) ? lcldd.getLoanOfferingMeetingDetail()
                    .getMeetingDetailsDto().getEvery() : meetingDto.getMeetingDetailsDto().getEvery();
            int loanRecurrenceTypeId = (lcldd.isRepaymentIndependentOfMeetingEnabled()) ? lcldd
                    .getLoanOfferingMeetingDetail().getMeetingDetailsDto().getRecurrenceTypeId() : meetingDto
                    .getMeetingDetailsDto().getRecurrenceTypeId();

            if (loanRecurrenceTypeId == 1) {
            	recurringSchedule = new WeeklySchedule(meetingEvery, meetingDto.getMeetingDetailsDto()
                        .getRecurrenceDetails().getDayOfWeek());
            } else if (loanRecurrenceTypeId == 2) {
            	if (meetingDto.getMeetingDetailsDto().getRecurrenceDetails().getWeekOfMonth().equals(0)) {
                    recurringSchedule = new MonthlyOnDayOfMonthSchedule(meetingEvery, meetingDto.getMeetingDetailsDto()
                            .getRecurrenceDetails().getDayNumber());
                } else {
                    recurringSchedule = new MonthlyOnWeekOfMonthSchedule(meetingEvery, meetingDto
                            .getMeetingDetailsDto().getRecurrenceDetails().getWeekOfMonth(), meetingDto
                            .getMeetingDetailsDto().getRecurrenceDetails().getDayOfWeek());
                }
            } else if (loanRecurrenceTypeId == 3) {
                recurringSchedule = new DailySchedule(meetingEvery);
            }
            
            CreateLoanAccount cla=new CreateLoanAccount(detail.getCustomerId(), new Integer(detail.getPrdOfferingId()), 
                    new Integer(detail.getStatus()), detail.getLoanAmount(), lcldd.getMinLoanAmount(), lcldd.getMaxLoanAmount(), 
                    detail.getInterestRate().doubleValue(), new LocalDate(detail.getDisbursalDate().getTime()), null, detail.getNumberOfInstallments(),
                    lcldd.getMinNumberOfInstallments(), lcldd.getMaxNumberOfInstallments(),
                    detail.getGracePeriod(), detail.getSourceOfFundId(), detail.getLoanPurposeId(), 
                    detail.getCollateralTypeId(), detail.getCollateralNotes(), detail.getExternalId(), 
                    lcldd.isRepaymentIndependentOfMeetingEnabled(), recurringSchedule, defaultAccountFees, defaultPenalties);
            cla.setPredefinedAccountNumber(detail.getAccountNumber());
            cla.setFlagId(detail.getFlag());
            this.loanAccountServiceFacade.createLoan(cla, questionGroupDetails, null);
        }
        return null;
    }

    @Override
    public ParsedLoansDto createLoansDtoFromSingleError(String error) {
        List<ImportedLoanDetail> parsedRows = new ArrayList<ImportedLoanDetail>();
        List<String> parseErrors = new ArrayList<String>();
        parseErrors.add(error);
        return new ParsedLoansDto(parseErrors, parsedRows);
    }

	@Override
	public ParsedSavingsDto parseImportSavings(InputStream stream) {
		xlsSavingsAccount.setLocale(personelServiceFacade.getUserPreferredLocale());
        ParsedSavingsDto savingDto=xlsSavingsAccount.parse(stream);
        return savingDto;
	}

	@Override
	public ParsedSavingsDto saveSavings(ParsedSavingsDto parsedSavingsDto) {
	    for(ImportedSavingDetail detail : parsedSavingsDto.getSuccessfullyParsedRows()){
	        OpeningBalanceSavingsAccount openingBalanceSavingsAccount = 
	                new OpeningBalanceSavingsAccount(detail.getCustomerId(), 
	                        detail.getPrdOfferingId(), detail.getStatus(), detail.getSavingsAmount().toString(), 
	                        detail.getSavingsBalance().toString(), detail.getDate(), detail.getAccountNumber());
            
        
        String savingsId = savingServiceFacade.createSavingsAccount(openingBalanceSavingsAccount);
	    }
        return null;
	}

	@Override
	public ParsedSavingsDto createSavingsDtoFromSingleError(String error) {
	    List<ImportedSavingDetail> parsedRows = new ArrayList<ImportedSavingDetail>();
        List<String> parseErrors = new ArrayList<String>();
        parseErrors.add(error);
        return new ParsedSavingsDto(parseErrors, parsedRows);
	}

}
