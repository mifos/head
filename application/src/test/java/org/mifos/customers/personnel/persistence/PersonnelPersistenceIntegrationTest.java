package org.mifos.customers.personnel.persistence;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Assert;

import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerNoteEntity;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeTemplate;
import org.mifos.customers.office.business.OfficeTemplateImpl;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelNotesEntity;
import org.mifos.customers.personnel.business.PersonnelTemplate;
import org.mifos.customers.personnel.business.PersonnelTemplateImpl;
import org.mifos.customers.personnel.business.PersonnelView;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.CustomerStatusFlag;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.security.util.UserContext;

public class PersonnelPersistenceIntegrationTest extends MifosIntegrationTestCase {

    public PersonnelPersistenceIntegrationTest() throws Exception {
        super();
    }

    private static final Short OFFICE_WITH_BRANCH_MANAGER = Short.valueOf("3");

    private MeetingBO meeting;
    private CustomerBO center;
    private CustomerBO client;
    private CustomerBO group;
    private OfficeBO office;
    private OfficeBO branchOffice;
    private OfficeBO createdBranchOffice;
    private PersonnelBO personnel;
    private Name name;
    private final PersonnelPersistence personnelPersistence = new PersonnelPersistence();
    private final OfficePersistence officePersistence = new OfficePersistence();

    private CustomerService customerService = DependencyInjectedServiceLocator.locateCustomerService();
    private CustomerDao customerDao = DependencyInjectedServiceLocator.locateCustomerDao();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        initializeStatisticsService();
    }

    @Override
    public void tearDown() throws Exception {
        office = null;
        branchOffice = null;
        name = null;
        TestObjectFactory.cleanUp(client);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        TestObjectFactory.cleanUp(personnel);
        TestObjectFactory.cleanUp(createdBranchOffice);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testCreatePersonnel() throws Exception {
        UserContext userContext = TestUtils.makeUser();
        OfficeTemplate officeTemplate = OfficeTemplateImpl.createNonUniqueOfficeTemplate(OfficeLevel.BRANCHOFFICE);
        long transactionCount = getStatisticsService().getSuccessfulTransactionCount();
        try {
            OfficeBO office = officePersistence.createOffice(userContext, officeTemplate);
            PersonnelTemplate template = PersonnelTemplateImpl.createNonUniquePersonnelTemplate(office.getOfficeId());
            PersonnelBO personnel = personnelPersistence.createPersonnel(userContext, template);

            Assert.assertNotNull(personnel.getPersonnelId());
            Assert.assertTrue(personnel.isActive());
            Assert.assertFalse(personnel.isPasswordChanged());
        } finally {
            StaticHibernateUtil.rollbackTransaction();
        }
        Assert.assertTrue(transactionCount == getStatisticsService().getSuccessfulTransactionCount());
    }

    public void testCreatePersonnelValidationFailure() throws Exception {
        UserContext userContext = TestUtils.makeUser();
        PersonnelTemplate template = PersonnelTemplateImpl.createNonUniquePersonnelTemplate(new Short((short) -1));
        long transactionCount = getStatisticsService().getSuccessfulTransactionCount();
        try {
            PersonnelBO personnel = personnelPersistence.createPersonnel(userContext, template);
            Assert.fail("Should not have been able to create personnel " + personnel.getDisplayName());
        } catch (ValidationException e) {
            Assert.assertTrue(e.getMessage().equals(PersonnelConstants.OFFICE));
        } finally {
            StaticHibernateUtil.rollbackTransaction();
        }
        Assert.assertTrue(transactionCount == getStatisticsService().getSuccessfulTransactionCount());
    }

    public void testActiveLoanOfficersInBranch() throws Exception {
        List<PersonnelView> personnels = personnelPersistence.getActiveLoanOfficersInBranch(
                PersonnelConstants.LOAN_OFFICER, Short.valueOf("3"), Short.valueOf("3"),
                PersonnelConstants.LOAN_OFFICER);
        Assert.assertEquals(1, personnels.size());
    }

    public void testNonLoanOfficerInBranch() throws Exception {
        List<PersonnelView> personnels = personnelPersistence.getActiveLoanOfficersInBranch(
                PersonnelConstants.LOAN_OFFICER, Short.valueOf("3"), OFFICE_WITH_BRANCH_MANAGER,
                PersonnelConstants.NON_LOAN_OFFICER);
        Assert.assertEquals(1, personnels.size());
    }

    public void testIsUserExistSucess() throws Exception {
        Assert.assertTrue(personnelPersistence.isUserExist("mifos"));
    }

    public void testIsUserExistFailure() throws Exception {
        Assert.assertFalse(personnelPersistence.isUserExist("XXX"));
    }

    public void testIsUserExistWithGovernmentIdSucess() throws Exception {
        Assert.assertTrue(personnelPersistence.isUserExistWithGovernmentId("123"));
    }

    public void testIsUserExistWithGovernmentIdFailure() throws Exception {
        Assert.assertFalse(personnelPersistence.isUserExistWithGovernmentId("XXX"));
    }

    public void testIsUserExistWithDobAndDisplayNameSucess() throws Exception {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Assert.assertTrue(personnelPersistence.isUserExist("mifos", dateFormat.parse("1979-12-12")));
    }

    public void testIsUserExistWithDobAndDisplayNameFailure() throws Exception {
        Assert.assertFalse(personnelPersistence.isUserExist("mifos", new GregorianCalendar(1989, 12, 12, 0, 0, 0)
                .getTime()));
    }

    public void testActiveCustomerUnderLO() throws Exception {
        createCustomers(CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_ACTIVE);
        Assert.assertTrue(personnelPersistence.getActiveChildrenForLoanOfficer(Short.valueOf("1"), Short.valueOf("3")));
    }

    public void testGetAllCustomerUnderLO() throws Exception {
        createCustomers(CustomerStatus.GROUP_CLOSED, CustomerStatus.CLIENT_CANCELLED);
        Assert.assertTrue(personnelPersistence.getAllChildrenForLoanOfficer(Short.valueOf("1"), Short.valueOf("3")));
        StaticHibernateUtil.commitTransaction();

        CustomerStatusFlag customerStatusFlag = null;
        CustomerNoteEntity customerNote = new CustomerNoteEntity("Made Inactive", new java.util.Date(), center.getPersonnel(), center);

        customerService.updateCenterStatus((CenterBO) center, CustomerStatus.CENTER_INACTIVE, customerStatusFlag, customerNote);
        center = customerDao.findCenterBySystemId(center.getGlobalCustNum());

        Assert.assertTrue(personnelPersistence.getAllChildrenForLoanOfficer(Short.valueOf("1"), Short.valueOf("3")));
    }

    public void testGetAllPersonnelNotes() throws Exception {
        office = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
        branchOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
        createdBranchOffice = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, office, "Office_BRanch1", "OFB");
        StaticHibernateUtil.closeSession();
        createdBranchOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(OfficeBO.class,
                createdBranchOffice.getOfficeId());
        createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
        Assert.assertEquals(branchOffice.getOfficeId(), personnel.getOffice().getOfficeId());
        createInitialObjects(branchOffice.getOfficeId(), personnel.getPersonnelId());

        PersonnelNotesEntity personnelNotes = new PersonnelNotesEntity("Personnel notes created",
                new PersonnelPersistence().getPersonnel(PersonnelConstants.SYSTEM_USER), personnel);
        personnel.addNotes(PersonnelConstants.SYSTEM_USER, personnelNotes);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        client = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class, client.getCustomerId());
        group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class, personnel.getPersonnelId());
        createdBranchOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(OfficeBO.class,
                createdBranchOffice.getOfficeId());
        Assert.assertEquals(1, personnelPersistence.getAllPersonnelNotes(personnel.getPersonnelId()).getSize());
    }

    public void testSuccessfullGetPersonnel() throws Exception {
        branchOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
        personnel = createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
        String oldUserName = personnel.getUserName();
        personnel = personnelPersistence.getPersonnelByUserName(personnel.getUserName());
        Assert.assertEquals(oldUserName, personnel.getUserName());
    }

    public void testGetPersonnelByGlobalPersonnelNum() throws Exception {
        Assert.assertNotNull(personnelPersistence.getPersonnelByGlobalPersonnelNum("1"));
    }

    public void testGetPersonnelRoleCount() throws Exception {
        Integer count = personnelPersistence.getPersonnelRoleCount((short) 2);
        Assert.assertEquals(0, (int) count);
        count = personnelPersistence.getPersonnelRoleCount((short) 1);
        Assert.assertEquals(3, (int) count);
    }

    public void testSearch() throws Exception {
        branchOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
        personnel = createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
        QueryResult queryResult = personnelPersistence.search(personnel.getUserName(), Short.valueOf("1"));
        Assert.assertNotNull(queryResult);
        Assert.assertEquals(1, queryResult.getSize());
        Assert.assertEquals(1, queryResult.get(0, 10).size());
    }

    public void testSearchFirstNameAndLastName() throws Exception {
        branchOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
        personnel = createPersonnelWithName(branchOffice, PersonnelLevel.LOAN_OFFICER, new Name("Rajender", null,
                "singh", "saini"));
        QueryResult queryResult = personnelPersistence.search(personnel.getPersonnelDetails().getName().getFirstName()
                + " " + personnel.getPersonnelDetails().getName().getLastName(), Short.valueOf("1"));
        Assert.assertNotNull(queryResult);
        Assert.assertEquals(1, queryResult.getSize());
        Assert.assertEquals(1, queryResult.get(0, 10).size());
    }

    public void testGetActiveLoanOfficersUnderOffice() throws Exception {
        branchOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
        personnel = createPersonnel(branchOffice, PersonnelLevel.LOAN_OFFICER);
        List<PersonnelBO> loanOfficers = personnelPersistence.getActiveLoanOfficersUnderOffice(branchOffice
                .getOfficeId());
        Assert.assertNotNull(loanOfficers);
        Assert.assertEquals(2, loanOfficers.size());
        Assert.assertEquals("loan officer", loanOfficers.get(0).getDisplayName());
        Assert.assertEquals("XYZ", loanOfficers.get(1).getDisplayName());
    }

    public void testGetSupportedLocale() throws Exception {
        // asserting only on not null as suppored locales can be added by user
        Assert.assertNotNull(personnelPersistence.getSupportedLocales());
    }

    public void testGetAllPersonnel() throws Exception {
        List<PersonnelBO> personnel = personnelPersistence.getAllPersonnel();
        Assert.assertNotNull(personnel);
        Assert.assertEquals(3, personnel.size());
    }

    private void createInitialObjects(final Short officeId, final Short personnelId) {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());

        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting, officeId, personnelId);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
    }

    private PersonnelBO createPersonnel(final OfficeBO office, final PersonnelLevel personnelLevel) throws Exception {
        name = new Name("XYZ", null, null, null);
        return create(personnelLevel, name, PersonnelConstants.SYSTEM_USER, office);
    }

    private PersonnelBO createPersonnelWithName(final OfficeBO office, final PersonnelLevel personnelLevel,
            final Name personnelName) throws Exception {
        return create(personnelLevel, personnelName, PersonnelConstants.SYSTEM_USER, office);
    }

    private PersonnelBO create(final PersonnelLevel personnelLevel, final Name name, final Short createdBy,
            final OfficeBO office) throws Exception {
        List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
        customFieldView.add(new CustomFieldView(Short.valueOf("9"), "123456", CustomFieldType.NUMERIC));
        Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd");
        Date date = new Date();
        personnel = new PersonnelBO(personnelLevel, office, Integer.valueOf("1"), Short.valueOf("1"), "ABCD", "XYZ",
                "xyz@yahoo.com", null, customFieldView, name, "111111", date, Integer.valueOf("1"), Integer
                        .valueOf("1"), date, date, address, createdBy);
        personnel.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        personnel = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class, personnel.getPersonnelId());
        return personnel;
    }

    private void createCustomers(final CustomerStatus groupStatus, final CustomerStatus clientStatus) {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("group", groupStatus, center);
        client = TestObjectFactory.createClient("client", clientStatus, group, new Date());
    }

    public void testGetActiveBranchManagerUnderOffice() throws Exception {
        List<PersonnelBO> activeBranchManagersUnderOffice = new PersonnelPersistence()
                .getActiveBranchManagersUnderOffice(OFFICE_WITH_BRANCH_MANAGER, new RolesPermissionsPersistence()
                        .getRole(Short.valueOf("1")));
        Assert.assertNotNull(activeBranchManagersUnderOffice);
        Assert.assertEquals(2, activeBranchManagersUnderOffice.size());
    }

}
