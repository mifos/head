package org.mifos.accounting.struts.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Session;
import org.mifos.accounting.struts.actionform.CoaBranchMappingActionForm;
import org.mifos.application.accounting.business.CoaBranchBO;
import org.mifos.application.servicefacade.AccountingServiceFacade;
import org.mifos.application.servicefacade.AccountingServiceFacadeWebTier;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.dto.domain.GLCodeDto;
import org.mifos.dto.domain.OfficeGlobalDto;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.struts.action.BaseAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoaBranchMappingAction extends BaseAction {

	private static final Logger logger = LoggerFactory
			.getLogger(CoaBranchMappingAction.class);

	private AccountingServiceFacade accountingServiceFacade = new AccountingServiceFacadeWebTier();


	    public ActionForward load(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
	            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
	        logger.debug("start Load method of loan Product Action");
	        CoaBranchMappingActionForm actionForm =  (CoaBranchMappingActionForm)form;
	        List<OfficeGlobalDto> officeDetailsDtos = null;
	        List<GLCodeDto> accountingDtos =null;
	        short branches=5;
	        officeDetailsDtos = accountingServiceFacade.loadOfficesForLevel(branches);
	        CoaBranchMappingActionForm acti =  (CoaBranchMappingActionForm)form;
	         accountingDtos  = accountingServiceFacade.coaBranchAccountHead();
	        List<GLCodeDto> emptyaccountingDtos= new ArrayList<GLCodeDto>();
	        storingSession(request, "CoaNamesList", accountingDtos);
	        storingSession(request, "emptycoanames", emptyaccountingDtos);
	        actionForm.setBranchoffice("");
	        storingSession(request, "OfficesOnHierarchy", null);
	        storingSession(request, "OfficesOnHierarchy", officeDetailsDtos);
	        return mapping.findForward(ActionForwards.load_success.toString());
	    }

	    public ActionForward findCoaNames(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
	            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
	        logger.debug("start Load method of loan Product Action");
	        CoaBranchMappingActionForm actionform =  (CoaBranchMappingActionForm)form;
	        List<GLCodeDto> accountingDtos =null;
	        accountingDtos  = accountingServiceFacade.coaBranchAccountHead();
	        List<GLCodeDto> remainingvalues = accountingServiceFacade.loadRemainingCoaNames(actionform.getBranchoffice());
	        List<GLCodeDto> coabranchvalues =accountingServiceFacade.loadCoaBranchNames(actionform.getBranchoffice());

	         storingSession(request, "CoaNamesList", accountingDtos);
	         storingSession(request, "emptycoanames", coabranchvalues);

	        return mapping.findForward(ActionForwards.load_success.toString());
		    }
	    public ActionForward submit(ActionMapping mapping, ActionForm form,HttpServletRequest request,@SuppressWarnings("unused") HttpServletResponse response)
				throws Exception {
			CoaBranchMappingActionForm coaBranchMappingActionForm =  (CoaBranchMappingActionForm)form;
			List<GLCodeDto> glcodelist=new ArrayList<GLCodeDto>();
			List<GLCodeDto> accountingglnames  = accountingServiceFacade.coaBranchAccountHead();
			int accinggl  = accountingServiceFacade.deleteGlobalNumRelatedCoaNames(coaBranchMappingActionForm.getBranchoffice());


			List<OfficeGlobalDto> officeDetailsDtos = null;

	        short branches=5;
	        officeDetailsDtos = accountingServiceFacade.loadOfficesForLevel(branches);
			List<Integer> glcodeint=new ArrayList<Integer>();
			CoaBranchBO coabranchbo=null;

			if(coaBranchMappingActionForm.getBranchoffice().equalsIgnoreCase("All"))
			{
				for(String glname :coaBranchMappingActionForm.getCoaBranchMainHeadglcode())
				{
					String glcode=glname;

					for( GLCodeDto accountinggln: accountingglnames )
					{
						 if(accountinggln.getGlcode().equalsIgnoreCase(glcode))
						 {
							for(OfficeGlobalDto officeGlobalDto: officeDetailsDtos)
							{
								 coabranchbo =new CoaBranchBO();
								 coabranchbo.setCoaname(accountinggln.getGlname());
								 coabranchbo.setGlcode(accountinggln.getGlcode());
								 coabranchbo.setGlobalofficenum( officeGlobalDto.getGlobalOfficeNum());
								 accountingServiceFacade.savingCoaBranchTransactions(coabranchbo);
							}

						 }}}
			}else{

			for(String glname :coaBranchMappingActionForm.getCoaBranchMainHeadglcode())
			{
				String glcode=glname;

				for( GLCodeDto accountinggln: accountingglnames )

				{
					 if(accountinggln.getGlcode().equalsIgnoreCase(glcode))
					 {

						 coabranchbo =new CoaBranchBO();
						 coabranchbo.setCoaname(accountinggln.getGlname());
						 coabranchbo.setGlcode(accountinggln.getGlcode());
						 coabranchbo.setGlobalofficenum(coaBranchMappingActionForm.getBranchoffice());
						 accountingServiceFacade.savingCoaBranchTransactions(coabranchbo);
					 }
				}
			}}
			List<GLCodeDto> coabranchvalues =accountingServiceFacade.loadCoaBranchNames(coaBranchMappingActionForm.getBranchoffice());

	        List<GLCodeDto> accountingDtos =null;
	        CoaBranchMappingActionForm acti =  (CoaBranchMappingActionForm)form;
	         accountingDtos  = accountingServiceFacade.coaBranchAccountHead();
	        List<GLCodeDto> emptyaccountingDtos= new ArrayList<GLCodeDto>();
	        storingSession(request, "CoaNamesList", accountingDtos);
	        storingSession(request, "emptycoanames", coabranchvalues);

	        return mapping.findForward(ActionForwards.submit_success.toString());
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
