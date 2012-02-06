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
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.mifos.accounts.fund.servicefacade.FundDto;
import org.mifos.dto.domain.ApplicationConfigurationDto;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.FeeDto;
import org.mifos.dto.domain.LoanAccountDetailsDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.domain.ProductDetailsDto;
import org.mifos.dto.domain.ValueListElement;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
public class LoanCreationLoanDetailsDto implements Serializable {

    private final boolean repaymentIndependentOfMeetingEnabled;
    private final MeetingDto loanOfferingMeetingDetail;
    private final MeetingDto customerMeetingDetail;
    private final List<ValueListElement> loanPurposes;
	private final ProductDetailsDto productDto;
	private final CustomerDetailDto customerDetailDto;
	private final List<PrdOfferingDto> loanProductDtos;
	private final String interestRateType;
	
	private final Map<String, String> productOptions = new LinkedHashMap<String, String>();
	private final Map<String, String> fundOptions = new LinkedHashMap<String, String>();
	private final Map<String, String> purposeOfLoanOptions;
	private final Map<String, String> collateralOptions;
	private final Map<String, String> defaultFeeOptions;
	private final Map<String, String> additionalFeeOptions;
	private final Map<String, String> daysOfTheWeekOptions;
	private final Map<String, String> weeksOfTheMonthOptions;

    private final List<FundDto> fundDtos;
    private final List<FeeDto> defaultFees;
    private final BigDecimal defaultLoanAmount;
    private final BigDecimal maxLoanAmount;
    private final BigDecimal minLoanAmount;
    private final Double defaultInterestRate;
    private final Double maxInterestRate;
    private final Double minInterestRate;
    private final Integer defaultNumberOfInstallments;
    private final Integer maxNumberOfInstallments;
    private final Integer minNumberOfInstallments;
    private final LocalDate nextPossibleDisbursementDate;
    private final boolean variableInstallmentsAllowed;
    private final boolean fixedRepaymentSchedule;
    private final Integer minGapInDays;
    private final Integer maxGapInDays;
    private final BigDecimal minInstallmentAmount;
    private boolean compareCashflowEnabled;
    private final boolean isGlimEnabled;
    private final boolean isGroup;
    private final List<LoanAccountDetailsDto> clientDetails;
    private final List<FeeDto> additionalFees;
    private final Integer gracePeriodInInstallments;
    private final ApplicationConfigurationDto appConfig;

    public LoanCreationLoanDetailsDto(boolean isRepaymentIndependentOfMeetingEnabled,
            MeetingDto loanOfferingMeetingDetail, MeetingDto customerMeetingDetail,
            List<ValueListElement> loanPurposes, ProductDetailsDto productDto, Integer gracePeriodInInstallments, CustomerDetailDto customerDetailDto, List<PrdOfferingDto> loanProductDtos, 
            String interestRateType, List<FundDto> fundDtos, LinkedHashMap<String, String> collateralOptions, 
            LinkedHashMap<String, String> purposeOfLoanOptions, Map<String, String> defaultFeeOptions, Map<String, String> additionalFeeOptions, List<FeeDto> defaultFees, 
            List<FeeDto> additionalFees, BigDecimal defaultLoanAmount, BigDecimal maxLoanAmount, BigDecimal minLoanAmount, 
            Double defaultInterestRate, Double maxInterestRate, Double minInterestRate, 
            Integer defaultNumberOfInstallments, Integer maxNumberOfInstallments, Integer minNumberOfInstallments, LocalDate nextPossibleDisbursementDate, 
            LinkedHashMap<String, String> daysOfTheWeekOptions, LinkedHashMap<String, String> weeksOfTheMonthOptions, 
            boolean variableInstallmentsAllowed, boolean fixedRepaymentSchedule, Integer minGapInDays, Integer maxGapInDays, BigDecimal minInstallmentAmount, boolean compareCashflowEnabled,
            boolean isGlimEnabled, boolean isGroup, List<LoanAccountDetailsDto> clientDetails, ApplicationConfigurationDto appConfig) {
        this.repaymentIndependentOfMeetingEnabled = isRepaymentIndependentOfMeetingEnabled;
        this.loanOfferingMeetingDetail = loanOfferingMeetingDetail;
        this.customerMeetingDetail = customerMeetingDetail;
        this.loanPurposes = loanPurposes;
		this.productDto = productDto;
        this.gracePeriodInInstallments = gracePeriodInInstallments;
		this.customerDetailDto = customerDetailDto;
		this.loanProductDtos = loanProductDtos;
		this.interestRateType = interestRateType;
		this.fundDtos = fundDtos;
        this.defaultFees = defaultFees;
        this.additionalFees = additionalFees;
        this.defaultLoanAmount = defaultLoanAmount;
        this.maxLoanAmount = maxLoanAmount;
        this.minLoanAmount = minLoanAmount;
        this.defaultInterestRate = defaultInterestRate;
        this.maxInterestRate = maxInterestRate;
        this.minInterestRate = minInterestRate;
        this.defaultNumberOfInstallments = defaultNumberOfInstallments;
        this.maxNumberOfInstallments = maxNumberOfInstallments;
        this.minNumberOfInstallments = minNumberOfInstallments;
        this.nextPossibleDisbursementDate = nextPossibleDisbursementDate;
        this.daysOfTheWeekOptions = daysOfTheWeekOptions;
        this.weeksOfTheMonthOptions = weeksOfTheMonthOptions;
        this.variableInstallmentsAllowed = variableInstallmentsAllowed;
        this.fixedRepaymentSchedule = fixedRepaymentSchedule;
        this.minGapInDays = minGapInDays;
        this.maxGapInDays = maxGapInDays;
        this.minInstallmentAmount = minInstallmentAmount;
        this.compareCashflowEnabled = compareCashflowEnabled;
        this.isGlimEnabled = isGlimEnabled;
        this.isGroup = isGroup;
        this.clientDetails = clientDetails;
        this.appConfig = appConfig;
		populateProductOptions(loanProductDtos);
		populateFundOptions(fundDtos);
		this.collateralOptions = collateralOptions;
		this.purposeOfLoanOptions = purposeOfLoanOptions;
		this.defaultFeeOptions = defaultFeeOptions;
		this.additionalFeeOptions = additionalFeeOptions;
    }

    public Integer getGracePeriodInInstallments() {
        return gracePeriodInInstallments;
    }

    private void populateFundOptions(List<FundDto> funds) {
    	for (FundDto fund : funds) {
			this.fundOptions.put(fund.getId(), fund.getName());
		}
	}
    
    private void populateProductOptions(List<PrdOfferingDto> loanProducts) {
    	for (PrdOfferingDto product : loanProducts) {
    		this.productOptions.put(product.getPrdOfferingId().toString(), product.getPrdOfferingName());			
		}
	}

	public String getInterestRateType() {
		return interestRateType;
	}

    public boolean isRepaymentIndependentOfMeetingEnabled() {
        return this.repaymentIndependentOfMeetingEnabled;
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
	
	public Map<String, String> getFundOptions() {
		return fundOptions;
	}
	
	public String getPurposeOfLoan(Integer key) {
	    String result = "-";
	    if (key.intValue() > 0) {
	        result = this.purposeOfLoanOptions.get(key.toString());
	    }
	    return result;
	}

	public Map<String, String> getPurposeOfLoanOptions() {
		return purposeOfLoanOptions;
	}

	public Map<String, String> getCollateralOptions() {
		return collateralOptions;
	}

	public List<FundDto> getFundDtos() {
		return fundDtos;
	}
	
	public Map<String, String> getDefaultFeeOptions() {
		return defaultFeeOptions;
	}

	public Map<String, String> getAdditionalFeeOptions() {
		return additionalFeeOptions;
	}
	
    public List<FeeDto> getDefaultFees() {
        return defaultFees;
    }
    
    public List<FeeDto> getAdditionalFees() {
        return additionalFees;
    }
    
    public BigDecimal getDefaultLoanAmount() {
        return defaultLoanAmount;
    }

    public BigDecimal getMaxLoanAmount() {
        return maxLoanAmount;
    }

    public BigDecimal getMinLoanAmount() {
        return minLoanAmount;
    }
    
    public Double getDefaultInterestRate() {
        return defaultInterestRate;
    }

    public Double getMaxInterestRate() {
        return maxInterestRate;
    }

    public Double getMinInterestRate() {
        return minInterestRate;
    }

    public Integer getDefaultNumberOfInstallments() {
        return defaultNumberOfInstallments;
    }

    public Integer getMaxNumberOfInstallments() {
        return maxNumberOfInstallments;
    }

    public Integer getMinNumberOfInstallments() {
        return minNumberOfInstallments;
    }
    
    public LocalDate getNextPossibleDisbursementDate() {
        return nextPossibleDisbursementDate;
    }
    
    public Map<String, String> getDaysOfTheWeekOptions() {
        return daysOfTheWeekOptions;
    }
    
    public boolean isVariableInstallmentsAllowed() {
        return variableInstallmentsAllowed;
    }

    public boolean isFixedRepaymentSchedule() {
        return fixedRepaymentSchedule;
    }

    public Integer getMinGapInDays() {
        return minGapInDays;
    }

    public Integer getMaxGapInDays() {
        return maxGapInDays;
    }

    public BigDecimal getMinInstallmentAmount() {
        return minInstallmentAmount;
    }
    
    public boolean isCompareCashflowEnabled() {
        return compareCashflowEnabled;
    }
    
    public boolean isGlimApplicable() {
        return this.isGlimEnabled && this.isGroup;
    }
    
    public boolean isGlimEnabled() {
        return isGlimEnabled;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public List<LoanAccountDetailsDto> getClientDetails() {
        return clientDetails;
    }
    
    public Map<String, String> getWeeksOfTheMonthOptions() {
        return weeksOfTheMonthOptions;
    }

    public ApplicationConfigurationDto getAppConfig() {
        return appConfig;
    }
    
    public void setCompareCashflowEnabled(boolean compareCashflowEnabled) {
        this.compareCashflowEnabled = compareCashflowEnabled;
    }
}