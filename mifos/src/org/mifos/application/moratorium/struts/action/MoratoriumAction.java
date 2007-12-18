package org.mifos.application.moratorium.struts.action;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.service.ClientBusinessService;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.login.struts.actionforms.LoginActionForm;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.service.MeetingBusinessService;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.moratorium.business.MoratoriumBO;
import org.mifos.application.moratorium.business.service.MoratoriumBusinessService;
import org.mifos.application.moratorium.struts.actionforms.MoratoriumActionForm;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.tabletag.TableTagConstants;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class MoratoriumAction extends BaseAction{
	
	@Override
	protected BusinessService getService() throws ServiceException {
		return ServiceFactory.getInstance().getBusinessService(
				BusinessServiceName.Office);
	}
	
	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}
	
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("moratoriumAction");
		security.allow("configure", SecurityConstants.VIEW);
		security.allow("load", SecurityConstants.VIEW);		
		security.allow("loadSearch", SecurityConstants.VIEW);		
		security.allow("edit",SecurityConstants.VIEW);
		security.allow("cancelCreate", SecurityConstants.VIEW);
		security.allow("cancelUpdate", SecurityConstants.VIEW);
		security.allow("create", SecurityConstants.VIEW);
		security.allow("get", SecurityConstants.VIEW);
		security.allow("edit", SecurityConstants.VIEW);
		security.allow("editPreview", SecurityConstants.VIEW);
		security.allow("lift", SecurityConstants.VIEW);
		security.allow("liftPreview", SecurityConstants.VIEW);
		security.allow("updateMoratorium", SecurityConstants.VIEW);
		security.allow("createConfirmed", SecurityConstants.VIEW);
		security.allow("previous", SecurityConstants.VIEW);
		security.allow("previousEdit", SecurityConstants.VIEW);
		security.allow("loadForBranch", SecurityConstants.VIEW);
		security.allow("previousEditLift", SecurityConstants.VIEW);
		security.allow("cancel", SecurityConstants.VIEW);		
		return security;
	}			
	
	
	// for edit moratorium screen
	@TransactionDemarcate(joinToken = true)
	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		//UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
		MoratoriumActionForm actionForm = (MoratoriumActionForm) form;
		
		if (null != request.getParameter(Constants.CURRENTFLOWKEY))
			request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter("currentFlowKey"));
		
		// get customerId, moratoriumId and officeId from request
		String moratoriumId = null;
		String customerId = null;
		String officeId = null;
		if(null != request.getParameter("moratoriumId"))
			moratoriumId = request.getParameter("moratoriumId");
		if(null != request.getParameter("customerId"))
			customerId = request.getParameter("customerId");
		if(null != request.getParameter("officeId"))
			officeId = request.getParameter("officeId");
		
		// get moratorium details for client/branch and save it to session, so that 
		// it can be used in actionForm for validation, i.e., overlapping of moratoriums
		List moratoriumList = new ArrayList<MoratoriumBO>();
		if(officeId != null && !officeId.equals(""))
			moratoriumList = getMoratoriumBizService().getMoratoriumByOfficeId(officeId);
		else
			moratoriumList = getMoratoriumBizService().getMoratoriumByCustomerId(customerId);
		// setting list of moratoriums in session
		SessionUtils.setCollectionAttribute("moratoriumLst", moratoriumList, request.getSession());
		
		try 
		{
			// get moratorium corresponding to the moratoriumId
			List moratoriumLst = getMoratoriumBizService().getMoratoriumById(moratoriumId);
			
			Iterator iter = moratoriumLst.iterator();
			if (iter.hasNext()) 
			{
				MoratoriumBO moratoriumBO = (MoratoriumBO) iter.next();
				
				// setting moratoriumBO in session, so that it can be used in the jsp page(editMoratoriums.jsp)
				SessionUtils.setAttribute(Constants.BUSINESS_KEY, moratoriumBO, request);
				
				// setting values of action form
				Calendar fromDateCalendar = new GregorianCalendar();
				fromDateCalendar.setTimeInMillis(moratoriumBO.getStartDate().getTime());
				int fromYear = fromDateCalendar.get(Calendar.YEAR);
				int fromMonth = fromDateCalendar.get(Calendar.MONTH) + 1;
				int fromDay = fromDateCalendar.get(Calendar.DAY_OF_MONTH);
				
				Calendar endDateCalendar = new GregorianCalendar();
				endDateCalendar.setTimeInMillis(moratoriumBO.getEndDate().getTime());
				int endYear = endDateCalendar.get(Calendar.YEAR);
				int endMonth = endDateCalendar.get(Calendar.MONTH) + 1;
				int endDay = endDateCalendar.get(Calendar.DAY_OF_MONTH);
								
				//actionForm.setFromDate(moratoriumBO.getStartDate());
				//actionForm.setEndDate(moratoriumBO.getEndDate());
				actionForm.setMoratoriumFromDateDD(String.valueOf(fromDay));
				actionForm.setMoratoriumFromDateMM(String.valueOf(fromMonth));
				actionForm.setMoratoriumFromDateYY(String.valueOf(fromYear));
				actionForm.setMoratoriumEndDateDD(String.valueOf(endDay));
				actionForm.setMoratoriumEndDateMM(String.valueOf(endMonth));
				actionForm.setMoratoriumEndDateYY(String.valueOf(endYear));
				actionForm.setMoratoriumNotes(moratoriumBO.getNotes());
			}
		}
		catch (ServiceException e) 
		{
			throw new RuntimeException(e);
		}
		
		return mapping.findForward(ActionForwards.edit_success.toString());		
	}
	
	//for edit preview screen
	@TransactionDemarcate(joinToken = true)
	public ActionForward editPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		MoratoriumActionForm actionForm = (MoratoriumActionForm) form;
		
		if (null != request.getParameter(Constants.CURRENTFLOWKEY))
			request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter("currentFlowKey"));
		
		// get officeId from request
		String officeId = null;
		if(null != request.getParameter("officeId"))
		{
			officeId = request.getParameter("officeId");
			// setting officeId in action form, so that it can be used in updateMoratorium() method
			actionForm.setOfficeId(officeId);
		}
		
		// get moratoriumId from request
		String moratoriumId = null;		
		if(null != request.getParameter("moratoriumId"))
		{
			moratoriumId = request.getParameter("moratoriumId");
			// setting moratoriumId in action form, so that it can be used in updateMoratorium() method
			actionForm.setMoratoriumId(moratoriumId);
		}
		
		try 
		{
			// get moratorium corresponding to the moratoriumId
			List moratoriumLst = getMoratoriumBizService().getMoratoriumById(moratoriumId);
			
			Iterator iter = moratoriumLst.iterator();
			if (iter.hasNext()) 
			{
				MoratoriumBO moratoriumBO = (MoratoriumBO) iter.next();
				
				// setting moratoriumBO in session, so that it can be used in the jsp page(editMoratoriumsPreview.jsp)
				SessionUtils.setAttribute(Constants.BUSINESS_KEY, moratoriumBO, request);
			}
		}
		catch (ServiceException e) 
		{
			throw new RuntimeException(e);
		}
		
		return mapping.findForward(ActionForwards.editpreview_success.toString());		
	}
	
	// for update moratorium from "edit preview screen"
	@TransactionDemarcate(joinToken = true)
	public ActionForward updateMoratorium(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		MoratoriumActionForm actionForm = (MoratoriumActionForm) form;		
		
		if (null != request.getParameter(Constants.CURRENTFLOWKEY))
			request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter("currentFlowKey"));
		
		// get value of "isLift" from actionForm which determines whether we are lifting moratorium or extending it.
		String isLift = null;
		if (null != actionForm.getIsLift())
		{
			isLift = actionForm.getIsLift();
			actionForm.setIsLift(null);
		}
		
		// get moratoriumId from request
		String moratoriumId = null;		
		if(null != actionForm.getMoratoriumId())
			moratoriumId = actionForm.getMoratoriumId();		
		
		try 
		{
			// get moratorium corresponding to the moratoriumId
			List moratoriumLst = getMoratoriumBizService().getMoratoriumById(moratoriumId);
			
			Iterator iter = moratoriumLst.iterator();
			if (iter.hasNext()) 
			{
				MoratoriumBO moratoriumBO = (MoratoriumBO) iter.next();
				
				// updating moratoriumBO object and saving it to database
				moratoriumBO.setEndDate((Date) actionForm.getEndDate());
				moratoriumBO.save();
												
				if(actionForm.getOfficeId() != null && actionForm.getOfficeId() != "")
				{
					// officeId
					String officeId = actionForm.getOfficeId();
					// getting office
					OfficeBO officeBO = null;
					officeBO = ((OfficeBusinessService) getService()).getOffice(Short.valueOf(officeId));
					Set clients = officeBO.getClients();
					if(clients != null)
					{
						Iterator clientsIter = clients.iterator();
						while (clientsIter.hasNext()) 
						{
							CustomerBO client = (CustomerBO) clientsIter.next();
							client.getCustomerId();
							
							// call method to reschedule loan and savings accounts
							// we will reschedule only for center and clients which belong to this branch, because
							// calling a reschedule on center will reschedule for it's corresponsding groups and clients
							CustomerLevel customerLevel = client.getLevel();
							Short id = customerLevel.getValue();
							if(id == 3)
								callRescsheduleLoanAndSavingsAccount(client, actionForm, officeId, isLift);
							else if(id == 1)
							{
								// get moratorium parent of this client(i.e., group)
								// if it's parent customer is null, then only we need to call reschdule, because
								// a client without group belongs to branch and for a client belonging to a group
								// we don't have to call reschedule, it is taken care by reschedule of center.
								CustomerBO group = client.getParentCustomer();
								if(group == null)
									callRescsheduleLoanAndSavingsAccount(client, actionForm, officeId, isLift);
							}
						}
					}					
				}
				else
				{
					// getting customer			
					CustomerBO client = getClientBusinessService().getClient(getIntegerValue(moratoriumBO.getCustomerId()));
					
					// call method to reschedule loan and savings accounts
					callRescsheduleLoanAndSavingsAccount(client, actionForm, null, isLift);
				}
			}
		}
		catch (ServiceException e) 
		{
			throw new RuntimeException(e);
		}
		
		return mapping.findForward(ActionForwards.update_success.toString());		
	}
	
	// for edit screen when ctrl is coming from editMoratoriumsPreview.jsp
	@TransactionDemarcate(joinToken = true)
	public ActionForward previousEdit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		if (null != request.getParameter(Constants.CURRENTFLOWKEY))
			request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter("currentFlowKey"));
		
		return mapping.findForward("edit_previous_success");
	}
	
	// for lift screen when ctrl is coming from liftMoratoriumsPreview.jsp
	@TransactionDemarcate(joinToken = true)
	public ActionForward previousEditLift(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		if (null != request.getParameter(Constants.CURRENTFLOWKEY))
			request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter("currentFlowKey"));
		
		return mapping.findForward("lift_previous_success");
	}
	
	// for view moratorium screen
	@TransactionDemarcate(saveToken = true)
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		if (null != request.getParameter(Constants.CURRENTFLOWKEY))
			request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter("currentFlowKey"));
		
		try 
		{
			List activeMoratoriums = new ArrayList();
			List closedMoratoriums = new ArrayList();
			List moratoriumLst = getMoratoriumBizService().getMoratoriums();
			
			Iterator iter = moratoriumLst.iterator();
			while (iter.hasNext()) 
			{
				MoratoriumBO moratoriumBO = (MoratoriumBO) iter.next();
				
				// get string value for date and set it into moratoriumBO, for display purpose on screen
				java.util.Date startDate = DateUtils.getDateWithoutTimeStamp(moratoriumBO.getStartDate().getTime());
				java.util.Date endDate = DateUtils.getDateWithoutTimeStamp(moratoriumBO.getEndDate().getTime());
				Calendar startDateCalendar = new GregorianCalendar();
				startDateCalendar.setTimeInMillis(startDate.getTime());
				int startYear = startDateCalendar.get(Calendar.YEAR);
				int startMonth = startDateCalendar.get(Calendar.MONTH);
				int startDay = startDateCalendar.get(Calendar.DAY_OF_MONTH);
				Calendar endDateCalendar = new GregorianCalendar();
				endDateCalendar.setTimeInMillis(endDate.getTime());
				int endYear = endDateCalendar.get(Calendar.YEAR);
				int endMonth = endDateCalendar.get(Calendar.MONTH);
				int endDay = endDateCalendar.get(Calendar.DAY_OF_MONTH);
				moratoriumBO.setStartDateString(startDay + "/" + startMonth + "/" + startYear);
				moratoriumBO.setEndDateString(endDay + "/" + endMonth + "/" + endYear);
				
				if(moratoriumBO.getLiftDate() == null)
				{
					// clarification is required from community
					if(DateUtils.getDateWithoutTimeStamp(moratoriumBO.getEndDate().getTime()).compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) < 0)
						closedMoratoriums.add(moratoriumBO);
					else
						activeMoratoriums.add(moratoriumBO);
					
					//activeMoratoriums.add(moratoriumBO); // remove after getting clarification
				}
				else
					closedMoratoriums.add(moratoriumBO);
			}
			// setting list of moratoriums in session			
			request.setAttribute("openMoratoriums", activeMoratoriums);
			request.setAttribute("closedMoratoriums", closedMoratoriums);
		}
		catch (ServiceException e) 
		{
			throw new RuntimeException(e);
		}
		
		return mapping.findForward(ActionForwards.get_success.toString());		
	}
	
	// for edit screen when ctrl is coming from applyMoratoriumConfirm.jsp or applyBranchMoratoriumConfirm.jsp
	@TransactionDemarcate(joinToken = true)
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		if (null != request.getParameter(Constants.CURRENTFLOWKEY))
			request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter("currentFlowKey"));
		
		String officeId = request.getParameter("officeId");
		if(officeId == null)
			return mapping.findForward(ActionForwards.previous_success.toString());
		else
			return mapping.findForward("previous_success_for_branch");
	}
	
	private MoratoriumBusinessService getMoratoriumBizService() {
		return new MoratoriumBusinessService();
	}
	
	public ActionForward configure(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		return mapping.findForward("configure");
	}
	
	@TransactionDemarcate(saveToken = true)
	public ActionForward loadSearch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		MoratoriumActionForm actionForm = (MoratoriumActionForm) form;
		//actionForm.setSearchString(null);
        if (request.getParameter("perspective") != null) {
            request.setAttribute("perspective", request.getParameter("perspective"));
        }
        cleanUpSearch(request);	
		
        if("1".equals(actionForm.getSearchString()))
        {
        	// code for search logic of branches [start]
        	UserContext userContext = (UserContext) SessionUtils.getAttribute(
    				Constants.USER_CONTEXT_KEY, request.getSession());
        	List<OfficeBO> officeList = getOffices(userContext, ((OfficeBusinessService) getService()).getOfficesTillBranchOffice());
    		SessionUtils.setCollectionAttribute(OfficeConstants.GET_HEADOFFICE, getOffice(officeList, OfficeLevel.HEADOFFICE), request);
    		SessionUtils.setCollectionAttribute(OfficeConstants.GET_REGIONALOFFICE, getOffice(officeList, OfficeLevel.REGIONALOFFICE), request);
    		SessionUtils.setCollectionAttribute(OfficeConstants.GET_SUBREGIONALOFFICE, getOffice(officeList, OfficeLevel.SUBREGIONALOFFICE), request);
    		SessionUtils.setCollectionAttribute(OfficeConstants.GET_AREAOFFICE, getOffice(officeList, OfficeLevel.AREAOFFICE), request);
        	SessionUtils.setCollectionAttribute(OfficeConstants.GET_BRANCHOFFICE, getOffices(userContext, ((OfficeBusinessService) getService()).getBranchOffices()), request);
        	loadofficeLevels(request);
        	// code for search logic of branches [end]
        	return mapping.findForward("loadSearchForBranch");
        }
        else
        	return mapping.findForward("loadSearch");
	}
	
	@TransactionDemarcate(saveToken = true) // for links on moratoriumSearchResult.jsp
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		MoratoriumActionForm actionForm = (MoratoriumActionForm) form;
		actionForm.clear();
		
		// customerId
		String customerId = request.getParameter("customerId");
		//ClientBO clientInSession = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
		if(customerId != null)
		{
			//ClientBO client = getClientBusinessService().getClient(getIntegerValue(customerId));
			CustomerBO client = getClientBusinessService().getClient(getIntegerValue(customerId));
			request.setAttribute("customerId", request.getParameter("customerId"));
			SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
		}
		
        if (request.getParameter("perspective") != null) {
            request.setAttribute("perspective", request.getParameter("perspective"));
        }        	
		
        // get moratorium details for client and save it to session, so that 
		// it can be used in actionForm for validation, i.e., overlapping of moratoriums
		List moratoriumLst = getMoratoriumBizService().getMoratoriumByCustomerId(customerId);
		// setting list of moratoriums in session			
		//request.setAttribute("moratoriumLst", moratoriumLst);
		//request.getSession().setAttribute("moratoriumLst", moratoriumLst);		
		SessionUtils.setCollectionAttribute("moratoriumLst", moratoriumLst, request.getSession());
        
		return mapping.findForward("load");
	}
	
	// for apply moratorium screen (applyMoratorium.jsp and applyMoratoriumForBranch.jsp)
	@TransactionDemarcate(saveToken = true)
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {               	
		
		// customerId
		String customerId = request.getParameter("customerId");		
		if(customerId != null)
		{
			// getting customer
			ClientBO client = getClientBusinessService().getClient(getIntegerValue(customerId));
			request.setAttribute("customerId", customerId);
			SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
			
			return mapping.findForward("create");
		}
		else
		{
			// officeId
			String officeId = request.getParameter("officeId");
			
			// getting office
			OfficeBO officeBO = null;
			officeBO = ((OfficeBusinessService) getService()).getOffice(Short.valueOf(officeId));
			request.setAttribute("officeId", officeId);
			SessionUtils.setAttribute(Constants.BUSINESS_KEY, officeBO, request);
			
			return mapping.findForward("createForBranch");
		}
	}
	
	// for apply moratorium confirm screen (applyMoratoriumConfirm.jsp & applyBranchMoratoriumConfirm.jsp)
	@TransactionDemarcate(saveToken = true)
	public ActionForward createConfirmed(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {               	
		
		UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
		String createdBy = userContext.getName();
		java.util.Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
		Calendar currentDateCalendar = new GregorianCalendar();
		currentDateCalendar.setTimeInMillis(currentDate.getTime());
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		if(createdBy != null && !createdBy.equals(""))
			createdBy = createdBy + " " + day + "/" + month + "/" + year;
		
		MoratoriumActionForm actionForm = (MoratoriumActionForm) form;		
		
		// customerId
		String customerId = request.getParameter("customerId");		
		if(customerId != null)
		{
			// getting customer			
			CustomerBO client = getClientBusinessService().getClient(getIntegerValue(customerId));
			
			request.setAttribute("customerId", customerId);
			SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);			
			
			// call method to reschedule loan and savings accounts
			callRescsheduleLoanAndSavingsAccount(client, actionForm, null, null);
			
			//save record to Moratorium table (refer holidayAction - update method)
			MoratoriumBO moratoriumBO = new MoratoriumBO();
			moratoriumBO.setAppliedTo(client.getDisplayName());
			moratoriumBO.setStartDate((Date) actionForm.getFromDate());
			moratoriumBO.setEndDate((Date) actionForm.getEndDate());			
			moratoriumBO.setMorCreatedBy(createdBy);
			moratoriumBO.setNotes(actionForm.getMoratoriumNotes());
			moratoriumBO.setCustomerId(customerId);
			// commented for testing, uncomment later
			moratoriumBO.save();			
		}
		else
		{
			// officeId
			String officeId = request.getParameter("officeId");
			
			// getting office
			OfficeBO officeBO = null;
			officeBO = ((OfficeBusinessService) getService()).getOffice(Short.valueOf(officeId));
			Set clients = officeBO.getClients();
			Iterator iter = clients.iterator();
			while (iter.hasNext()) 
			{
				CustomerBO client = (CustomerBO) iter.next();
				client.getCustomerId();
				
				// call method to reschedule loan and savings accounts
				// we will reschedule only for center and clients which belong to this branch, because
				// calling a reschedule on center will reschedule for it's corresponsding groups and clients
				CustomerLevel customerLevel = client.getLevel();
				Short id = customerLevel.getValue();
				if(id == 3)
					callRescsheduleLoanAndSavingsAccount(client, actionForm, officeId, null);
				else if(id == 1)
				{
					// get moratorium parent of this client(i.e., group)
					// if it's parent customer is null, then only we need to call reschdule, because
					// a client without group belongs to branch and for a client belonging to a group
					// we don't have to call reschedule, it is taken care by reschedule of center.
					CustomerBO group = client.getParentCustomer();
					if(group == null)
						callRescsheduleLoanAndSavingsAccount(client, actionForm, officeId, null);
				}				
			}
			
			// save record to Moratorium table (refer holidayAction - update method)
			MoratoriumBO moratoriumBO = new MoratoriumBO();
			moratoriumBO.setAppliedTo(officeBO.getOfficeName());
			moratoriumBO.setStartDate((Date) actionForm.getFromDate());
			moratoriumBO.setEndDate((Date) actionForm.getEndDate());			
			moratoriumBO.setMorCreatedBy(createdBy);
			moratoriumBO.setNotes(actionForm.getMoratoriumNotes());			
			moratoriumBO.setOfficeId(officeId);
			// commented for testing, uncomment later
			moratoriumBO.save();
		}
		
		return mapping.findForward("create_success");
		
	}
	
	@TransactionDemarcate(saveToken = true) // for links on configureBranchMoratorium.jsp
	public ActionForward loadForBranch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		MoratoriumActionForm actionForm = (MoratoriumActionForm) form;
		actionForm.clear();
		
		// officeId
		String officeId = request.getParameter("officeId");
		
		if(officeId != null)
		{
			OfficeBO officeBO = null;
			officeBO = ((OfficeBusinessService) getService()).getOffice(Short.valueOf(officeId));
			
			request.setAttribute("officeId", request.getParameter("officeId"));
			SessionUtils.setAttribute(Constants.BUSINESS_KEY, officeBO, request);
		}
		
        if (request.getParameter("perspective") != null) {
            request.setAttribute("perspective", request.getParameter("perspective"));
        }        	
		
        // get moratorium details for branch office and save it to session, so that 
		// it can be used in actionForm for validation, i.e., overlapping of moratoriums
		List moratoriumLst = getMoratoriumBizService().getMoratoriumByOfficeId(officeId);
		// setting list of moratoriums in session			
		//request.setAttribute("moratoriumLst", moratoriumLst);
		//request.getSession().setAttribute("moratoriumLst", moratoriumLst);		
		SessionUtils.setCollectionAttribute("moratoriumLst", moratoriumLst, request.getSession());
        
		return mapping.findForward("loadForBranch");
	}
	
	private void callRescsheduleLoanAndSavingsAccount(CustomerBO client, MoratoriumActionForm actionForm, String officeId, String isLift) throws Exception
	{
		// getting clientLevel >> it will decide type of client
		// 3 stands for "center", 2 stands for "group" and 1 stands for "client"
		CustomerLevel customerLevel = client.getLevel();
		Short id = customerLevel.getValue();
		if(id == 3)
		{	
			// get moratorium list for this center
			List centerMoratoriumLst = getMoratoriumBizService().getMoratoriumByCustomerId(client.getCustomerId().toString());
			
			// get moratorium list for office, if officeId is given
			if(officeId != null)
			{				
				List branchMoratoriumLst = getMoratoriumBizService().getMoratoriumByOfficeId(officeId);
				centerMoratoriumLst.addAll(branchMoratoriumLst);
			}
			
			// rescheduling for savings account of center(loan account is not applicable for center)
			if(null != isLift && isLift.equals("true"))
				rescheduleLoanAndSavingsAccountsForLift(client, actionForm, centerMoratoriumLst);
			else
				rescheduleLoanAndSavingsAccounts(client, actionForm, centerMoratoriumLst);
			
			// rescheduling for loan and savings account of groups under this center
			Set groups = client.getChildren();
			if(groups != null)
			{
				Iterator groupsIter = groups.iterator();
				while (groupsIter.hasNext()) 
				{
					//ClientBO group = (ClientBO) groupsIter.next();
					CustomerBO group = (CustomerBO) groupsIter.next();
					
					// get moratorium list for this group
					List groupMoratoriumLst = getMoratoriumBizService().getMoratoriumByCustomerId(client.getCustomerId().toString());
					groupMoratoriumLst.addAll(centerMoratoriumLst);
					
					if(null != isLift && isLift.equals("true"))
						rescheduleLoanAndSavingsAccountsForLift(group, actionForm, groupMoratoriumLst);
					else
						rescheduleLoanAndSavingsAccounts(group, actionForm, groupMoratoriumLst);
					
					// rescheduling for loan and savings account of clients under this group
					Set clients = group.getChildren();
					if(clients != null)
					{
						Iterator clientsIter = clients.iterator();
						while (clientsIter.hasNext()) 
						{						
							CustomerBO tempClient = (CustomerBO) clientsIter.next();
							
							// get moratorium list for this client
							List clientMoratoriumLst = getMoratoriumBizService().getMoratoriumByCustomerId(client.getCustomerId().toString());
							clientMoratoriumLst.addAll(groupMoratoriumLst);
							
							if(null != isLift && isLift.equals("true"))
								rescheduleLoanAndSavingsAccountsForLift(tempClient, actionForm, clientMoratoriumLst);
							else
								rescheduleLoanAndSavingsAccounts(tempClient, actionForm, clientMoratoriumLst);
						}
					}
				}
			}			
		}
		else if(id == 2)
		{				
			// get moratorium list for parent of this group(i.e., center)				
			CustomerBO center = client.getParentCustomer();
			List centerMoratoriumLst = new ArrayList();
			if(center != null)
				centerMoratoriumLst = getMoratoriumBizService().getMoratoriumByCustomerId(center.getCustomerId().toString());				
			
			// get moratorium list for office, if officeId is given
			if(officeId != null)
			{				
				List branchMoratoriumLst = getMoratoriumBizService().getMoratoriumByOfficeId(officeId);
				centerMoratoriumLst.addAll(branchMoratoriumLst);
			}
			
			// get moratorium list for this group
			List groupMoratoriumLst = getMoratoriumBizService().getMoratoriumByCustomerId(client.getCustomerId().toString());
			
			groupMoratoriumLst.addAll(centerMoratoriumLst);
			
			// rescheduling for loan and savings account of group
			if(null != isLift && isLift.equals("true"))
				rescheduleLoanAndSavingsAccountsForLift(client, actionForm, groupMoratoriumLst);
			else
				rescheduleLoanAndSavingsAccounts(client, actionForm, groupMoratoriumLst);
			
			// rescheduling for loan and savings account of clients under this group
			Set clients = client.getChildren();
			if(clients != null)
			{
				Iterator clientsIter = clients.iterator();
				while (clientsIter.hasNext()) 
				{
					//ClientBO tempClient = (ClientBO) clientsIter.next();
					CustomerBO tempClient = (CustomerBO) clientsIter.next();
					
					// get moratorium list for this client
					List clientMoratoriumLst = getMoratoriumBizService().getMoratoriumByCustomerId(tempClient.getCustomerId().toString());
					clientMoratoriumLst.addAll(groupMoratoriumLst);
					
					// rescheduling for loan and savings account of group
					if(null != isLift && isLift.equals("true"))
						rescheduleLoanAndSavingsAccountsForLift(tempClient, actionForm, clientMoratoriumLst);
					else
						rescheduleLoanAndSavingsAccounts(tempClient, actionForm, clientMoratoriumLst);
				}
			}			
		}
		else
		{				
			// get moratorium list for this client
			List clientMoratoriumLst = getMoratoriumBizService().getMoratoriumByCustomerId(client.getCustomerId().toString());
			
			// get moratorium list for office, if officeId is given
			if(officeId != null)
			{				
				List branchMoratoriumLst = getMoratoriumBizService().getMoratoriumByOfficeId(officeId);
				clientMoratoriumLst.addAll(branchMoratoriumLst);
			}
			
			// get moratorium list for parent of this client(i.e., group)
			CustomerBO group = client.getParentCustomer();
			if(group != null)
			{
				clientMoratoriumLst.addAll(getMoratoriumBizService().getMoratoriumByCustomerId(group.getCustomerId().toString()));
				
				// get moratorium for the center
				CustomerBO center = group.getParentCustomer();				
				if(center != null)
					clientMoratoriumLst.addAll(getMoratoriumBizService().getMoratoriumByCustomerId(center.getCustomerId().toString()));
			}
			
			if(null != isLift && isLift.equals("true"))
				rescheduleLoanAndSavingsAccountsForLift(client, actionForm, clientMoratoriumLst);
			else
				rescheduleLoanAndSavingsAccounts(client, actionForm, clientMoratoriumLst);
		}
	}
	
	private void rescheduleLoanAndSavingsAccounts(CustomerBO client, MoratoriumActionForm actionForm, List moratoriumList) throws Exception
	{
		// getting meeting details
		MeetingBO meeting = null;
		if (client.getCustomerMeeting() != null) 
		{				
			meeting = getMeetingBusinessService().getMeeting(client.getCustomerMeeting().getMeeting().getMeetingId());
			meeting.getMeetingId();
		}
		
		// code for getting min date from moratorium list [start]
		// for eg >> 10/10/2007 to 12/10/2007
		//           15/10/2007 to 17/10/2007
		//           20/10/2007 to 22/10/2007
		// in above example mindate = 20/10/2007 (installments falling after this date needs to be rescheduled)
		Date minDate = null;
		Iterator iter = moratoriumList.iterator();
		while (iter.hasNext()) 
		{
			MoratoriumBO moratoriumBO = (MoratoriumBO) iter.next();
			if(minDate == null)
				minDate = moratoriumBO.getStartDate();
			else
			{
				if(DateUtils.getDateWithoutTimeStamp(moratoriumBO.getStartDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(minDate.getTime())) > 0)
					minDate = moratoriumBO.getStartDate();
			}
		}
		// code for getting min date from moratorium list [end]
		
		// code for rescheduling of loan repayment [start]
		// getting open loan accounts
		List loanAccounts = client.getOpenLoanAccounts();
		Iterator loanAccountsIter = loanAccounts.iterator();
		while (loanAccountsIter.hasNext()) 
		{
			LoanBO loanBO = (LoanBO) loanAccountsIter.next();
			//Integer accountId = loanBO.getAccountId();
			
			// getting repayment schedule			
			List modifiedInstallmentLst = new ArrayList();						
			List totalInstallmentsLst = loanBO.getAllInstallments(); // of type LoanScheduleEntity
			
			// check whether any installment falls between moratorium period or not,
			// if yes then only we need to reschedule, otherwise don't reschedule.
			// for e.g., 1	2	3	4	5	6	7  >> installment is on 1
			//           8	9	10	11	12	13	14 >> installment is on 8
			//           15	16	17	18	19	20	21 >> installment is on 15
			// if we are creating moratorium from 2-7 then we donn't need to reschedule.
			boolean chkTotalInstFlag = false;
			Iterator<LoanScheduleEntity> chkTotalInstIter = totalInstallmentsLst.iterator();
			while (chkTotalInstIter.hasNext())
			{
				LoanScheduleEntity loanScheduleEntity = chkTotalInstIter.next();
				if(!loanScheduleEntity.isPaid())
				{
					if(DateUtils.getDateWithoutTimeStamp(loanScheduleEntity.getActionDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(actionForm.getFromDate().getTime())) >= 0 && DateUtils.getDateWithoutTimeStamp(loanScheduleEntity.getActionDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(actionForm.getEndDate().getTime())) <= 0)
					{
						chkTotalInstFlag = true;
						break;
					}
				}
			}
			
			if(chkTotalInstFlag) // this means atleast one action date falls between moratorium period
			{
				Iterator totalInstlIter = totalInstallmentsLst.iterator();
				while (totalInstlIter.hasNext())
				{					
					LoanScheduleEntity obj = (LoanScheduleEntity) totalInstlIter.next();
					
					// changing actionDate for loan repayment
					// checking whether an installment is paid or not, and whether the installment date is greater than moratorium start date,
					// if installment date is greater than moratorium min date(from moratorium list) then only we need to reschedule the installment.
					//if(!obj.isPaid() && !(DateUtils.getDateWithoutTimeStamp(obj.getActionDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(actionForm.getFromDate().getTime())) < 0))
					if(minDate != null)
					{
						if(!obj.isPaid() && DateUtils.getDateWithoutTimeStamp(obj.getActionDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(minDate.getTime())) >= 0)
						{													
							Date newActionDate = null;
							newActionDate = getNewActionDate(obj, meeting, actionForm, moratoriumList);						
							obj.setActionDate(newActionDate);
							// commented for testing, uncomment later
							obj.save();
						}
					}
					else if(minDate == null) // this means we are creating moratorium first time
					{
						if(!obj.isPaid() && DateUtils.getDateWithoutTimeStamp(obj.getActionDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(actionForm.getFromDate().getTime())) >= 0)
						{													
							Date newActionDate = null;
							newActionDate = getNewActionDate(obj, meeting, actionForm, moratoriumList);						
							obj.setActionDate(newActionDate);
							// commented for testing, uncomment later
							obj.save();
						}
					}
					modifiedInstallmentLst.add(obj);
					obj = null;
				}
			}							
		}
		// code for rescheduling of loan repayment [end]
		
		// code for rescheduling of savings account [start]
		List savingsAccounts = client.getActiveSavingsAccounts();
		Iterator savingsAccountsIter = savingsAccounts.iterator();
		while (savingsAccountsIter.hasNext())
		{
			SavingsBO savingsBO = (SavingsBO) savingsAccountsIter.next();
			List modifiedSavingsInstallmentLst = new ArrayList();
			
			List totalInstallmentsLst = savingsBO.getAllInstallments(); // of type SavingsScheduleEntity
			
			// check whether any installment falls between moratorium period or not,
			// if yes then only we need to reschedule, otherwise don't reschedule.
			// for e.g., 1	2	3	4	5	6	7  >> installment is on 1
			//           8	9	10	11	12	13	14 >> installment is on 8
			//           15	16	17	18	19	20	21 >> installment is on 15
			// if we are creating moratorium from 2-7 then we donn't need to reschedule.
			boolean chkTotalInstFlag = false;
			Iterator<SavingsScheduleEntity> chkTotalInstIter = totalInstallmentsLst.iterator();
			while (chkTotalInstIter.hasNext())
			{
				SavingsScheduleEntity savingsScheduleEntity = chkTotalInstIter.next();
				if(!savingsScheduleEntity.isPaid())
				{
					if(DateUtils.getDateWithoutTimeStamp(savingsScheduleEntity.getActionDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(actionForm.getFromDate().getTime())) >= 0 && DateUtils.getDateWithoutTimeStamp(savingsScheduleEntity.getActionDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(actionForm.getEndDate().getTime())) <= 0)
					{
						chkTotalInstFlag = true;
						break;
					}
				}
			}
			
			if(chkTotalInstFlag) // this means atleast one action date falls between moratorium period
			{
				Iterator totalInstlIter = totalInstallmentsLst.iterator();
				while (totalInstlIter.hasNext())
				{
					SavingsScheduleEntity obj = (SavingsScheduleEntity) totalInstlIter.next();
					
					// store max date into temp variable (not required)
					Date maxDate = null;
					Iterator modifiedSavingsInstallmentLstIter = modifiedSavingsInstallmentLst.iterator();
					while (modifiedSavingsInstallmentLstIter.hasNext()) 
					{
						SavingsScheduleEntity tempObj = (SavingsScheduleEntity) modifiedSavingsInstallmentLstIter.next();
						if(maxDate == null)							
							maxDate = tempObj.getActionDate();
						else
						{
							if(DateUtils.getDateWithoutTimeStamp(tempObj.getActionDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(maxDate.getTime())) > 0)
								maxDate = tempObj.getActionDate();
						}
						tempObj = null;
					}
					modifiedSavingsInstallmentLstIter = null;	
					
					if(!obj.isPaid() && DateUtils.getDateWithoutTimeStamp(obj.getActionDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(minDate.getTime())) >= 0)
					{													
						Date newActionDate = null;
						newActionDate = getNewActionDate(obj, meeting, actionForm, moratoriumList);						
						obj.setActionDate(newActionDate);
						// commented for testing, uncomment later
						obj.save();
					}
					else if(minDate == null) // this means we are creating moratorium first time
					{
						if(!obj.isPaid() && DateUtils.getDateWithoutTimeStamp(obj.getActionDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(actionForm.getFromDate().getTime())) >= 0)
						{													
							Date newActionDate = null;
							newActionDate = getNewActionDate(obj, meeting, actionForm, moratoriumList);						
							obj.setActionDate(newActionDate);
							// commented for testing, uncomment later
							obj.save();
						}
					}
					modifiedSavingsInstallmentLst.add(obj);
					obj = null;
				}
			}			
		}
		// code for rescheduling of savings account [end]
	}
		
	private void rescheduleLoanAndSavingsAccountsForLift(CustomerBO client, MoratoriumActionForm actionForm, List moratoriumList) throws Exception
	{
		// getting meeting details
		MeetingBO meeting = null;
		if (client.getCustomerMeeting() != null) 
		{				
			meeting = getMeetingBusinessService().getMeeting(client.getCustomerMeeting().getMeeting().getMeetingId());
			meeting.getMeetingId();
		}
		
		// code for rescheduling of loan repayment [start]
		// getting open loan accounts
		List loanAccounts = client.getOpenLoanAccounts();
		Iterator loanAccountsIter = loanAccounts.iterator();
		while (loanAccountsIter.hasNext()) 
		{
			LoanBO loanBO = (LoanBO) loanAccountsIter.next();
			//Integer accountId = loanBO.getAccountId();
			
			// getting repayment schedule
			List totalInstallmentsLst = loanBO.getAllInstallments(); // of type LoanScheduleEntity
			
			// in case of lift moratorium get the baseActionDate for rescheduling of installments
			// based on this baseActionDate we will reschedule the action Dates which are falling
			// after the baseActionDate
			// for e.g., old moratorium >> 10/07/2007 to 10/09/2007
			//           new moratorium >> 10/07/2007 to 10/08/2007
			// in above example we need actionDate of installment which is nearest to 10/07/2007
			Date baseActionDate = null;
			Iterator<LoanScheduleEntity> chkTotalInstIter = totalInstallmentsLst.iterator();
			while (chkTotalInstIter.hasNext())
			{
				LoanScheduleEntity loanScheduleEntity = chkTotalInstIter.next();
				if(baseActionDate == null)
					baseActionDate = loanScheduleEntity.getActionDate();
				else
				{
					if(DateUtils.getDateWithoutTimeStamp(loanScheduleEntity.getActionDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(actionForm.getFromDate().getTime())) < 0 && DateUtils.getDateWithoutTimeStamp(loanScheduleEntity.getActionDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(baseActionDate.getTime())) > 0)
						baseActionDate = loanScheduleEntity.getActionDate();
				}				
			}
			
			if(baseActionDate != null)
			{
				Iterator totalInstlIter = totalInstallmentsLst.iterator();
				while (totalInstlIter.hasNext())
				{					
					LoanScheduleEntity obj = (LoanScheduleEntity) totalInstlIter.next();

					// changing actionDate for loan repayment
					if(!obj.isPaid() && DateUtils.getDateWithoutTimeStamp(obj.getActionDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(actionForm.getFromDate().getTime())) >= 0)
					{													
						Date newActionDate = null;
						newActionDate = new java.sql.Date(getNextDate(baseActionDate, meeting).getTime());
						baseActionDate = newActionDate;
						while(isBetween(newActionDate, moratoriumList))
						{
							newActionDate = new java.sql.Date(getNextDate(baseActionDate, meeting).getTime());
							baseActionDate = newActionDate;
						}
						
						obj.setActionDate(newActionDate);
						// commented for testing, uncomment later
						obj.save();
					}														
					obj = null;
				}
			}													
		}
		// code for rescheduling of loan repayment [end]
		
		// code for rescheduling of savings account [start]
		List savingsAccounts = client.getActiveSavingsAccounts();
		Iterator savingsAccountsIter = savingsAccounts.iterator();
		while (savingsAccountsIter.hasNext())
		{
			SavingsBO savingsBO = (SavingsBO) savingsAccountsIter.next();			
			
			List totalInstallmentsLst = savingsBO.getAllInstallments(); // of type SavingsScheduleEntity
			
			// in case of lift moratorium get the baseActionDate for rescheduling of installments
			// based on this baseActionDate we will reschedule the action Dates which are falling
			// after the baseActionDate
			// for e.g., old moratorium >> 10/07/2007 to 10/09/2007
			//           new moratorium >> 10/07/2007 to 10/08/2007
			// in above example we need actionDate of installment which is nearest to 10/07/2007
			Date baseActionDate = null;
			Iterator<SavingsScheduleEntity> chkTotalInstIter = totalInstallmentsLst.iterator();
			while (chkTotalInstIter.hasNext())
			{
				SavingsScheduleEntity savingsScheduleEntity = chkTotalInstIter.next();
				if(baseActionDate == null)
					baseActionDate = savingsScheduleEntity.getActionDate();
				else
				{
					if(DateUtils.getDateWithoutTimeStamp(savingsScheduleEntity.getActionDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(actionForm.getFromDate().getTime())) < 0 && DateUtils.getDateWithoutTimeStamp(savingsScheduleEntity.getActionDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(baseActionDate.getTime())) > 0)
						baseActionDate = savingsScheduleEntity.getActionDate();
				}				
			}			
			
			if(baseActionDate != null) // this means atleast one action date falls between moratorium period
			{	
				Iterator totalInstlIter = totalInstallmentsLst.iterator();
				while (totalInstlIter.hasNext())
				{					
					SavingsScheduleEntity obj = (SavingsScheduleEntity) totalInstlIter.next();

					// changing actionDate for loan repayment
					if(!obj.isPaid() && DateUtils.getDateWithoutTimeStamp(obj.getActionDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(actionForm.getFromDate().getTime())) >= 0)
					{													
						Date newActionDate = null;
						newActionDate = new java.sql.Date(getNextDate(baseActionDate, meeting).getTime());
						baseActionDate = newActionDate;
						while(isBetween(newActionDate, moratoriumList))
						{
							newActionDate = new java.sql.Date(getNextDate(baseActionDate, meeting).getTime());
							baseActionDate = newActionDate;
						}
						
						obj.setActionDate(newActionDate);
						// commented for testing, uncomment later
						obj.save();
					}														
					obj = null;
				}				
			}			
		}
		// code for rescheduling of savings account [end]
	}	
	
	private Date getNewActionDate(Object obj, MeetingBO meeting, MoratoriumActionForm actionForm, List moratoriumList)
	{
		Calendar cal = new GregorianCalendar();
		//Date actionDate = obj.getActionDate();
		Date actionDate = null;
		Date newActionDate = null;
		
		if (obj instanceof LoanScheduleEntity)
		{
			LoanScheduleEntity newObj = (LoanScheduleEntity) obj;
			actionDate = newObj.getActionDate();
		}
		else if(obj instanceof SavingsScheduleEntity)
		{
			SavingsScheduleEntity newObj = (SavingsScheduleEntity) obj;
			actionDate = newObj.getActionDate();
		}
		
		cal.setTime(actionDate);
		
		// if we are creating moratorium for first time
		if(moratoriumList == null || moratoriumList.size() == 0)
		{										
			newActionDate = new java.sql.Date(getNextDate(actionDate,meeting).getTime());
			// comparing with moratorium end date 
			while (DateUtils.getDateWithoutTimeStamp(newActionDate.getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(actionForm.getEndDate().getTime())) <= 0) 
			{
				newActionDate = new java.sql.Date(getNextDate(actionDate,meeting).getTime());
			}						
		}
		else// if we are creating moratorium for second(or more) time
		{											
			newActionDate = new java.sql.Date(getNextDate(actionDate,meeting).getTime());			
			while (isBetween(newActionDate, moratoriumList))
			{
				newActionDate = new java.sql.Date(getNextDate(actionDate,meeting).getTime());
			}			
		}
		return newActionDate;
	}
	
	private boolean isBetween(Date newActionDate, List moratoriumLst)
	{
		boolean result = false;
		Iterator iter = moratoriumLst.iterator();
		while (iter.hasNext()) 
		{
			MoratoriumBO moratoriumBO = (MoratoriumBO) iter.next();
			if(DateUtils.getDateWithoutTimeStamp(newActionDate.getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(moratoriumBO.getStartDate().getTime())) >= 0 && DateUtils.getDateWithoutTimeStamp(newActionDate.getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(moratoriumBO.getEndDate().getTime())) <= 0)
			{
				result = true;
				break;
			}			
		}
		
		return result;
	}
	
	private void cleanUpSearch(HttpServletRequest request) throws PageExpiredException
	{
		SessionUtils.setRemovableAttribute("TableCache",null,TableTagConstants.PATH,request.getSession());
		SessionUtils.setRemovableAttribute("current",null,TableTagConstants.PATH,request.getSession());
		SessionUtils.setRemovableAttribute("meth",null,TableTagConstants.PATH,request.getSession());
		SessionUtils.setRemovableAttribute("forwardkey",null,TableTagConstants.PATH,request.getSession());
		SessionUtils.setRemovableAttribute("action",null,TableTagConstants.PATH,request.getSession());
		SessionUtils.removeAttribute(Constants.SEARCH_RESULTS,request);
	}
	
	@TransactionDemarcate (joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		String forward = null;
		if (method != null) 
		{
			if (method.equals("create"))
			{
				String officeId = request.getParameter("officeId");
				if(officeId == null)
					forward = "previewCreateMoratorium_failure";
				else
					forward = "previewCreateBranchMoratorium_failure";
			}
			else if(method.equals("editPreview"))
				forward = "editPreviewMoratorium_failure";
			else if(method.equals("liftPreview"))
				forward = "liftPreviewMoratorium_failure";			
		}
		return mapping.findForward(forward);
	}
	
	private ClientBusinessService getClientBusinessService() {
		return (ClientBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Client);
	}
	
	private MeetingBusinessService getMeetingBusinessService()
			throws ServiceException {
		return new MeetingBusinessService();
	}
	
	private List<OfficeBO> getOffice(List<OfficeBO> officeList,
			OfficeLevel officeLevel) throws Exception {
		if (officeList != null) {
			List<OfficeBO> newOfficeList = new ArrayList<OfficeBO>();
			for (OfficeBO officeBO : officeList) {
				if (officeBO.getOfficeLevel().equals(officeLevel)) {
					newOfficeList.add(officeBO);
				}
			}
			if (newOfficeList.isEmpty())
				return null;
			return newOfficeList;
		}
		return null;
	}
	
	private List<OfficeBO> getOffices(UserContext userContext,
			List<OfficeBO> officeList) throws Exception {
		if (officeList != null) {
			for (OfficeBO officeBO : officeList) {
				officeBO.getLevel().setLocaleId(userContext.getLocaleId());
				officeBO.getStatus().setLocaleId(userContext.getLocaleId());
			}
		}
		return officeList;
	}
	
	private void loadofficeLevels(HttpServletRequest request) throws Exception {
		SessionUtils.setCollectionAttribute(OfficeConstants.OFFICELEVELLIST,
				((OfficeBusinessService) getService())
						.getConfiguredLevels(getUserContext(request)
								.getLocaleId()), request);
	}
	
	/*private void setValuesInActionForm(MeetingActionForm form, MeetingBO meeting) {		
		form.setFrequency(RecurrenceType.MONTHLY.getValue().toString());
		form.setMonthType("1");
		form.setDayRecurMonth(String.valueOf(2));
		form.setMonthDay(String.valueOf(10));			 		
		form.setMeetingPlace(meeting.getMeetingPlace());
	}*/
	/*private void setValuesInActionForm(MeetingActionForm form, MeetingBO meeting) {		
		form.setFrequency(RecurrenceType.MONTHLY.getValue().toString());
		form.setMonthType("2");
		form.setRecurMonth("2");
		form.setMonthWeek("2");
		form.setMonthRank("2");
		form.setMeetingPlace(meeting.getMeetingPlace());
	}*/
	
	// code for getting next date [start]
	private java.util.Date getNextDate(Date startDate, MeetingBO meeting){
		if(meeting.isWeekly())
			return getNextDateForWeek(startDate, meeting);
		else if(meeting.isMonthly())
			return getNextDateForMonth(startDate, meeting);
		else
			return getNextDateForDay(startDate, meeting);
	}
	
	private java.util.Date getNextDateForWeek(Date startDate, MeetingBO meeting){
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(startDate);
		gc.add(Calendar.WEEK_OF_MONTH,meeting.getMeetingDetails().getRecurAfter());
		return gc.getTime();
	}
	
	private java.util.Date getNextDateForMonth(Date startDate, MeetingBO meeting){
		GregorianCalendar gc = new GregorianCalendar();
		java.util.Date scheduleDate=null;
		gc.setTime(startDate);
		if(meeting.isMonthlyOnDate()){
			//move to next month and return date.
			gc.add(GregorianCalendar.MONTH, meeting.getMeetingDetails().getRecurAfter());
			int M1 = gc.get(GregorianCalendar.MONTH);
			gc.set(GregorianCalendar.DATE, meeting.getMeetingDetails().getDayNumber());
			int M2 = gc.get(GregorianCalendar.MONTH);
			int daynum= meeting.getMeetingDetails().getDayNumber();
			while(M1!=M2){
				gc.set(GregorianCalendar.MONTH,gc.get(GregorianCalendar.MONTH)-1);
				gc.set(GregorianCalendar.DATE,daynum-1);
				M2 = gc.get(GregorianCalendar.MONTH);
				daynum--;
			}
			scheduleDate=gc.getTime();
		}else{
			if(!meeting.getMeetingDetails().getWeekRank().equals(RankType.LAST))
			{
				//apply month recurrence
				gc.add(GregorianCalendar.MONTH, meeting.getMeetingDetails().getRecurAfter());
				gc.set(Calendar.DAY_OF_WEEK, meeting.getMeetingDetails().getWeekDay().getValue());
				gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, meeting.getMeetingDetails().getWeekRank().getValue());
				scheduleDate=gc.getTime();
			}else{//weekCount=-1
				gc.set(GregorianCalendar.DATE,15);
				gc.add(GregorianCalendar.MONTH, meeting.getMeetingDetails().getRecurAfter());
				gc.set(Calendar.DAY_OF_WEEK, meeting.getMeetingDetails().getWeekDay().getValue());
				int M1 = gc.get(GregorianCalendar.MONTH);
				//assumption: there are 5 weekdays in the month
				gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,5);
				int M2 = gc.get(GregorianCalendar.MONTH);
				//if assumption fails, it means there exists 4 weekdays in a month, return last weekday date
				//if M1==M2, means there exists 5 weekdays otherwise 4 weekdays	in a month			
				if (M1!=M2){
					gc.set(GregorianCalendar.MONTH,gc.get(GregorianCalendar.MONTH)-1);
					gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,4);
				}
				scheduleDate=gc.getTime();
			}
		}
		return scheduleDate;
	}
	
	private java.util.Date getNextDateForDay(Date startDate, MeetingBO meeting){
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(startDate);
		gc.add(Calendar.DAY_OF_WEEK, meeting.getMeetingDetails().getRecurAfter());
		return gc.getTime();		
	}
	// code for getting next date [end]
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		if (null != request.getParameter(Constants.CURRENTFLOWKEY))
			request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter("currentFlowKey"));
		
		try 
		{
			List activeMoratoriums = new ArrayList();
			List closedMoratoriums = new ArrayList();
			List moratoriumLst = getMoratoriumBizService().getMoratoriums();
			
			Iterator iter = moratoriumLst.iterator();
			while (iter.hasNext()) 
			{
				MoratoriumBO moratoriumBO = (MoratoriumBO) iter.next();
				if(moratoriumBO.getLiftDate() == null)
				{
					// clarification is required from community
					if(DateUtils.getDateWithoutTimeStamp(moratoriumBO.getEndDate().getTime()).compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) < 0)
						closedMoratoriums.add(moratoriumBO);
					else
						activeMoratoriums.add(moratoriumBO);
					
					//activeMoratoriums.add(moratoriumBO); // remove after getting clarification
				}
				else
					closedMoratoriums.add(moratoriumBO);
			}
			// setting list of moratoriums in session			
			request.setAttribute("openMoratoriums", activeMoratoriums);
			request.setAttribute("closedMoratoriums", closedMoratoriums);
		}
		catch (ServiceException e) 
		{
			throw new RuntimeException(e);
		}
		
		return mapping.findForward(ActionForwards.get_success.toString());
	}
}
