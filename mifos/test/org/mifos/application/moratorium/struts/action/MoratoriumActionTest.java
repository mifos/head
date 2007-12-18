package org.mifos.application.moratorium.struts.action;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.moratorium.business.MoratoriumBO;
import org.mifos.application.moratorium.business.service.MoratoriumBusinessService;
import org.mifos.application.moratorium.struts.actionforms.MoratoriumActionForm;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class MoratoriumActionTest
        extends MifosMockStrutsTestCase {
	
	private String flowKey;
	
	private CustomerBO client;

	private CustomerBO group;

	private CustomerBO center;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
				.getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/moratorium/struts-config.xml")
				.getPath());
		UserContext userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);		
		flowKey = createFlow(request, MoratoriumAction.class);
	}
	
	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	public void testCreateForClient() throws Exception {
		createInitialObjects();
		setRequestPathInfo("/moratoriumAction");
		addRequestParameter("method", "create");
		addRequestParameter("customerId", String.valueOf(client.getCustomerId()));
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		
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
		
		addRequestParameter("moratoriumFromDateDD", moratoriumFromDateDD);
		addRequestParameter("moratoriumFromDateMM", moratoriumFromDateMM);
		addRequestParameter("moratoriumFromDateYY", moratoriumFromDateYY);		
		addRequestParameter("moratoriumEndDateDD", moratoriumEndDateDD);
		addRequestParameter("moratoriumEndDateMM", moratoriumEndDateMM);
		addRequestParameter("moratoriumEndDateYY", moratoriumEndDateYY);
		
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("create");
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}
	
	public void testCreateForOffice() throws Exception {
		createInitialObjects();
		setRequestPathInfo("/moratoriumAction");
		addRequestParameter("method", "create");
		addRequestParameter("officeId", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		
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
		
		addRequestParameter("moratoriumFromDateDD", moratoriumFromDateDD);
		addRequestParameter("moratoriumFromDateMM", moratoriumFromDateMM);
		addRequestParameter("moratoriumFromDateYY", moratoriumFromDateYY);		
		addRequestParameter("moratoriumEndDateDD", moratoriumEndDateDD);
		addRequestParameter("moratoriumEndDateMM", moratoriumEndDateMM);
		addRequestParameter("moratoriumEndDateYY", moratoriumEndDateYY);
		
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("createForBranch");
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}
	
	public void testCreateConfirmedForClient() throws Exception {
		createInitialObjects();
		setRequestPathInfo("/moratoriumAction");
		addRequestParameter("method", "createConfirmed");
		addRequestParameter("customerId", String.valueOf(client.getCustomerId()));
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		
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
		
		addRequestParameter("moratoriumFromDateDD", moratoriumFromDateDD);
		addRequestParameter("moratoriumFromDateMM", moratoriumFromDateMM);
		addRequestParameter("moratoriumFromDateYY", moratoriumFromDateYY);		
		addRequestParameter("moratoriumEndDateDD", moratoriumEndDateDD);
		addRequestParameter("moratoriumEndDateMM", moratoriumEndDateMM);
		addRequestParameter("moratoriumEndDateYY", moratoriumEndDateYY);
		
		MoratoriumActionForm form = new MoratoriumActionForm();
		form.setMoratoriumFromDateDD(moratoriumFromDateDD);
		form.setMoratoriumFromDateMM(moratoriumFromDateMM);
		form.setMoratoriumFromDateYY(moratoriumFromDateYY);
		form.setMoratoriumEndDateDD(moratoriumEndDateDD);
		form.setMoratoriumEndDateMM(moratoriumEndDateMM);
		form.setMoratoriumEndDateYY(moratoriumEndDateYY);
		form.setMoratoriumNotes("inserted for testing, while running test case");
		Date fromDate = null;
		fromDate = DateUtils.getDateAsSentFromBrowser(moratoriumFromDateDD + "/" + moratoriumFromDateMM + "/" + moratoriumFromDateYY);
		form.setFromDate(fromDate);
		Date endDate = null;
		endDate = DateUtils.getDateAsSentFromBrowser(moratoriumEndDateDD + "/" + moratoriumEndDateMM + "/" + moratoriumEndDateYY);
		form.setEndDate(endDate);
		setActionForm(form);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("create_success");
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}
	
	public void testCreateConfirmedForOffice() throws Exception {
		createInitialObjects();
		setRequestPathInfo("/moratoriumAction");
		addRequestParameter("method", "createConfirmed");
		// officeId >> 3, it is for TestBranchOffice, it is inserted by testdbinsertionscript.sql
		addRequestParameter("officeId", String.valueOf("3"));
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		
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
		
		addRequestParameter("moratoriumFromDateDD", moratoriumFromDateDD);
		addRequestParameter("moratoriumFromDateMM", moratoriumFromDateMM);
		addRequestParameter("moratoriumFromDateYY", moratoriumFromDateYY);		
		addRequestParameter("moratoriumEndDateDD", moratoriumEndDateDD);
		addRequestParameter("moratoriumEndDateMM", moratoriumEndDateMM);
		addRequestParameter("moratoriumEndDateYY", moratoriumEndDateYY);
		
		MoratoriumActionForm form = new MoratoriumActionForm();
		form.setMoratoriumFromDateDD(moratoriumFromDateDD);
		form.setMoratoriumFromDateMM(moratoriumFromDateMM);
		form.setMoratoriumFromDateYY(moratoriumFromDateYY);
		form.setMoratoriumEndDateDD(moratoriumEndDateDD);
		form.setMoratoriumEndDateMM(moratoriumEndDateMM);
		form.setMoratoriumEndDateYY(moratoriumEndDateYY);
		form.setMoratoriumNotes("inserted for testing, while running test case");
		Date fromDate = null;
		fromDate = DateUtils.getDateAsSentFromBrowser(moratoriumFromDateDD + "/" + moratoriumFromDateMM + "/" + moratoriumFromDateYY);
		form.setFromDate(fromDate);
		Date endDate = null;
		endDate = DateUtils.getDateAsSentFromBrowser(moratoriumEndDateDD + "/" + moratoriumEndDateMM + "/" + moratoriumEndDateYY);
		form.setEndDate(endDate);
		setActionForm(form);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("create_success");
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}
	
	public void testEditForClient() throws Exception {
		createInitialObjects();
		setRequestPathInfo("/moratoriumAction");
		addRequestParameter("method", "edit");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		
		String customerId = null;
		String moratoriumId = null;
		List moratoriumList = getMoratoriumBizService().getMoratoriums();
		if(moratoriumList != null && moratoriumList.size() != 0)
		{
			Iterator<MoratoriumBO> iter = moratoriumList.iterator();
			while (iter.hasNext()) 
			{
				MoratoriumBO moratoriumBO = iter.next();
				if(moratoriumBO.getCustomerId() != null)
				{
					customerId = moratoriumBO.getCustomerId();
					moratoriumId = String.valueOf(moratoriumBO.getMoratoriumId());
					break;
				}
			}
		}
		
		if(customerId != null)
		{
			addRequestParameter("customerId", customerId);
			addRequestParameter("moratoriumId", moratoriumId);
			actionPerform();
			verifyNoActionErrors();
			verifyNoActionMessages();
			verifyForward("edit_success");
			assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		}
	}
	
	public void testEditForOffice() throws Exception {
		createInitialObjects();
		setRequestPathInfo("/moratoriumAction");
		addRequestParameter("method", "edit");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		
		String officeId = null;
		String moratoriumId = null;
		List moratoriumList = getMoratoriumBizService().getMoratoriums();
		if(moratoriumList != null && moratoriumList.size() != 0)
		{
			Iterator<MoratoriumBO> iter = moratoriumList.iterator();
			while (iter.hasNext()) 
			{
				MoratoriumBO moratoriumBO = iter.next();
				if(moratoriumBO.getOfficeId() != null)
				{
					officeId = moratoriumBO.getOfficeId();
					moratoriumId = String.valueOf(moratoriumBO.getMoratoriumId());
					break;
				}
			}
		}
		
		if(officeId != null)
		{
			addRequestParameter("officeId", officeId);
			addRequestParameter("moratoriumId", moratoriumId);
			actionPerform();
			verifyNoActionErrors();
			verifyNoActionMessages();
			verifyForward("edit_success");
			assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		}
	}
	
	public void testEditPreviewForClient() throws Exception {
		createInitialObjects();
		setRequestPathInfo("/moratoriumAction");
		addRequestParameter("method", "editPreview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		
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
		
		addRequestParameter("moratoriumFromDateDD", moratoriumFromDateDD);
		addRequestParameter("moratoriumFromDateMM", moratoriumFromDateMM);
		addRequestParameter("moratoriumFromDateYY", moratoriumFromDateYY);		
		addRequestParameter("moratoriumEndDateDD", moratoriumEndDateDD);
		addRequestParameter("moratoriumEndDateMM", moratoriumEndDateMM);
		addRequestParameter("moratoriumEndDateYY", moratoriumEndDateYY);
		
		String customerId = null;
		String moratoriumId = null;
		List moratoriumList = getMoratoriumBizService().getMoratoriums();
		if(moratoriumList != null && moratoriumList.size() != 0)
		{
			Iterator<MoratoriumBO> iter = moratoriumList.iterator();
			while (iter.hasNext()) 
			{
				MoratoriumBO moratoriumBO = iter.next();
				if(moratoriumBO.getCustomerId() != null)
				{
					customerId = moratoriumBO.getCustomerId();
					moratoriumId = String.valueOf(moratoriumBO.getMoratoriumId());
					break;
				}
			}
		}
		
		if(customerId != null)
		{
			addRequestParameter("customerId", customerId);
			addRequestParameter("moratoriumId", moratoriumId);
			actionPerform();
			verifyNoActionErrors();
			verifyNoActionMessages();
			verifyForward("editpreview_success");
			assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		}
	}
	
	public void testEditPreviewForOffice() throws Exception {
		createInitialObjects();
		setRequestPathInfo("/moratoriumAction");
		addRequestParameter("method", "editPreview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		
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
		
		addRequestParameter("moratoriumFromDateDD", moratoriumFromDateDD);
		addRequestParameter("moratoriumFromDateMM", moratoriumFromDateMM);
		addRequestParameter("moratoriumFromDateYY", moratoriumFromDateYY);		
		addRequestParameter("moratoriumEndDateDD", moratoriumEndDateDD);
		addRequestParameter("moratoriumEndDateMM", moratoriumEndDateMM);
		addRequestParameter("moratoriumEndDateYY", moratoriumEndDateYY);
		
		String officeId = null;
		String moratoriumId = null;
		List moratoriumList = getMoratoriumBizService().getMoratoriums();
		if(moratoriumList != null && moratoriumList.size() != 0)
		{
			Iterator<MoratoriumBO> iter = moratoriumList.iterator();
			while (iter.hasNext()) 
			{
				MoratoriumBO moratoriumBO = iter.next();
				if(moratoriumBO.getOfficeId() != null)
				{
					officeId = moratoriumBO.getOfficeId();
					moratoriumId = String.valueOf(moratoriumBO.getMoratoriumId());
					break;
				}
			}
		}
		
		if(officeId != null)
		{
			addRequestParameter("officeId", officeId);
			addRequestParameter("moratoriumId", moratoriumId);
			actionPerform();
			verifyNoActionErrors();
			verifyNoActionMessages();
			verifyForward("editpreview_success");
			assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		}
	}
	
	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client", 
				CustomerStatus.CLIENT_ACTIVE,
				group);
	}
	
	private MoratoriumBusinessService getMoratoriumBizService() {
		return new MoratoriumBusinessService();
	}
}
