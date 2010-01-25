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

package org.mifos.framework.components.fieldConfiguration.util.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.framework.components.fieldConfiguration.business.EntityMaster;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.persistence.FieldConfigurationPersistence;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;

public class FieldConfig {

    private static FieldConfigurationPersistence fieldConfigurationPersistence = new FieldConfigurationPersistence();

    private static FieldConfig instance = new FieldConfig();

    private Map<Short, List<FieldConfigurationEntity>> entityFieldMap = new HashMap<Short, List<FieldConfigurationEntity>>();

    private Map<Short, List<FieldConfigurationEntity>> entityMandatoryFieldMap = new HashMap<Short, List<FieldConfigurationEntity>>();

    private Map<Object, Object> entityMap = EntityMasterData.getEntityMasterMap();

    private FieldConfig() {
    }

    public Map<Short, List<FieldConfigurationEntity>> getEntityMandatoryFieldMap() {
        return entityMandatoryFieldMap;
    }

    public Map<Short, List<FieldConfigurationEntity>> getEntityFieldMap() {
        return entityFieldMap;
    }

    public Map<Object, Object> getEntityMap() {
        return entityMap;
    }

    public static FieldConfig getInstance() {
        return instance;
    }

    public boolean isFieldHidden(String labelName) {
        if (labelName == null || labelName.equals("") || labelName.indexOf(".") == -1)
            return false;
        labelName = labelName.trim();
        String entityName = labelName.substring(0, labelName.indexOf("."));
        String fieldName = labelName.substring(labelName.indexOf(".") + 1);
        List<FieldConfigurationEntity> fieldList = getEntityFieldMap().get(getEntityMap().get(entityName));
        if (fieldList != null && fieldList.size() > 0)
            for (FieldConfigurationEntity fieldConfigurationEntity : fieldList) {
                FieldConfigurationEntity parentfieldConfigurationEntity = fieldConfigurationEntity
                        .getParentFieldConfig();
                if ((fieldConfigurationEntity.getHiddenFlag().equals(FieldConfigurationConstant.YES) || (parentfieldConfigurationEntity != null && parentfieldConfigurationEntity
                        .getHiddenFlag().equals(FieldConfigurationConstant.YES)))
                        && fieldConfigurationEntity.getFieldName().equals(fieldName)) {
                    return true;
                }
            }
        return false;
    }

    public boolean isFieldManadatory(String labelName) {
        if (labelName == null || labelName.equals("") || labelName.indexOf(".") == -1)
            return false;
        labelName = labelName.trim();
        String entityName = labelName.substring(0, labelName.indexOf("."));
        String fieldName = labelName.substring(labelName.indexOf(".") + 1);
        List<FieldConfigurationEntity> fieldList = getEntityFieldMap().get(getEntityMap().get(entityName));
        if (fieldList != null && fieldList.size() > 0)
            for (FieldConfigurationEntity fieldConfigurationEntity : fieldList) {
                FieldConfigurationEntity parentfieldConfigurationEntity = fieldConfigurationEntity
                        .getParentFieldConfig();
                if (parentfieldConfigurationEntity == null) {
                    if (fieldConfigurationEntity.getMandatoryFlag().equals(FieldConfigurationConstant.YES)
                            && fieldConfigurationEntity.getHiddenFlag().equals(FieldConfigurationConstant.NO)
                            && fieldConfigurationEntity.getFieldName().equals(fieldName)) {
                        return true;
                    }
                } else if ((parentfieldConfigurationEntity.getMandatoryFlag().equals(FieldConfigurationConstant.YES)
                        && parentfieldConfigurationEntity.getHiddenFlag().equals(FieldConfigurationConstant.NO)
                        && fieldConfigurationEntity.getFieldName().equals(fieldName) && fieldConfigurationEntity
                        .getHiddenFlag().equals(FieldConfigurationConstant.NO))
                        || ((parentfieldConfigurationEntity.getMandatoryFlag().equals(FieldConfigurationConstant.NO)
                                && parentfieldConfigurationEntity.getHiddenFlag().equals(FieldConfigurationConstant.NO)
                                && fieldConfigurationEntity.getFieldName().equals(fieldName)
                                && fieldConfigurationEntity.getHiddenFlag().equals(FieldConfigurationConstant.NO) && fieldConfigurationEntity
                                .getMandatoryFlag().equals(FieldConfigurationConstant.YES)))) {
                    return true;
                }
            }
        return false;
    }

    /* This method is used to intialize the mandatory and entiyField maps */
    public void init() throws HibernateProcessException, PersistenceException {
        List<EntityMaster> entityMasterList = fieldConfigurationPersistence.getEntityMasterList();
        for (EntityMaster entityMaster : entityMasterList) {
            getEntityFieldMap().put(entityMaster.getId(),
                    fieldConfigurationPersistence.getListOfFields(entityMaster.getId()));
            getEntityMandatoryFieldMap().put(entityMaster.getId(), getMandatoryFieldList(entityMaster.getId()));
        }
    }

    private List<FieldConfigurationEntity> getMandatoryFieldList(Short entityId) {
        List<FieldConfigurationEntity> fieldList = getEntityFieldMap().get(entityId);
        List<FieldConfigurationEntity> mandatoryFieldList = new ArrayList<FieldConfigurationEntity>();
        for (FieldConfigurationEntity fieldConfigurationEntity : fieldList) {
            if (isFieldManadatory(fieldConfigurationEntity.getLabel())) {
                mandatoryFieldList.add(fieldConfigurationEntity);
            }
        }
        return mandatoryFieldList;
    }

}
