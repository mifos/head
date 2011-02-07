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

package org.mifos.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.util.resource.Resource;

/**
 * ServletContext which finds WEB-INF/lib/*.jar from the classpath.
 * 
 * This is needed because certain libraries look for stuff in JAR archives of a Web application through the
 * ServletContext. In case of the "embedded" set-up used here, there is no physical WEB-INF/lib/*.jar, as the JARs are
 * on the classpath. This "special" ServletContext 'pretends' that the JARs on the classpath are in WEB-INF/lib/
 * anyways.
 * 
 * Freemarker's TaglibFactory does this to find *.tld definitions. If Freemarker used the new Servlet 3.0
 * ServletContext.getJspConfigDescriptor()'s TaglibDescriptor getTaglibLocation() & getTaglibURI(), then this would not
 * be necessary.
 * 
 * @see https://sourceforge.net/tracker/?func=detail&aid=3151460&group_id=794&atid=100794
 * 
 * @author Michael Vorburger
 */
public class WebContextWithServletContextResourceExtension extends WebContextWithExtraConfigurations {

    private static final String WEB_INF_LIB = "/WEB-INF/lib";

    private Map<String, Resource> webInfLibResourceMap;
    private Set<String> webInfLibResourcePaths;

    public WebContextWithServletContextResourceExtension(String webApp, String contextPath) {
        super(webApp, contextPath);
    }

    private Map<String, Resource> getWebInfLibResourcesMap() {
        if (webInfLibResourceMap == null) {
            List<Resource> jarResources = this.getMetaData().getOrderedContainerJars();
            webInfLibResourceMap = new HashMap<String, Resource>(jarResources.size());
            for (Resource jarResource : jarResources) {
                try {
                    webInfLibResourceMap.put(WEB_INF_LIB + "/" + jarResource.getFile().getName(), jarResource);
                } catch (IOException e) {
                    throw new IllegalArgumentException(jarResource.getName() + " getFile() failed", e);
                }
            }
            webInfLibResourcePaths = Collections.unmodifiableSet(webInfLibResourceMap.keySet());
        }
        return webInfLibResourceMap;
    }

    private Set<String> getWebInfLibResourcePaths() {
        if (webInfLibResourcePaths == null) {
            getWebInfLibResourcesMap();
        }
        return webInfLibResourcePaths;
    }

    @Override
    public Set<String> getResourcePaths(String path) {
        if (WEB_INF_LIB.equals(path)) {
            return getWebInfLibResourcePaths();
        } else if (path != null && path.startsWith(WEB_INF_LIB)) {
            throw new IllegalArgumentException(path + " not handled here yet (probably easy to implement)");
        } else {
            // Nothing in WEB-INF/lib, so business as usual:
            return super.getResourcePaths(path);
        }
    }

    @Override
    public Resource getResource(String uriInContext) throws MalformedURLException {
        if (uriInContext != null && uriInContext.startsWith(WEB_INF_LIB)) {
            return getWebInfLibResourcesMap().get(uriInContext);
        } else {
            return super.getResource(uriInContext);
        }
    }

}
