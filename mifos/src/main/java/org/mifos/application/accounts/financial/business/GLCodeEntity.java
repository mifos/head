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
 
package org.mifos.application.accounts.financial.business;

import org.mifos.framework.business.PersistentObject;

public class GLCodeEntity extends PersistentObject {
	private final Short glcodeId;

	private final String glcode;

	private final COABO associatedCOA;

	protected GLCodeEntity() {
		this(null, null);
	}

	public GLCodeEntity(Short glcodeId, String glcode) {
		this.glcodeId = glcodeId;
		this.glcode = glcode;
		this.associatedCOA = null;
	}

	public String getGlcode() {
		return glcode;
	}

	public Short getGlcodeId() {
		return glcodeId;
	}

	public COABO getAssociatedCOA() {
		return associatedCOA;
	}

    @Override
	public String toString() {
        return glcode;
    }
}
