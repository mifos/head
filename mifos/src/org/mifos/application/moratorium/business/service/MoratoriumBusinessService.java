package org.mifos.application.moratorium.business.service;

import java.util.List;

import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.moratorium.business.MoratoriumBO;
import org.mifos.application.moratorium.persistence.MoratoriumPersistence;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;

public class MoratoriumBusinessService extends BusinessService {

	public MoratoriumBusinessService()
	{}
	
	public static MoratoriumBusinessService getInstance(UserContext userContext) {
		return new MoratoriumBusinessService();
	}
	
	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}
	
	// for getting all records from moratorium table(closed & open)
	public List<MoratoriumBO> getMoratoriums() throws ServiceException 
	{
		try 
		{
			return new MoratoriumPersistence().getMoratoriums();
		}
		catch (PersistenceException pe) 
		{
			throw new ServiceException(pe);
		}
	}
	
	// for getting moratorium by moratoriumId
	public List<MoratoriumBO> getMoratoriumById(String moratoriumId) throws ServiceException 
	{
		try 
		{
			return new MoratoriumPersistence().getMoratoriumById(moratoriumId);
		}
		catch (PersistenceException pe) 
		{
			throw new ServiceException(pe);
		}
	}
	
	// for getting moratoriums by customerId
	public List<MoratoriumBO> getMoratoriumByCustomerId(String customerId) throws ServiceException 
	{
		try 
		{
			return new MoratoriumPersistence().getMoratoriumByCustomerId(customerId);
		}
		catch (PersistenceException pe) 
		{
			throw new ServiceException(pe);
		}
	}
	
	// for getting moratoriums by officeId
	public List<MoratoriumBO> getMoratoriumByOfficeId(String officeId) throws ServiceException 
	{
		try 
		{
			return new MoratoriumPersistence().getMoratoriumByOfficeId(officeId);
		}
		catch (PersistenceException pe) 
		{
			throw new ServiceException(pe);
		}
	}
}
