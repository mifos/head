package org.mifos.application.office.struts;

import java.net.URISyntaxException;
import java.util.List;

import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OperationMode;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestOfficeAction extends MifosMockStrutsTestCase {
	
	private UserContext userContext ;
	protected void setUp() throws Exception {
		
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/framework/util/helpers/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		 userContext = TestObjectFactory.getUserContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
	}
	
	public void testGetAllOffices(){
		setRequestPathInfo("/offAction.do");
		addRequestParameter("method", "getAllOffices");
		actionPerform();
		verifyForward(ActionForwards.search_success.toString());
		assertEquals(1,((List<OfficeBO>)SessionUtils.getAttribute(OfficeConstants.GET_HEADOFFICE, request.getSession())).size());
		assertNull(SessionUtils.getAttribute(OfficeConstants.GET_REGIONALOFFICE, request.getSession()));
		assertNull(SessionUtils.getAttribute(OfficeConstants.GET_SUBREGIONALOFFICE, request.getSession()));
		assertEquals(1,((List)SessionUtils.getAttribute(OfficeConstants.GET_BRANCHOFFICE, request.getSession())).size());
		assertEquals(1,((List)SessionUtils.getAttribute(OfficeConstants.GET_AREAOFFICE, request.getSession())).size());
	}

	public void testLoad() {
		setRequestPathInfo("/offAction.do");
		addRequestParameter("method",Methods.load.toString());
		addRequestParameter("officeLevel", "5");
		actionPerform();
		verifyForward(ActionForwards.load_success.toString());
		List<OfficeView> parents = (List<OfficeView>) request.getSession()
				.getAttribute(OfficeConstants.PARENTS);
		assertEquals(2, parents.size());
		List<OfficeView> levels = (List<OfficeView>) request.getSession()
				.getAttribute(OfficeConstants.OFFICELEVELLIST);
		assertEquals(4, levels.size());
	}

	public void testLoadParent() {
		setRequestPathInfo("/offAction.do");
		addRequestParameter("method", Methods.loadParent.toString());
		addRequestParameter("officeLevel", "2");
		actionPerform();
		verifyForward(ActionForwards.load_success.toString());
		List<OfficeView> parents = (List<OfficeView>) request.getSession()
				.getAttribute(OfficeConstants.PARENTS);
		assertEquals(1, parents.size());
	}

	public void testPreview() {
		setRequestPathInfo("/offAction.do");
		addRequestParameter("method", Methods.preview.toString());
		addRequestParameter("officeName", "abcd");
		addRequestParameter("shortName", "abcd");
		addRequestParameter("officeLevel", "5");
		addRequestParameter("parentOfficeId", "1");
		actionPerform();
		verifyForward(ActionForwards.preview_success.toString());
	}

	public void testPreview_failure() {
		setRequestPathInfo("/offAction.do");
		addRequestParameter("method", Methods.preview.toString());
		actionPerform();
		assertEquals("Office Name", 1,
				getErrrorSize(OfficeConstants.OFFICE_NAME));
		assertEquals("Office Short  Name", 1,
				getErrrorSize(OfficeConstants.OFFICESHORTNAME));
		assertEquals("Office level", 1,
				getErrrorSize(OfficeConstants.OFFICETYPE));
		assertEquals("Office parent", 1,
				getErrrorSize(OfficeConstants.PARENTOFFICE));
		verifyInputForward();
	}

	public void testPrevious() {
		setRequestPathInfo("/offAction.do");
		addRequestParameter("method", Methods.previous.toString());
		actionPerform();
		verifyForward(ActionForwards.previous_success.toString());
	}
	public void testCreate() {
		setRequestPathInfo("/offAction.do");
		addRequestParameter("method", Methods.create.toString());
		addRequestParameter("officeName", "abcd");
		addRequestParameter("shortName", "abcd");
		addRequestParameter("officeLevel", "5");
		addRequestParameter("parentOfficeId", "1");
		addRequestParameter("address.line1", "123");
		actionPerform();
		verifyForward(ActionForwards.create_success.toString());
		 OfficeBO office =  (OfficeBO)request.getSession()
		.getAttribute(Constants.BUSINESS_KEY);
		 assertEquals("abcd",office.getOfficeName());
		 assertEquals("abcd",office.getShortName());
		 assertEquals("123",office.getAddress().getAddress().getLine1());
		 TestObjectFactory.cleanUp(office);
	}
	public void testGet(){
		setRequestPathInfo("/offAction.do");
		addRequestParameter("method", Methods.get.toString());
		addRequestParameter("officeId","1");
		actionPerform();
		 OfficeBO office =  (OfficeBO)request.getSession()
			.getAttribute(Constants.BUSINESS_KEY);
		 assertNotNull(office);
		 assertEquals(1,office.getOfficeId().intValue());
		
	}
	public void testEdit(){
		setRequestPathInfo("/offAction.do");
		addRequestParameter("method", Methods.edit.toString());
		actionPerform();
		verifyForward(ActionForwards.edit_success.toString());
		
	}
	public void testEditPreview(){
		setRequestPathInfo("/offAction.do");
		addRequestParameter("method", Methods.editpreview.toString());
		actionPerform();
		verifyInputForward();
	}
	public void testEditPrevious(){
		setRequestPathInfo("/offAction.do");
		addRequestParameter("method", Methods.editprevious.toString());
		actionPerform();
		verifyForward(ActionForwards.editprevious_success.toString());
	}
	public void testUpdate()throws Exception{
		setRequestPathInfo("/offAction.do");
		addRequestParameter("method", Methods.update.toString());
		
		OfficeBO parent = TestObjectFactory.getOffice(Short.valueOf("1"));

		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
		TestObjectFactory.flushandCloseSession();
		officeBO = TestObjectFactory.getOffice(officeBO.getOfficeId());
		request.getSession().setAttribute(Constants.BUSINESS_KEY,officeBO);
		addRequestParameter("officeName","RAJOFFICE");
		addRequestParameter("shortName","OFFI");
		addRequestParameter("officeLevel",officeBO.getOfficeLevel().getValue().toString());
		addRequestParameter("parentOfficeId",officeBO.getParentOffice().getOfficeId().toString());
		addRequestParameter("officeStatus",officeBO.getOfficeStatus().getValue().toString());
		actionPerform();
		verifyForward(ActionForwards.update_success.toString());
		TestObjectFactory.flushandCloseSession();
		officeBO = TestObjectFactory.getOffice(officeBO.getOfficeId());
		assertEquals("RAJOFFICE",officeBO.getOfficeName());
		assertEquals("OFFI",officeBO.getShortName());
		TestObjectFactory.cleanUp(officeBO);
	}

	
}
