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

package org.mifos.security.activity;

import junit.framework.Assert;

import org.hibernate.Session;
import org.junit.Test;
import org.mifos.application.master.business.LookUpEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.config.Localization;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.security.rolesandpermission.business.ActivityEntity;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.security.rolesandpermission.persistence.LegacyRolesPermissionsDao;
import org.mifos.security.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.springframework.beans.factory.annotation.Autowired;

public class LegacyRolesPermissionsDaoIntegrationTest extends MifosIntegrationTestCase {

    @Autowired
    LegacyMasterDao legacyMasterDao;

    @Autowired
    LegacyRolesPermissionsDao legacyRolesPermissionsDao;

    @Test
    public void testShouldInsertSuccessActivity() throws Exception {
        Session session = StaticHibernateUtil.getSessionTL();
        short parentId = 13;
        int numberOfActivitiesBefore = session.createQuery("from ActivityEntity r where r.parent.id =" + parentId).list().size();
        RoleBO roleBo = (RoleBO) session.load(RoleBO.class, (short) RolesAndPermissionConstants.ADMIN_ROLE);
        int adminActivities = roleBo.getActivities().size();

        LookUpEntity lookUpEntity = new LookUpEntity();
        lookUpEntity.setEntityId((short) LookUpEntity.ACTIVITY);

        int lookUpId = legacyRolesPermissionsDao.createActivityForReports(parentId, "abcd");
        Assert.assertEquals("abcd",legacyMasterDao.retrieveOneLookUpValueLocaleEntity(Localization.ENGLISH_LOCALE_ID, lookUpId).getLookUpValue());
        Assert.assertEquals(legacyRolesPermissionsDao.calculateDynamicActivityId(), (int) legacyRolesPermissionsDao.getActivityEntity(lookUpId).getId() - 1);

        int numberOfActivitiesAfter = session.createQuery("from ActivityEntity r where r.parent.id =" + parentId).list().size();

        Assert.assertEquals(numberOfActivitiesBefore + 1, numberOfActivitiesAfter);
        Assert.assertEquals(adminActivities + 1, roleBo.getActivities().size());

    }

    @Test
    public void testShouldSuccessWhenChangeActivityParent() throws PersistenceException {
        ActivityEntity activity = legacyRolesPermissionsDao.getPersistentObject(ActivityEntity.class, Short.valueOf((short) 2));
        Assert.assertEquals(1, activity.getParent().getId().shortValue());
        legacyRolesPermissionsDao.reparentActivityUsingHibernate((short) 2, (short) 13);
        activity = legacyRolesPermissionsDao.getPersistentObject(ActivityEntity.class, Short.valueOf((short) 2));
        Assert.assertEquals(13, activity.getParent().getId().shortValue());
        legacyRolesPermissionsDao.reparentActivityUsingHibernate((short) 2, (short) 1);
    }

    @Test
    public void testShouldSuccessWhenChangeActivityMessage() throws Exception {
        ActivityEntity activityEntity = legacyRolesPermissionsDao.getPersistentObject(ActivityEntity.class, Short.valueOf((short) 3));
        Integer lookUpId = activityEntity.getActivityNameLookupValues().getLookUpId();
        Assert.assertEquals(373, lookUpId.intValue());

        short localeId = Localization.ENGLISH_LOCALE_ID;
        LookUpValueLocaleEntity lookUpValueLocaleEntity = legacyMasterDao.retrieveOneLookUpValueLocaleEntity(localeId,
                lookUpId.intValue());
        Assert.assertNull(lookUpValueLocaleEntity.getLookUpValue());

        legacyRolesPermissionsDao.changeActivityMessage((short) 3, localeId, "wahaha");
        lookUpValueLocaleEntity = legacyMasterDao.retrieveOneLookUpValueLocaleEntity(localeId, lookUpId.intValue());

        Assert.assertEquals("wahaha", lookUpValueLocaleEntity.getLookUpValue());
        legacyRolesPermissionsDao.changeActivityMessage((short) 3, localeId, null);

    }

    @Test
    public void testShouldGenerateMinActivityIdWhenCalculate() throws Exception {
        short minActivityId = -32767;
        ActivityEntity activity = insertActivityForTest(minActivityId);
        Assert.assertEquals(minActivityId - 1, legacyRolesPermissionsDao.calculateDynamicActivityId());
        deleteActivityForTest(activity);
    }

    private ActivityEntity insertActivityForTest(short activityId) throws PersistenceException {
        LookUpValueEntity anLookUp = new LookUpValueEntity();
        LookUpEntity lookUpEntity = legacyMasterDao.getPersistentObject(LookUpEntity.class,
                Short.valueOf((short) LookUpEntity.ACTIVITY));
        anLookUp.setLookUpEntity(lookUpEntity);
        ActivityEntity parent = legacyMasterDao.getPersistentObject(ActivityEntity.class, (short) 13);
        ActivityEntity activityEntity = new ActivityEntity(activityId, parent, anLookUp);
        legacyRolesPermissionsDao.createOrUpdate(anLookUp);
        legacyRolesPermissionsDao.createOrUpdate(activityEntity);
        return activityEntity;
    }

    private void deleteActivityForTest(ActivityEntity activityEntity) throws PersistenceException {
        LookUpValueEntity anLookUp = activityEntity.getActivityNameLookupValues();
        legacyRolesPermissionsDao.delete(activityEntity);
        legacyRolesPermissionsDao.delete(anLookUp);
    }
}
