package org.mifos.accounting.struts.action;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.Format;
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
import org.joda.time.LocalDate;
import org.mifos.accounting.struts.actionform.MultipleGeneralLedgerActionForm;
import org.mifos.accounting.struts.actionform.ProcessAccountingTransactionsActionForm;
import org.mifos.accounting.struts.actionform.VoucherBranchMappingActionForm;
import org.mifos.application.accounting.business.GlDetailBO;
import org.mifos.application.accounting.business.GlMasterBO;
import org.mifos.application.servicefacade.AccountingServiceFacade;
import org.mifos.application.servicefacade.AccountingServiceFacadeWebTier;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.dto.domain.CoaNamesDto;
import org.mifos.dto.domain.DynamicOfficeDto;
import org.mifos.dto.domain.GLCodeDto;
import org.mifos.dto.domain.OfficeGlobalDto;
import org.mifos.dto.domain.OfficesList;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoucherBranchMappingAction extends BaseAction {
	private static final Logger logger = LoggerFactory
			.getLogger(VoucherBranchMappingAction.class);
	private AccountingServiceFacade accountingServiceFacade = new AccountingServiceFacadeWebTier();


	    public ActionForward load(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
	            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
	        logger.debug("start Load method of loan Product Action");
	        VoucherBranchMappingActionForm actionForm = (VoucherBranchMappingActionForm) form;
			java.util.Date trxnDate = DateUtils.getCurrentDateWithoutTimeStamp();
			//actionForm.setTransactiondate(trxnDate);
	        List<OfficeGlobalDto> officeDetailsDtos = null;
	        UserContext context = getUserContext(request);
			actionForm.setOfficeLevelId(String.valueOf(context.getOfficeLevelId()));
			List<OfficesList> offices = new ArrayList<OfficesList>();


	        short branches=5;
	        officeDetailsDtos = accountingServiceFacade.loadOfficesForLevel(branches);

	        List<GLCodeDto> accountingDtos  = accountingServiceFacade.coaBranchAccountHead();
	        List<GLCodeDto> coaNameslist= new ArrayList<GLCodeDto>();
	        storingSession(request, "coaNamesList", coaNameslist);
	        storingSession(request, "MainAccountGlCodes", null);
	        actionForm.setBranch("");actionForm.setTransactiondate("");
	        storingSession(request, "OfficesOnHierarchy",  officeDetailsDtos);

	        return mapping.findForward(ActionForwards.load_success.toString());
	    }
	    public ActionForward loadCoaNames(ActionMapping mapping,
				ActionForm form, HttpServletRequest request,
				@SuppressWarnings("unused") HttpServletResponse response)
				throws Exception {

		VoucherBranchMappingActionForm actionForm = (VoucherBranchMappingActionForm) form;
			String lastupdatedateStringvalue=accountingServiceFacade.getLastProcessUpdatedDate(actionForm.getBranch());

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
		    } catch (ParseException e) {
		        e.printStackTrace();
		    }



		//List<CoaNamesDto> coaNamesDtos=accountingServiceFacade.loadCoaNames(actionForm.getBranch());
		List<GLCodeDto> coabranchvalues =accountingServiceFacade.loadCoaBranchNames(actionForm.getBranch());
		List<GLCodeDto> coaNameslist=null;
		coaNameslist=new ArrayList<GLCodeDto>();
		int sno=1;
		for(GLCodeDto coanamesdto:coabranchvalues)
		{

			GLCodeDto coanames=new GLCodeDto();
			coanames.setSno(sno);
			coanames.setCoaName(coanamesdto.getGlname());
			coaNameslist.add(coanames);
			sno++;
		}
			storingSession(request, "coaNamesList", coaNameslist);
			return mapping.findForward(ActionForwards.load_success.toString());
		}

	    public ActionForward preview(ActionMapping mapping, ActionForm form,
				HttpServletRequest request,
				@SuppressWarnings("unused") HttpServletResponse response)
				throws Exception {
		VoucherBranchMappingActionForm actionForm = (VoucherBranchMappingActionForm) form;
		String[] coanames= actionForm.getCoaname();
		String[] amounts=actionForm.getAmount();
		String[] notes=actionForm.getTransactionnotes();
		List<GLCodeDto> coaNameslist=null;
		int sno=1;int total=0;
		coaNameslist=new ArrayList<GLCodeDto>();
		for(int i=0;i<coanames.length;i++)
		{
			total=total +	Integer.parseInt(amounts[i]);
			GLCodeDto GLcodedto=new GLCodeDto();
			GLcodedto.setSno(sno);sno++;
			GLcodedto.setCoaName(coanames[i]);
			GLcodedto.setTrannotes(notes[i]);
			GLcodedto.setAmounts(amounts[i]);
			coaNameslist.add(GLcodedto);
		}
		actionForm.setTotal(Integer.toString(total));
		storingSession(request, "coaNamesList", coaNameslist);
			storingSession(request, "VoucherBranchMappingActionForm", actionForm);
			return mapping.findForward(ActionForwards.preview_success.toString());
		}

	    public ActionForward previous(ActionMapping mapping, ActionForm form,
				HttpServletRequest request,
				@SuppressWarnings("unused") HttpServletResponse response)
				throws Exception {

		VoucherBranchMappingActionForm actionForm = (VoucherBranchMappingActionForm) form;
		String[] coanames= actionForm.getCoaname();
		String[] amounts=actionForm.getAmount();
		String[] notes=actionForm.getTransactionnotes();
		List<GLCodeDto> coaNameslist=null;
		int sno=1;
		coaNameslist=new ArrayList<GLCodeDto>();
		for(int i=0;i<coanames.length;i++)
		{

			GLCodeDto GLcodedto=new GLCodeDto();
			GLcodedto.setSno(sno);sno++;
			GLcodedto.setCoaName(coanames[i]);
			GLcodedto.setTrannotes(notes[i]);
			GLcodedto.setAmounts(amounts[i]);
			coaNameslist.add(GLcodedto);
		}
		storingSession(request, "coaNamesList", coaNameslist);
			storingSession(request, "VoucherBranchMappingActionForm", actionForm);
			return mapping.findForward(ActionForwards.previous_success.toString());
		}
	    public ActionForward submit(ActionMapping mapping,
				ActionForm form, HttpServletRequest request,
				@SuppressWarnings("unused") HttpServletResponse response)
				throws Exception {
		String  totalAmount="0";
		String[] amounts;
		VoucherBranchMappingActionForm actionForm = (VoucherBranchMappingActionForm) form;
		//storingSession(request, "coaNamesList", coaNameslist);
		List<String> amountActionList = getAmountAction(actionForm);
			List<GlDetailBO> glDetailBOList = getGlDetailBOList(actionForm,
					amountActionList);


			String[] amounts1 = actionForm.getAmount();
		 amounts = actionForm.getAmount();

		 int totalcheck=Integer.parseInt(actionForm.getTotal());
		 if(totalcheck !=0){
				GlMasterBO glMasterBO = new GlMasterBO();
					glMasterBO.setTransactionDate(DateUtils.getDate(actionForm
							.getTransactiondate()));
					glMasterBO.setTransactionType(actionForm.getTransactiontype());
					glMasterBO.setFromOfficeLevel(new Integer("5"));
					glMasterBO.setFromOfficeId(actionForm.getBranch());
					glMasterBO
							.setToOfficeLevel(new Integer("5"));
					glMasterBO.setToOfficeId(actionForm.getBranch());
					glMasterBO.setMainAccount(actionForm.getMainAccount());
					glMasterBO.setTransactionAmount(new BigDecimal(Integer.parseInt(actionForm.getTotal())));
					glMasterBO.setAmountAction(amountActionList.get(0));
					glMasterBO.setTransactionNarration("success");
					glMasterBO.setGlDetailBOList(glDetailBOList);
					glMasterBO.setStatus("");// default value
					glMasterBO.setStage(0);
					glMasterBO.setTransactionBy(0); // default value
					glMasterBO.setCreatedBy(getUserContext(request).getId());
					glMasterBO.setCreatedDate(DateUtils.getCurrentDateWithoutTimeStamp());
					accountingServiceFacade.savingAccountingTransactions(glMasterBO);


					actionForm.setTransactiontype(null);

		 }


			return mapping.findForward(ActionForwards.submit_success.toString());
		}
	    public ActionForward loadMainAccounts(ActionMapping mapping,
				ActionForm form, HttpServletRequest request,
				@SuppressWarnings("unused") HttpServletResponse response)
				throws Exception {
		VoucherBranchMappingActionForm actionForm = (VoucherBranchMappingActionForm) form;
			List<GLCodeDto> accountingDtos = null;
			if (actionForm.getTransactiontype().equals("CR")
					|| actionForm.getTransactiontype().equals("CP")) {
				accountingDtos = accountingServiceFacade.mainAccountForCash();
			} else if (actionForm.getTransactiontype().equals("BR")
					|| actionForm.getTransactiontype().equals("BP")) {
				accountingDtos = accountingServiceFacade.mainAccountForBank();
			}

			storingSession(request, "MainAccountGlCodes", accountingDtos);
			return mapping.findForward(ActionForwards.load_success.toString());
		}
	    public List<String> getAmountAction(VoucherBranchMappingActionForm actionForm) {
			List<String> amountActionList = new ArrayList<String>();

			if (actionForm.getTransactiontype().equals("CR")
					|| actionForm.getTransactiontype().equals("BR")) {
				amountActionList.add("debit");// for MainAccount amountAction
				amountActionList.add("credit");// for SubAccount amountAction
			} else if (actionForm.getTransactiontype().equals("CP")
					|| actionForm.getTransactiontype().equals("BP")) {
				amountActionList.add("credit");// for MainAccount amountAction
				amountActionList.add("debit");// for SubAccount amountAction
			}

			return amountActionList;
		}
	    List<GlDetailBO> getGlDetailBOList(VoucherBranchMappingActionForm actionForm,
				List<String> amountActionList) {

			List<GlDetailBO> glDetailBOList = new ArrayList<GlDetailBO>();
			String[] amounts = actionForm.getAmount();
		String[] transactionnotes  = actionForm.getTransactionnotes();
		String[] coanames=actionForm.getCoaname();
		List <CoaNamesDto> coaNamesDtolist=new ArrayList<CoaNamesDto>();
		for (int i = 0; i < actionForm.getAmount().length; i++) {
			CoaNamesDto coanamesob=new CoaNamesDto();
			coanamesob.setAmount(amounts[i]);
			coanamesob.setTrxnnotes(transactionnotes[i]);
			coanamesob.setCoaName(coanames[i]);
			coaNamesDtolist.add(coanamesob);
			}
		for(CoaNamesDto namesdto:coaNamesDtolist)
		{
			List<CoaNamesDto> subaccounthead=accountingServiceFacade.loadCoaNamesWithGlcodeValues(namesdto.getCoaName());
			for(CoaNamesDto subaccount:subaccounthead)
			{

				int amount=Integer.parseInt(namesdto.getAmount());
				if(amount>0)
				{
			glDetailBOList.add(new GlDetailBO(subaccount.getGlcodeValue(),
						new BigDecimal(namesdto.getAmount()),
						amountActionList.get(1), actionForm.getChequeNo(), DateUtils
								.getDate(actionForm.getChequeDate()), actionForm
								.getBankName(), actionForm.getBankBranch(),namesdto.getTrxnnotes()));
			}
		}
		}

			return glDetailBOList;
		}

	    public ActionForward cancel(ActionMapping mapping, ActionForm form,
				HttpServletRequest request,
				@SuppressWarnings("unused") HttpServletResponse response)
				throws Exception {
			return mapping.findForward(ActionForwards.cancel_success.toString());

		}
	    public void storingSession(HttpServletRequest httpServletRequest, String s,
				Object o) {
			httpServletRequest.getSession().setAttribute(s, o);
		}
}
