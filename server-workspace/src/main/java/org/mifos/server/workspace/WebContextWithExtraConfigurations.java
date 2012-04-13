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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * WebAppContext allowing to register additional Configurations.
 * 
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=330189
 * 
 * @author Michael Vorburger
 */
public class WebContextWithExtraConfigurations extends WebAppContext {

    public WebContextWithExtraConfigurations(String webApp, String contextPath) {
        super(webApp, contextPath);
    }

    public <T extends Configuration> void replaceConfiguration(Class<T> toReplace, Configuration newConfiguration)
            throws Exception {
        loadConfigurations(); // Force loading of default configurations
        final Configuration[] configs = getConfigurations();
        for (int i = 0; i < configs.length; i++) {
            if (configs[i].getClass().equals(toReplace)) {
                configs[i] = newConfiguration;
                return;
            }
        }
        throw new IllegalStateException(toReplace.toString() + " not found");
    }

    public void addConfiguration(Configuration configuration) throws Exception {
        loadConfigurations(); // Force loading of default configurations

        final Configuration[] configs = getConfigurations();
        List<Configuration> confs = new ArrayList<Configuration>(Arrays.asList(configs));
        confs.add(configuration);

        setConfigurations(confs.toArray(new Configuration[confs.size()]));
    }

}
