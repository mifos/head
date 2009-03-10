package org.mifos.ui.loan.command;

import org.mifos.loan.domain.LoanProductStatus;
import org.mifos.loan.service.LoanProductDto;

public class LoanProductFormBean extends LoanProductDto {

	private final void setDefaults(){
		setStatus(LoanProductStatus.ACTIVE);
	}
	
	public LoanProductFormBean() {
	    super();
	    this.setDefaults();
	}
	
	public LoanProductFormBean (LoanProductDto dto) {
	    super();
	    this.id = dto.getId();
	    this.longName = dto.getLongName();
	    this.shortName = dto.getShortName();
	    this.minInterestRate = dto.getMinInterestRate();
	    this.maxInterestRate = dto.getMaxInterestRate();
	    this.status = dto.getStatus();
	    this.setDefaults();
	}
		
}
