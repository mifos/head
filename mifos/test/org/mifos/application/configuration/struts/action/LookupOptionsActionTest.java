package org.mifos.application.configuration.struts.action;

import java.util.List;
import java.util.ResourceBundle;

import org.mifos.application.configuration.struts.actionform.LookupOptionsActionForm;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.configuration.util.helpers.LookupOptionData;
import org.mifos.application.master.business.CustomValueList;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
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

	private final Short DEFAULT_LOCALE = 1;
	private final String UPDATE_NAME = "updated";
	private final String NEW_ELEMENT_NAME = "new";
	private final String DUPLICATE = "DUPLICATE";

	private final String ADD = "add";
	private final String EDIT = "edit";
	
	private final String[] operations = {ADD, EDIT};
	private final String[] names = {NEW_ELEMENT_NAME, UPDATE_NAME};
	
	// indexes into the configurationNameSet below
	private final int MASTER_CONSTANT = 0;
	private final int CONFIG_CONSTANT = 1;
	private final int LIST_NAME = 2;
	
	private final String[][] configurationNameSet = {
			{MasterConstants.SALUTATION, 		ConfigurationConstants.CONFIG_SALUTATION, 		"salutationList"}
			,{MasterConstants.PERSONNEL_TITLE, 	ConfigurationConstants.CONFIG_PERSONNEL_TITLE, 		"userTitleList"}
			,{MasterConstants.MARITAL_STATUS, 	ConfigurationConstants.CONFIG_MARITAL_STATUS, 	"maritalStatusList"}
			,{MasterConstants.ETHINICITY, 		ConfigurationConstants.CONFIG_ETHNICITY, 		"ethnicityList"}
			,{MasterConstants.EDUCATION_LEVEL, 	ConfigurationConstants.CONFIG_EDUCATION_LEVEL,	"educationLevelList"}
			,{MasterConstants.CITIZENSHIP, 		ConfigurationConstants.CONFIG_CITIZENSHIP, 		"citizenshipList"}
			,{MasterConstants.LOAN_PURPOSES, 	ConfigurationConstants.CONFIG_LOAN_PURPOSE, 	"purposesOfLoanList"}
			,{MasterConstants.HANDICAPPED, 		ConfigurationConstants.CONFIG_HANDICAPPED, 		"handicappedList"}
			,{MasterConstants.COLLATERAL_TYPES, ConfigurationConstants.CONFIG_COLLATERAL_TYPE, 	"collateralTypeList"}
			,{MasterConstants.OFFICER_TITLES, 	ConfigurationConstants.CONFIG_OFFICER_TITLE, 	"officerTitleList"}
			,{MasterConstants.ATTENDENCETYPES, 	ConfigurationConstants.CONFIG_ATTENDANCE, 		"attendanceList"}

			};
	
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
		
		assertEquals(expectedLength, first.size());
		for (int index = 0; index < second.length; ++index) {
			assertEquals(second[index], first.get(index).getLookUpValue());
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

	
	private String setupAddOrEditForOneList(String masterConstant, String configurationConstant, 
			String listName, String addOrEdit) throws SystemException, ApplicationException {
		MasterPersistence masterPersistence = new MasterPersistence();
		CustomValueList valueList = masterPersistence.getLookUpEntity(masterConstant, DEFAULT_LOCALE);
		Short valueListId = valueList.getEntityId();
		CustomValueListElement valueListElement = valueList.getCustomValueListElements().get(0);

		addRequestParameter(ConfigurationConstants.ENTITY,configurationConstant);
		addRequestParameter(ConfigurationConstants.ADD_OR_EDIT, addOrEdit);
		SessionUtils.setAttribute(configurationConstant, valueListId, request);

		String originalName = "";
		
		if (addOrEdit.equals(ADD)) {
			CustomValueListElement newValueListElement = new CustomValueListElement();
			newValueListElement.setLookupValue(NEW_ELEMENT_NAME);
			String[] changesList = { 
					MifosValueList.mapAddedCustomValueListElementToString(newValueListElement)};
			addRequestParameter(listName, changesList);
		} else { // edit
			originalName = valueListElement.getLookUpValue();
			valueListElement.setLookupValue(UPDATE_NAME);
			String[] changesList = { 
					MifosValueList.mapUpdatedCustomValueListElementToString(valueListElement)};
			addRequestParameter(listName, changesList);
		}
		
		return originalName;
	}

	private void setupNoSelectionForOneList(String masterConstant, String configurationConstant, 
			String listName) throws SystemException, ApplicationException {
		MasterPersistence masterPersistence = new MasterPersistence();
		CustomValueList valueList = masterPersistence.getLookUpEntity(masterConstant, DEFAULT_LOCALE);
		Short valueListId = valueList.getEntityId();

		addRequestParameter(ConfigurationConstants.ENTITY,configurationConstant);
		addRequestParameter(ConfigurationConstants.ADD_OR_EDIT, EDIT);
		SessionUtils.setAttribute(configurationConstant, valueListId, request);

		addRequestParameter(listName, "");
		
	}
	
	
	private String prepareForUpdate(String masterConstant, String configurationConstant, 
			String listName, String addOrEdit, LookupOptionData data, String nameString) throws SystemException, ApplicationException {
		MasterPersistence masterPersistence = new MasterPersistence();
		CustomValueList valueList = masterPersistence.getLookUpEntity(masterConstant, DEFAULT_LOCALE);
		Short valueListId = valueList.getEntityId();
		CustomValueListElement valueListElement = valueList.getCustomValueListElements().get(0);
		CustomValueListElement valueListElement1 = valueList.getCustomValueListElements().get(1);

		data.setValueListId(valueListId);			
		
		String originalName = "";
		
		if (addOrEdit.equals(ADD)) {
			data.setLookupId(0);
		} else { // edit
			data.setLookupId(valueListElement.getLookUpId());

			originalName = valueListElement.getLookUpValue();
		}

		LookupOptionsActionForm form = new LookupOptionsActionForm();
		
		// if the name string is DUPLACATE, then set the name to the name of the 
		// second element in the list (if it exists) to generate a duplicate error
		if (nameString.equals(DUPLICATE) && valueListElement1 != null) {
			form.setLookupValue(valueListElement1.getLookUpValue());
		} else {
			form.setLookupValue(nameString);
		}
		form.setOneList(configurationConstant, valueList.getCustomValueListElements());
		setActionForm(form);
		
		return originalName;
	}
	
	
	public void testAddEditLookupOption() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/lookupOptionsAction.do");
		addRequestParameter("method", "addEditLookupOption");

		for (int listIndex = 0; listIndex < configurationNameSet.length; ++listIndex) {
			for (String operation : operations) {
				createFlowAndAddToRequest(LookupOptionsAction.class);
				setupAddOrEditForOneList(
						configurationNameSet[listIndex][MASTER_CONSTANT], 
						configurationNameSet[listIndex][CONFIG_CONSTANT], 
						configurationNameSet[listIndex][LIST_NAME],
						operation);

				actionPerform();
				verifyNoActionErrors();
				verifyNoActionMessages();
				verifyForward(ActionForwards.addEditLookupOption_success.toString());

			}
		}				
	}
	
	public void testUpdate() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
			
		for (int listIndex = 0; listIndex < configurationNameSet.length; ++listIndex) {
			String originalValue = "";
			HibernateUtil.getSessionTL();
			
			for (int operationIndex = 0; operationIndex < operations.length; ++operationIndex) { 
				createFlowAndAddToRequest(LookupOptionsAction.class);

				setRequestPathInfo("/lookupOptionsAction.do");
				addRequestParameter("method", "update");

				LookupOptionData data = new LookupOptionData();
				originalValue = prepareForUpdate(
						configurationNameSet[listIndex][MASTER_CONSTANT], 
						configurationNameSet[listIndex][CONFIG_CONSTANT], 
						configurationNameSet[listIndex][LIST_NAME],
						operations[operationIndex],
						data,
						names[operationIndex]);

				SessionUtils.setAttribute(ConfigurationConstants.LOOKUP_OPTION_DATA, data, request);
				addRequestParameter(ConfigurationConstants.ENTITY, 
					configurationNameSet[listIndex][CONFIG_CONSTANT]);

				actionPerform();
				verifyNoActionErrors();
				verifyNoActionMessages();
				verifyForward(ActionForwards.update_success.toString());
			}
			HibernateUtil.flushAndCloseSession();
			HibernateUtil.getSessionTL();
			
			HibernateUtil.startTransaction();
			
			verifyOneListAndRestoreOriginalValues(
					configurationNameSet[listIndex][MASTER_CONSTANT], 
					configurationNameSet[listIndex][CONFIG_CONSTANT], 
					configurationNameSet[listIndex][LIST_NAME],
					originalValue);
			HibernateUtil.commitTransaction();
		}
		
	}
	
	public void testBadStringUpdate() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/lookupOptionsAction.do");
		addRequestParameter("method", "update");
			
		String tooShort = "";
		String tooLong = 
			  "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
			+ "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
			+ "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
			+ "0";
		
		ResourceBundle resources = ResourceBundle.getBundle ("org.mifos.application.configuration.util.resources.ConfigurationUIResources", 
				request.getLocale());
		
		String errorTooShort = resources.getString("errors.novalue");
		String errorTooLong = resources.getString("errors.toolong"); 
		errorTooLong = errorTooLong.replace("{0}", LookUpValueLocaleEntity.MAX_LOOKUP_VALUE_STRING_LENGTH.toString());	
		String errorDuplicate = "errors.duplicatevalue";
		
		String[] names = 	  {tooShort,      tooShort,      tooLong,      tooLong, 	DUPLICATE, 		DUPLICATE};
		String[] operations = {ADD,           EDIT,          ADD,          EDIT,		ADD,			EDIT};
		String[] errors = 	  {errorTooShort, errorTooShort, errorTooLong, errorTooLong,errorDuplicate,	errorDuplicate};
		
		for (int operationIndex = 0; operationIndex < operations.length; ++operationIndex) { 
			flowKey = createFlow(request, LookupOptionsAction.class);
			request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);

			LookupOptionData data = new LookupOptionData();
			prepareForUpdate(
					configurationNameSet[0][MASTER_CONSTANT], 
					configurationNameSet[0][CONFIG_CONSTANT], 
					configurationNameSet[0][LIST_NAME],
					operations[operationIndex],
					data,
					names[operationIndex]);

			SessionUtils.setAttribute(ConfigurationConstants.LOOKUP_OPTION_DATA, data, request);
			addRequestParameter(ConfigurationConstants.ENTITY, 
					configurationNameSet[0][CONFIG_CONSTANT]);

			actionPerform();
			String [] errorMessages = {errors[operationIndex]};
			verifyActionErrors(errorMessages);
			verifyInputForward();
		}

	}

	public void testNoSelectionToEdit() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/lookupOptionsAction.do");
		addRequestParameter("method", "addEditLookupOption");
					
		String errorNoSelection = "errors.selectvalue";
			
		for (int listIndex = 0; listIndex < configurationNameSet.length; ++listIndex) {
			flowKey = createFlow(request, LookupOptionsAction.class);
			request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);

			LookupOptionData data = new LookupOptionData();
			setupNoSelectionForOneList(
					configurationNameSet[listIndex][MASTER_CONSTANT], 
					configurationNameSet[listIndex][CONFIG_CONSTANT], 
					configurationNameSet[listIndex][LIST_NAME]);

			SessionUtils.setAttribute(ConfigurationConstants.LOOKUP_OPTION_DATA, data, request);

			actionPerform();
			String [] errorMessages = {errorNoSelection};
			verifyActionErrors(errorMessages);
			verifyInputForward();
		}

	}
	
}
