package org.mifos.application.configuration.struts.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.mifos.application.accounts.loan.struts.action.MultipleLoanAccountsCreationAction;
import org.mifos.application.configuration.struts.actionform.LookupOptionsActionForm;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.master.business.CustomValueList;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.MifosValueList;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LookupOptionsActionTest extends MifosMockStrutsTestCase{

	private UserContext userContext;
	private String flowKey;

	final Short DEFAULT_LOCALE = 1;
	final String UPDATE_NAME = "updated";
	final String NEW_ELEMENT_NAME = "new";
	
	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml").getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/configuration/struts-config.xml")
				.getPath());
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext",
				TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, LookupOptionsAction.class);
	}

	private boolean compareLists(List<CustomValueListElement> first, String[] second, int expectedLength) {
		
		assertEquals(first.size(),expectedLength);
		for (int index = 0; index < second.length; ++index) {
			assertEquals(first.get(index).getLookUpValue(), second[index]);
		}
		return true;
	}
	
	public void testLoad() throws Exception {
		setRequestPathInfo("/lookupOptionsAction.do");
		addRequestParameter("method", "load");
		performNoErrors();
		verifyForward(ActionForwards.load_success.toString());
		LookupOptionsActionForm lookupOptionsActionForm = 
			(LookupOptionsActionForm) request
				.getSession().getAttribute("lookupoptionsactionform");
		
		String[] EXPECTED_SALUTATIONS = {"Mr","Mrs","Ms"};
		assertTrue(compareLists(lookupOptionsActionForm.getSalutations(), EXPECTED_SALUTATIONS, 3));

		String[] EXPECTED_PERSONNEL_TITLES = {"Cashier","Center Manager","Branch Manager"};
		assertTrue(compareLists(lookupOptionsActionForm.getUserTitles(), EXPECTED_PERSONNEL_TITLES, 9));
		
		String[] EXPECTED_MARITAL_STATUS = {"Married","UnMarried","Widowed"};
		assertTrue(compareLists(lookupOptionsActionForm.getMaritalStatuses(), EXPECTED_MARITAL_STATUS, 3));

		String[] EXPECTED_ETHNICITY = {"SC","BC","ST"};
		assertTrue(compareLists(lookupOptionsActionForm.getEthnicities(), EXPECTED_ETHNICITY, 5));

		String[] EXPECTED_EDUCATION_LEVEL = {"Only Client","Only Husband","Both Literate"};
		assertTrue(compareLists(lookupOptionsActionForm.getEducationLevels(), EXPECTED_EDUCATION_LEVEL, 4));

		String[] EXPECTED_CITIZENSHIP = {"Hindu","Muslim","Christian"};
		assertTrue(compareLists(lookupOptionsActionForm.getCitizenships(), EXPECTED_CITIZENSHIP, 3));

		String[] EXPECTED_LOAN_PURPOSES = {"0000-Animal Husbandry","0001-Cow Purchase","0002-Buffalo Purchase"};
		assertTrue(compareLists(lookupOptionsActionForm.getPurposesOfLoan(), EXPECTED_LOAN_PURPOSES, 129));

		String[] EXPECTED_COLLATERAL_TYPES = {"Type 1","Type 2"};
		assertTrue(compareLists(lookupOptionsActionForm.getCollateralTypes(), EXPECTED_COLLATERAL_TYPES, 2));

		String[] EXPECTED_HANDICAPPED = {"Yes","No"};
		assertTrue(compareLists(lookupOptionsActionForm.getHandicappeds(), EXPECTED_HANDICAPPED, 2));

		String[] EXPECTED_ATTENDENCE = {"P","Ab","AL"};
		assertTrue(compareLists(lookupOptionsActionForm.getAttendances(), EXPECTED_ATTENDENCE, 4));

		String[] EXPECTED_OFFICER_TITLES = {"President","Vice President"};
		assertTrue(compareLists(lookupOptionsActionForm.getOfficerTitles(), EXPECTED_OFFICER_TITLES, 2));
		
	}
	
	public void testCancel() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/lookupOptionsAction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.cancel_success.toString());
	}

	/**
	 * Change the name of the first element in the list and add a new element to 
	 * the end of the list.
	 */
	private String updateOneList(String masterConstant, String configurationConstant, 
			String listName) throws SystemException, ApplicationException {
		MasterPersistence masterPersistence = new MasterPersistence();
		CustomValueList valueList = masterPersistence.getLookUpEntity(masterConstant, DEFAULT_LOCALE);
		Short valueListId = valueList.getEntityId();
		addRequestParameter(configurationConstant,"" + valueListId);
		CustomValueListElement valueListElement = valueList.getCustomValueListElements().get(0);

		String originalName = valueListElement.getLookUpValue();
		valueListElement.setLookupValue(UPDATE_NAME);
		CustomValueListElement newValueListElement = new CustomValueListElement();
		newValueListElement.setLookupValue(NEW_ELEMENT_NAME);
		String[] changesList = { 
				MifosValueList.mapUpdatedCustomValueListElementToString(valueListElement),  
				MifosValueList.mapAddedCustomValueListElementToString(newValueListElement)};

		addRequestParameter(listName, changesList);
		
		return originalName;
	}

	/**
	 * Verify that the name of the first element has been changed, restore the original name,
	 * then verify that an element was added to the list, then remove it to leave the list
	 * in its original state.
	 */
	private void verifyOneListAndRestoreOriginalValues(String masterConstant, String configurationConstant, 
			String listName, String originalName) throws SystemException, ApplicationException {
		MasterPersistence masterPersistence = new MasterPersistence();
		CustomValueList valueList = masterPersistence.getLookUpEntity(masterConstant, DEFAULT_LOCALE);
		List<CustomValueListElement> elementList = valueList.getCustomValueListElements();
		// compare the updated element
		CustomValueListElement valueListElement = elementList.get(0);
		assertEquals(UPDATE_NAME, valueListElement.getLookUpValue());
		// restore the original name
		masterPersistence.updateValueListElementForLocale(valueListElement.getLookUpId(), DEFAULT_LOCALE, originalName);
		// compare the added element
		valueListElement = elementList.get(elementList.size()-1);
		assertEquals(NEW_ELEMENT_NAME, valueListElement.getLookUpValue());
		// remove the added element from the list
		masterPersistence.deleteValueListElement(Integer.valueOf(valueListElement.getLookUpId()));				
	}
	
	public void testUpdate() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/lookupOptionsAction.do");
		addRequestParameter("method", "update");
		
		// list of the original list element names to use when restoring data
		ArrayList<String> originalValues = new ArrayList();

		// indexes into the listData below
		final int MASTER_CONSTANT = 0;
		final int CONFIG_CONSTANT = 1;
		final int LIST_NAME = 2;
		
		String[][] listData = {
			{MasterConstants.SALUTATION, 		ConfigurationConstants.CONFIG_SALUTATION, 		"salutationList"}
			,{MasterConstants.PERSONNEL_TITLE, 	ConfigurationConstants.CONFIG_USER_TITLE, 		"userTitleList"}
			,{MasterConstants.MARITAL_STATUS, 	ConfigurationConstants.CONFIG_MARITAL_STATUS, 	"maritalStatusList"}
			,{MasterConstants.ETHINICITY, 		ConfigurationConstants.CONFIG_ETHNICITY, 		"ethnicityList"}
			,{MasterConstants.EDUCATION_LEVEL, 	ConfigurationConstants.CONFIG_EDUCATION_LEVEL,	"educationLevelList"}
			,{MasterConstants.CITIZENSHIP, 		ConfigurationConstants.CONFIG_CITIZENSHIP, 		"citizenshipList"}
			,{MasterConstants.LOAN_PURPOSES, 	ConfigurationConstants.CONFIG_PURPOSE_OF_LOAN, 	"purposesOfLoanList"}
			,{MasterConstants.HANDICAPPED, 		ConfigurationConstants.CONFIG_HANDICAPPED, 		"handicappedList"}
			,{MasterConstants.COLLATERAL_TYPES, ConfigurationConstants.CONFIG_COLLATERAL_TYPE, 	"collateralTypeList"}
			,{MasterConstants.OFFICER_TITLES, 	ConfigurationConstants.CONFIG_OFFICER_TITLE, 	"officerTitleList"}
			,{MasterConstants.ATTENDENCETYPES, 	ConfigurationConstants.CONFIG_ATTENDANCE, 		"attendanceList"}
			};
		
		for (int listIndex = 0; listIndex < listData.length; ++listIndex) {
			originalValues.add(updateOneList(
					listData[listIndex][MASTER_CONSTANT], 
					listData[listIndex][CONFIG_CONSTANT], 
					listData[listIndex][LIST_NAME]));
		}
		
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.update_success.toString());
		HibernateUtil.flushAndCloseSession();
		HibernateUtil.getSessionTL();
		
		HibernateUtil.startTransaction();
		for (int listIndex = 0; listIndex < listData.length; ++listIndex) {
			verifyOneListAndRestoreOriginalValues(
					listData[listIndex][MASTER_CONSTANT], 
					listData[listIndex][CONFIG_CONSTANT], 
					listData[listIndex][LIST_NAME],
					originalValues.get(listIndex));
		}
		HibernateUtil.getTransaction().commit();

	}
	
}
