package org.mifos.framework.components.fieldConfiguration.util.helpers;

import java.util.List;
import java.util.Map;

import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;

public interface FieldConfigItf {

	public Map<Short, List<FieldConfigurationEntity>> getEntityMandatoryFieldMap();

	public Map<Short, List<FieldConfigurationEntity>> getEntityFieldMap();

	public Map<Object, Object> getEntityMap();

	public boolean isFieldHidden(String fieldName);

	public boolean isFieldManadatory(String fieldName);

	public void init() throws HibernateProcessException, ApplicationException;
}
