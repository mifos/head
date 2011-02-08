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
package org.mifos.framework.components.batchjobs;

import java.io.IOException;

import org.springframework.batch.core.configuration.support.ClassPathXmlApplicationContextFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/* package-local */
class ApplicationContextFactory {

    // TODO Suprised that Spring didn't have something like this already? 
    
    /* package-local */
    static ConfigurableApplicationContext createApplicationContext(Resource resource) throws BeansException, IOException {
        if (resource instanceof ClassPathResource) {
            return new ClassPathXmlApplicationContextFactory(resource).createApplicationContext();
        } else if (resource instanceof FileSystemResource) {
            return new FileSystemXmlApplicationContext(resource.getFile().toString());
        } else {
            throw new IOException(resource.getDescription() + " is neither a ClassPathResource nor a FileSystemResource, I don't know what to do");
        }
    }
    
}
