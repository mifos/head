package org.mifos.application.personnel.business;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerAddressDetailEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerPositionView;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.exceptions.AssociatedObjectStaleException;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.master.util.valueobjects.SupportedLocales;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OfficeStatus;
import org.mifos.application.personnel.exceptions.HierarchyChangeException;
import org.mifos.application.personnel.exceptions.PersonnelException;
import org.mifos.application.personnel.exceptions.StatusChangeException;
import org.mifos.application.personnel.exceptions.TransferNotPossibleException;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.personnel.util.helpers.PersonnelStatus;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.EncryptionException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.authentication.EncryptionService;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.valueobjects.Context;

public class PersonnelBO extends BusinessObject {

	private final Short personnelId;

	private PersonnelLevelEntity level;

	private String globalPersonnelNum;

	private OfficeBO office;

	private Integer title;

	private String displayName;

	private PersonnelStatusEntity status;

	private SupportedLocalesEntity preferredLocale;

	private String searchId;

	private Integer maxChildCount;

	private byte[] encriptedPassword;

	private final String userName;

	private String emailId;

	private Short passwordChanged;

	private Date lastLogin;

	private Short locked;

	private Short noOfTries;

	private PersonnelDetailsEntity personnelDetails;

	private Set<PersonnelRoleEntity> personnelRoles;

	private Set<PersonnelCustomFieldEntity> customFields;

	private MifosLogger logger;

	protected PersonnelBO() {
		this.level = null;
		this.personnelDetails = new PersonnelDetailsEntity();
		this.preferredLocale = new SupportedLocalesEntity();
		this.customFields = new HashSet<PersonnelCustomFieldEntity>();
		this.personnelId = null;
		this.userName = null;

	}

	public PersonnelBO(PersonnelLevel level, OfficeBO office,
			Integer title, Short preferredLocale, String password,
			String userName, String emailId, Set<PersonnelRoleEntity> personnelRoles,
			List<CustomFieldView> customFields,
			Name name, String governmentIdNumber,
			Date dob, Integer maritalStatus, Integer gender,
			Date dateOfJoiningMFI, Date dateOfJoiningBranch, Address address,
			Short createdBy) throws PersonnelException {
		super();
		setCreateDetails(createdBy,new Date());
		logger = MifosLogManager.getLogger(LoggerConstants.PERSONNEL_LOGGER);
		this.displayName = name.getDisplayName();
		verifyFields(userName, governmentIdNumber, dob);
		this.level = new PersonnelLevelEntity(level);
		this.office = office;
		this.title = title;
		this.preferredLocale = new SupportedLocalesEntity(preferredLocale);
		this.userName = userName;
		this.emailId = emailId;
		this.personnelDetails = new PersonnelDetailsEntity(name,
				governmentIdNumber, dob, maritalStatus, gender,
				dateOfJoiningMFI, dateOfJoiningBranch, this, address);
		this.personnelRoles = personnelRoles;
		this.customFields = new HashSet<PersonnelCustomFieldEntity>();
		this.personnelId = null;
		this.globalPersonnelNum = "XX";
		if (customFields != null)
			for (CustomFieldView view : customFields) {
				this.customFields.add(new PersonnelCustomFieldEntity(view
						.getFieldValue(), view.getFieldId(), this));
			}
		this.passwordChanged = Constants.NO;
		this.unLock();
		this.noOfTries = Constants.NO;
		this.encriptedPassword = getEncryptedPassword(password);

	}

	public Set<PersonnelCustomFieldEntity> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Set<PersonnelCustomFieldEntity> customFields) {
		this.customFields = customFields;
	}

	public String getEmailId() {
		return emailId;
	}

	public String getGlobalPersonnelNum() {
		return globalPersonnelNum;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public PersonnelLevelEntity getLevel() {
		return level;
	}

	public Integer getMaxChildCount() {
		return maxChildCount;
	}

	public OfficeBO getOffice() {
		return office;
	}

	public boolean isPasswordChanged() {
		return this.passwordChanged > 0;
	}

	public PersonnelDetailsEntity getPersonnelDetails() {
		return personnelDetails;
	}

	public void setPersonnelDetails(PersonnelDetailsEntity personnelDetails) {
		this.personnelDetails = personnelDetails;
	}

	public Short getPersonnelId() {
		return personnelId;
	}

	public SupportedLocalesEntity getPreferredLocale() {
		return preferredLocale;
	}

	public Integer getTitle() {
		return title;
	}

	public String getUserName() {
		return userName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public boolean isLocked() {
		return this.locked > 0;
	}

	public void lock() {
		this.locked = Short.valueOf("1");
	}

	public void unLock() {
		this.locked = Short.valueOf("0");
	}

	public Set<PersonnelRoleEntity> getPersonnelRoles() {
		return personnelRoles;
	}

	public void setPersonnelRoles(Set<PersonnelRoleEntity> personnelRoles) {
		this.personnelRoles = personnelRoles;
	}

	private void updateCustomFields(List<CustomFieldView> customfields) {
		if (this.customFields != null && customfields != null) {
			for (CustomFieldView fieldView : customfields)
				for (PersonnelCustomFieldEntity fieldEntity : this.customFields)
					if (fieldView.getFieldId().equals(fieldEntity.getFieldId()))
						fieldEntity.setFieldValue(fieldView.getFieldValue());
		}
	}

	public void setEncriptedPassword(byte[] encriptedPassword) {
		this.encriptedPassword = encriptedPassword;
	}

	public PersonnelStatusEntity getStatus() {
		return status;
	}

	public void save() throws PersonnelException {
		try {
			PersonnelPersistence persistence = new PersonnelPersistence();
			persistence.createOrUpdate(this);
			this.globalPersonnelNum = generateGlobalPersonnelNum(this.office
					.getGlobalOfficeNum(), this.personnelId);
			persistence.createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new PersonnelException(e);

		}
	}

	private String generateGlobalPersonnelNum(String officeGlobalNum,
			int maxPersonnelId) {
		logger.debug("Passed office global no is : ".concat(officeGlobalNum)
				.concat(" and maxpersonnelid is : " + maxPersonnelId));
		String userId = "";
		int numberOfZeros = 5 - String.valueOf(maxPersonnelId).length();
		for (int i = 0; i < numberOfZeros; i++) {
			userId = userId + "0";
		}
		userId = userId + maxPersonnelId;
		String userGlobalNum = officeGlobalNum + "-" + userId;
		logger.debug("Generated userGlobalNum is : ".concat(userGlobalNum));
		return userGlobalNum;
	}

	private byte[] getEncryptedPassword(String password)
			throws PersonnelException {
		byte[] encryptedPassword = null;
		try {
			logger.debug("Passed password string is : " + password);
			encryptedPassword = EncryptionService.getInstance()
					.createEncryptedPassword(password);
			logger.debug("got Encripted Password from EncryptionService");

		} catch (EncryptionException e) {
			throw new PersonnelException(e);
		} catch (SystemException e) {
			throw new PersonnelException(e);
		}

		return encryptedPassword;
	}

	private void verifyFields(String userName, String governmentIdNumber,
			Date dob) throws PersonnelException {

		PersonnelPersistence persistence = new PersonnelPersistence();
		if (StringUtils.isNullOrEmpty(userName))
			throw new PersonnelException(
					PersonnelConstants.ERRORMANDATORY);
		if (persistence.isUserExist(userName))
			throw new PersonnelException(PersonnelConstants.DUPLICATE_USER,
					new Object[] { userName });
		if (!StringUtils.isNullOrEmpty(governmentIdNumber)) {
			if (persistence.isUserExistWithGovernmentId(governmentIdNumber))
				throw new PersonnelException(
						PersonnelConstants.DUPLICATE_GOVT_ID,
						new Object[] { governmentIdNumber });
		} else {
			if (persistence.isUserExist(displayName, dob))
				throw new PersonnelException(
						PersonnelConstants.DUPLICATE_USER_NAME_OR_DOB,
						new Object[] { displayName });
		}
	}
	
	/*public void update(UserContext userContext ,PersonnelStatus newStatus,PersonnelLevel newLevel, Short officeId,  Name name, Integer maritalStatus, Integer gender, Address address,  List<CustomFieldView> customFields) throws PersonnelException {
			validateForUpdate(newStatus,officeId,newLevel);
			this.setUserContext(userContext);
			updateCustomFields(customFields);
			Date dateOfJoiningBranch = null;
			if(!officeId.equals(this.getOffice() .getOfficeId())){
				dateOfJoiningBranch = new Date();
			}
			updatePersonnelDetails(name, maritalStatus, gender, address,dateOfJoiningBranch);
	}*/
	
	public void updatePersonnelDetails(Name name, Integer maritalStatus, Integer gender, Address address, Date dateOfJoiningBranch ) throws PersonnelException {
		if(getPersonnelDetails()==null){
			getPersonnelDetails().updateDetails(name,maritalStatus, gender, address,dateOfJoiningBranch);
			getPersonnelDetails().setAddress(address);
			getPersonnelDetails().setName(name);
			getPersonnelDetails().setAddress(address);
			getPersonnelDetails().setAddress(address);
		
		}
	}
	
	private void validateForUpdate(PersonnelStatus newStatus , Short newOffice, PersonnelLevel newLevel)throws PersonnelException{
		
		if(!level.getId().equals(newLevel))
			validateUserHierarchyChange(newLevel, newOffice);
		if( !office.getOfficeId().equals(newOffice)){
			validateOfficeTransfer(newOffice, newLevel);
		}
		if(!status.getId().equals(newStatus)){
			validateStatusChange(newStatus, newLevel,newOffice);
		}
		
	}	
	
	private void validateStatusChange(PersonnelStatus newStatus, PersonnelLevel newLevel, Short newOffice) throws PersonnelException{
		if( status.getId().equals(PersonnelStatus.ACTIVE) && newStatus.equals(PersonnelStatus.INACTIVE) && newLevel.equals(PersonnelLevel.LOAN_OFFICER)){
			if(new PersonnelPersistence().getActiveChildrenForLoanOfficer(personnelId, office.getOfficeId())){
				Object values[]=new Object[1];
				values[0]=globalPersonnelNum;
				throw new PersonnelException(PersonnelConstants.STATUS_CHANGE_EXCEPTION,values);
			}
		}
		else if( status.getId().equals(PersonnelStatus.INACTIVE) && newStatus.equals(PersonnelStatus.ACTIVE)&& !(office.isActive())){
			Object values[]=new Object[1];
			values[0]=office.getOfficeId();
			throw new PersonnelException(PersonnelConstants.INACTIVE_BRANCH,values);
		}
		
	}
	
	private void validateOfficeTransfer(Short newOfficeId ,PersonnelLevel newLevel)throws PersonnelException {
		if(newLevel.equals(PersonnelLevel.LOAN_OFFICER)){
			if(!newOfficeId.equals(OfficeLevel.BRANCHOFFICE.getValue() )){
				Object values[]=new Object[1];
				values[0]=globalPersonnelNum;
				throw new PersonnelException(PersonnelConstants.LO_ONLY_IN_BRANCHES,values);
			}
		}
		if(new PersonnelPersistence().getActiveChildrenForLoanOfficer(personnelId, office.getOfficeId())){
				Object values[]=new Object[1];
				values[0]=globalPersonnelNum;
				throw new PersonnelException(PersonnelConstants.TRANSFER_NOT_POSSIBLE_EXCEPTION,values);
		}
		
	}
	
	private void validateUserHierarchyChange(PersonnelLevel newLevel , Short officeId)throws PersonnelException{
		if(level.getId().equals(PersonnelLevel.LOAN_OFFICER.getValue()) && newLevel.equals(PersonnelLevel.NON_LOAN_OFFICER)){
			if(new PersonnelPersistence().getAllChildrenForLoanOfficer(personnelId, getOffice().getOfficeId())){
				Object values[]=new Object[1];
				values[0]=globalPersonnelNum;
				throw new PersonnelException(PersonnelConstants.HIERARCHY_CHANGE_EXCEPTION,values);
			}
		}
		else if(level.getId().equals(PersonnelLevel.NON_LOAN_OFFICER.getValue()) && newLevel.equals(PersonnelLevel.LOAN_OFFICER) && (!officeId.equals(OfficeLevel.BRANCHOFFICE))){
				Object values[]=new Object[1];
				values[0]=globalPersonnelNum;
				throw new PersonnelException(PersonnelConstants.LO_ONLY_IN_BRANCHES,values);
			
		}
		
	}
	
}
