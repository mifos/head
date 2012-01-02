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

package org.mifos.accounts.financial.business;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.mifos.dto.domain.GLCodeDto;
import org.mifos.framework.business.AbstractEntity;

@NamedQueries( { @NamedQuery(name = "GLCode.findById", query = "from GLCodeEntity glCode where glCode.glcodeId = :glcodeId")})


@Entity
@Table(name = "gl_code")
public class GLCodeEntity extends AbstractEntity {
	
	public static final String GLCODE_AND_NAME_SEPARATOR = ": ";

    @Id
    @GeneratedValue
    @Column(name = "glcode_id")
    private Short glcodeId;

    @Column(name = "glcode_value")
    private String glcode;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "associatedGlcode")
    private COABO associatedCOA;

    protected GLCodeEntity() {
        this(null, null);
    }

    public GLCodeEntity(Short glcodeId, String glcode) {
        this.glcodeId = glcodeId;
        this.glcode = glcode;
        associatedCOA = null;
    }

    public Short getGlcodeId() {
        return glcodeId;
    }

    public String getGlcode() {
        return glcode;
    }

    public String getGlCodeAndAccountName() {
    	String codeAndName = getGlcode();
    	String accountName = "";
    	COABO account = getAssociatedCOA();
    	if (account != null) {
    		accountName = account.getAccountName();
    	}
    	if (!accountName.isEmpty()) {
    		codeAndName = codeAndName + GLCODE_AND_NAME_SEPARATOR + accountName;
    	}
    	return codeAndName;
    }
    
    public COABO getAssociatedCOA() {
        return associatedCOA;
    }

    protected void setGlcodeId(Short glcodeId) {
        this.glcodeId = glcodeId;
    }

    protected void setGlcode(String glcode) {
        this.glcode = glcode;
    }

    protected void setAssociatedCOA(COABO associatedCOA) {
        this.associatedCOA = associatedCOA;
    }

    @Override
    public String toString() {
        return glcode;
    }

    public GLCodeDto toDto() {
        GLCodeDto glCodeDto = new GLCodeDto();
        glCodeDto.setGlcode(this.glcode);
        glCodeDto.setGlcodeId(this.glcodeId);
        return glCodeDto;
    }
}
