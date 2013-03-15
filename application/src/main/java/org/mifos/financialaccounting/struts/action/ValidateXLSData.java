package org.mifos.financialaccounting.struts.action;

import java.util.Date;
import java.util.List;

import org.apache.struts.action.ActionErrors;
import org.mifos.application.servicefacade.AccountingServiceFacade;
import org.mifos.application.servicefacade.AccountingServiceFacadeWebTier;
import org.mifos.dto.domain.GLCodeDto;
import org.mifos.dto.domain.GlobalOfficeNumDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ValidateXLSData {


	private static final Logger logger = LoggerFactory
			.getLogger(ValidateXLSData.class);
	private AccountingServiceFacade accountingServiceFacade = new AccountingServiceFacadeWebTier();
	public int getOfficeLevel(String officelevel)
	{

		int offlevel = 0;
		if(officelevel.equals("Head Office"))
		{
			offlevel=1;
		}else if (officelevel.equals("Regional Office")) {
			offlevel=2;
		}else if (officelevel.equals("Divisional Office")) {
			offlevel=3;
		}else if (officelevel.equals("Area Office")) {
			offlevel=4;
		}else if (officelevel.equals("Branch Office")) {
			offlevel=5;
		}else if (officelevel.equals("Center")) {
			offlevel=6;
		}else if (officelevel.equals("Group")) {
			offlevel=7;
		}

		return offlevel;
	}

	public ActionErrors get(Date value)
	{
		return null;
	}

	public String getTransactionType(String transType)
	{
		String transactiontype=null;
			if (transType.equals("CR")||transType.equals("CP"))
			 {
				transactiontype=transType;
			 } else if (transType.equals("BR")||transType.equals("BP"))
			 {
				transactiontype=transType;
			 }

		return transactiontype;
	}

	public String getmainAccountHead(String accountheads)
	{
		String glcode=null;
		List<GLCodeDto> glCodeDto= accountingServiceFacade.findMainAccountHeadGlCodes(accountheads);
		for(GLCodeDto glcode1:glCodeDto)
		{
		 glcode=glcode1.getGlcode();
		}
		return glcode;
	}
	public String getglobalnum(String officename)
	{
		String globalnum=null;
		List<GlobalOfficeNumDto> globalofficnum=accountingServiceFacade.findGlobalDiplayNum(officename);
		for(GlobalOfficeNumDto globaloffnum:globalofficnum)
		{
			 globalnum=globaloffnum.getGlobalofficenum();
		}
		return globalnum;
	}


}
