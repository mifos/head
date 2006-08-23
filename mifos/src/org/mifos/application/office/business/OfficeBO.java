package org.mifos.application.office.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OfficeStatus;
import org.mifos.application.office.util.helpers.OperationMode;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.security.util.EventManger;
import org.mifos.framework.security.util.OfficeSearch;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.plugin.helper.EntityMasterConstants;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.framework.util.helpers.StringUtils;

import com.sun.corba.se.impl.ior.NewObjectKeyTemplateBase;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

public class OfficeBO extends BusinessObject {

	private MifosLogger logger = null;

	private final Short officeId;

	private String globalOfficeNum;

	private OfficeLevelEntity level;

	private final Integer maxChildCount;

	private String searchId;

	private final Short operationMode;

	private OfficeBO parentOffice;

	private OfficeStatusEntity status;

	private Set<OfficeCustomFieldEntity> customFields;

	private String officeName;

	private String shortName;

	private OfficeAddressEntity address;

	private Set<OfficeBO> children;

	public OfficeBO() {
		super();
		maxChildCount = null;
		officeId = null;
		operationMode = null;
		status = new OfficeStatusEntity(OfficeStatus.ACTIVE);
		address = new OfficeAddressEntity();
	}

	public OfficeBO(UserContext userContext, OfficeLevel level,
			OfficeBO parentOffice, List<CustomFieldView> customFields,
			String officeName, String shortName, Address address,
			OperationMode operationMode) throws OfficeException {
		super(userContext);
		// initialize logger
		logger = MifosLogManager.getLogger(LoggerConstants.OFFICELOGGER);
		verifyFields(officeName, shortName, level, operationMode, parentOffice);
		logger.debug(new StringBuilder().append(
				"Creating office object with data # officeName : ").append(
				officeName).append(" shortName :").append(shortName).append(
				" officeLevel :").append(level.getValue()).append(
				" operationMode : ").append(operationMode.getValue())
				.toString());

		setCreateDetails();

		this.globalOfficeNum = null;
		this.operationMode = operationMode.getValue();
		this.maxChildCount = 0;
		this.searchId = null;
		this.officeId = null;
		this.level = new OfficeLevelEntity(level);
		this.status = new OfficeStatusEntity(OfficeStatus.ACTIVE);
		this.parentOffice = parentOffice;

		this.officeName = officeName;
		this.shortName = shortName;
		if (address != null)
			this.address = new OfficeAddressEntity(this, address);
		this.customFields = new HashSet<OfficeCustomFieldEntity>();
		if (customFields != null)
			for (CustomFieldView view : customFields) {
				this.customFields.add(new OfficeCustomFieldEntity(view
						.getFieldValue(), view.getFieldId(), this));
			}
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getGlobalOfficeNum() {
		return globalOfficeNum;
	}

	public OfficeLevel getOfficeLevel() throws OfficeException {
		try {
			return OfficeLevel.getOfficeLevel(level.getId());
		} catch (PropertyNotFoundException e) {
			throw new OfficeException(e);
		}

	}

	public OfficeStatus getOfficeStatus() throws OfficeException {
		try {
			return OfficeStatus.getOfficeStatus(status.getId());
		} catch (PropertyNotFoundException e) {
			throw new OfficeException(e);
		}
	}

	public OfficeStatusEntity getStatus() {
		return this.status;
	}

	public OfficeLevelEntity getLevel() {
		return this.level;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public Short getOfficeId() {
		return officeId;
	}

	public OperationMode getMode() throws OfficeException {
		try {
			return OperationMode.getOperationMode(operationMode);
		} catch (PropertyNotFoundException e) {
			throw new OfficeException(e);
		}
	}

	public OfficeBO getParentOffice() {
		return parentOffice;
	}

	public void setParentOffice(OfficeBO parentOffice) {
		this.parentOffice = parentOffice;
	}

	public String getSearchId() {
		return searchId;
	}
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public OfficeAddressEntity getAddress() {
		return address;
	}

	public Set<OfficeCustomFieldEntity> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Set<OfficeCustomFieldEntity> customFields) {
		if (customFields != null)
			this.customFields = customFields;
	}

	public void setAddress(OfficeAddressEntity address) throws OfficeException {
		this.address = address;
	}

	private void verifyFields(String officeName, String shortName,
			OfficeLevel level, OperationMode operationMode,
			OfficeBO parentOffice) throws OfficeException {

		OfficePersistence officePersistence = new OfficePersistence();
		if (StringUtils.isNullOrEmpty(officeName))
			throw new OfficeException(
					OfficeConstants.ERRORMANDATORYFIELD,
					new Object[] { getLocaleString(OfficeConstants.OFFICE_NAME) });
		if (officePersistence.isOfficeNameExist(officeName))
			throw new OfficeException(OfficeConstants.OFFICENAMEEXIST);
		if (StringUtils.isNullOrEmpty(shortName))
			throw new OfficeException(
					OfficeConstants.ERRORMANDATORYFIELD,
					new Object[] { getLocaleString(OfficeConstants.OFFICESHORTNAME) });
		if (officePersistence.isOfficeShortNameExist(shortName))
			throw new OfficeException(OfficeConstants.OFFICESHORTNAMEEXIST);
		if (level == null)
			throw new OfficeException(
					OfficeConstants.ERRORMANDATORYFIELD,
					new Object[] { getLocaleString(OfficeConstants.OFFICELEVEL) });
		if (operationMode == null)
			throw new OfficeException(
					OfficeConstants.ERRORMANDATORYFIELD,
					new Object[] { getLocaleString(OfficeConstants.OFFICEOPERATIONMODE) });
		if (parentOffice == null)
			throw new OfficeException(
					OfficeConstants.ERRORMANDATORYFIELD,
					new Object[] { getLocaleString(OfficeConstants.PARENTOFFICE) });

	}

	private String getLocaleString(String key) {
		logger.debug("Getting resource text with key :  " + key);
		ResourceBundle resourceBundle = ResourceBundle.getBundle(
				OfficeConstants.OFFICERESOURCEPATH, userContext
						.getPereferedLocale());
		return resourceBundle.getString(key);

	}

	private String generateOfficeGlobalNo() throws OfficeException {

		try {
			String officeGlobelNo = String.valueOf(new OfficePersistence()
					.getMaxOfficeId().intValue() + 1);
			if (officeGlobelNo.length() > 4) {
				throw new OfficeException(OfficeConstants.MAXOFFICELIMITREACHED);
			}
			StringBuilder temp = new StringBuilder("");
			for (int i = officeGlobelNo.length(); i < 4; i++) {
				temp.append("0");
			}

			return officeGlobelNo = temp.append(officeGlobelNo).toString();
		} catch (ParseException e) {
			throw new OfficeException(e);
		}

	}

	private String generateSearchId() {
		Integer noOfChildern = new OfficePersistence()
				.getChildCount(parentOffice.getOfficeId());
		String parentSearchId = HierarchyManager.getInstance().getSearchId(
				parentOffice.getOfficeId());
		parentSearchId += ".";
		parentSearchId += ++noOfChildern;
		return parentSearchId;
	}

	public void save() throws OfficeException {

		this.globalOfficeNum = generateOfficeGlobalNo();
		this.searchId = generateSearchId();
		new OfficePersistence().createOrUpdate(this);
		// if we are here it means office created sucessfully
		// we need to update hierarchy manager cache
		OfficeSearch os = new OfficeSearch(getOfficeId(), getSearchId(),
				getParentOffice().getOfficeId());
		List<OfficeSearch> osList = new ArrayList<OfficeSearch>();
		osList.add(os);
		EventManger.postEvent(Constants.CREATE, osList,
				SecurityConstants.OFFICECHANGEEVENT);

	}

	private void changeStatus(OfficeStatus status) throws OfficeException {
		try {

			if (!this.status.getId().equals(status.getValue())) {

				if (status == OfficeStatus.INACTIVE) {
					canInactivateOffice();
				} else {
					canActivateOffice();
				}
				// still here we can update the status
				MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory
						.getInstance().getPersistenceService(
								PersistenceServiceName.MasterDataService);
				this.status = (OfficeStatusEntity) masterPersistenceService
						.findById(OfficeStatusEntity.class, status.getValue());
			}
		} catch (ServiceException e) {
			throw new OfficeException(e);
		}

	}

	private void canInactivateOffice() throws OfficeException {
		OfficePersistence officePersistence = new OfficePersistence();
		if (officePersistence.hasActiveChildern(this.officeId))
			throw new OfficeException(OfficeConstants.KEYHASACTIVECHILDREN);
		if (officePersistence.hasActivePeronnel(this.officeId)) {
			throw new OfficeException(OfficeConstants.KEYHASACTIVEPERSONNEL);

		}
	}

	private void canActivateOffice() throws OfficeException {

		if (parentOffice.getOfficeStatus().equals(OfficeStatus.INACTIVE))
			throw new OfficeException(OfficeConstants.KEYPARENTNOTACTIVE);
	}

	public void update(String newName, String newShortName,
			OfficeStatus newStatus, OfficeLevel newLevel, OfficeBO newParent, Address address, List<CustomFieldView> customFileds)
			throws OfficeException {
		changeOfficeName(newName);
		changeOfficeShortName(newShortName);
		changeStatus(newStatus);
		updateParent(newParent);
		updateLevel(newLevel);
		updateAddress(address);
		updateCustomFields(customFileds);
		setUpdateDetails();
		new OfficePersistence().createOrUpdate(this);
	}

	private void changeOfficeName(String newName) throws OfficeException {

		if (!this.officeName.equalsIgnoreCase(newName)) {
			if (new OfficePersistence().isOfficeNameExist(newName))
				throw new OfficeException(OfficeConstants.OFFICENAMEEXIST);
			this.officeName = newName;
		}
	}

	private void changeOfficeShortName(String newShortName)
			throws OfficeException {

		if (!this.shortName.equalsIgnoreCase(newShortName)) {
			if (new OfficePersistence().isOfficeShortNameExist(newShortName))
				throw new OfficeException(OfficeConstants.OFFICESHORTNAMEEXIST);
			this.shortName = newShortName;
		}
	}

	private void updateParent(OfficeBO parentOffice) {

		if (parentOffice != null) {
			if (this.parentOffice != null) {
				if (!this.parentOffice.getOfficeId().equals(
						parentOffice.getOfficeId())) {

					// TODO : check the code of updating searchid's
					// remove this child from old parent
					this.parentOffice.removeChild(this);
					parentOffice.addChild(this);
					updateSearchId(parentOffice.getSearchId() + "."
							+ (parentOffice.getChildren().size()));
					this.parentOffice.updateSearchId(this.parentOffice
							.getSearchId());
					this.parentOffice = parentOffice;
				}
			}
		}
	}

	public Set<OfficeBO> getChildren() {
		return children;
	}

	private void removeChild(OfficeBO office) {
		children.remove(office);
	}

	private void addChild(OfficeBO office) {
		children.add(office);
	}

	private void updateSearchId(String searchId) {

		this.searchId = searchId;
		int i = 1;
		if (this.children != null) {
			Iterator iter = this.children.iterator();
			while (iter.hasNext()) {
				OfficeBO element = (OfficeBO) iter.next();
				element.updateSearchId(this.searchId + "." + i);
				i++;

			}

		}

	}

	private void updateLevel(OfficeLevel level) throws OfficeException {
		try {
			if (this.getOfficeLevel() != level) {
				// TODO: pass proper key
				if (!canUpdateLevel(level))
					throw new OfficeException();
				MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory
						.getInstance().getPersistenceService(
								PersistenceServiceName.MasterDataService);
				this.level = (OfficeLevelEntity) masterPersistenceService
						.findById(OfficeLevelEntity.class, level.getValue());
			}
		} catch (ServiceException e) {
			throw new OfficeException(e);
		}
	}

	private boolean canUpdateLevel(OfficeLevel level) throws OfficeException {
		if (this.getOfficeLevel().getValue() > level.getValue())
			return true;
		else {
			for (OfficeBO office : this.children) {
				if (office.getLevel().getId() <= level.getValue())
					return false;
			}
			return true;
		}
	}
	private void updateAddress(Address address){
		if(this.address!=null&&address!=null)
			this.address.setAddress(address);
		else if (this.address==null&&address!=null){
			this.address = new OfficeAddressEntity(this,address);
		}
	}
	private void updateCustomFields(List<CustomFieldView> customfields){
		if(this.customFields !=null&&customfields!=null){
			for(CustomFieldView fieldView : customfields)
				for(OfficeCustomFieldEntity fieldEntity: this.customFields)
					if(fieldView.getFieldId().equals(fieldEntity.getFieldId()))
						fieldEntity.setFieldValue(fieldView.getFieldValue());
		}
		else if (this.customFields ==null&&customfields!=null){
			this.customFields = new HashSet<OfficeCustomFieldEntity>();
			for (CustomFieldView view : customfields) {
				this.customFields.add(new OfficeCustomFieldEntity(view
						.getFieldValue(), view.getFieldId(), this));
			}
		}
	}
}
