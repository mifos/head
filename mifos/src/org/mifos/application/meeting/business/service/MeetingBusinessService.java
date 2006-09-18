package org.mifos.application.meeting.business.service;

import java.util.List;

import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.persistence.MeetingPersistence;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;

public class MeetingBusinessService extends BusinessService{
	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}
	
	public List<WeekDaysEntity> getWorkingDays(Short localeId)throws ServiceException{
		try {
			return new MeetingPersistence().getWorkingDays(localeId);
		} catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}	
	}
	
}
