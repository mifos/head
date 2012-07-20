package org.mifos.application.importexport.xls;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.LocalDate;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.dto.domain.ImportedSavingDetail;
import org.mifos.dto.domain.ParsedSavingsDto;
import org.mifos.dto.domain.PrdOfferingDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

public class XlsSavingsAccountImporter implements MessageSourceAware {

    private MessageSource messageSource;
    private Locale locale;
    private CustomerDao customerDao;
    private SavingsDao savingsDao;
    private LoanDao loanDao;
    private SavingsProductDao savingsProductDao;

    @Autowired
    public XlsSavingsAccountImporter(CustomerDao customerDao, SavingsDao savingsDao,
            SavingsProductDao savingsProductDao, LoanDao loanDao) {
        super();
        this.customerDao = customerDao;
        this.savingsDao = savingsDao;
        this.savingsProductDao = savingsProductDao;
        this.loanDao = loanDao;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public ParsedSavingsDto parse(InputStream is) {
        ParsedSavingsDto result = null;
        List<String> errors = new ArrayList<String>();
        List<String> newAccountsNumbers = new ArrayList<String>(); // temporary list for new accounts numbers, currently
                                                                   // not used
        List<ImportedSavingDetail> parsedSavingDetails = new ArrayList<ImportedSavingDetail>();
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(is);
            HSSFSheet sheet = workbook.getSheetAt(0);
            HSSFRow row = sheet.getRow(XlsSavingsImportTemplateConstants.FIRST_ROW_WITH_DATA.getValue());
            if (row == null) {
                throw new XlsParsingException(getMessage(XlsMessageConstants.NOT_ENOUGH_INPUT_ROW, null));
            }

            Iterator<Row> iterator = sheet.rowIterator();
            while (iterator.hasNext()
                    && (iterator.next().getRowNum() < XlsSavingsImportTemplateConstants.FIRST_ROW_WITH_DATA.getValue() - 1))
                ;
            while (iterator.hasNext()) {
                row = (HSSFRow) iterator.next();
                List<Object> params = new ArrayList<Object>();
                XlsSavingsImportTemplateConstants currentCell = XlsSavingsImportTemplateConstants.ACCOUNT_NUMBER;
                try {
                    String accountNumber = getCellStringValue(row, currentCell);
                    if (StringUtils.isBlank(accountNumber)) {
                        accountNumber = null;
                    } else if (!StringUtils.isBlank(accountNumber)) {
                        // ...but it's duplicate
                        if (!validateAccountNumber(accountNumber, newAccountsNumbers)) {
                            params.clear();
                            params.add(accountNumber);
                            throw new XlsParsingException(getCellError(XlsMessageConstants.DUPLICATE_GLOBAL_NUM_ERROR,
                                    row, currentCell.getValue(), params));
                        }
                    }
                    currentCell = XlsSavingsImportTemplateConstants.CUSTOMER_GLOBAL_ID;
                    String customerGlobalId = getCellStringValue(row, currentCell);
                    CustomerBO customerBO = null;
                    customerBO = validateCustomerGlobalId(customerGlobalId);
                    if (customerBO == null) {
                        params.clear();
                        params.add(customerGlobalId);
                        throw new XlsParsingException(getCellError(XlsMessageConstants.CUSTOMER_NOT_FOUND, row,
                                currentCell.getValue(), params));
                    }
                    currentCell = XlsSavingsImportTemplateConstants.PRODUCT_NAME;
                    String productName = getCellStringValue(row, currentCell);
                    PrdOfferingDto prdOfferingDto = null;
                    prdOfferingDto = validateProductName(productName, customerBO, row, currentCell.getValue());
                    currentCell = XlsSavingsImportTemplateConstants.STATUS_NAME;
                    String statusName = getCellStringValue(row, currentCell);
                    XlsLoanSavingsAccountStatesConstants statusConstant = null;
                    statusConstant = validateStatusName(statusName, customerBO, row, currentCell.getValue());
                    currentCell = XlsSavingsImportTemplateConstants.CANCEL_FlAG_REASON;
                    String cancelReason = getCellStringValue(row, currentCell);
                    XlsLoanSavingsFlagsConstants flagConstant = null;
                    flagConstant = validateStatusFlagReason(cancelReason, statusName, AccountTypes.SAVINGS_ACCOUNT, row,
                            currentCell.getValue());

                    currentCell = XlsSavingsImportTemplateConstants.SAVINGS_AMOUNT;
                    BigDecimal savingAmount = getCellDecimalValue(row,currentCell);
                    if(savingAmount == BigDecimal.valueOf(0)){
            			savingAmount = savingsProductDao.findBySystemId(prdOfferingDto.getGlobalPrdOfferingNum()).getRecommendedAmount().getAmount(); 
            		}
                    currentCell = XlsSavingsImportTemplateConstants.SAVINGS_BALANCE;
                    BigDecimal savingBalance = getCellDecimalValue(row, currentCell);
                    
                    if (accountNumber != null) {
                        newAccountsNumbers.add(accountNumber);
                    }
                    LocalDate date = new LocalDate();
                    Short flagValue = flagConstant == null ? null : flagConstant.getFlag().getValue();
                    ImportedSavingDetail detail = new ImportedSavingDetail(accountNumber, customerGlobalId,
                            prdOfferingDto.getGlobalPrdOfferingNum(), statusConstant.getState().getValue(), flagValue, savingAmount, savingBalance, date);
                    parsedSavingDetails.add(detail);
                } catch (XlsParsingException xex) {
                    if (xex.isMultiple()) {
                        for (String msg : xex.getMessages()) {
                            errors.add(msg);
                        }
                    } else {
                        errors.add(xex.getMessage());
                    }
                } catch (Exception cex) {
                    errors.add(cex.getMessage());
                }
            }

        } catch (Exception ex) {
            errors.add(ex.getMessage());
        }
        result = new ParsedSavingsDto(errors, parsedSavingDetails);
        return result;
    }

	public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    private String getMessage(XlsMessageConstants xlsMessageConstant, int param) {
        Object[] params = { String.valueOf(param) };
        return messageSource.getMessage(xlsMessageConstant.getText(), params, locale);
    }

    private String getMessage(XlsMessageConstants xlsMessageConstant, Object[] params) {
        return messageSource.getMessage(xlsMessageConstant.getText(), params, locale);
    }

    private String getCellStringValue(final HSSFRow row, final XlsSavingsImportTemplateConstants xlsImportConstant) {
        final HSSFCell cell = row.getCell(xlsImportConstant.getValue(), HSSFRow.RETURN_BLANK_AS_NULL);
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

    private boolean validateAccountNumber(String accountNumber, List<String> newAccountsNumbers) throws CellException {
        boolean allOk = false;
        if (!newAccountsNumbers.contains(accountNumber)) {
            if (loanDao.findByGlobalAccountNum(accountNumber) == null
                    && savingsDao.findBySystemId(accountNumber) == null) {
                allOk = true;
            }
        }
        return allOk;
    }

    private String getCellError(XlsMessageConstants message, HSSFRow row, int currentCell, List<Object> moreParams) {
        final StringBuilder sb = new StringBuilder(getMessage(XlsMessageConstants.ROW_ERROR, row.getRowNum() + 1)); 
        sb.append(", ");
        sb.append(getMessage(XlsMessageConstants.CELL_ERROR, currentCell + 1)); // +1 for friendly cell number
        sb.append(": ");
        Object[] detailedParams = null;
        if (moreParams != null) {
            detailedParams = moreParams.toArray();
        }
        sb.append(getMessage(message, detailedParams));
        return sb.toString();
    }

    private CustomerBO validateCustomerGlobalId(String customerGlobalId) {
        CustomerBO customer = customerDao.findCustomerBySystemId(customerGlobalId);
        return customer;
    }

    private PrdOfferingDto validateProductName(String productName, CustomerBO customerBO, HSSFRow row, int currentCell)
            throws XlsParsingException {
        if (StringUtils.isBlank(productName)) {
            throw new XlsParsingException(
                    getCellError(XlsMessageConstants.MISSING_PRODUCT_NAME, row, currentCell, null));
        }
        List<PrdOfferingDto> products = savingsProductDao.findSavingsProductByCustomerLevel(customerBO
                .getCustomerLevel());
        PrdOfferingDto foundProduct = null;
        for (PrdOfferingDto prdOfferingDto : products) {
            if (prdOfferingDto.getPrdOfferingName().equals(productName)) {
                foundProduct = prdOfferingDto;
                break;
            }
        }
        if (foundProduct == null) {
            List<Object> params = new ArrayList<Object>();
            params.add(productName);
            throw new XlsParsingException(getCellError(XlsMessageConstants.PRODUCT_NOT_FOUND, row, currentCell, params));
        }
        return foundProduct;
    }

    protected class XlsParsingException extends Exception {
        private static final long serialVersionUID = -4197455680575016180L;
        private ArrayList<String> messages;
        private boolean multiple;

        public XlsParsingException(String message) {
            super(message);
            this.multiple = false;
        }

        public XlsParsingException(boolean multiple) {
            this.multiple = multiple;
            messages = new ArrayList<String>();
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

    private XlsLoanSavingsAccountStatesConstants validateStatusName(String statusName, CustomerBO customerBO,
            HSSFRow row, int currentCell) throws XlsParsingException {
        if (StringUtils.isBlank(statusName)) {
            throw new XlsParsingException(getCellError(XlsMessageConstants.MISSING_ACCOUNT_STATUS, row, currentCell,
                    null));
        }
        List<XlsLoanSavingsAccountStatesConstants> states = XlsLoanSavingsAccountStatesConstants
                .getAccountStatesForAccountType(AccountTypes.SAVINGS_ACCOUNT);
        XlsLoanSavingsAccountStatesConstants found = null;
        for (XlsLoanSavingsAccountStatesConstants accountState : states) {
        	if (accountState.getName().equalsIgnoreCase(statusName)) {
                found = accountState;
                break;
            }
        }
        if (found == null) {
            throw new XlsParsingException(getCellError(XlsMessageConstants.SAVINGS_STATUS_NOT_FOUND, row, currentCell,
                    null));
        }
        if (!customerBO.isActive()) {
            throw new XlsParsingException(getCellError(XlsMessageConstants.CUSTOMER_FOR_LOAN_INACTIVE, row,
                    currentCell, null));
        }
        return found;
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
    
    private BigDecimal getCellDecimalValue(HSSFRow row,
            XlsSavingsImportTemplateConstants currentCell) throws XlsParsingException {
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
}
