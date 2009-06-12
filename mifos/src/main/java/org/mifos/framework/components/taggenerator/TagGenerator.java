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

package org.mifos.framework.components.taggenerator;

import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.exceptions.PageExpiredException;

public abstract class TagGenerator {

    protected TagGenerator associatedGenerator;

    protected TagGenerator getAssociatedGenerator() {
        return this.associatedGenerator;
    }

    protected void setAssociatedGenerator(TagGenerator associatedGenerator) {
        this.associatedGenerator = associatedGenerator;
    }

    public static String createHeaderLinks(BusinessObject bo, boolean selfLinkRequired, Object randomNum)
            throws PageExpiredException {
        TagGenerator generator = TagGeneratorFactory.getInstance().getGenerator(bo);
        return generator.build(bo, selfLinkRequired, randomNum).toString();
    }

    protected StringBuilder build(BusinessObject obj, Object randomNum) {
        return build(obj, false, randomNum);
    }

    protected abstract StringBuilder build(BusinessObject obj, boolean selfLinkRequired, Object randomNum);
}
