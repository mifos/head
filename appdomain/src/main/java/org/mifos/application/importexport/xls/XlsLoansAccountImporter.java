package org.mifos.application.importexport.xls;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.fund.servicefacade.FundDto;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.clientportfolio.loan.service.RecurringSchedule;
import org.mifos.clientportfolio.newloan.applicationservice.CreateLoanAccount;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.AccountStatusDto;
import org.mifos.dto.domain.CreateAccountFeeDto;
import org.mifos.dto.domain.CreateAccountPenaltyDto;
import org.mifos.dto.domain.ImportedLoanDetail;
import org.mifos.dto.domain.MeetingRecurrenceDto;
import org.mifos.dto.domain.ParsedLoansDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.dto.screen.LoanCreationLoanDetailsDto;
import org.mifos.platform.accounting.dao.AccountingDao;
import org.mifos.platform.validations.ErrorEntry;
import org.mifos.platform.validations.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import freemarker.template.utility.StringUtil;

/**
 * Parser for xls files with loan accounts' data. Use for importing new accounts for existing products
 * partially implemented) or editing existing loan accounts (not yet implemented)
 * @author lgadomski
 */
public class XlsLoansAccountImporter implements MessageSourceAware {

    private static final String NESTED_LEVEL_IND = "--> ";
    private MessageSource messageSource;
    private LoanDao loanDao;
    private SavingsDao savingsDao;
    private LoanProductDao loanProductDao;
    private CustomerDao customerDao;
    private Locale locale;
    /**
     * If true it means we are trying to edit existing account
     */
    private boolean isEdit;
    private LoanAccountServiceFacade loanAccountServiceFacade;
    /**
     * Main constructor, all parameters should be autowired by Spring.
     * @param loanDao
     * @param loanProductDao
     * @param customerDao
     * @param savingsDao
     * @param loanAccountServiceFacade
     */
    @Autowired
    public XlsLoansAccountImporter(LoanDao loanDao,
            LoanProductDao loanProductDao, CustomerDao customerDao,
            SavingsDao savingsDao,
            LoanAccountServiceFacade loanAccountServiceFacade) {
        super();
        this.loanDao = loanDao;
        this.loanProductDao = loanProductDao;
        this.customerDao = customerDao;
        this.isEdit = false;
        this.savingsDao = savingsDao;
        this.loanAccountServiceFacade = loanAccountServiceFacade;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    /**
     * Parse input stream.
     * @param is input stream containing loan accounts' data
     * @return object containing successfully parsed rows and rows with errors
     */
    public ParsedLoansDto parse(InputStream is) {
        //prepare objects: result, lists for rows
        ParsedLoansDto result = null;
        List<String> errors = new ArrayList<String>();
        List<String> newAccountsNumbers = new ArrayList<String>(); //temporary list for new accounts numbers, currently not used
        List<ImportedLoanDetail> parsedLoanDetails = new ArrayList<ImportedLoanDetail>();
        // open spreadsheet
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(is);
            HSSFSheet sheet = workbook.getSheetAt(0);
            // check first row of data
            HSSFRow row = sheet
                    .getRow(XlsLoansImportTemplateConstants.FIRST_ROW_WITH_DATA
                            .getValue());
            if (row == null) {
                throw new XlsParsingException(getMessage(XlsMessageConstants.NOT_ENOUGH_INPUT_ROW,null));
            }
            
            Iterator<Row> iterator = sheet.rowIterator();
            // skip to rows with data
            while (iterator.hasNext()
                    && (iterator.next().getRowNum() < XlsLoansImportTemplateConstants.FIRST_ROW_WITH_DATA
                            .getValue() - 1));
            // parse loan account's data
            while (iterator.hasNext()) {
                row = (HSSFRow) iterator.next();
                List<Object> params=new ArrayList<Object>();
                // setup the first cell
                XlsLoansImportTemplateConstants currentCell = XlsLoansImportTemplateConstants.ACCOUNT_NUMBER;
                try {
                    // account number
                    String accountNumber = getCellStringValue(row, currentCell);
                    // TODO: rewrite this account number validation to more universal and extract method
                    if (StringUtils.isBlank(accountNumber) && isEdit) {//editing and account number is missing
                        throw new XlsParsingException(getCellError(XlsMessageConstants.MISSING_ACCOUNT_NUMBER,row,currentCell.getValue(),null));
                    }
                    //TODO: validation if account for edition exists
                    else if(StringUtils.isBlank(accountNumber) && !isEdit){ //not editing, adding with predefined account number and account number is not good
                       accountNumber=null;
                    }
                    //account number is good for creating new account with predefined account number...
                    else if(!StringUtils.isBlank(accountNumber) && !isEdit){
                        //...but it's duplicate
                        if (!validateAccountNumber(accountNumber,
                                newAccountsNumbers)) {
                            params.clear();
                            params.add(accountNumber);
                            throw new XlsParsingException(getCellError(XlsMessageConstants.DUPLICATE_GLOBAL_NUM_ERROR,row,currentCell.getValue(),params));
                        }
                    }//all good, account is either predefined from xls document or null and will be generated 
                    
                    //TODO: extract methods that can be shared between loans and savings
                    // customer global id
                    currentCell = XlsLoansImportTemplateConstants.CUSTOMER_GLOBAL_ID;
                    String customerGlobalId = getCellStringValue(row,currentCell);
                    CustomerBO customerBO = null;
                    customerBO = validateCustomerGlobalId(customerGlobalId);
                    if (customerBO==null) {
                        params.clear();
                        params.add(customerGlobalId);
                        throw new XlsParsingException(getCellError(XlsMessageConstants.CUSTOMER_NOT_FOUND,row,currentCell.getValue(),params));
                    }
                    // product name
                    currentCell = XlsLoansImportTemplateConstants.PRODUCT_NAME;
                    String productName = getCellStringValue(row, currentCell);
                    LoanOfferingBO loanOfferingBO = null;
                    loanOfferingBO = validateProductName(productName, customerBO,row,currentCell.getValue());
                    //TODO: add support for backdated payments
                    LoanCreationLoanDetailsDto lcldd = loanAccountServiceFacade
                            .retrieveLoanDetailsForLoanAccountCreation(
                                    customerBO.getCustomerId(),
                                    loanOfferingBO.getPrdOfferingId(), false);
                    // status name
                    currentCell = XlsLoansImportTemplateConstants.STATUS_NAME;
                    String statusName = getCellStringValue(row, currentCell);
                    XlsLoanSavingsAccountStatesConstants statusConstant = null;
                    statusConstant= validateStatusName(statusName, customerBO,this.isEdit,row,currentCell.getValue());
                    // status reason flag
                    currentCell = XlsLoansImportTemplateConstants.CANCEL_FlAG_REASON;
                    String cancelReason = getCellStringValue(row, currentCell);
                    XlsLoanSavingsFlagsConstants flagConstant = null;
                    flagConstant = validateStatusFlagReason(cancelReason, statusName,
                            AccountTypes.LOAN_ACCOUNT, row, currentCell.getValue());

                    // loan amount
                    currentCell = XlsLoansImportTemplateConstants.LOAN_AMOUNT;
                    BigDecimal loanAmount = getCellDecimalValue(row,
                            currentCell);
                    validateAmount(loanAmount, loanOfferingBO,
                            customerBO,lcldd,row,currentCell.getValue());
                    
                    // Interest rate
                    currentCell = XlsLoansImportTemplateConstants.INTEREST_RATE;
                    BigDecimal interestRate = getCellDecimalValue(row,
                            currentCell);
                    validateInterestRate(interestRate, loanOfferingBO,
                            customerBO,lcldd,row,currentCell.getValue());
    
                    // number of installments
                    currentCell=XlsLoansImportTemplateConstants.NO_OF_INSTALLMENTS;
                    Integer numberOfInstallments=getCellIntegerValue(row, currentCell);
                    validateNumberOfInstallments(numberOfInstallments,customerBO,loanOfferingBO,lcldd,row,currentCell.getValue());

                    // disbursal date
                    currentCell=XlsLoansImportTemplateConstants.DISBURLSAL_DATE;
                    Date disbursalDate=getCellDateValue(row, currentCell);
                    validateDisbursalDate(disbursalDate,customerBO,loanOfferingBO,currentCell.getValue(),row);

                    // grace period
                    currentCell=XlsLoansImportTemplateConstants.GRACE_PERIOD;
                    Integer gracePeriod=getCellIntegerValue(row, currentCell);
                    validateGracePeriod(gracePeriod,loanOfferingBO,customerBO,numberOfInstallments,row,currentCell.getValue());

                    // source of founds
                    currentCell=XlsLoansImportTemplateConstants.SOURCE_OF_FOUNDS;
                    List<FundDto> funds=lcldd.getFundDtos();
                    String sourceOfFund=getCellStringValue(row, currentCell);
                    Integer sourceOfFundId=null;
                    sourceOfFundId=validateSourceOfFund(sourceOfFund,funds,row,currentCell.getValue());
                    
                    // purpose
                    List<ValueListElement> purposes=lcldd.getLoanPurposes();
                    currentCell=XlsLoansImportTemplateConstants.PURPOSE;
                    String loanPurpose=getCellStringValue(row, currentCell);                    
                    Integer loanPurposeId=null;
                    loanPurposeId=validateLoanPurposeId(loanPurpose,purposes,row,currentCell.getValue());
                    // collateral type
                    currentCell=XlsLoansImportTemplateConstants.COLLATERAL_TYPE;
                    Integer collateralTypeId=null;
                    String collateralType=getCellStringValue(row, currentCell);
                    Map<String,String> collaterals=lcldd.getCollateralOptions();
                    collateralTypeId=validateCollateralType(collateralType,collaterals,row, currentCell.getValue());
                    // collateral notes
                    currentCell=XlsLoansImportTemplateConstants.COLLATERAL_NOTES;
                    String collateralNotes=getCellStringValue(row, currentCell);
                    collateralNotes=StringUtils.isBlank(collateralNotes) ? null : collateralNotes;
                    // external id
                    currentCell=XlsLoansImportTemplateConstants.EXTERNAL_ID;
                    String externalId=getCellStringValue(row, currentCell);
                    externalId=StringUtils.isBlank(externalId) ? null : externalId;

                    //all is good, so add accepted account number to temporary list...
                    //...will be used for editing/adding loans with predefined account numbers
                    if(accountNumber!=null){ 
                        newAccountsNumbers.add(accountNumber);
                    }
                    //create final objects
                    //TODO handle backdated payments

                    Short flagValue=flagConstant==null ? null : flagConstant.getFlag().getValue();
                    ImportedLoanDetail detail=new ImportedLoanDetail(accountNumber,customerBO.getCustomerId(),loanOfferingBO.getPrdOfferingId(),
                            statusConstant.getState().getValue(),flagValue,
                            loanAmount,interestRate,numberOfInstallments,disbursalDate,gracePeriod,sourceOfFundId,loanPurposeId,
                            collateralTypeId,collateralNotes,externalId);
                    parsedLoanDetails.add(detail);
                }catch(XlsParsingException xex){
                    if(xex.isMultiple()){
                        for (String msg : xex.getMessages()) {
                            errors.add(msg);
                        }
                    }else{
                        errors.add(xex.getMessage());
                    }
                }
                catch (Exception cex) {
                    errors.add(cex.getMessage());
                }
            }

        } catch (Exception ex) {
            errors.add(ex.getMessage());
        }
        result = new ParsedLoansDto(errors, parsedLoanDetails);
        return result;
    }

    private Integer validateCollateralType(String collateralType,
            Map<String, String> collaterals, HSSFRow row, int currentCell) throws XlsParsingException {
        if(StringUtils.isBlank(collateralType)){
            return null;
        }
        String resultKey=null;
        for (String key : collaterals.keySet()) {
            if(collaterals.containsValue(collateralType)){
               resultKey=key; 
            }
        }
        if(resultKey==null){
            List<Object> params=new ArrayList<Object>();
            params.add(collateralType);
            throw new XlsParsingException(getCellError(XlsMessageConstants.UNKNOWN_COLLATERAL_TYPE, row, currentCell, params));
        }
        return new Integer(resultKey);
    }

    private Integer validateLoanPurposeId(String loanPurpose,
            List<ValueListElement> purposes, HSSFRow row, int currentCell) throws XlsParsingException {
        if(StringUtils.isBlank(loanPurpose)){
            return null;
        }
        Integer loanPurposeId=null;
        for (ValueListElement element : purposes) {
            if(element.getName().equals(loanPurpose)){
                loanPurposeId=element.getId();
                break;
            }
        }
        if(loanPurposeId==null){
            List<Object> params=new ArrayList<Object>();
            params.add(loanPurpose);
            throw new XlsParsingException(getCellError(XlsMessageConstants.UNKNOWN_LOAN_PURPOSE, row, currentCell, params));
        }
        return loanPurposeId;
    }

    private Integer validateSourceOfFund(String sourceOfFund,
            List<FundDto> funds,HSSFRow row, int currentCell) throws XlsParsingException {
        if(StringUtils.isBlank(sourceOfFund)){
            return null;
        }else{
            Integer result=null;
            for (FundDto fundDto : funds) {
                if(fundDto.getName().equalsIgnoreCase(sourceOfFund)){
                    result =new Integer(fundDto.getId());
                    break;
                }
            }
            if(result!=null){
                return result;
            }else{
                List<Object> params=new ArrayList<Object>();
                params.add(sourceOfFund);
                throw new XlsParsingException(getCellError(XlsMessageConstants.UNKNOWN_SOURCE_OF_FUND, row, currentCell, params));
            }
        }

        
    }

    private void validateGracePeriod(Integer gracePeriod,
            LoanOfferingBO loanOfferingBO, CustomerBO customerBO, Integer numberOfInstallments, HSSFRow row, int currentCell) throws XlsParsingException {
        if(!GraceType.isGraceTypeNONE(loanOfferingBO.getGraceType().getValue())){
            if(gracePeriod==null){
                throw new XlsParsingException(getCellError(XlsMessageConstants.MISSING_GRACE_PERIOD, row, currentCell, null));
            }
            if(gracePeriod>numberOfInstallments){
                throw new XlsParsingException(getCellError(XlsMessageConstants.GRACE_PERIOD_IS_GREATER_THAN_INSTALLMENTS, row, currentCell, null));
            }
            if(gracePeriod>loanOfferingBO.getGracePeriodDuration()){
                throw new XlsParsingException(getCellError(XlsMessageConstants.GRACE_PERIOD_EXCEEDS_PRODUCT_DEFINITION, row, currentCell, null));
            }
        }
    }

    private void validateDisbursalDate(Date disbursalDate,
            CustomerBO customerBO, LoanOfferingBO loanOfferingBO, int currentCell, HSSFRow currentRow) throws XlsParsingException {
        if(disbursalDate==null){
            throw new XlsParsingException(getCellError(XlsMessageConstants.MISSING_DISBURSAL_DATE, currentRow, currentCell, null));
        }
        LocalDate localDisbursalDate=new LocalDate(disbursalDate.getTime());
        Errors errors=loanAccountServiceFacade.validateLoanDisbursementDate(localDisbursalDate, customerBO.getCustomerId(), loanOfferingBO.getPrdOfferingId().intValue());
        if(errors.hasErrors()){
            XlsParsingException xlEx=new XlsParsingException(true);
            List<Object> tmp=new ArrayList<Object>();
            tmp.add(localDisbursalDate);
            xlEx.getMessages().add(getCellError(XlsMessageConstants.INVALID_DATE, currentRow, currentCell, tmp));
            for (ErrorEntry entry : errors.getErrorEntries()) {
               xlEx.getMessages().add(getNestedMessage(entry.getErrorCode(),entry.getArgs()));
            }
            throw xlEx;
        }
    }

    private void validateNumberOfInstallments(
            Integer numberOfInstallments, CustomerBO customerBO,
            LoanOfferingBO loanOfferingBO,LoanCreationLoanDetailsDto lcldd, HSSFRow row, int currentCell) throws XlsParsingException {
        if(numberOfInstallments==null){
            throw new XlsParsingException(getCellError(XlsMessageConstants.MISSING_NUMBER_OF_INSTALLMENTS, row, currentCell, null));
        }
        if(numberOfInstallments.compareTo(lcldd.getMinNumberOfInstallments())==-1 || numberOfInstallments.compareTo(lcldd.getMaxNumberOfInstallments())==1){
            throw new XlsParsingException(getCellError(XlsMessageConstants.NUMBER_OF_INSTALLMENTS_OUT_OF_RANGE, row, currentCell, null));
        }
    }

    private Integer getCellIntegerValue(HSSFRow row,
            XlsLoansImportTemplateConstants currentCell) throws XlsParsingException {
        final HSSFCell cell = row.getCell(currentCell.getValue(),
                HSSFRow.RETURN_BLANK_AS_NULL);
        Integer result = null;
        if (cell != null) {
            switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                HSSFRichTextString richText = cell.getRichStringCellValue();
                try{
                    result = new Integer(richText.getString());
                }catch(NumberFormatException ex){
                    throw new XlsParsingException(getCellError(XlsMessageConstants.NOT_A_NUMBER, row, currentCell.getValue(),null));
                }
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                double val = cell.getNumericCellValue();
                result = new Integer((int) val);
                break;
            default:
                return null;
            }
        }
        return result;
    }

    private void validateInterestRate(BigDecimal interestRate,
            LoanOfferingBO loanOfferingBO, CustomerBO customerBO, LoanCreationLoanDetailsDto lcldd,HSSFRow row, int currentCell) throws XlsParsingException {
        if (interestRate == null) {
            throw new XlsParsingException(getCellError(XlsMessageConstants.MISSING_INTEREST_RATE, row, currentCell, null));
        }
        boolean tmpCondition = interestRate.doubleValue() < lcldd
                .getMinInterestRate()
                || interestRate.doubleValue() > lcldd.getMaxInterestRate();
        if (tmpCondition) {
            throw new XlsParsingException(getCellError(XlsMessageConstants.INTEREST_RATE_OUT_OF_RANGE, row, currentCell, null));
        }
        tmpCondition = interestRate.scale() > lcldd.getAppConfig()
                .getDigitsAfterDecimalForInterest();
        if (tmpCondition) {
            List<Object> params=new ArrayList<Object>();
            params.add(lcldd.getAppConfig().getDigitsAfterDecimalForInterest());
            throw new XlsParsingException(getCellError(XlsMessageConstants.DIGITS_AFTER_DECIMAL, row, currentCell, params));
        }
        tmpCondition = interestRate.toBigInteger().toString().length() > lcldd
                .getAppConfig().getDigitsBeforeDecimalForInterest();
        if (tmpCondition) {
            List<Object> params=new ArrayList<Object>();
            params.add(lcldd.getAppConfig().getDigitsBeforeDecimalForInterest());
            throw new XlsParsingException(getCellError(XlsMessageConstants.DIGITS_BEFORE_DECIMAL, row, currentCell, params));
        }
    }

    private BigDecimal getCellDecimalValue(HSSFRow row,
            XlsLoansImportTemplateConstants currentCell) throws XlsParsingException {
        final HSSFCell cell = row.getCell(currentCell.getValue(),
                HSSFRow.RETURN_BLANK_AS_NULL);
        BigDecimal result = null;
        if (cell != null) {
            switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                HSSFRichTextString richText = cell.getRichStringCellValue();
                try{
                    result = new BigDecimal(richText.getString());
                }catch(NumberFormatException ex){
                    throw new XlsParsingException(getCellError(XlsMessageConstants.NOT_A_NUMBER, row, currentCell.getValue(),null));
                }
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                double val = cell.getNumericCellValue();
                result = new BigDecimal(val);
                break;
            default:
                return null;
            }
        }
        return result;
    }

    private void validateAmount(BigDecimal rawAmount,
            LoanOfferingBO loanOfferingBO, CustomerBO customerBO, LoanCreationLoanDetailsDto lcldd, HSSFRow row, int currentCell) throws XlsParsingException {
        if (rawAmount == null) {
            throw new XlsParsingException(getCellError(XlsMessageConstants.MISSING_LOAN_AMOUNT, row, currentCell, null));
        }
        boolean wrongAmount = (rawAmount.compareTo(lcldd.getMinLoanAmount()) == -1)
                || (rawAmount.compareTo(lcldd.getMaxLoanAmount()) == 1);
        if (wrongAmount) {
            throw new XlsParsingException(getCellError(XlsMessageConstants.LOAN_AMOUNT_OUF_OF_RANGE, row, currentCell, null));
        }
        boolean tooManyAfter = rawAmount.scale() > lcldd.getAppConfig()
                .getDigitsAfterDecimalForMonetaryAmounts();
        boolean tooManyBefore = rawAmount.toBigInteger().toString().length() > lcldd
                .getAppConfig().getDigitsBeforeDecimalForMonetaryAmounts();
        if (tooManyBefore) {
            List<Object> params=new ArrayList<Object>();
            params.add(lcldd.getAppConfig().getDigitsBeforeDecimalForMonetaryAmounts());
            throw new XlsParsingException(getCellError(XlsMessageConstants.DIGITS_BEFORE_DECIMAL,row,currentCell,params));
        }
        if (tooManyAfter) {
            List<Object> params=new ArrayList<Object>();
            params.add(lcldd.getAppConfig().getDigitsAfterDecimalForMonetaryAmounts());
            throw new XlsParsingException(getCellError(XlsMessageConstants.DIGITS_AFTER_DECIMAL,row,currentCell,params));
        }
    }

    private XlsLoanSavingsFlagsConstants validateStatusFlagReason(String cancelReason,
            String statusName, AccountTypes type, HSSFRow row, int currentCell) throws Exception {
        boolean cancelled = false;
        XlsLoanSavingsFlagsConstants flag = null;
        // check if status is equals to 'cancelled'
        if (type.equals(AccountTypes.LOAN_ACCOUNT)) {
            cancelled = statusName
                    .equalsIgnoreCase(XlsLoanSavingsAccountStatesConstants.LOAN_CANCELLED
                            .getName());
        } else if (type.equals(AccountTypes.SAVINGS_ACCOUNT)) {
            cancelled = statusName
                    .equalsIgnoreCase(XlsLoanSavingsAccountStatesConstants.SAVINGS_CANCELLED
                            .getName());
        } else {
            throw new XlsParsingException("This parser does not support accounts other than loans and savings");
        }
        if (cancelled) {
            if (StringUtils.isBlank(cancelReason)) {
                throw new XlsParsingException(getCellError(XlsMessageConstants.MISSING_STATUS_REASON, row, currentCell, null));
            }
            flag = XlsLoanSavingsFlagsConstants.findByNameForAccountType(
                    cancelReason, type);
            if (flag == null) {
                throw new XlsParsingException(getCellError(XlsMessageConstants.WRONG_STATUS_REASON, row, currentCell, null));
            }
        }
        return flag;
    }

    private XlsLoanSavingsAccountStatesConstants validateStatusName(String statusName,
            CustomerBO customerBO, boolean edit, HSSFRow row, int currentCell) throws XlsParsingException {
        // TODO change method so it will support both loans and savings
        // check if blank
        if (StringUtils.isBlank(statusName)) {
            throw new XlsParsingException(getCellError(XlsMessageConstants.MISSING_ACCOUNT_STATUS, row, currentCell, null));
        }
        // check if not known status
        List<XlsLoanSavingsAccountStatesConstants> states = XlsLoanSavingsAccountStatesConstants
                .getAccountStatesForAccountType(AccountTypes.LOAN_ACCOUNT);
        XlsLoanSavingsAccountStatesConstants found = null;
        for (XlsLoanSavingsAccountStatesConstants accountState : states) {
            if (accountState.getName().equalsIgnoreCase(statusName)) {
                found = accountState;
                break;
            }
        }
        if (found == null) {
            throw new XlsParsingException(getCellError(XlsMessageConstants.LOAN_STATUS_NOT_FOUND, row, currentCell, null));
        }
        // check if customer active
        if (!edit) {
            if (!customerBO.isActive()) {
                throw new XlsParsingException(getCellError( XlsMessageConstants.CUSTOMER_FOR_LOAN_INACTIVE, row, currentCell, null));
            }
        }
        // TODO more status checks for importing archival data and editing
        return found;
    }

    private LoanOfferingBO validateProductName(String productName, CustomerBO customerBO, HSSFRow row, int currentCell) throws XlsParsingException {
        // check if blank
        if (StringUtils.isBlank(productName)) {
            throw new XlsParsingException(getCellError(XlsMessageConstants.MISSING_PRODUCT_NAME, row, currentCell, null));
        }
        // check if product is definied
        List<LoanOfferingBO> products = loanProductDao.findActiveLoanProductsApplicableToCustomerLevel(
                customerBO.getCustomerLevel());
        LoanOfferingBO foundProduct = null;
        for (LoanOfferingBO loanOfferingBO : products) {
            if (loanOfferingBO.getPrdOfferingName().equals(productName)
                    && loanOfferingBO.isActive()) {
                foundProduct = loanOfferingBO;
                break;
            }
        }
        if (foundProduct == null) {
            List<Object> params=new ArrayList<Object>();
            params.add(productName);
            throw new XlsParsingException(getCellError(XlsMessageConstants.PRODUCT_NOT_FOUND, row, currentCell, params));
        }
        // check if installments date is synchronized with meetings
        boolean meetingOk = foundProduct.getLoanOfferingMeeting().getMeeting()
                .hasSameRecurrenceAs(customerBO.getCustomerMeetingValue());
        if (!meetingOk) {
            throw new XlsParsingException(getCellError(XlsMessageConstants.DIFFERENT_MEETING_FREQUENCY, row, currentCell, null));
        }
        return foundProduct;
    }

    private CustomerBO validateCustomerGlobalId(String customerGlobalId) {
        CustomerBO customer = customerDao
                .findCustomerBySystemId(customerGlobalId);
        return customer;
    }

    private boolean validateAccountNumber(String accountNumber,
            List<String> newAccountsNumbers) throws CellException {
        boolean allOk = false;
        if (!newAccountsNumbers.contains(accountNumber)) {
            if (loanDao.findByGlobalAccountNum(accountNumber) == null
                    && savingsDao.findBySystemId(accountNumber) == null) {
                allOk = true;
            }
        }
        return allOk;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    private String getMessage(XlsMessageConstants xlsMessageConstant,int param) {
        Object[] params={String.valueOf(param)};
        return messageSource.getMessage(xlsMessageConstant.getText(), params,locale);
    }
    private String getMessage(XlsMessageConstants xlsMessageConstant,Object[] params) {
        return messageSource.getMessage(xlsMessageConstant.getText(), params,locale);
    }
    private String getNestedMessage(String nested,List<String> args){
        Object[] params=args.toArray();
        return NESTED_LEVEL_IND+messageSource.getMessage(nested,params,locale);
    }
    

    protected class XlsParsingException extends Exception {
        private static final long serialVersionUID = -4197455680575016180L;
        private ArrayList<String> messages;
        private boolean multiple;
        public XlsParsingException(String message) {
            super(message);
            this.multiple=false;
        }
        public XlsParsingException(boolean multiple) {
            this.multiple=multiple;
            messages=new ArrayList<String>();
        }
        public ArrayList<String> getMessages() {
            return messages;
        }
        public void setMessages(ArrayList<String> messages) {
            this.messages = messages;
        }
        public boolean isMultiple() {
            return multiple;
        }
        public void setMultiple(boolean multiple) {
            this.multiple = multiple;
        }
    }

    private String getCellStringValue(final HSSFRow row,
            final XlsLoansImportTemplateConstants xlsImportConstant) {
        final HSSFCell cell = row.getCell(xlsImportConstant.getValue(),
                HSSFRow.RETURN_BLANK_AS_NULL);
        if (cell != null) {
            switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                HSSFRichTextString richText = cell.getRichStringCellValue();
                return (richText == null) ? "" : richText.getString();
            case HSSFCell.CELL_TYPE_NUMERIC:
                long intVal = (long) cell.getNumericCellValue();
                return String.valueOf(intVal);
            default:
                return "";
            }
        } else {
            return "";
        }
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }
    private String getRowError(XlsMessageConstants message,HSSFRow row, List<Object> moreParams) {
        final StringBuilder sb = new StringBuilder();
        sb.append(getMessage(XlsMessageConstants.ROW_ERROR, row.getRowNum()+1)); //+1 for friendly row number
        sb.append(": ");
        Object[] detailedParams=null;
        if(moreParams!=null){
            detailedParams=moreParams.toArray();
        }
        sb.append(getMessage(message, detailedParams));
        return sb.toString();
    }

    private String getCellError(XlsMessageConstants message,HSSFRow row,int currentCell, List<Object> moreParams) {
        final StringBuilder sb = new StringBuilder(getMessage(XlsMessageConstants.ROW_ERROR,
               row.getRowNum()+1)); //+1 for friendly row number
        sb.append(", ");
        sb.append(getMessage(XlsMessageConstants.CELL_ERROR, currentCell+1)); //+1 for friendly cell number
        sb.append(": ");
        Object[] detailedParams=null;
        if(moreParams!=null){
            detailedParams=moreParams.toArray();
        }
        sb.append(getMessage(message, detailedParams));
        return sb.toString();
    }
    private Date getCellDateValue(final HSSFRow row, final XlsLoansImportTemplateConstants currentCell) throws Exception {
        HSSFCell cell = null;
        try {
            cell = row.getCell(currentCell.getValue(), HSSFRow.RETURN_BLANK_AS_NULL);
            Date rawDate=null;
            rawDate= (cell == null) ? null : cell.getDateCellValue();
            if(rawDate==null){
                throw new NullPointerException();
            }
            return rawDate;
        } catch (Exception ex) {
            String invalidDateString = (cell == null) ? "" : getCellStringValue(row, currentCell);
            List<Object> moreParams=new ArrayList<Object>();
            moreParams.add(invalidDateString);
            throw new XlsParsingException(getCellError(XlsMessageConstants.INVALID_DATE, row, currentCell.getValue(), moreParams));
        }
    }

}
