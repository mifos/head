package org.mifos.framework.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.mifos.application.IntegrationTests;
import org.mifos.application.MiscTestsSuite;
import org.mifos.application.acceptedpaymenttype.ApplicationAcceptedPaymentTypeTestSuite;
import org.mifos.application.accounts.AccountTestSuite;
import org.mifos.application.accounts.financial.FinancialTestSuite;
import org.mifos.application.accounts.loan.LoanTestSuite;
import org.mifos.application.accounts.savings.SavingsTestSuite;
import org.mifos.application.admin.AdminTestSuite;
import org.mifos.application.branchreport.BranchReportTestSuite;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationReportTestSuite;
import org.mifos.application.checklist.CheckListTestSuite;
import org.mifos.application.collectionsheet.CollectionSheetTestSuite;
import org.mifos.application.collectionsheet.business.CollectionSheetReportTestSuite;
import org.mifos.application.configuration.ApplicationConfigurationTestSuite;
import org.mifos.application.customer.CustomerTestSuite;
import org.mifos.application.fees.FeeTestSuite;
import org.mifos.application.fund.FundTestSuite;
import org.mifos.application.holiday.HolidayTestSuite;
import org.mifos.application.login.LoginTestSuite;
import org.mifos.application.master.MasterTestSuite;
import org.mifos.application.meeting.MeetingTestSuite;
import org.mifos.application.office.OfficeTestSuite;
import org.mifos.application.personnel.PersonnelTestSuite;
import org.mifos.application.ppi.PPITestSuite;
import org.mifos.application.productdefinition.ProductDefinitionTestSuite;
import org.mifos.application.reports.ReportsTestSuite;
import org.mifos.application.rolesandpermission.RolesAndPermissionTestSuite;
import org.mifos.application.surveys.SurveysTestSuite;
import org.mifos.framework.components.ComponentsTestSuite;
import org.mifos.framework.components.audit.AuditLogTestSuite;
import org.mifos.framework.components.batchjobs.BatchJobTestSuite;
import org.mifos.framework.components.configuration.ConfigurationTestSuite;
import org.mifos.framework.components.fieldConfiguration.FieldConfigurationTestSuite;
import org.mifos.framework.security.SecurityTestSuite;
import org.mifos.framework.struts.StrutsTestSuite;
import org.mifos.framework.util.helpers.FrameworkUtilsSuite;

/*
 * Generate a TestNG suite XML fragment corresponding to the class list in generateTestNgXml
 * (taken from ApplicationTestSuite.java) where the output will be of the form:
 *
 *  <test name="SecurityTestSuite" >
 *      <classes>
 *          <class name="org.mifos.framework.security.util.SecurityHelperIntegrationTest" />
 *          <class name="org.mifos.framework.security.util.LoginFilterTest" />
 *          <class name="org.mifos.framework.security.AddActivityTest" />
 *          <class name="org.mifos.framework.security.util.ActivityMapperTest" />
 *      </classes>
 *  </test>
 *
 */
public class TestNGScriptGenerator {

    private Map<String,String> shortToFullClassName = new HashMap<String,String>();
    private boolean readingClasses = false;
    private final String TEST_CODE_ROOT_PATH = "/home/van/workspace/mifos-gazelle/mifos/src/test/java/";

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        new TestNGScriptGenerator().generateTestNgXml();
    }

    public void generateTestNgXml() throws ClassNotFoundException, IOException {
        // list of test suites to convert corresponding to what's in ApplicationTestSuite.java
        Class[] testSuiteClassArray = {
            SecurityTestSuite.class,
            CollectionSheetTestSuite.class,
            CustomerTestSuite.class,
            ApplicationConfigurationTestSuite.class,
            MasterTestSuite.class,
            AccountTestSuite.class,
            FinancialTestSuite.class,
            ConfigurationTestSuite.class,
            BatchJobTestSuite.class,
            LoanTestSuite.class,
            SavingsTestSuite.class,
            ProductDefinitionTestSuite.class,
            ReportsTestSuite.class,
            FeeTestSuite.class,
            FieldConfigurationTestSuite.class,
            OfficeTestSuite.class,
            ComponentsTestSuite.class,
            PersonnelTestSuite.class,
            RolesAndPermissionTestSuite.class,
            MeetingTestSuite.class,
            LoginTestSuite.class,
            FundTestSuite.class,
            AuditLogTestSuite.class,
            CheckListTestSuite.class,
            AdminTestSuite.class,
            StrutsTestSuite.class,
            MiscTestsSuite.class,
            FrameworkUtilsSuite.class,
            HolidayTestSuite.class,
            SurveysTestSuite.class,
            PPITestSuite.class,
            ApplicationAcceptedPaymentTypeTestSuite.class,
            CollectionSheetTestSuite.class,
            CollectionSheetReportTestSuite.class,
            BranchReportTestSuite.class,
            BranchCashConfirmationReportTestSuite.class,
            IntegrationTests.class };

        for (Class testSuiteClass: testSuiteClassArray) {
            String suiteName = testSuiteClass.getName();
            //System.out.println(suiteName);
            generateTestSuiteXml(suiteName);
        }

    }

    public void printFooter() {
        System.out.println("        </classes>\n    </test>");
    }

    public void printHeader(String suiteName) {
        String last = suiteName.substring(suiteName.lastIndexOf('.')+1);
        System.out.println("    <test name=\"" + last + "\" >\n        <classes>");
    }

    public void generateTestSuiteXml(String suiteName) throws ClassNotFoundException, IOException {
        printHeader(suiteName);
        processTestSuiteFile(suiteName);
        printFooter();
    }

    private void processTestSuiteFile(String suiteName) throws ClassNotFoundException, IOException {
        String filename = TEST_CODE_ROOT_PATH + suiteName;
        // convert "/home/mypath/org.mifos.MyTest" to "/home/mypath/org/mifos/MyTest.java"
        filename = filename.replace('.','/') + ".java";

        BufferedReader in = new BufferedReader(new FileReader(filename));
        String str;
        while ((str = in.readLine()) != null) {
            processFileLine(str);
        }
        in.close();
    }

    public void processFileLine(String line) throws ClassNotFoundException {
        StringTokenizer tokenizer = new StringTokenizer(line,"() ;{}");
        while (tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken();
            //System.out.println(word);

            // if we find an import, then add a mapping from class name only
            // to fully qualified class name (e.g. "MyTest" -> "org.mifos.MyTest")
            if (word.equals("import")) {
                String fullName = tokenizer.nextToken();
                String shortName = fullName.substring(fullName.lastIndexOf(".")+1);
                shortToFullClassName.put(shortName,fullName);
                //System.out.println("put:" + shortName + "," + fullName);
            }
            // if we find a line ending with "addTestSuite" then pull off the
            // testName (e.g. "MyTest.class") and output a line with "org.mifos.MyTest"
            if (word.endsWith(".addTestSuite")) {
                String testName = tokenizer.nextToken();
                String className = testName.substring(0,testName.indexOf("."));
                //System.out.println(className);
                System.out.println("            <class name=\"" + shortToFullClassName.get(className) + "\" />");
            }
            // the "public" token marks the end of a JUnit4 style list of classes
            if (word.endsWith("public")) {
                readingClasses = false;
            }
            // when we see "@Suite.SuiteClasses" then start reading the JUnit4 style list classes that follows
            if (word.endsWith("@Suite.SuiteClasses")) {
                readingClasses = true;
                if (tokenizer.hasMoreTokens()) {
                    word = tokenizer.nextToken();
                }
            }
            // if we're reading a JUnit4 style list of classes then generate a script entry for each
            // element (e.g. "MyTest.class") in the list
            if (readingClasses && !word.endsWith("@Suite.SuiteClasses")) {
                String testName = word;
                String className = testName.substring(0,testName.indexOf("."));
                //System.out.println(className);
                System.out.println("            <class name=\"" + shortToFullClassName.get(className) + "\" />");
            }
        }
    }

}
