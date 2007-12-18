package org.mifos.application.moratorium.struts.actionforms;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.moratorium.business.MoratoriumBO;
import org.mifos.application.moratorium.util.resources.MoratoriumConstants;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class MoratoriumActionForm extends BaseActionForm{
	
	private String searchString;
	
	private String input;
	
	private Date fromDate;
	
	private Date endDate;
		
	private String moratoriumFromDate;
	
	private String moratoriumEndDate;
	
	private String moratoriumFromDateDD;
	
	private String moratoriumFromDateMM;
	
	private String moratoriumFromDateYY;
	
	private String moratoriumEndDateDD;
	
	private String moratoriumEndDateMM;
	
	private String moratoriumEndDateYY;
	
	private String moratoriumNotes;
	
	private String moratoriumId;
	
	private String officeId;
	
	private String isLift;
	
	public String getIsLift() {
		return isLift;
	}

	public void setIsLift(String isLift) {
		this.isLift = isLift;
	}

	public String getMoratoriumId() {
		return moratoriumId;
	}

	public void setMoratoriumId(String moratoriumId) {
		this.moratoriumId = moratoriumId;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getMoratoriumNotes() {
		return moratoriumNotes;
	}

	public void setMoratoriumNotes(String moratoriumNotes) {		
		this.moratoriumNotes = moratoriumNotes;
	}	
	
	public String getMoratoriumEndDateDD() {
		return moratoriumEndDateDD;
	}

	public void setMoratoriumEndDateDD(String moratoriumEndDateDD) {
		this.moratoriumEndDateDD = moratoriumEndDateDD;
	}

	public String getMoratoriumEndDateMM() {
		return moratoriumEndDateMM;
	}

	public void setMoratoriumEndDateMM(String moratoriumEndDateMM) {
		this.moratoriumEndDateMM = moratoriumEndDateMM;
	}

	public String getMoratoriumEndDateYY() {
		return moratoriumEndDateYY;
	}

	public void setMoratoriumEndDateYY(String moratoriumEndDateYY) {
		this.moratoriumEndDateYY = moratoriumEndDateYY;
	}

	public String getMoratoriumFromDateDD() {
		return moratoriumFromDateDD;
	}

	public void setMoratoriumFromDateDD(String moratoriumFromDateDD) {
		this.moratoriumFromDateDD = moratoriumFromDateDD;
	}

	public String getMoratoriumFromDateMM() {
		return moratoriumFromDateMM;
	}

	public void setMoratoriumFromDateMM(String moratoriumFromDateMM) {
		this.moratoriumFromDateMM = moratoriumFromDateMM;
	}

	public String getMoratoriumFromDateYY() {
		return moratoriumFromDateYY;
	}

	public void setMoratoriumFromDateYY(String moratoriumFromDateYY) {
		this.moratoriumFromDateYY = moratoriumFromDateYY;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String method = request.getParameter("method");

		Locale userLocale = getUserLocale(request);

		if (null != request.getParameter(Constants.CURRENTFLOWKEY) && null == request.getAttribute(Constants.CURRENTFLOWKEY))
			request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
		
		if (method.equals("create") || method.equals("editPreview")) 
		{
			if (!StringUtils.isNullOrEmpty(getMoratoriumFromDate())) 
			{
				try
				{
					fromDate = DateUtils.getLocaleDate(userLocale, getMoratoriumFromDate());
				}
				catch (InvalidDateException e) 
				{					
					addError(errors, "From Date", MoratoriumConstants.INVALID_FORMAT_FOR_FROM_DATE);									
				}
			}
			else
			{
				addError(errors, "From Date", MoratoriumConstants.MORATORIUM_FROM_DATE);								
			}

			if (!StringUtils.isNullOrEmpty(getMoratoriumEndDate())) 
			{
				try
				{
					endDate = DateUtils.getLocaleDate(userLocale, getMoratoriumEndDate());
				}
				catch (InvalidDateException e) 
				{
					addError(errors, "End Date", MoratoriumConstants.INVALID_FORMAT_FOR_END_DATE);					
				}
			}
			else 
			{
				addError(errors, "End Date", MoratoriumConstants.MORATORIUM_END_DATE);								
			}
			
			if (fromDate != null && DateUtils.getDateWithoutTimeStamp(fromDate.getTime()).compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) < 0) 
			{				
				//check whether from date is less than current date by 30 days or not.
				Calendar fromDateCalendar = new GregorianCalendar();
				fromDateCalendar.setTimeInMillis(getFromDate().getTime());
				int year1 = fromDateCalendar.get(Calendar.YEAR);
				int month1 = fromDateCalendar.get(Calendar.MONTH);
				int day1 = fromDateCalendar.get(Calendar.DAY_OF_MONTH);
				fromDateCalendar = new GregorianCalendar(year1, month1, day1);
				
				Date currentDateMinusThirty = new Date();
				Calendar calendarMinusThirty = new GregorianCalendar();
				calendarMinusThirty.setTimeInMillis(currentDateMinusThirty.getTime());
				int year = calendarMinusThirty.get(Calendar.YEAR);
				int month = calendarMinusThirty.get(Calendar.MONTH);
				int day = calendarMinusThirty.get(Calendar.DAY_OF_MONTH) - 30;
				calendarMinusThirty = new GregorianCalendar(year, month, day);
				
				if(fromDateCalendar.compareTo(calendarMinusThirty) < 0)				
					addError(errors, "From Date", MoratoriumConstants.INVALIDFROMDATE);
			}

			if (method.equals("create"))
			{
				if (fromDate != null && endDate != null && DateUtils.getDateWithoutTimeStamp(fromDate.getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(endDate.getTime())) > 0) 
				{
					addError(errors, "End Date", MoratoriumConstants.INVALIDENDDATE);
				}
			}
			
			// for validation of "overlapping of moratorium" [start]
			if (method.equals("create"))
			{
				if(fromDate != null && endDate != null)
				{
					boolean flag = false;
					if(null != SessionUtils.getAttribute("moratoriumLst", request.getSession()))
					{
						List lstOfMoratoriums = (List) SessionUtils.getAttribute("moratoriumLst", request.getSession());
						Iterator iter = lstOfMoratoriums.iterator(); 
						while (iter.hasNext())
						{
							MoratoriumBO moratoriumBO = (MoratoriumBO) iter.next();
							moratoriumBO.getStartDate();
							if(DateUtils.getDateWithoutTimeStamp(getFromDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(moratoriumBO.getStartDate().getTime())) == 0)
							{
								addError(errors, "Moratorium Overlap", MoratoriumConstants.MORATORIUM_OVERLAP_EXCEPTION);
								flag = true;
							}						
							else if(DateUtils.getDateWithoutTimeStamp(getFromDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(moratoriumBO.getStartDate().getTime())) > 0)
							{
								if(DateUtils.getDateWithoutTimeStamp(getFromDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(moratoriumBO.getEndDate().getTime())) <= 0)
								{
									addError(errors, "Moratorium Overlap", MoratoriumConstants.MORATORIUM_OVERLAP_EXCEPTION);
									flag = true;
								}
							}
							else if(DateUtils.getDateWithoutTimeStamp(getFromDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(moratoriumBO.getStartDate().getTime())) < 0)
							{
								if(DateUtils.getDateWithoutTimeStamp(getEndDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(moratoriumBO.getStartDate().getTime())) >= 0)
								{
									addError(errors, "Moratorium Overlap", MoratoriumConstants.MORATORIUM_OVERLAP_EXCEPTION);
									flag = true;
								}
							}
							
							if(flag)
								break;
						}
					}					
				}				
			}
			else if(method.equals("editPreview"))
			{
				if(fromDate != null && endDate != null)
				{
					boolean flag = false;
					String moratoriumId = request.getParameter("moratoriumId");// id of the moratorium which we are editing
					if(null != SessionUtils.getAttribute("moratoriumLst", request.getSession()))
					{
						List lstOfMoratoriums = (List) SessionUtils.getAttribute("moratoriumLst", request.getSession());
						Iterator iter = lstOfMoratoriums.iterator(); 
						while (iter.hasNext())
						{
							MoratoriumBO moratoriumBO = (MoratoriumBO) iter.next();
							moratoriumBO.getStartDate();
							
							// if we are editing moratorium then the validation for same moratorium needs to be skipped,
							// because while editing we can only change end date, we need to check whether that end date
							// overlaps with other moratorium or not.
							// for e.g., 10/10/2007 to 15/10/2007 (moratoriumId = 1)
							//           20/10/2007 to 25/10/2007 (moratoriumId = 2)
							//           28/10/2007 to 30/10/2007 (moratoriumId = 3)
							// in above eg, if we won't skip iteration for same moratoriumId then all the below validations will fail,
							// coz in that case we will compare same moratorium.
							if(moratoriumId.equals(String.valueOf(moratoriumBO.getMoratoriumId())))
							{
								// chk for lift moratorium [start]
								if(DateUtils.getDateWithoutTimeStamp(getEndDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(moratoriumBO.getEndDate().getTime())) > 0)
								{
									// do nothing, skip this iteration and chk for overlap of moratorium
									continue;
								}
								else if(DateUtils.getDateWithoutTimeStamp(getEndDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(moratoriumBO.getEndDate().getTime())) < 0)
								{
									// as of now we can't lift moratorium in past date, it has to be
									// either greater than or equal to system date.
									if(DateUtils.getDateWithoutTimeStamp(getEndDate().getTime()).compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) < 0)
									{
										// display error
										addError(errors, "Lift Moratorium", MoratoriumConstants.MORATORIUM_LIFT_DATE_IS_INVALID);
										flag = true;
									}
									else
									{
										// this will be used in updateMoratorium method in action class
										flag = true;
										setIsLift("true");
									}
								}
								else if(DateUtils.getDateWithoutTimeStamp(getEndDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(moratoriumBO.getEndDate().getTime())) == 0)
								{
									// do nothing, skip the loop, coz user has not changed the end date,
									// so there is no point in chking for overlap or lifting of moratorium.
									// display message that on the end date moratorium will be closed automatically
									// so there is no point in lifting the moratorium on end date.
									addError(errors, "Lift Moratorium", MoratoriumConstants.MORATORIUM_LIFT_DATE_EQUALS_END_DATE_EXCEPTION);
									flag = true;
								}
								
								if(flag)
									break;
								else
									continue;//chk for overlap
								// chk for lift moratorium [end]
							}
							
							if(DateUtils.getDateWithoutTimeStamp(getFromDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(moratoriumBO.getStartDate().getTime())) == 0)
							{
								addError(errors, "Moratorium Overlap", MoratoriumConstants.MORATORIUM_OVERLAP_EXCEPTION);
								flag = true;
							}						
							else if(DateUtils.getDateWithoutTimeStamp(getFromDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(moratoriumBO.getStartDate().getTime())) > 0)
							{
								if(DateUtils.getDateWithoutTimeStamp(getFromDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(moratoriumBO.getEndDate().getTime())) <= 0)
								{
									addError(errors, "Moratorium Overlap", MoratoriumConstants.MORATORIUM_OVERLAP_EXCEPTION);
									flag = true;
								}
							}
							else if(DateUtils.getDateWithoutTimeStamp(getFromDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(moratoriumBO.getStartDate().getTime())) < 0)
							{
								if(DateUtils.getDateWithoutTimeStamp(getEndDate().getTime()).compareTo(DateUtils.getDateWithoutTimeStamp(moratoriumBO.getStartDate().getTime())) >= 0)
								{
									addError(errors, "Moratorium Overlap", MoratoriumConstants.MORATORIUM_OVERLAP_EXCEPTION);
									flag = true;
								}
							}
							
							if(flag)
								break;
						}
					}					
				}
			}
			// for validation of "overlapping of moratorium" [end]

			try
			{
				errors.add(super.validate(mapping, request));
			}
			catch(Exception e)
			{}
		}
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", method);
		}

		return errors;
	}
	
	protected Locale getUserLocale(HttpServletRequest request) {
		Locale locale = null;

		UserContext userContext = (UserContext) request.getSession()
				.getAttribute(LoginConstants.USERCONTEXT);
		if (null != userContext) {
			locale = userContext.getPreferredLocale();
			if (null == locale) {
				locale = userContext.getMfiLocale();
			}
		}
		return locale;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	
	@Override
	protected UserContext getUserContext(HttpServletRequest request) {
		return (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
	}
	
	public String getMoratoriumEndDate() {
		String tempDate = null;
		if (!StringUtils.isNullAndEmptySafe(moratoriumEndDateDD)
				|| !StringUtils.isNullAndEmptySafe(moratoriumEndDateMM)
				|| !StringUtils.isNullAndEmptySafe(moratoriumEndDateYY)) {			
		}
		else {
			tempDate = moratoriumEndDateDD + "/" + moratoriumEndDateMM + "/" + moratoriumEndDateYY;
		}
		moratoriumEndDate = tempDate;
		return moratoriumEndDate;
	}

	public void setMoratoriumEndDate(String moratoriumEndDate) {
		this.moratoriumEndDate = moratoriumEndDate;		
	}

	public String getMoratoriumFromDate() {
		String tempDate = null;
		if (!StringUtils.isNullAndEmptySafe(moratoriumFromDateDD)
				|| !StringUtils.isNullAndEmptySafe(moratoriumFromDateMM)
				|| !StringUtils.isNullAndEmptySafe(moratoriumFromDateYY)) {			
		}
		else {
			tempDate = moratoriumFromDateDD + "/" + moratoriumFromDateMM + "/" + moratoriumFromDateYY;
		}
		moratoriumFromDate = tempDate;
		return moratoriumFromDate;
	}

	public void setMoratoriumFromDate(String moratoriumFromDate) {
		this.moratoriumFromDate = moratoriumFromDate;		
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}	
	
	public void clear() {		
		this.moratoriumFromDate = null;
		this.moratoriumEndDate = null;				
		this.moratoriumFromDateDD = null;
		this.moratoriumFromDateMM = null;
		this.moratoriumFromDateYY = null;		
		this.moratoriumEndDateDD = null;
		this.moratoriumEndDateMM = null;
		this.moratoriumEndDateYY = null;
		this.moratoriumNotes = null;
	}
}
