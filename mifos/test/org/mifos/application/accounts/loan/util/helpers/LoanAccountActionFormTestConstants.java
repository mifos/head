package org.mifos.application.accounts.loan.util.helpers;

import org.mifos.framework.util.helpers.NumberUtils;

public class LoanAccountActionFormTestConstants {

	public static final LoanAccountDetailsViewHelper LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_NULL = new LoanAccountDetailsViewHelper("1", null, null);

	public static final LoanAccountDetailsViewHelper LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_ZERO = new LoanAccountDetailsViewHelper("1",null,NumberUtils.DOUBLE_ZERO);
	public static final LoanAccountDetailsViewHelper LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_100 = new LoanAccountDetailsViewHelper("1",null,Double.valueOf(100));
	public static  final LoanAccountDetailsViewHelper LOAN_ACCOUNT_DETAILS_WITH_PURPOSE_NULL = new LoanAccountDetailsViewHelper("1",null,null);
	public static final LoanAccountDetailsViewHelper LOAN_ACCOUNT_DETAILS_WITH_VALID_PURPOSE = new LoanAccountDetailsViewHelper("1","Valid loan purpose",null); 
	public static final LoanAccountDetailsViewHelper LOAN_ACCOUNT_DETAILS_WITH_PURPOSE_EMPTY = new LoanAccountDetailsViewHelper("1","",null); 
	public static final LoanAccountDetailsViewHelper LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_AND_PURPOSE_NULL = new LoanAccountDetailsViewHelper("2", null, null); 
	public static final LoanAccountDetailsViewHelper LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_AND_PURPOSE_NULL2 = new LoanAccountDetailsViewHelper("3", null, null); 
	
	public static final LoanAccountDetailsViewHelper LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_200 = new LoanAccountDetailsViewHelper("2","1",Double.valueOf(200), "1");
}
