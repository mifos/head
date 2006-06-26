/**

 * PrdOfferingFees.java    version: xxx

 

 * Copyright © 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

package org.mifos.application.productdefinition.util.valueobjects;

import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author ashishsm
 *
 */
public class PrdOfferingFees extends ValueObject {
	/**
	 * Default Constructor 
	 */
	public PrdOfferingFees() {
	}
	/**
	 * serial version UID for serialization
	 */
	private static final long serialVersionUID = 7155114906401787071L;
	/** The composite primary key value. */
    private LoanOffering loanOffering;

    /** The value of the fees association. */
    private Fees fees;

    /** The value of the simple prdOfferingId property. */
    private java.lang.Short prdOfferingFeeId;

    
   

    /**
	 * @return Returns the loanOffering.
	 */
	public LoanOffering getLoanOffering() {
		return loanOffering;
	}

	/**
	 * @param loanOffering The loanOffering to set.
	 */
	public void setLoanOffering(LoanOffering loanOffering) {
		this.loanOffering = loanOffering;
	}

	/**
     * Return the value of the FEE_ID column.
     * @return Fees
     */
    public Fees getFees()
    {
        return this.fees;
    }

    /**
     * Set the value of the FEE_ID column.
     * @param fees
     */
    public void setFees(Fees fees)
    {
        this.fees = fees;
    }

	/**
	 * @return Returns the prdOfferingFeeId}.
	 */
	public java.lang.Short getPrdOfferingFeeId() {
		return prdOfferingFeeId;
	}

	/**
	 * @param prdOfferingFeeId The prdOfferingFeeId to set.
	 */
	public void setPrdOfferingFeeId(java.lang.Short prdOfferingFeeId) {
		this.prdOfferingFeeId = prdOfferingFeeId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		PrdOfferingFees prdOfferingFees=null;
		boolean value=false;
		if(object !=null) {
			prdOfferingFees=(PrdOfferingFees)object;
			if(prdOfferingFees.getPrdOfferingFeeId().equals(this.prdOfferingFeeId)) {
				value= true;
			}
		}
		MifosLogManager.getLogger(LoggerConstants.PRDDEFINITIONLOGGER).info(
			"In Equals of loanOffering fund+----------------------"+value);
		return value;
	}
}
