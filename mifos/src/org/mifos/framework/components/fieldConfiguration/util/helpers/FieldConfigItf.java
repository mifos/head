package org.mifos.framework.components.fieldConfiguration.util.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;


public interface FieldConfigItf {
	
	public Map<Short,List<FieldConfigurationEntity>> getEntityMandatoryFieldMap();//TODO This will be removed after M1-M2 migration
	public Map<Short,List<FieldConfigurationEntity>> getEntityFieldMap();
	public Map<Object,Object> getEntityMap();
	public boolean isFieldHidden(String fieldName);
	public boolean isFieldManadatory(String fieldName);
	public void init() throws HibernateProcessException;
}
