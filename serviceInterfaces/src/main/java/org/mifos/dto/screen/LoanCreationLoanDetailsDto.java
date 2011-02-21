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

package org.mifos.dto.screen;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.domain.ProductDetailsDto;
import org.mifos.dto.domain.ValueListElement;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
public class LoanCreationLoanDetailsDto implements Serializable {

    private final boolean isRepaymentIndependentOfMeetingEnabled;
    private final MeetingDto loanOfferingMeetingDetail;
    private final MeetingDto customerMeetingDetail;
    private final List<ValueListElement> loanPurposes;
	private final ProductDetailsDto productDto;
	private final CustomerDetailDto customerDetailDto;
	private final List<PrdOfferingDto> loanProductDtos;
	private final Map<String, String> productOptions = new HashMap<String, String>();
	private final String interestRateType;
	private final boolean principalDueOnLastInstallment;

	public LoanCreationLoanDetailsDto(boolean isRepaymentIndependentOfMeetingEnabled,
            MeetingDto loanOfferingMeetingDetail, MeetingDto customerMeetingDetail,
            List<ValueListElement> loanPurposes, ProductDetailsDto productDto, CustomerDetailDto customerDetailDto, List<PrdOfferingDto> loanProductDtos, 
            String interestRateType, boolean principalDueOnLastInstallment) {
        this.isRepaymentIndependentOfMeetingEnabled = isRepaymentIndependentOfMeetingEnabled;
        this.loanOfferingMeetingDetail = loanOfferingMeetingDetail;
        this.customerMeetingDetail = customerMeetingDetail;
        this.loanPurposes = loanPurposes;
		this.productDto = productDto;
		this.customerDetailDto = customerDetailDto;
		this.loanProductDtos = loanProductDtos;
		this.interestRateType = interestRateType;
		this.principalDueOnLastInstallment = principalDueOnLastInstallment;
		populateProductOptions(loanProductDtos);
    }
	
    public String getInterestRateType() {
		return interestRateType;
	}

	public boolean isPrincipalDueOnLastInstallment() {
		return principalDueOnLastInstallment;
	}

	private void populateProductOptions(List<PrdOfferingDto> loanProducts) {
    	for (PrdOfferingDto product : loanProducts) {
    		this.productOptions.put(product.getPrdOfferingId().toString(), product.getPrdOfferingName());			
		}
	}

    public boolean isRepaymentIndependentOfMeetingEnabled() {
        return this.isRepaymentIndependentOfMeetingEnabled;
    }

    public MeetingDto getLoanOfferingMeetingDetail() {
        return this.loanOfferingMeetingDetail;
    }

    public MeetingDto getCustomerMeetingDetail() {
        return this.customerMeetingDetail;
    }

    public List<ValueListElement> getLoanPurposes() {
        return this.loanPurposes;
    }
    
	public ProductDetailsDto getProductDto() {
		return productDto;
	}

	public CustomerDetailDto getCustomerDetailDto() {
		return customerDetailDto;
	}
	
    public List<PrdOfferingDto> getLoanProductDtos() {
		return loanProductDtos;
	}
    
	public Map<String, String> getProductOptions() {
		return productOptions;
	}
}