package org.mifos.application.moratorium.struts.actionforms;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.moratorium.util.resources.MoratoriumConstants;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class MoratoriumActionFormTest
        extends MifosMockStrutsTestCase  {
	
	UserContext userContext = null;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();				
		setConfigFile(ResourceLoader
				.getURI(
						"org/mifos/application/moratorium/struts-config.xml")
				.getPath());
		
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
	}
	
	public void testValidFromDate() throws Exception {		
		addRequestParameter("method", "create");
		addRequestParameter("moratoriumLst", "");
		
		// get today's date
		Calendar currentCalendar = new GregorianCalendar();
		int year = currentCalendar.get(Calendar.YEAR);
		int month = currentCalendar.get(Calendar.MONTH) + 1;
		int day = currentCalendar.get(Calendar.DAY_OF_MONTH);
		String moratoriumFromDateDD = String.valueOf(day + 1);
		String moratoriumFromDateMM = String.valueOf(month);
		String moratoriumFromDateYY = String.valueOf(year);
		String moratoriumEndDateDD = String.valueOf(day + 2);
		String moratoriumEndDateMM = String.valueOf(month);
		String moratoriumEndDateYY = String.valueOf(year);
		
		MoratoriumActionForm form = new MoratoriumActionForm();
		ActionErrors errors = new ActionErrors();
		form.setMoratoriumFromDateDD(moratoriumFromDateDD);
		form.setMoratoriumFromDateMM(moratoriumFromDateMM);
		form.setMoratoriumFromDateYY(moratoriumFromDateYY);
		form.setMoratoriumEndDateDD(moratoriumEndDateDD);
		form.setMoratoriumEndDateMM(moratoriumEndDateMM);
		form.setMoratoriumEndDateYY(moratoriumEndDateYY);
		
		errors = form.validate(new ActionMapping(), request);
		assertEquals(0, errors.size());
	}
	
	public void testInValidFromDate() throws Exception {		
		addRequestParameter("method", "create");
		addRequestParameter("moratoriumLst", "");
		
		// get today's date
		Calendar currentCalendar = new GregorianCalendar();
		int year = currentCalendar.get(Calendar.YEAR);
		int month = currentCalendar.get(Calendar.MONTH) + 1;
		int day = currentCalendar.get(Calendar.DAY_OF_MONTH);
		String moratoriumFromDateDD = String.valueOf(day -35);
		String moratoriumFromDateMM = String.valueOf(month);
		String moratoriumFromDateYY = String.valueOf(year);
		String moratoriumEndDateDD = String.valueOf(day + 2);
		String moratoriumEndDateMM = String.valueOf(month);
		String moratoriumEndDateYY = String.valueOf(year);
		
		MoratoriumActionForm form = new MoratoriumActionForm();
		ActionErrors errors = new ActionErrors();
		form.setMoratoriumFromDateDD(moratoriumFromDateDD);
		form.setMoratoriumFromDateMM(moratoriumFromDateMM);
		form.setMoratoriumFromDateYY(moratoriumFromDateYY);
		form.setMoratoriumEndDateDD(moratoriumEndDateDD);
		form.setMoratoriumEndDateMM(moratoriumEndDateMM);
		form.setMoratoriumEndDateYY(moratoriumEndDateYY);
		
		errors = form.validate(new ActionMapping(), request);
		assertEquals(1, errors.size());
		ActionMessage message = (ActionMessage) errors.get().next();
		assertEquals(MoratoriumConstants.INVALIDFROMDATE, message.getKey());
	}
	
	public void testInValidFormatForFromDate() throws Exception {		
		addRequestParameter("method", "create");
		addRequestParameter("moratoriumLst", "");
		
		// get today's date
		Calendar currentCalendar = new GregorianCalendar();
		int year = currentCalendar.get(Calendar.YEAR);
		int month = currentCalendar.get(Calendar.MONTH) + 1;
		int day = currentCalendar.get(Calendar.DAY_OF_MONTH);		
		String moratoriumFromDateMM = String.valueOf(month);
		String moratoriumFromDateYY = String.valueOf(year);
		String moratoriumEndDateDD = String.valueOf(day + 2);
		String moratoriumEndDateMM = String.valueOf(month);
		String moratoriumEndDateYY = String.valueOf(year);
		
		MoratoriumActionForm form = new MoratoriumActionForm();
		ActionErrors errors = new ActionErrors();
		form.setMoratoriumFromDateDD("asdf");
		form.setMoratoriumFromDateMM(moratoriumFromDateMM);
		form.setMoratoriumFromDateYY(moratoriumFromDateYY);
		form.setMoratoriumEndDateDD(moratoriumEndDateDD);
		form.setMoratoriumEndDateMM(moratoriumEndDateMM);
		form.setMoratoriumEndDateYY(moratoriumEndDateYY);
		
		errors = form.validate(new ActionMapping(), request);
		assertEquals(1, errors.size());
		ActionMessage message = (ActionMessage) errors.get().next();
		assertEquals(MoratoriumConstants.INVALID_FORMAT_FOR_FROM_DATE, message.getKey());
	}
	
	public void testInValidEndDate() throws Exception {		
		addRequestParameter("method", "create");
		addRequestParameter("moratoriumLst", "");
		
		// get today's date
		Calendar currentCalendar = new GregorianCalendar();
		int year = currentCalendar.get(Calendar.YEAR);
		int month = currentCalendar.get(Calendar.MONTH) + 1;
		int day = currentCalendar.get(Calendar.DAY_OF_MONTH);
		String moratoriumFromDateDD = String.valueOf(day + 1);
		String moratoriumFromDateMM = String.valueOf(month);
		String moratoriumFromDateYY = String.valueOf(year);
		String moratoriumEndDateDD = String.valueOf(day - 2);
		String moratoriumEndDateMM = String.valueOf(month);
		String moratoriumEndDateYY = String.valueOf(year);
		
		MoratoriumActionForm form = new MoratoriumActionForm();
		ActionErrors errors = new ActionErrors();
		form.setMoratoriumFromDateDD(moratoriumFromDateDD);
		form.setMoratoriumFromDateMM(moratoriumFromDateMM);
		form.setMoratoriumFromDateYY(moratoriumFromDateYY);
		form.setMoratoriumEndDateDD(moratoriumEndDateDD);
		form.setMoratoriumEndDateMM(moratoriumEndDateMM);
		form.setMoratoriumEndDateYY(moratoriumEndDateYY);
		
		errors = form.validate(new ActionMapping(), request);
		assertEquals(1, errors.size());
		ActionMessage message = (ActionMessage) errors.get().next();
		assertEquals(MoratoriumConstants.INVALIDENDDATE, message.getKey());
	}
	
	public void testInValidFormatForEndDate() throws Exception {		
		addRequestParameter("method", "create");
		addRequestParameter("moratoriumLst", "");
		
		// get today's date
		Calendar currentCalendar = new GregorianCalendar();
		int year = currentCalendar.get(Calendar.YEAR);
		int month = currentCalendar.get(Calendar.MONTH) + 1;
		int day = currentCalendar.get(Calendar.DAY_OF_MONTH);
		String moratoriumFromDateDD = String.valueOf(day + 1);
		String moratoriumFromDateMM = String.valueOf(month);
		String moratoriumFromDateYY = String.valueOf(year);		
		String moratoriumEndDateMM = String.valueOf(month);
		String moratoriumEndDateYY = String.valueOf(year);
		
		MoratoriumActionForm form = new MoratoriumActionForm();
		ActionErrors errors = new ActionErrors();		
		form.setMoratoriumFromDateDD(moratoriumFromDateDD);
		form.setMoratoriumFromDateMM(moratoriumFromDateMM);
		form.setMoratoriumFromDateYY(moratoriumFromDateYY);
		form.setMoratoriumEndDateDD("asdf");
		form.setMoratoriumEndDateMM(moratoriumEndDateMM);
		form.setMoratoriumEndDateYY(moratoriumEndDateYY);
		
		errors = form.validate(new ActionMapping(), request);
		assertEquals(1, errors.size());
		ActionMessage message = (ActionMessage) errors.get().next();
		assertEquals(MoratoriumConstants.INVALID_FORMAT_FOR_END_DATE, message.getKey());
	}
}
