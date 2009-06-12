/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.framework.components.audit.util.helpers;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;

import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.configuration.persistence.ApplicationConfigurationPersistence;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.config.Localization;
import org.mifos.framework.components.audit.persistence.AuditConfigurationPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.FilePaths;

public class AuditConfigurtion {

    private final static Map<String, String> entityToClassPath = new HashMap<String, String>();

    public final static Map<Object, Map> entityMap = new HashMap<Object, Map>();
    private Map<String, Map> propertyMap;
    private Map<String, String> valueMap;

    public final static Map<Object, Map> entityMapForColumn = new HashMap<Object, Map>();
    private Map<String, String> columnPropertyMap;

    public final static Map<Object, EntitiesToLog> entitiesToLog = new HashMap<Object, EntitiesToLog>();

    private PropertyResourceBundle columnNames;
    static private Locale locale;
    private MifosConfiguration labelConfig = MifosConfiguration.getInstance();

    private List<Short> locales;

    private ApplicationConfigurationPersistence configurationPersistence;
    private MasterPersistence masterPersistence;

    public static AuditConfigurtion auditConfigurtion = new AuditConfigurtion();

    private AuditConfigurtion() {
        masterPersistence = new MasterPersistence();
        configurationPersistence = new ApplicationConfigurationPersistence();

        locales = Localization.getInstance().getSupportedLocaleIds();
        locale = Localization.getInstance().getMainLocale();
        columnNames = (PropertyResourceBundle) PropertyResourceBundle.getBundle(
                FilePaths.COLUMN_MAPPING_BUNDLE_PROPERTYFILE, locale);

    }

    public static void init(Locale currentLocale) throws SystemException {
        auditConfigurtion.createEntityValueMap();
        locale = currentLocale;
    }

    private void createEntityValueMap() throws SystemException {
        ColumnPropertyMapping columnPropertyMapping = XMLParser.getInstance().parser();
        EntityType[] entityTypes = columnPropertyMapping.getEntityTypes();
        for (int i = 0; i < entityTypes.length; i++) {
            entityToClassPath.put(entityTypes[i].getClassPath(), entityTypes[i].getName());
            entityMapForColumn.put(entityTypes[i].getName(), createColumnNames(entityTypes[i]));
            entityMap.put(entityTypes[i].getName(), createPropertyNames(entityTypes[i].getPropertyNames()));
            entitiesToLog.put(entityTypes[i].getName(), entityTypes[i].getEntitiesToLog());
        }
    }

    public static String getEntityToClassPath(String classPath) {
        return entityToClassPath.get(classPath);
    }

    public static boolean isObjectToBeLogged(String entityType, String name, String parentName) {
        Boolean flag = false;
        EntitiesToLog objectToBeLogged = entitiesToLog.get(entityType);
        if (objectToBeLogged == null || objectToBeLogged.getEntities() == null) {
            return flag;
        }
        Entity[] entities = objectToBeLogged.getEntities();
        for (Entity entity : entities) {
            if (entity.getName().equalsIgnoreCase(name)) {
                if (parentName == null) {
                    flag = entity.getParentName() == null;
                    return flag;
                } else if (parentName != null && entity.getParentName() != null) {
                    flag = entity.getParentName().equalsIgnoreCase(parentName);
                    return flag;
                }
            }
        }
        return flag;
    }

    public static boolean isObjectPropertiesToBeMerged(String entityType, String name, String parentName) {
        EntitiesToLog objectToBeLogged = entitiesToLog.get(entityType);
        if (objectToBeLogged == null || objectToBeLogged.getEntities() == null) {
            return false;
        }
        Entity[] entities = objectToBeLogged.getEntities();
        for (Entity entity : entities) {
            if (entity.getName().equalsIgnoreCase(name)) {
                if (parentName == null) {
                    if (entity.getParentName() == null && entity.getMergeProperties() != null
                            && entity.getMergeProperties().equalsIgnoreCase("yes")) {
                        return true;
                    }
                } else {
                    if (entity.getParentName().equalsIgnoreCase(parentName) && entity.getMergeProperties() != null
                            && entity.getMergeProperties().equalsIgnoreCase("yes")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String getValueOfCorrespondingId(String entityType, String propertyName, Object id, Short localeId) {
        Map<String, Map> propertyMap = entityMap.get(entityType);
        String propName = propertyName + "_" + localeId;
        Map<String, String> valueMap = propertyMap.get(propName);
        String value = null;
        if (id == null || valueMap == null) {
            return "";
        }
        value = valueMap.get(id.toString());
        if (value == null) {
            return "";
        } else {
            return value;
        }
    }

    public static boolean checkForPropertyName(String entityType, String propertyName, Short localeId) {
        Map<String, Map> propertyMap = entityMap.get(entityType);
        if (propertyMap == null) {
            return false;
        }
        String propName = propertyName + "_" + localeId;
        Map<String, String> valueMap = propertyMap.get(propName);
        if (valueMap == null) {
            return false;
        } else {
            return true;
        }
    }

    public static String getColumnNameForPropertyName(String entityType, String propertyName) {
        String columnName = "";
        Map<String, String> columnPropertyMap = entityMapForColumn.get(entityType);
        if (columnPropertyMap == null) {
            return "";
        }
        columnName = columnPropertyMap.get(propertyName);
        if (columnName == null) {
            return "";
        } else {
            return columnName;
        }
    }

    private Map<String, String> createColumnNames(EntityType entityType) {
        columnPropertyMap = new HashMap<String, String>();
        EntitiesToLog objectToBeLogged = entityType.getEntitiesToLog();
        if (objectToBeLogged != null && objectToBeLogged.getEntities() != null
                && objectToBeLogged.getEntities().length > 0) {
            Entity[] entities = objectToBeLogged.getEntities();
            for (Entity entity : entities) {
                if (entity != null) {
                    if (entity.getMergeProperties() != null && entity.getMergeProperties().equalsIgnoreCase("yes")) {
                        if (entity.getParentName() == null) {
                            columnPropertyMap.put(entity.getName(), getColumnName(entity.getDisplayKey()));
                        } else {
                            columnPropertyMap.put(entity.getParentName().concat(entity.getName()), getColumnName(entity
                                    .getDisplayKey()));

                        }
                    }
                }
            }
        }
        PropertyName[] propertyNames = entityType.getPropertyNames();
        for (int i = 0; i < propertyNames.length; i++) {
            String propertyName = null;
            if (propertyNames[i].getParentName() != null) {
                propertyName = propertyNames[i].getParentName().concat(propertyNames[i].getName());
            } else {
                propertyName = propertyNames[i].getName();
            }
            if (propertyNames[i].getDoNotLog().equalsIgnoreCase(XMLConstants.YES)) {
                columnPropertyMap.put(propertyName, XMLConstants.DONOTLOGTHISPROPERTY);
            } else {
                columnPropertyMap.put(propertyName, getColumnName(propertyNames[i].getDisplayKey()));
            }
        }
        return columnPropertyMap;
    }

    private Map<String, Map> createPropertyNames(PropertyName[] propertyNames) throws SystemException {
        propertyMap = new HashMap<String, Map>();
        for (int i = 0; i < propertyNames.length; i++) {
            if (propertyNames[i].getLookUp().equalsIgnoreCase(XMLConstants.YES)
                    && propertyNames[i].getMethodName() == null) {
                for (Short localeId : locales) {
                    if (propertyNames[i].getParentName() != null) {
                        propertyMap.put(propertyNames[i].getParentName().concat(propertyNames[i].getName()).concat(
                                "_" + String.valueOf(localeId)), createValueMap(propertyNames[i].getEntityName(),
                                localeId));
                    } else {
                        propertyMap.put(propertyNames[i].getName().concat("_" + String.valueOf(localeId)),
                                createValueMap(propertyNames[i].getEntityName(), localeId));
                    }
                }
            } else if (propertyNames[i].getLookUp().equalsIgnoreCase(XMLConstants.YES)
                    && propertyNames[i].getMethodName() != null) {
                for (Short localeId : locales) {
                    if (propertyNames[i].getParentName() != null) {
                        propertyMap.put(propertyNames[i].getParentName().concat(propertyNames[i].getName()).concat(
                                "_" + String.valueOf(localeId)), callMethodToCreateValueMap(propertyNames[i]
                                .getMethodName(), localeId));
                    } else {
                        propertyMap.put(propertyNames[i].getName().concat("_" + String.valueOf(localeId)),
                                callMethodToCreateValueMap(propertyNames[i].getMethodName(), localeId));
                    }
                }
            }
        }
        return propertyMap;
    }

    private Map<String, String> callMethodToCreateValueMap(String methodName, Short localeId) throws SystemException {
        valueMap = new HashMap<String, String>();
        Method[] methods = AuditConfigurationPersistence.class.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equalsIgnoreCase(methodName)) {
                try {
                    valueMap = (Map<String, String>) methods[i].invoke(new AuditConfigurationPersistence(),
                            new Object[] { localeId });
                } catch (Exception e) {
                    throw new SystemException(e);
                }
            }
        }
        return valueMap;
    }

    private Map<String, String> createValueMap(EntityName entityName, Short localeId) throws SystemException {
        valueMap = new HashMap<String, String>();
        if (entityName.getClassPath() == null) {
            fetchMasterData(entityName.getName(), localeId);
        } else {
            fetchMasterData(entityName.getName(), localeId, entityName.getClassPath().getPath());
        }
        return valueMap;
    }

    private void fetchMasterData(String entityName, Short localeId) throws SystemException {
        try {
            List<ValueListElement> businessActivityList = masterPersistence
                    .retrieveMasterEntities(entityName, localeId);
            for (ValueListElement businessActivityEntity : businessActivityList) {
                valueMap.put(businessActivityEntity.getId().toString(), businessActivityEntity.getName());
            }
        } catch (PersistenceException e) {
            throw new SystemException(e);
        }
    }

    private void fetchMasterData(String entityName, Short localeId, String classPath) throws SystemException {
        try {
            List<MasterDataEntity> masterDataList = masterPersistence.retrieveMasterDataEntity(classPath);
            for (MasterDataEntity masterDataEntity : masterDataList) {
                masterDataEntity.setLocaleId(localeId);
                valueMap.put(masterDataEntity.getId().toString(), masterDataEntity.getName());
            }
        } catch (PersistenceException e) {
            throw new SystemException(e);
        }
    }

    private String getColumnName(String DisplayKey) {
        String keys[] = getKeys(DisplayKey);

        String columnName = "";
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].contains("."))
                columnName = columnName + " " + columnNames.getString(keys[i]);
            else
                try {
                    columnName = columnName + " " + labelConfig.getLabel(keys[i], locale);
                } catch (ConfigurationException ce) {
                    // ignore it user may not see the label
                }
        }
        return columnName;
    }

    private String[] getKeys(String DisplayKey) {
        String keys[] = null;
        if (DisplayKey.contains(","))
            keys = DisplayKey.split(",");
        else {
            keys = new String[] { DisplayKey };
        }
        return keys;
    }

}
