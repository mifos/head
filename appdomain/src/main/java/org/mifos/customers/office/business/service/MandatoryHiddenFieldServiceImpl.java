/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.customers.office.business.service;

import java.util.List;

import org.mifos.application.util.helpers.EntityType;
import org.mifos.config.util.helpers.HiddenMandatoryFieldNamesConstants;
import org.mifos.core.MifosRuntimeException;
import org.mifos.dto.domain.MandatoryHiddenFieldsDto;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;

public class MandatoryHiddenFieldServiceImpl implements MandatoryHiddenFieldService {

    private HibernateTransactionHelper transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();

    @Override
    public void updateMandatoryHiddenFields(MandatoryHiddenFieldsDto dto, List<FieldConfigurationEntity> confFieldList) {
        try {
            transactionHelper.startTransaction();
            updateFieldConfiguration(dto, confFieldList);
            transactionHelper.commitTransaction();
            FieldConfig.getInstance().init();
        } catch (Exception e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            transactionHelper.closeSession();
        }
    }

    private void updateFieldConfiguration(MandatoryHiddenFieldsDto dto, List<FieldConfigurationEntity> confFieldList)
            throws Exception {
        if (confFieldList != null && confFieldList.size() > 0) {
            for (FieldConfigurationEntity fieldConfiguration : confFieldList) {
                if (fieldConfiguration.getEntityType() == EntityType.CLIENT
                        || fieldConfiguration.getEntityType() == EntityType.PERSONNEL) {
                    updateClientDetails(dto, fieldConfiguration);
                } else if (fieldConfiguration.getEntityType() == EntityType.GROUP) {
                    updateGrouptDetails(dto, fieldConfiguration);
                } else {
                    updateSystemFields(dto, fieldConfiguration);
                }
            }
        }
    }

    private void updateSystemFields(MandatoryHiddenFieldsDto dto, FieldConfigurationEntity fieldConfiguration)
            throws Exception {
        if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.EXTERNAL_ID)) {
            fieldConfiguration.update(getShortValue(dto.isMandatorySystemExternalId()), getShortValue(dto
                    .isHideSystemExternalId()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS1)) {
            fieldConfiguration.update(getShortValue(dto.isMandatorySystemAddress1()), fieldConfiguration
                    .getHiddenFlag());
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS2)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto.isHideSystemAddress2()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS3)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto.isHideSystemAddress3()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.CITY)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto.isHideSystemCity()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.STATE)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto.isHideSystemState()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.COUNTRY)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto.isHideSystemCountry()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.POSTAL_CODE)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto.isHideSystemCountry()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.RECEIPT_ID)
                || fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.RECEIPT_DATE)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto
                    .isHideSystemReceiptIdDate()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.PURPOSE_OF_LOAN)) {
            // TODO: move to a new method with other loan account field updates
            fieldConfiguration.update(getShortValue(dto.isMandatoryLoanAccountPurpose()), fieldConfiguration
                    .getHiddenFlag());
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.SOURCE_OF_FUND)) {
            fieldConfiguration.update(getShortValue(dto.isMandatoryLoanSourceOfFund()), fieldConfiguration
                    .getHiddenFlag());
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.COLLATERAL_TYPE)
                || fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.COLLATERAL_NOTES)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto
                    .isHideSystemCollateralTypeNotes()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ASSIGN_CLIENTS)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto
                    .isHideSystemAssignClientPostions()));
        }
    }

    private void updateGrouptDetails(MandatoryHiddenFieldsDto dto, FieldConfigurationEntity fieldConfiguration)
            throws Exception {
        if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS1)) {
            fieldConfiguration.update(getShortValue(dto.isMandatorySystemAddress1()), fieldConfiguration
                    .getHiddenFlag());
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS2)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto.isHideSystemAddress2()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS3)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto.isHideSystemAddress3()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.TRAINED)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto.isHideGroupTrained()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.EXTERNAL_ID)) {
            fieldConfiguration.update(getShortValue(dto.isMandatorySystemExternalId()), getShortValue(dto
                    .isHideSystemExternalId()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.CITY)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto.isHideSystemCity()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.STATE)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto.isHideSystemState()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.COUNTRY)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto.isHideSystemCountry()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.POSTAL_CODE)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto.isHideSystemCountry()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.EXTERNAL_ID)) {
            fieldConfiguration.update(getShortValue(dto.isMandatorySystemExternalId()), getShortValue(dto
                    .isHideSystemExternalId()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ETHNICITY)) {
            fieldConfiguration.update(getShortValue(dto.isMandatorySystemEthnicity()), getShortValue(dto
                    .isHideSystemEthnicity()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.CITIZENSHIP)) {
            fieldConfiguration.update(getShortValue(dto.isMandatorySystemCitizenShip()), getShortValue(dto
                    .isHideSystemCitizenShip()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.HANDICAPPED)) {
            fieldConfiguration.update(getShortValue(dto.isMandatorySystemHandicapped()), getShortValue(dto
                    .isHideSystemHandicapped()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.EDUCATION_LEVEL)) {
            fieldConfiguration.update(getShortValue(dto.isMandatorySystemEducationLevel()), getShortValue(dto
                    .isHideSystemEducationLevel()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.PHOTO)) {
            fieldConfiguration.update(getShortValue(dto.isMandatorySystemPhoto()), getShortValue(dto
                    .isHideSystemPhoto()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.RECEIPT_ID)
                || fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.RECEIPT_DATE)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto
                    .isHideSystemReceiptIdDate()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ASSIGN_CLIENTS)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto
                    .isHideSystemAssignClientPostions()));
        }
    }

    private void updateClientDetails(MandatoryHiddenFieldsDto dto, FieldConfigurationEntity fieldConfiguration)
            throws Exception {
        if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.MIDDLE_NAME)) {
            fieldConfiguration.update(getShortValue(dto.isMandatoryClientMiddleName()), getShortValue(dto
                    .isHideClientMiddleName()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.SECOND_LAST_NAME)) {
            fieldConfiguration.update(getShortValue(dto.isMandatoryClientSecondLastName()), getShortValue(dto
                    .isHideClientSecondLastName()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.GOVERNMENT_ID)) {
            fieldConfiguration.update(getShortValue(dto.isMandatoryClientGovtId()), getShortValue(dto
                    .isHideClientGovtId()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.MARITAL_STATUS)) {
            fieldConfiguration
                    .update(getShortValue(dto.isMandatoryMaritalStatus()), fieldConfiguration.getHiddenFlag());
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.POVERTY_STATUS)) {
            fieldConfiguration.update(getShortValue(dto.isMandatoryClientPovertyStatus()), getShortValue(dto
                    .isHideClientPovertyStatus()));
        } else if (fieldConfiguration.getFieldName().equals(
                HiddenMandatoryFieldNamesConstants.SPOUSE_FATHER_INFORMATION)) {
            fieldConfiguration.update(getShortValue(dto.isMandatoryClientSpouseFatherInformation()), getShortValue(dto
                    .isHideClientSpouseFatherInformation()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.FAMILY_DETAILS)) {
            fieldConfiguration.update(getShortValue(dto.isMandatoryClientFamilyDetails()), fieldConfiguration
                    .getHiddenFlag());
        } else if (fieldConfiguration.getFieldName().equals(
                HiddenMandatoryFieldNamesConstants.SPOUSE_FATHER_MIDDLE_NAME)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto
                    .isHideClientSpouseFatherMiddleName()));
        } else if (fieldConfiguration.getFieldName().equals(
                HiddenMandatoryFieldNamesConstants.SPOUSE_FATHER_SECOND_LAST_NAME)) {
            fieldConfiguration.update(getShortValue(dto.isMandatoryClientSpouseFatherSecondLastName()),
                    getShortValue(dto.isHideClientSpouseFatherSecondLastName()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.PHONE_NUMBER)) {
            fieldConfiguration.update(getShortValue(dto.isMandatoryClientPhone()), getShortValue(dto
                    .isHideClientPhone()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.TRAINED)) {
            fieldConfiguration.update(getShortValue(dto.isMandatoryClientTrained()), getShortValue(dto
                    .isHideClientTrained()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.TRAINED_DATE)) {
            fieldConfiguration.update(getShortValue(dto.isMandatoryClientTrainedOn()), getShortValue(dto
                    .isHideClientTrained())); // hidden field from Trained is shared with TrainedDate (#MIFOS-2731)
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.BUSINESS_ACTIVITIES)) {
            fieldConfiguration.update(getShortValue(dto.isMandatoryClientBusinessWorkActivities()), getShortValue(dto
                    .isHideClientBusinessWorkActivities()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.NUMBER_OF_CHILDREN)) {
            fieldConfiguration.update(getShortValue(dto.isMandatoryNumberOfChildren()), fieldConfiguration
                    .getHiddenFlag());
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.EXTERNAL_ID)) {
            fieldConfiguration.update(getShortValue(dto.isMandatorySystemExternalId()), getShortValue(dto
                    .isHideSystemExternalId()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ETHNICITY)) {
            fieldConfiguration.update(getShortValue(dto.isMandatorySystemEthnicity()), getShortValue(dto
                    .isHideSystemEthnicity()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.CITIZENSHIP)) {
            fieldConfiguration.update(getShortValue(dto.isMandatorySystemCitizenShip()), getShortValue(dto
                    .isHideSystemCitizenShip()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.HANDICAPPED)) {
            fieldConfiguration.update(getShortValue(dto.isMandatorySystemHandicapped()), getShortValue(dto
                    .isHideSystemHandicapped()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.EDUCATION_LEVEL)) {
            fieldConfiguration.update(getShortValue(dto.isMandatorySystemEducationLevel()), getShortValue(dto
                    .isHideSystemEducationLevel()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.PHOTO)) {
            fieldConfiguration.update(getShortValue(dto.isMandatorySystemPhoto()), getShortValue(dto
                    .isHideSystemPhoto()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS1)) {
            fieldConfiguration.update(getShortValue(dto.isMandatorySystemAddress1()), fieldConfiguration
                    .getHiddenFlag());
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS2)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto.isHideSystemAddress2()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ADDRESS3)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto.isHideSystemAddress3()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.CITY)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto.isHideSystemCity()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.STATE)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto.isHideSystemState()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.COUNTRY)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto.isHideSystemCountry()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.POSTAL_CODE)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto.isHideSystemCountry()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.RECEIPT_ID)
                || fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.RECEIPT_DATE)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto
                    .isHideSystemReceiptIdDate()));
        } else if (fieldConfiguration.getFieldName().equals(HiddenMandatoryFieldNamesConstants.ASSIGN_CLIENTS)) {
            fieldConfiguration.update(fieldConfiguration.getMandatoryFlag(), getShortValue(dto
                    .isHideSystemAssignClientPostions()));
        }
    }

    private Short getShortValue(boolean b) {
        return b == true ? (short) 1 : (short) 0;
    }
}
