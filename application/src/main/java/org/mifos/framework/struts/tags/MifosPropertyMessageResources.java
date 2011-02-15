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

package org.mifos.framework.struts.tags;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.struts.util.MessageResourcesFactory;
import org.apache.struts.util.PropertyMessageResources;
import org.mifos.application.master.business.CustomValueDto;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.config.business.MifosConfiguration;
import org.mifos.config.exceptions.ConfigurationException;
import org.mifos.framework.util.helpers.BundleKey;
import org.mifos.framework.util.helpers.MifosSelectHelper;

public class MifosPropertyMessageResources extends PropertyMessageResources {

    private final Map<BundleKey, String> dbMap_labels = new ConcurrentHashMap<BundleKey, String>();
    private final Map<BundleKey, Collection> dbMap_Values = new ConcurrentHashMap<BundleKey, Collection>();
    private final LegacyMasterDao legacyMasterDao = ApplicationContextProvider.getBean(LegacyMasterDao.class);

    public MifosPropertyMessageResources(final MessageResourcesFactory factory, final String config, final boolean returnNull) {
        super(factory, config, returnNull);
    }

    public MifosPropertyMessageResources(final MessageResourcesFactory factory, final String config) {
        super(factory, config);
    }

    /**
     * serial version UID for serailization
     */
    private static final long serialVersionUID = 75674564734673651L;

    /**
     * Used in one method: LabelTagUtils.getLabel()
     */
    @Override
    public String getMessage(final Locale locale, final String key) {

        String returnVal = null;
        returnVal = super.getMessage(locale, key);
        if (returnVal == null) {
            // try to get from the local hashmap
            try {

                returnVal = MifosConfiguration.getInstance().getLabel(key, locale);
            } catch (ConfigurationException ce) {
                // eat it
                // TODO we may log it
            }

            // logger.debug("The value from the local hash map is - " +
            // (returnVal == null ? "null" : returnVal) );
        }

        if (returnVal == null) {
            returnVal = dbMap_labels.get(new BundleKey(locale, key));
        }

        if (returnVal == null) {
            // try to get it from the database
            try {

                CustomValueDto entity = getEntity( key);
                if (entity != null) {
                    returnVal = entity.getEntityLabel();
                    // put it into hash map for further use
                    dbMap_labels.put(new BundleKey(locale, key), returnVal);
                }

            } catch (Exception e) {
                // logger.error(e.getMessage());
            }
        }

        // logger.debug("The final value is - " + (returnVal == null ? "null" :
        // returnVal) );

        return returnVal;
    }

    /**
     * Only used in one method (LabelTagUtils.getCustomValueListElements), which
     * may never be called
     */
    public Collection<?> getCustomValueListElements(final Locale locale, final String key, final String mappingKey) {

        Collection<?> returnVal = null;
        returnVal = dbMap_Values.get(new BundleKey(locale, key));

        if (returnVal == null) {
                CustomValueDto entity = null;
                if (mappingKey == null || mappingKey.equals("")) {
                    entity = legacyMasterDao.getLookUpEntity(key);
                } else {
                    String[] mappingValues = MifosSelectHelper.getInstance().getValue(mappingKey);
                    if (mappingValues != null && mappingValues.length == 2) {
                        entity = legacyMasterDao.getCustomValueList(key, mappingValues[0], mappingValues[1]);
                    }
                }
                if (entity != null) {
                    returnVal = entity.getCustomValueListElements();
                    dbMap_Values.put(new BundleKey(locale, key), returnVal);
                }
        }
        return returnVal;
    }

    /**
     * Only called from one place: MifosPropertyMessageResources.getMessage in
     * order to get a label.
     */
    public CustomValueDto getEntity(final String key) {
        CustomValueDto entity = null;
        entity = legacyMasterDao.getLookUpEntity(key);
        return entity;

    }
}
