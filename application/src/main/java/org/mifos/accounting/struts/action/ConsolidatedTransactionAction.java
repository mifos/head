package org.mifos.accounting.struts.action;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounting.struts.actionform.ConsolidatedTransactionActionForm;
import org.mifos.application.accounting.business.GlDetailBO;
import org.mifos.application.accounting.business.GlMasterBO;
import org.mifos.application.servicefacade.AccountingServiceFacade;
import org.mifos.application.servicefacade.AccountingServiceFacadeWebTier;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.dto.domain.GLCodeDto;
import org.mifos.dto.domain.GlDetailDto;
import org.mifos.dto.domain.OfficeGlobalDto;
import org.mifos.dto.domain.OfficesList;
import org.mifos.dto.domain.ViewStageTransactionsDto;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsolidatedTransactionAction extends BaseAction{
	private static final Logger logger = LoggerFactory
			.getLogger(VoucherBranchMappingAction.class);
	private AccountingServiceFacade accountingServiceFacade = new AccountingServiceFacadeWebTier();
	public ActionForward load(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("start Load method of loan Product Action");

        ConsolidatedTransactionActionForm actionForm = (ConsolidatedTransactionActionForm) form;
		java.util.Date trxnDate = DateUtils.getCurrentDateWithoutTimeStamp();
        List<OfficeGlobalDto> officeDetailsDtos = null;
        UserContext context = getUserContext(request);
		actionForm.setOfficeLevelId(String.valueOf(context.getOfficeLevelId()));
		List<OfficesList> offices = new ArrayList<OfficesList>();
        short branches=5;
        officeDetailsDtos = accountingServiceFacade.loadOfficesForLevel(branches);
        List<GLCodeDto> accountingDtos  = accountingServiceFacade.coaBranchAccountHead();
        storingSession(request, "viewStageTransactionsDtoCRBRListValues",  null);
		storingSession(request, "viewStageTransactionsDtoCPBPListValues",  null);
		 storingSession(request, "OfficesOnHierarchy",  null);
		 actionForm.setTransactiondate("");
		 actionForm.setBranch("");
        storingSession(request, "OfficesOnHierarchy",  officeDetailsDtos);

        return mapping.findForward(ActionForwards.load_success.toString());
    }
	
	public ActionForward loadConsolidatedTransaction(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("start Load method of loan Product Action");
        ConsolidatedTransactionActionForm actionForm = (ConsolidatedTransactionActionForm) form;
       
        
        
        String lastupdatedateStringvalue=accountingServiceFacade.getLastProcessUpdatedDate(actionForm.getBranch());
		//LocalDate localDate=new LocalDate(lastupdatedateStringvalue).plusDays(1);
		 //Date date=localDate.toDateMidnight().toDate();

	    DateFormat df = new SimpleDateFormat("dd/mm/yyyy");
	   String date1 = "01/01/2012";
	    Date startDate;
	    try {
	        startDate = df.parse(lastupdatedateStringvalue);
	        startDate.setDate(startDate.getDate() + 1);
	        String newDateString = df.format(startDate);

	        Date date2=df.parse(date1);
	        if(date1.equalsIgnoreCase(lastupdatedateStringvalue))
	        {

			actionForm.setLastProcessDate(accountingServiceFacade.getLastProcessUpdatedDate(actionForm.getBranch()));
			actionForm.setTransactiondate(accountingServiceFacade.getLastProcessUpdatedDate(actionForm.getBranch()));
	        }
	        else{
			actionForm.setLastProcessDate(newDateString);
			actionForm.setTransactiondate(newDateString);
	        }
	        System.out.println(newDateString);
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }

		List<ViewStageTransactionsDto> viewStageTransactionsDtos = accountingServiceFacade.getConsolidatedTransactions(actionForm.getBranch());
		List<ViewStageTransactionsDto> viewStageTransactionsDtoCRBRList=new ArrayList<ViewStageTransactionsDto>();	
		List<ViewStageTransactionsDto> viewStageTransactionsDtoCPBPList=new ArrayList<ViewStageTransactionsDto>();	
		//Cash Payment
		
		int crbrTotal=0;
		int cpbpTotal=0;
		for(ViewStageTransactionsDto viewStageTransactionsDtosvalues:viewStageTransactionsDtos )
		{
			
			if(viewStageTransactionsDtosvalues.getTransactionType().equalsIgnoreCase("Cash Receipt") || viewStageTransactionsDtosvalues.getTransactionType().equalsIgnoreCase("Bank Receipt"))
			{
				ViewStageTransactionsDto viewstageTransactionsDto=new ViewStageTransactionsDto();
				
				crbrTotal=crbrTotal + viewStageTransactionsDtosvalues.getTransactionAmount().intValue();
				
			viewstageTransactionsDto.setTransactionNo(viewStageTransactionsDtosvalues.getTransactionNo());
			viewstageTransactionsDto.setTransactionDate(viewStageTransactionsDtosvalues.getTransactionDate());
			viewstageTransactionsDto.setTransactionType(viewStageTransactionsDtosvalues.getTransactionType());
			viewstageTransactionsDto.setOfficeLevel(viewStageTransactionsDtosvalues.getOfficeLevel());
			viewstageTransactionsDto.setDisplayName(viewStageTransactionsDtosvalues.getDisplayName());
			viewstageTransactionsDto.setMainAccount(viewStageTransactionsDtosvalues.getMainAccount());
			viewstageTransactionsDto.setAmountAction(viewStageTransactionsDtosvalues.getAmountAction());
			viewstageTransactionsDto.setSubAccount(viewStageTransactionsDtosvalues.getSubAccount());
			viewstageTransactionsDto.setTransactionAmount(viewStageTransactionsDtosvalues.getTransactionAmount());
			viewstageTransactionsDto.setNarration(viewStageTransactionsDtosvalues.getNarration());
			viewstageTransactionsDto.setTransactionID(viewStageTransactionsDtosvalues.getTransactionID());
			viewstageTransactionsDto.setAudit(viewStageTransactionsDtosvalues.getAudit());
			viewStageTransactionsDtoCRBRList.add(viewstageTransactionsDto);
			}
			else if (viewStageTransactionsDtosvalues.getTransactionType().equalsIgnoreCase("Bank Payment") || viewStageTransactionsDtosvalues.getTransactionType().equalsIgnoreCase("Cash Payment")) 
			{
				ViewStageTransactionsDto viewstageTransactionsDtoCpBp=new ViewStageTransactionsDto();
				cpbpTotal=cpbpTotal + viewStageTransactionsDtosvalues.getTransactionAmount().intValue();
				/*viewstageTransactionsDtoCpBp.setCpBptransactionNo(viewStageTransactionsDtosvalues.getTransactionNo());
				viewstageTransactionsDtoCpBp.setCpBptransactionAmount(viewStageTransactionsDtosvalues.getTransactionAmount());
				viewstageTransactionsDtoCpBp.setCpBptransactionType(viewStageTransactionsDtosvalues.getTransactionType());
				viewstageTransactionsDtoCpBp.setCpBpsubAccount(viewStageTransactionsDtosvalues.getSubAccount());*/
				viewstageTransactionsDtoCpBp.setTransactionNo(viewStageTransactionsDtosvalues.getTransactionNo());
				viewstageTransactionsDtoCpBp.setTransactionAmount(viewStageTransactionsDtosvalues.getTransactionAmount());
				viewstageTransactionsDtoCpBp.setSubAccount(viewStageTransactionsDtosvalues.getSubAccount());
				
				viewStageTransactionsDtoCPBPList.add(viewstageTransactionsDtoCpBp);
			}
			
		}
		actionForm.setCrBrTotal(Integer.toString(crbrTotal));
		actionForm.setCpBpTotal(Integer.toString(cpbpTotal));
		
		
		storingSession(request, "viewStageTransactionsDtoCRBRListValues",  viewStageTransactionsDtoCRBRList);
		storingSession(request, "viewStageTransactionsDtoCPBPListValues",  viewStageTransactionsDtoCPBPList);
        	
        	return mapping.findForward(ActionForwards.load_success.toString());
        }
	
	public ActionForward approve(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception
            {
		 ConsolidatedTransactionActionForm actionForm = (ConsolidatedTransactionActionForm) form;
		String [] Transactionno=actionForm.getTransactionNo();
		if(Transactionno!=null)
		{
		for(int i=0;i<Transactionno.length;i++)
		{
			int transactionNoValue=Integer.parseInt( Transactionno[i]);
			approveCRBRTransactions(actionForm,transactionNoValue,request);
		}
		}
		String [] cpbpTransactionNO=actionForm.getTransactionCpBpNo();
		if(cpbpTransactionNO != null)
		{
		for(int i=0;i<cpbpTransactionNO.length;i++)
		{
			int transactioncpbpNoValue=Integer.parseInt( cpbpTransactionNO[i]);
			approveCPBPTransactions(actionForm,transactioncpbpNoValue,request);
		}
		}
		return mapping.findForward("submit_success");
            }
public void approveCRBRTransactions(ConsolidatedTransactionActionForm actionForm,int Transactionno,HttpServletRequest request)
{
	ViewStageTransactionsDto viewStageTransactionsDto = accountingServiceFacade
			.getstagedAccountingTransactions(Transactionno);
	actionForm.setStageTrxnDate(changeDateFormat(viewStageTransactionsDto.getTransactionDate().toString()));
	actionForm.setStageOfficeHierarchy(this.getOfficeHierarchy(viewStageTransactionsDto.getOfficeLevel()));
	actionForm.setStageTrxnType(getTranType(viewStageTransactionsDto.getTransactionType()));
	actionForm.setStageMainAccount(viewStageTransactionsDto.getMainAccount());
	viewStageTransactionsDto.getSubAccount();
	actionForm.setStageAccountHead(viewStageTransactionsDto.getSubAccount());
	actionForm.setStageOffice(viewStageTransactionsDto.getFromOfficeId());
	GlDetailDto glDetailDto = accountingServiceFacade.getChequeDetails(Transactionno);
	if(glDetailDto!=null){
		actionForm.setStageChequeNo(glDetailDto.getChequeNo());
		if(glDetailDto.getChequeDate()!=null){
		actionForm.setChequeDate(changeDateFormat(glDetailDto.getChequeDate().toString()));
		}
		actionForm.setStageBankName(glDetailDto.getBankName());
		actionForm.setStageankBranch(glDetailDto.getBankBranch());
	}
	short s = new Integer(actionForm.getStageOfficeHierarchy()).shortValue();
	List<OfficeGlobalDto> officeDetailsDtos = null;

	if (actionForm.getStageOfficeHierarchy()== "0") {
		officeDetailsDtos = null;
	// To recognize center
	} else if (actionForm.getStageOfficeHierarchy()=="6") {
		officeDetailsDtos = accountingServiceFacade
				.loadCustomerForLevel(new Short("3"));
	// to recognize group
	} else if (actionForm.getStageOfficeHierarchy()=="7") {
		officeDetailsDtos = accountingServiceFacade
				.loadCustomerForLevel(new Short("2"));
	} else {
		officeDetailsDtos = accountingServiceFacade
				.loadOfficesForLevel(s);
	}

	storingSession(request, "OfficesOnHierarchy", officeDetailsDtos);
	// load main accounts
	List<GLCodeDto> accountingDtos = null;
	if (actionForm.getStageTrxnType().equals("CR")
			|| actionForm.getStageTrxnType().equals("CP")
			|| actionForm.getStageTrxnType().equals("BR")
			|| actionForm.getStageTrxnType().equals("BP")
			|| actionForm.getStageTrxnType().equals("JV")) {
		accountingDtos = accountingServiceFacade.auditAccountHeads();
	}

	actionForm.setTransactionDetailID(new Integer(viewStageTransactionsDto.getTransactionID()).toString());
	actionForm.setStageMainAccount(viewStageTransactionsDto.getMainAccount());
	actionForm.setStageAccountHead(viewStageTransactionsDto.getSubAccount());
	actionForm.setStageNotes(viewStageTransactionsDto.getNarration());
	actionForm.setStageAmount(bigdecimalToInt(viewStageTransactionsDto.getTransactionAmount()));
	String [] Transactionno1=actionForm.getTransactionNo();
	String [] cpbpTransactionNO1=actionForm.getTransactionCpBpNo();
	for(int j=0;j<Transactionno1.length;j++)
	{

	GlMasterBO glMasterBO = new GlMasterBO();
	int stage = 1;
	List<String> amountActionList = getAmountAction(actionForm);
	List<GlDetailBO> glDetailBOList = getGlDetailBOList(actionForm,
			amountActionList,Integer.parseInt(actionForm.getTransactionDetailID()));

	glMasterBO.setTransactionMasterId(Integer.parseInt(Transactionno1[j]));
	glMasterBO.setTransactionDate(DateUtils.getDate(actionForm
			.getStageTrxnDate()));
	glMasterBO.setTransactionType(actionForm.getStageTrxnType());
	glMasterBO.setFromOfficeLevel(new Integer(actionForm.getStageOfficeHierarchy()));
	glMasterBO.setFromOfficeId(actionForm.getStageOffice());
	glMasterBO.setToOfficeLevel(new Integer(actionForm.getStageOfficeHierarchy()));
	glMasterBO.setToOfficeId(actionForm.getStageOffice());
	glMasterBO.setMainAccount(actionForm.getStageMainAccount());
	glMasterBO.setTransactionAmount(new BigDecimal(actionForm.getStageAmount()));
	glMasterBO.setAmountAction(amountActionList.get(0));
	glMasterBO.setTransactionNarration(actionForm.getStageNotes());
	glMasterBO.setStage(stage);
	glMasterBO.setGlDetailBOList(glDetailBOList);
	glMasterBO.setStatus("");// default value
	glMasterBO.setTransactionBy(0); // default value
	glMasterBO.setCreatedBy(getUserContext(request).getId());
	glMasterBO.setCreatedDate(DateUtils.getCurrentDateWithoutTimeStamp());
	accountingServiceFacade.savingAccountingTransactions(glMasterBO);
	}
}

public void approveCPBPTransactions(ConsolidatedTransactionActionForm actionForm,int TransactioncpbpNoValue,HttpServletRequest request)
{
	ViewStageTransactionsDto viewStageTransactionsDto = accountingServiceFacade
			.getstagedAccountingTransactions(TransactioncpbpNoValue);
	try{
	actionForm.setStageTrxnDate(changeDateFormat(viewStageTransactionsDto.getTransactionDate().toString()));
	}catch (Exception e) {e.printStackTrace();	}
	//actionForm.setStageTrxnDate(viewStageTransactionsDto.getTransactionDate().toString());
	actionForm.setStageOfficeHierarchy(this.getOfficeHierarchy(viewStageTransactionsDto.getOfficeLevel()));
	actionForm.setStageTrxnType(getTranType(viewStageTransactionsDto.getTransactionType()));
	actionForm.setStageMainAccount(viewStageTransactionsDto.getMainAccount());
	viewStageTransactionsDto.getSubAccount();
	actionForm.setStageAccountHead(viewStageTransactionsDto.getSubAccount());
	actionForm.setStageOffice(viewStageTransactionsDto.getFromOfficeId());
	GlDetailDto glDetailDto = accountingServiceFacade.getChequeDetails(TransactioncpbpNoValue);
	if(glDetailDto!=null){
		actionForm.setStageChequeNo(glDetailDto.getChequeNo());
		if(glDetailDto.getChequeDate()!=null){
		actionForm.setChequeDate(changeDateFormat(glDetailDto.getChequeDate().toString()));
		}
		actionForm.setStageBankName(glDetailDto.getBankName());
		actionForm.setStageankBranch(glDetailDto.getBankBranch());
	}
	short s = new Integer(actionForm.getStageOfficeHierarchy()).shortValue();
	List<OfficeGlobalDto> officeDetailsDtos = null;

	if (actionForm.getStageOfficeHierarchy()== "0") {
		officeDetailsDtos = null;
	// To recognize center
	} else if (actionForm.getStageOfficeHierarchy()=="6") {
		officeDetailsDtos = accountingServiceFacade
				.loadCustomerForLevel(new Short("3"));
	// to recognize group
	} else if (actionForm.getStageOfficeHierarchy()=="7") {
		officeDetailsDtos = accountingServiceFacade
				.loadCustomerForLevel(new Short("2"));
	} else {
		officeDetailsDtos = accountingServiceFacade
				.loadOfficesForLevel(s);
	}

	storingSession(request, "OfficesOnHierarchy", officeDetailsDtos);
	// load main accounts
	List<GLCodeDto> accountingDtos = null;
	if (actionForm.getStageTrxnType().equals("CR")
			|| actionForm.getStageTrxnType().equals("CP")
			|| actionForm.getStageTrxnType().equals("BR")
			|| actionForm.getStageTrxnType().equals("BP")
			|| actionForm.getStageTrxnType().equals("JV")) {
		accountingDtos = accountingServiceFacade.auditAccountHeads();
	}

	actionForm.setTransactionDetailID(new Integer(viewStageTransactionsDto.getTransactionID()).toString());
	actionForm.setStageMainAccount(viewStageTransactionsDto.getMainAccount());
	actionForm.setStageAccountHead(viewStageTransactionsDto.getSubAccount());
	actionForm.setStageNotes(viewStageTransactionsDto.getNarration());
	actionForm.setStageAmount(bigdecimalToInt(viewStageTransactionsDto.getTransactionAmount()));
	String [] cpbpTransactionNO1=actionForm.getTransactionCpBpNo();
	for(int j=0;j<cpbpTransactionNO1.length;j++)
	{

	GlMasterBO glMasterBO = new GlMasterBO();
	int stage = 1;
	List<String> amountActionList = getAmountAction(actionForm);
	List<GlDetailBO> glDetailBOList = getGlDetailBOList(actionForm,
			amountActionList,Integer.parseInt(actionForm.getTransactionDetailID()));

	glMasterBO.setTransactionMasterId(Integer.parseInt(cpbpTransactionNO1[j]));
	glMasterBO.setTransactionDate(DateUtils.getDate(actionForm
			.getStageTrxnDate()));
	glMasterBO.setTransactionType(actionForm.getStageTrxnType());
	glMasterBO.setFromOfficeLevel(new Integer(actionForm.getStageOfficeHierarchy()));
	glMasterBO.setFromOfficeId(actionForm.getStageOffice());
	glMasterBO.setToOfficeLevel(new Integer(actionForm.getStageOfficeHierarchy()));
	glMasterBO.setToOfficeId(actionForm.getStageOffice());
	glMasterBO.setMainAccount(actionForm.getStageMainAccount());
	glMasterBO.setTransactionAmount(new BigDecimal(actionForm.getStageAmount()));
	glMasterBO.setAmountAction(amountActionList.get(0));
	glMasterBO.setTransactionNarration(actionForm.getStageNotes());
	glMasterBO.setStage(stage);
	glMasterBO.setGlDetailBOList(glDetailBOList);
	glMasterBO.setStatus("");// default value
	glMasterBO.setTransactionBy(0); // default value
	glMasterBO.setCreatedBy(getUserContext(request).getId());
	glMasterBO.setCreatedDate(DateUtils.getCurrentDateWithoutTimeStamp());
	accountingServiceFacade.savingAccountingTransactions(glMasterBO);
	}
}
	public ActionForward reject(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception
            {
		 ConsolidatedTransactionActionForm actionForm = (ConsolidatedTransactionActionForm) form;
		 String [] Transactionno=actionForm.getTransactionNo();
		 if(Transactionno !=null)
		 {
			for(int i=0;i<Transactionno.length;i++)
			{
				int transactioncrbrNoValue=Integer.parseInt( Transactionno[i]);
		accountingServiceFacade.approveStageAccountingTransactions(
				transactioncrbrNoValue, ConsolidatedTransactionActionForm.rejectStage);
			}
		 }
			String [] cpbpTransactionNO=actionForm.getTransactionCpBpNo();
			 if(cpbpTransactionNO !=null)
			 {
			for(int i=0;i<cpbpTransactionNO.length;i++)
			{
				int transactioncpbpNoValue=Integer.parseInt( cpbpTransactionNO[i]);
				accountingServiceFacade.approveStageAccountingTransactions(
						transactioncpbpNoValue, ConsolidatedTransactionActionForm.rejectStage);
			}
			 }
        return mapping.findForward("submit_success");
            }
	
	
	List<GlDetailBO> getGlDetailBOList(ConsolidatedTransactionActionForm actionForm,
			List<String> amountActionList,int transactionDetailID) {
		List<GlDetailBO> glDetailBOList = new ArrayList<GlDetailBO>();
		glDetailBOList.add(new GlDetailBO(transactionDetailID,actionForm.getStageAccountHead(),
				new BigDecimal(actionForm.getStageAmount()),
				amountActionList.get(1), actionForm.getStageChequeNo(), DateUtils
						.getDate(actionForm.getChequeDate()), actionForm
						.getStageBankName(), actionForm.getStageankBranch()));
		return glDetailBOList;
	}
	public List<String> getAmountAction(ConsolidatedTransactionActionForm actionForm) {
		List<String> amountActionList = new ArrayList<String>();

		if (actionForm.getStageTrxnType().equals("CR")
				|| actionForm.getStageTrxnType().equals("BR") ) {
			amountActionList.add("debit");// for MainAccount amountAction
			amountActionList.add("credit");// for SubAccount amountAction
		} else if (actionForm.getStageTrxnType().equals("CP")
				|| actionForm.getStageTrxnType().equals("BP")) {
			amountActionList.add("credit");// for MainAccount amountAction
			amountActionList.add("debit");// for SubAccount amountAction
		} else if (actionForm.getStageTrxnType().equals("JV")){
			amountActionList.add("debit");// for MainAccount amountAction
			amountActionList.add("credit");// for SubAccount amountAction
		}

		return amountActionList;
	}

	  public void storingSession(HttpServletRequest httpServletRequest, String s,
				Object o) {
			httpServletRequest.getSession().setAttribute(s, o);
		}
	  public int nullIntconv(String str) {
			int conv = 0;
			if (str == null) {
				str = "0";
			} else if ((str.trim()).equals("null")) {
				str = "0";
			} else if (str.equals("")) {
				str = "0";
			}
			try {
				conv = Integer.parseInt(str);
			} catch (Exception e) {

			}
			return conv;
		}
	  public String getOfficeHierarchy(String office){

			String returnValue = "1";

			if(office.equals("Head Office")){
				returnValue = "1";
			}
			if(office.equals("Regional Office")){
				returnValue = "2";
			}
			if(office.equals("Divisional Office")){
				returnValue = "3";
			}
			if(office.equals("Area Office")){
				returnValue = "4";
			}
			if(office.equals("Branch Office")){
				returnValue = "5";
			}
			if(office.equals("Center")){
				returnValue = "6";
			}
			if(office.equals("Group")){
				returnValue = "7";
			}


			return returnValue;
		}
	  public String getTranType(String tranxnType){
			String returntranxn = null;
			if(tranxnType.equals("Cash Receipt")){
				returntranxn = "CR";
			}
			if(tranxnType.equals("Cash Payment")){
				returntranxn = "CP";
			}
			if(tranxnType.equals("Bank Receipt")){
				returntranxn = "BR";
			}
			if(tranxnType.equals("Bank Payment")){
				returntranxn =  "BP";
			}
			if (tranxnType.equals("Journal Voucher")) {
				returntranxn = "JV";
			}
			return returntranxn;
		}
	  public String bigdecimalToInt(BigDecimal amount){
			return new Integer(amount.intValue()).toString();
		}
	  
	  public String changeDateFormat(String date){
			String[] yymmdds = date.split("-");

			return yymmdds[2]+"/"+yymmdds[1]+"/"+yymmdds[0];
		}
}
