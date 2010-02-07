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

package org.mifos.framework.struts.plugin.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.framework.components.fieldConfiguration.business.EntityMaster;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public final class EntityMasterData {

    public static Map<Object, Object> entityMap = new HashMap<Object, Object>();

    private static EntityMasterData entityMasterData = new EntityMasterData();

    private EntityMasterData() {
    }

    public static EntityMasterData getInstance() {
        return entityMasterData;
    }

    /**
     * This method creates a map of entity master table as sets it into the
     * servletcontext so that it is available till the application is up.
     * 
     * @param servlet
     * @param config
     * @throws HibernateProcessException
     */
    public void init() throws HibernateProcessException {
        Session session = null;
        try {
            session = StaticHibernateUtil.openSession();
            session.beginTransaction();
            Query query = session.getNamedQuery(NamedQueryConstants.GET_ENTITY_MASTER);

            List<EntityMaster> entityMasterData = query.list();

            for (EntityMaster entityMaster : entityMasterData) {
                entityMap.put(entityMaster.getEntityType(), entityMaster.getId());
            }
        } catch (HibernateException e) {
            MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).error(
                    "table entity_master could not be fetched", false, null, e);
        } finally {
            StaticHibernateUtil.closeSession(session);
        }
    }

    public static Map<Object, Object> getEntityMasterMap() {
        return entityMap;
    }

}
