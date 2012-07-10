package org.mifos.application.importexport.servicefacade;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.application.importexport.xls.XlsClientsImporter;
import org.mifos.application.importexport.xls.XlsLoansAccountImporter;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.clientportfolio.newloan.applicationservice.CreateLoanAccount;
import org.mifos.dto.domain.CreateAccountFeeDto;
import org.mifos.dto.domain.CreateAccountPenaltyDto;
import org.mifos.dto.domain.ImportedClientDetail;
import org.mifos.dto.domain.ImportedLoanDetail;
import org.mifos.dto.domain.ParsedLoansDto;
import org.mifos.dto.screen.LoanCreationLoanDetailsDto;
import org.mifos.dto.screen.LoanCreationResultDto;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.platform.questionnaire.domain.QuestionGroup;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public class ImportLoansSavingsFacadeWebTier implements
        ImportLoansSavingsFacade {
    private XlsLoansAccountImporter xlsLoansAccount;
    private PersonnelServiceFacade personelServiceFacade;
    private LoanAccountServiceFacade loanAccountServiceFacade;
    

    @Autowired
    public ImportLoansSavingsFacadeWebTier(XlsLoansAccountImporter xlsLoansAccount, PersonnelServiceFacade personelServiceFacade,
            LoanAccountServiceFacade loanAccountServiceFacade) {
        this.xlsLoansAccount=xlsLoansAccount;
        this.personelServiceFacade=personelServiceFacade;
        this.loanAccountServiceFacade=loanAccountServiceFacade;
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
            CreateLoanAccount cla=new CreateLoanAccount(detail.getCustomerId(), new Integer(detail.getPrdOfferingId()), 
                    new Integer(detail.getStatus()), detail.getLoanAmount(), lcldd.getMinLoanAmount(), lcldd.getMaxLoanAmount(), 
                    detail.getInterestRate().doubleValue(), new LocalDate(detail.getDisbursalDate().getTime()), null, detail.getNumberOfInstallments(),
                    lcldd.getMinNumberOfInstallments(), lcldd.getMaxNumberOfInstallments(),
                    detail.getGracePeriod(), detail.getSourceOfFundId(), detail.getLoanPurposeId(), 
                    detail.getCollateralTypeId(), detail.getCollateralNotes(), detail.getExternalId(), 
                    lcldd.isRepaymentIndependentOfMeetingEnabled(), null, defaultAccountFees, defaultPenalties);
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

}
