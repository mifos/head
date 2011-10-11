/*
 * Copyright (c) 2011 Grameen Foundation USA
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

package org.mifos.server.workspace;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;

/**
 * Extended WebInfConfiguration.
 * 
 * Helps to accept e.g. web-fragment.xml from anywhere on the classpath, folders or JARs, and not only from JARs
 * necessarily inside a WEB-INF/lib.
 * 
 * @see WebInfConfiguration
 * 
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=330189
 * 
 * @author Michael Vorburger
 */
public class WebInfFolderExtendedConfiguration extends WebInfConfiguration {

    @Override
    protected List<Resource> findJars(WebAppContext context) throws Exception {
        List<Resource> r = super.findJars(context); // let original WebInfConfiguration do it's thing first
        if (r == null) {
            r = new LinkedList<Resource>();
        }

        final List<Resource> containerJarResources = context.getMetaData().getOrderedContainerJars();
        r.addAll(containerJarResources);

        return r;
    }

}
