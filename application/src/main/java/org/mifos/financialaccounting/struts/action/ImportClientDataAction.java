package org.mifos.financialaccounting.struts.action;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.mifos.application.accounting.business.GlDetailBO;
import org.mifos.application.accounting.business.GlMasterBO;
import org.mifos.application.accounting.util.helpers.SimpleAccountingConstants;
import org.mifos.application.servicefacade.AccountingServiceFacade;
import org.mifos.application.servicefacade.AccountingServiceFacadeWebTier;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.financialaccounting.struts.actionform.ImportClientDataActionForm;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportClientDataAction extends BaseAction{

	private static final Logger logger = LoggerFactory
			.getLogger(ImportClientDataAction.class);

	private AccountingServiceFacade accountingServiceFacade = new AccountingServiceFacadeWebTier();

    ActionErrors actionerrors;
   private String transactionDate,transactionType,officeLevel,officeName,mainAccount,accountHead,narration,chequeNo,chequeDate,bankName,bankBranch,amount;

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {



		return mapping.findForward(ActionForwards.load_success.toString());
	}
	public ActionForward uploadXLSData(ActionMapping mapping, ActionForm form,HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)throws Exception
	{
		ImportClientDataActionForm actionForm=(ImportClientDataActionForm)form;
		final List<String> errorsList = new ArrayList<String>();
		final List<String> displayerrorsList = new ArrayList<String>();
		 Date datevalue;
         Date cheqDate;

		FormFile formFile = null;
		formFile = actionForm.getImportTransactionsFile();
		String importtransactionfilepath = getServlet().getServletContext().getRealPath("")+"/"+formFile.getFileName();
		FileOutputStream outputStream = null;
		outputStream = new FileOutputStream(new File(importtransactionfilepath));
		outputStream.write(formFile.getFileData());
		FileInputStream myInput = new FileInputStream(importtransactionfilepath);
		POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
		final HSSFWorkbook workbook = new HSSFWorkbook(myFileSystem);
        final HSSFSheet sheet = workbook.getSheetAt(0);

         HSSFRow row = sheet.getRow(UploadXLSConstants.FIRST_CLIENT_ROW.value());
         if (row == null) {
         }
         @SuppressWarnings("rawtypes")
         Iterator rowIterator = sheet.rowIterator();
         /* Skip first rows */
         if (errorsList.isEmpty()) {
             for (int i = 1; i < UploadXLSConstants.SKIPPED_ROWS.value(); i++) {

                 if (rowIterator.hasNext())
                 {
                     rowIterator.next();
                 } else {
                     break;
                 }
             }
         }
         UploadXLSConstants currentCell =  UploadXLSConstants.TRANSACTION_DATE_CELL;
         List<GlMasterBO> glmasterbolist=new ArrayList<GlMasterBO>();
        // List<GlDetailBO> gldetailbolist=new ArrayList<GlDetailBO>();
         storingSession(request, "glmasterbolist", glmasterbolist );
         storingSession(request, "displayerrorsList", displayerrorsList );
         int friendlyRowNumber = 0;
          ValidateXLSData validatexlsdata=new ValidateXLSData();
                 if (errorsList.isEmpty()) {
                  while (rowIterator.hasNext()) {
                     row = (HSSFRow) rowIterator.next();
                     friendlyRowNumber = row.getRowNum() + 1;
                     saveErrors(request, actionerrors);
                     currentCell = UploadXLSConstants.TRANSACTION_DATE_CELL;
                      transactionDate = getCellStringValue(row, currentCell);
                     SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
                     try{
                         datevalue = sdFormat.parse(transactionDate);
                        }
                        catch (Exception e) {
				actionerrors=validateTransactionDate(friendlyRowNumber);
						break;
					}
                      currentCell = UploadXLSConstants.TRANSACTION_TYPE_CELL;
                      transactionType= getCellStringValue(row, currentCell);
                      String transactionTypevale=validatexlsdata.getTransactionType(transactionType);
                      currentCell = UploadXLSConstants.OFFICE_LEVEL_CELL;
                      officeLevel = getCellStringValue(row, currentCell);
                      int  officelevelvalue=validatexlsdata.getOfficeLevel(officeLevel);

                      currentCell = UploadXLSConstants.OFFICE_NAME_CELL;
                      officeName = getCellStringValue(row, currentCell);
                      String GlobalNum=validatexlsdata.getglobalnum(officeName);

                      currentCell = UploadXLSConstants.MAIN_ACCOUNT_CELL;
                      mainAccount = getCellStringValue(row, currentCell);
                      String mainAccountValue=validatexlsdata.getmainAccountHead(mainAccount);
                      currentCell = UploadXLSConstants.ACCOUNT_HEAD_CELL;//subaccount
                      accountHead = getCellStringValue(row, currentCell);

                      String  subAccountValue=validatexlsdata.getmainAccountHead(accountHead);
                      currentCell = UploadXLSConstants.AMOUNT_CELL;
                      amount = getCellStringValue(row, currentCell);

                      int transactionAmount=Integer.parseInt(amount);
                      currentCell = UploadXLSConstants.NARRATION_CELL;
                      narration = getCellStringValue(row, currentCell);

                      currentCell = UploadXLSConstants.CHEQUE_NO_CELL;
                      chequeNo= getCellStringValue(row, currentCell);

                      currentCell = UploadXLSConstants.CHEQUE_DATE_CELL;
                      chequeDate = getCellStringValue(row, currentCell);
                      try{
                          cheqDate=sdFormat.parse(chequeDate);
                         }
                         catch (Exception e) {
				actionerrors=validateChequeDate(friendlyRowNumber);
						break;
					}
                      currentCell = UploadXLSConstants.BANK_NAME_CELL;
                      bankName= getCellStringValue(row, currentCell);

                      currentCell = UploadXLSConstants.BANK_BRANCH_CELL;
                      bankBranch = getCellStringValue(row, currentCell);

                    List<String> amountActionList = getAmountAction(transactionType);





                    BigDecimal tranamount=new BigDecimal(transactionAmount);
                    actionerrors=getErrormessagesvalues(datevalue, transactionTypevale,
                    officelevelvalue,GlobalNum, mainAccountValue, subAccountValue, tranamount,narration,chequeNo,cheqDate, bankName,
			bankBranch,friendlyRowNumber);
                    if(actionerrors.size()>0)
			    break;
                    List<GlDetailBO> gldetailbolist=new ArrayList<GlDetailBO>();
                    GlDetailBO gldetailbo=new GlDetailBO(subAccountValue,tranamount,amountActionList.get(0),chequeNo,cheqDate, bankName,bankBranch);
                    gldetailbolist.add(gldetailbo);
                    GlMasterBO glmasterbo=new GlMasterBO(datevalue, transactionTypevale, officelevelvalue, GlobalNum, mainAccountValue, tranamount, amountActionList.get(1), narration, gldetailbolist);
                    glmasterbolist.add(glmasterbo);


             }}
         if(actionerrors.isEmpty())
         {
		 storingSession(request, "message",SimpleAccountingConstants.BANK_NAME_CELL);
		 return mapping.findForward(ActionForwards.process_success.toString());
         }else
         {
		 return mapping.findForward(ActionForwards.load_success.toString());

         }
	}

	public ActionForward  submit(ActionMapping mapping, ActionForm form,HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)throws Exception
	{
		HttpSession session = request.getSession(true);
		 List<GlMasterBO> glmasterbolist=(List<GlMasterBO>) session.getAttribute("glmaster");
		 for(GlMasterBO glmasterlist:glmasterbolist)
		 {
			 GlMasterBO glMasterBO = new GlMasterBO();
				glMasterBO.setTransactionDate(glmasterlist.getTransactionDate());
				glMasterBO.setTransactionType(glmasterlist.getTransactionType());
				glMasterBO.setFromOfficeLevel(glmasterlist.getFromOfficeLevel());
				glMasterBO.setFromOfficeId(glmasterlist.getFromOfficeId());
				glMasterBO.setToOfficeLevel(glmasterlist.getFromOfficeLevel());
				glMasterBO.setToOfficeId(glmasterlist.getFromOfficeId());
			    glMasterBO.setMainAccount(glmasterlist.getMainAccount());
			    glMasterBO.setTransactionAmount(glmasterlist.getTransactionAmount());
			    glMasterBO.setAmountAction(glmasterlist.getAmountAction());
			    glMasterBO.setTransactionNarration(glmasterlist.getTransactionNarration());
				glMasterBO.setGlDetailBOList(glmasterlist.getGlDetailBOList());
			    glMasterBO.setStatus("");// default value
			    glMasterBO.setStage(1);
			    glMasterBO.setTransactionBy(0); // default value
			    glMasterBO.setCreatedBy(getUserContext(request).getId());
			    glMasterBO.setCreatedDate(DateUtils.getCurrentDateWithoutTimeStamp());
			 accountingServiceFacade.savingAccountingTransactions(glMasterBO);
		 }

		return mapping.findForward(ActionForwards.load_success.toString());
	}
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.cancel_success.toString());

	}
	private void storingSession(HttpServletRequest request, String name,
			Object object) {

		request.getSession().setAttribute(name,object);

	}
	private String getCellStringValue(final HSSFRow row, final UploadXLSConstants uploadXLSConstants) {
        final HSSFCell cell = row.getCell(uploadXLSConstants.value(), HSSFRow.RETURN_BLANK_AS_NULL);
        if (cell != null) {
            switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                HSSFRichTextString richText = cell.getRichStringCellValue();
                return (richText == null) ? "" : richText.getString();
            case HSSFCell.CELL_TYPE_NUMERIC:
                int intVal = (int) cell.getNumericCellValue();
                return String.valueOf(intVal);
            default:
                return "";
            }
        } else {
            return "";
        }
    }

	public List<String> getAmountAction(String AmountAction) {
		List<String> amountActionList = new ArrayList<String>();

		if (AmountAction.equals("CR")
				|| AmountAction.equals("BR")) {
			amountActionList.add("credit");// for SubAccount amountAction
			amountActionList.add("debit");// for MainAccount amountAction

		} else if (AmountAction.equals("CP")
				|| AmountAction.equals("BP")) {

			amountActionList.add("debit");// for SubAccount amountAction
			amountActionList.add("credit");// for MainAccount amountAction
		}

		return amountActionList;
	}

	public ActionErrors validateTransactionDate(int friendlyRowNumber)
	{
		ActionErrors errors=new  ActionErrors();
		errors.add(SimpleAccountingConstants.TRANSACTION_DATE_CELL,new ActionMessage(SimpleAccountingConstants.TRANSACTION_DATE_CELL ,friendlyRowNumber,transactionDate));
		return errors;
	}
	public ActionErrors validateChequeDate(int friendlyRowNumber)
	{
		ActionErrors errors=new  ActionErrors();
		errors.add(SimpleAccountingConstants.CHEQUE_DATE_CELL,new ActionMessage(SimpleAccountingConstants.CHEQUE_DATE_CELL ,friendlyRowNumber,chequeDate));
		return errors;
	}
	public ActionErrors getErrormessagesvalues(Date datevalue, String transactionTypevalue,
			int officelevelvalue, String globalNum, String mainAccountValue,
			String subAccountValue, BigDecimal tranamount,
			String narration, String chequeNo, Date cheqDate, String bankName,
			String bankBranch, int friendlyRowNumber) {


		ActionErrors errors=new  ActionErrors();
		  if(globalNum==null)
        {
	  errors.add(SimpleAccountingConstants.OFFICE_NAME_CELL,new ActionMessage(SimpleAccountingConstants.OFFICE_NAME_CELL ,friendlyRowNumber,officeName));

        } if(transactionTypevalue==null)
        {
		  errors.add(SimpleAccountingConstants.TRANSACTION_TYPE_CELL,new ActionMessage(SimpleAccountingConstants.TRANSACTION_TYPE_CELL ,friendlyRowNumber,transactionType));
        } if(officelevelvalue==0)
        {
		  errors.add(SimpleAccountingConstants.OFFICE_LEVEL_CELL,new ActionMessage(SimpleAccountingConstants.OFFICE_LEVEL_CELL ,friendlyRowNumber,officeLevel));
        }if( mainAccountValue==null)
        {
		  errors.add(SimpleAccountingConstants.MAIN_ACCOUNT_CELL,new ActionMessage(SimpleAccountingConstants.MAIN_ACCOUNT_CELL ,friendlyRowNumber,mainAccount));
        } if(subAccountValue==null)
        {
		  errors.add(SimpleAccountingConstants.ACCOUNT_HEAD_CELL,new ActionMessage(SimpleAccountingConstants.ACCOUNT_HEAD_CELL ,friendlyRowNumber,accountHead));
        } if(tranamount==null)
        {
		  errors.add(SimpleAccountingConstants.AMOUNT_CELL,new ActionMessage(SimpleAccountingConstants.AMOUNT_CELL ,friendlyRowNumber,amount));
        } if(narration==null)
        {
		  errors.add(SimpleAccountingConstants.NARRATION_CELL,new ActionMessage(SimpleAccountingConstants.NARRATION_CELL ,friendlyRowNumber,narration));
        } if(chequeNo==null)
        {
		  errors.add(SimpleAccountingConstants.CHEQUE_NO_CELL,new ActionMessage(SimpleAccountingConstants.CHEQUE_NO_CELL ,friendlyRowNumber,chequeNo));
        } if(bankName==null)
        {
	  errors.add(SimpleAccountingConstants.BANK_NAME_CELL,new ActionMessage(SimpleAccountingConstants.BANK_NAME_CELL ,friendlyRowNumber,bankName));
        }  if(bankBranch==null)
           {
	  errors.add(SimpleAccountingConstants.BANK_BRANCH_CELL,new ActionMessage(SimpleAccountingConstants.BANK_BRANCH_CELL ,friendlyRowNumber,bankBranch));
           }
        return errors;

	}


	}
