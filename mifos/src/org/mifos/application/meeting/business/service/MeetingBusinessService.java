package org.mifos.application.meeting.business.service;

import java.util.List;

import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.persistence.MeetingPersistence;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.application.meeting.util.helpers.WeekDay;

public class MeetingBusinessService extends BusinessService{
	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}
	
	
	public List<WeekDay> getWorkingDays()throws RuntimeException{
			return new MeetingPersistence().getWorkingDays();
		
	}
	
	public MeetingBO getMeeting(Integer meetingId)throws ServiceException{
		try {
			return new MeetingPersistence().getMeeting(meetingId);
		} catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}
	
	public void checkPermissionForEditMeetingSchedule(CustomerLevel customerLevel,
			UserContext userContext, Short recordOfficeId,
			Short recordLoanOfficerId) throws ApplicationException {
		if (!isPermissionAllowed(customerLevel, userContext,
				recordOfficeId, recordLoanOfficerId))
			throw new CustomerException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
	}

	private boolean isPermissionAllowed(CustomerLevel customerLevel,
			UserContext userContext, Short recordOfficeId,
			Short recordLoanOfficerId) {
		return ActivityMapper.getInstance().isEditMeetingSchedulePermittedForCustomers(
				customerLevel, userContext, recordOfficeId,
				recordLoanOfficerId);
	}
}
