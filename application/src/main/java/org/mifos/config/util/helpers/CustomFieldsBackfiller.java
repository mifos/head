/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.config.util.helpers;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.accounts.business.AccountCustomFieldEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeCustomFieldEntity;
import org.mifos.customers.office.business.service.OfficeBusinessService;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelCustomFieldEntity;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

/**
 * Handle business logic related to backfilling existing clients, groups, etc.
 * after adding a new custom field.
 */
public class CustomFieldsBackfiller {
    // TODO: factor out CustomFieldEntity type as a parent of each of these
    // custom field entity types?
    public void addCustomFieldsForExistingRecords(EntityType categoryType, Short levelId,
            CustomFieldDefinitionEntity customField) throws ApplicationException {
        // 1) check entity_type
        // 2) based on entity_type, retrieve all the id's for corresponding
        // entity type
        // 3) for all the id's enter records in corresponding table, so that
        // this custom field will be
        // available for entities created prior to creation of this custom field
        // 4) for example, if the entity_type is "1", then it corresponds to
        // Client(check EntityType), retrieve all the customer_id(for customers
        // of type client) and add records to customer_custom_field table for
        // the id's retrieved
        if (categoryType.equals(EntityType.CLIENT) || categoryType.equals(EntityType.GROUP)
                || categoryType.equals(EntityType.CENTER)) {
            // 1>>For Client, 12>>For Group, 20>>For Center
            Session session = StaticHibernateUtil.getSessionTL();
            StaticHibernateUtil.startTransaction();
            try {
                Query insert = session.getNamedQuery(NamedQueryConstants.INSERT_CUSTOMER_CUSTOM_FIELD_ENTITY);
                insert.setParameter("levelId", levelId);
                insert.setParameter("fieldId", customField.getFieldId());
                insert.executeUpdate();
                StaticHibernateUtil.commitTransaction();
            } catch (Exception e) {
                StaticHibernateUtil.rollbackTransaction();
                throw new ApplicationException(e);
            }
            finally {
                StaticHibernateUtil.closeSession();
            }
        } else if (categoryType.equals(EntityType.OFFICE)) // For Office
        {
            OfficeBusinessService officeService = new OfficeBusinessService();
            List<OfficeBO> listOfOffices = officeService.getAllofficesForCustomFIeld();
            if (null != listOfOffices && listOfOffices.size() != 0) {
                Iterator<OfficeBO> listOfOfficesIter = listOfOffices.iterator();
                while (listOfOfficesIter.hasNext()) {
                    OfficeBO officeBO = listOfOfficesIter.next();
                    OfficeCustomFieldEntity officeCustomFieldEntity = new OfficeCustomFieldEntity(customField
                            .getDefaultValue(), customField.getFieldId(), officeBO);
                    officeCustomFieldEntity.save(officeCustomFieldEntity);
                }
            }
        } else if (categoryType.equals(EntityType.PERSONNEL)) // For Personnel
        {
            PersonnelBusinessService personnelBusinessService = new PersonnelBusinessService();
            List<PersonnelBO> listOfPersonnel = personnelBusinessService.getAllPersonnel();
            if (null != listOfPersonnel && listOfPersonnel.size() != 0) {
                Iterator<PersonnelBO> listOfPersonnelIter = listOfPersonnel.iterator();
                while (listOfPersonnelIter.hasNext()) {
                    PersonnelBO personnelBO = listOfPersonnelIter.next();
                    PersonnelCustomFieldEntity personnelCustomFieldEntity = new PersonnelCustomFieldEntity(customField
                            .getDefaultValue(), customField.getFieldId(), personnelBO);
                    personnelCustomFieldEntity.save(personnelCustomFieldEntity);
                }
            }
        } else if (categoryType.equals(EntityType.SAVINGS)) // For Savings
        {
            SavingsBusinessService savingsBusinessService = new SavingsBusinessService();
            List<SavingsBO> listOfSavingsAccount = savingsBusinessService.getAllSavingsAccount();
            if (null != listOfSavingsAccount && listOfSavingsAccount.size() != 0) {
                Iterator<SavingsBO> listOfSavingsAccountIter = listOfSavingsAccount.iterator();
                while (listOfSavingsAccountIter.hasNext()) {
                    SavingsBO savingsBO = listOfSavingsAccountIter.next();
                    AccountCustomFieldEntity accountCustomFieldEntity = new AccountCustomFieldEntity(savingsBO,
                            customField.getFieldId(), customField.getDefaultValue());
                    accountCustomFieldEntity.save(accountCustomFieldEntity);
                }
            }
        } else if (categoryType.equals(EntityType.LOAN)) // For Loan
        {
            LoanBusinessService loanBusinessService = new LoanBusinessService();
            List<LoanBO> listOfLoanAccount = loanBusinessService.getAllLoanAccounts();
            if (null != listOfLoanAccount && listOfLoanAccount.size() != 0) {
                Iterator<LoanBO> listOfLoanAccountIter = listOfLoanAccount.iterator();
                while (listOfLoanAccountIter.hasNext()) {
                    LoanBO loanBO = listOfLoanAccountIter.next();
                    AccountCustomFieldEntity accountCustomFieldEntity = new AccountCustomFieldEntity(loanBO,
                            customField.getFieldId(), customField.getDefaultValue());
                    accountCustomFieldEntity.save(accountCustomFieldEntity);
                }
            }
        }
    }

}
