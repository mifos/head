package org.mifos.test.acceptance.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.DefineLookupOptionParameters;
import org.mifos.test.acceptance.framework.admin.ImportLoansReviewPage;
import org.mifos.test.acceptance.framework.admin.ImportLoansSaveSummaryPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.AdminTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.ClientTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.GroupTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.loanproduct.LoanProductTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(singleThreaded = true, groups = {"admin", "acceptance", "ui", "no_db_unit"})
public class LoanImportTest extends UiTestCaseBase {
    NavigationHelper navigationHelper;
    ClientTestHelper clientTestHelper;
    GroupTestHelper groupTestHelper;
    LoanProductTestHelper loanProductTestHelper;
    AdminTestHelper adminTestHelper;
    DateTime targetTime;
    String office="MyOfficeDHMFT";
    String loanOfficer="loan officer";
    String productForClient="TestLoanProductForClient";
    String externalID="VeryExternalID";
    String collNotes="Collateral Notes";
    String fund="Non Donor";
    String[] arrayOfErrors;
    boolean valuesDefined=false;
    @Override
    @BeforeMethod(alwaysRun = true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper=new NavigationHelper(selenium);
        clientTestHelper=new ClientTestHelper(selenium);
        groupTestHelper=new GroupTestHelper(selenium);
        loanProductTestHelper=new LoanProductTestHelper(selenium);
        adminTestHelper=new AdminTestHelper(selenium);
        targetTime=new DateTime(2012, 6, 22, 12, 0, 0, 0); //changing date so dates in xls spreadsheet will be appropriate
        DateTimeUpdaterRemoteTestingService dtUpdate=new DateTimeUpdaterRemoteTestingService(selenium);
        dtUpdate.setDateTime(targetTime);
    }
    @AfterMethod(alwaysRun = true)
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }
    /**
     * MIFOS-5662: Add the possibility to import new Loans data.
     * Test loads basic xls spreadsheet and test for rows parsed with errors. Then submits successfully parsed rows.
     * Accounts are imported to client.
     */
    @Test(enabled=true)
    public void importLoanAccountsToClientTest(){
        if(!valuesDefined){
            valuesDefined=defineValuesForProducts();
        }
        String succesNumber="1";
        String errorNumber="27";
        String testID="TID1";
        arrayOfErrors=buildArrayOfErrorsForImportLoanTest(testID);
        SubmitFormParameters parameters=loanProductTestHelper.defineLoanProductParameters(10, 100, 1, 1);
        parameters.setOfferingName(productForClient);
        parameters.setMinLoanAmount("5");
        parameters.setMaxLoanAmount("1000");
        parameters.setMinInterestRate("1");
        parameters.setMaxInterestRate("50");
        parameters.setMinInstallemnts("2");
        parameters.setMaxInstallments("50");
        parameters.setGracePeriodType(SubmitFormParameters.GRACE_ON_ALL_REPAYMENTS);
        parameters.setGracePeriodDuration("10");
        try{
        loanProductTestHelper.defineNewLoanProduct(parameters);
        }catch(AssertionError ae){
            Logger.getAnonymousLogger().info("Product exists");
        }
        String importFile=this.getClass().getResource("/ImportLoanAccountsTest.xls").toString();
        ImportLoansReviewPage reviewPage=adminTestHelper.loadImportLoansFileAndSubmitForReview(importFile);
        reviewPage.validateErrors(arrayOfErrors);
        reviewPage.validateSuccesText(succesNumber);
        ImportLoansSaveSummaryPage summaryPage= reviewPage.saveSuccessfullRows();
        summaryPage.verifySuccesString(succesNumber);
        summaryPage.verifyErrorStroing(errorNumber);
    }
    private String[] buildArrayOfErrorsForImportLoanTest(String testID) {
        String[] result;
        String tid1="TID1";
        if(testID.equals(tid1)){
            String[] arrayString={"Error in row 3, Column 2: Customer with global id 2 not found",
                    "Error in row 4, Column 3: Missing product name",
                    "Error in row 5, Column 3: Active and applicable product with name WrongProductName not found",
                    "Error in row 6, Column 4: Missing account status name",
                    "Error in row 7, Column 4: Loan status is incorrect",
                    "Error in row 8, Column 5: Missing reason for cancellation",
                    "Error in row 9, Column 5: Wrong reason for cancelled account",
                    "Error in row 10, Column 6: Missing loan amount",
                    "Error in row 11, Column 6: Loan amount is out of allowed range",
                    "Error in row 12, Column 6: The amount is invalid because the number of digits after the decimal separator exceeds the allowed number of 1.",
                    "Error in row 13, Column 7: Missing interest rate",
                    "Error in row 14, Column 7: Interest rate is out of allowed range",
                    "Error in row 15, Column 7: The amount is invalid because the number of digits after the decimal separator exceeds the allowed number of 5.",
                    "Error in row 16, Column 8: Missing number of installments",
                    "Error in row 17, Column 8: Number of installments is out of allowed range",
                    "Error in row 18, Column 9: Invalid date: datetext",
                    "Error in row 19, Column 9: Invalid date: 1998-11-20",
                    "--> The disbursement date is invalid. Disbursement date must be on or after todays date.",
                    "--> The disbursement date is invalid. It cannot be before the customers activation date 18-Feb-2011.",
                    "--> The disbursement date is invalid. It cannot be before the product start date 22-Jun-2012.",
                    "--> The disbursement date is invalid. It must fall on a valid customer meeting schedule.",
                    "Error in row 20, Column 10: Missing grace period",
                    "Error in row 21, Column 10: Grace period for repayments must be less than number of loan installments.",
                    "Error in row 22, Column 10: The Grace period cannot be greater than in loan product definition.",
                    "Error in row 23, Column 11: Unknown source of fund: TestMissingSoF",
                    "Error in row 24, Column 12: Unknown loan purpose: UnknownLoanPurpose",
                    "Error in row 25, Column 13: Unknown collateral type: TestCollTypeNotKnown"
            };
            result= arrayString;
        }else{
            result= (String[]) new ArrayList<String>().toArray();
        }
        return result;
    }
    private boolean defineValuesForProducts(){
        try{
            DefineLookupOptionParameters lookupParameters=new DefineLookupOptionParameters();
            Map<Integer,String> parameters=new HashMap<Integer, String>();
            parameters.put(DefineLookupOptionParameters.TYPE_PURPOSES_OF_LOAN,"MoonPatrol");
            parameters.put(DefineLookupOptionParameters.TYPE_COLLATERAL_TYPE,"CollTypeOne");
            for (Integer key : parameters.keySet()) {
                lookupParameters.setType(key);
                lookupParameters.setName(parameters.get(key));        
                adminTestHelper.defineNewLookupOption(lookupParameters);
            }
        }catch (AssertionError e) {
            Logger.getAnonymousLogger().info("Values exist");
        }

        return true;
        
    }

}
