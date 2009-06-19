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

public class TestNGScriptGenerator {

    Map<String,String> shortToFullClassName = new HashMap<String,String>();
    boolean readingClasses = false;

    public static void main(String[] args) throws ClassNotFoundException {
        new TestNGScriptGenerator().test();
    }

    public void test() throws ClassNotFoundException {

    Class[] classes = {
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

        for (Class aClass: classes) {
            String suiteName = aClass.getName();
            //System.out.println(suiteName);
            processSuite(suiteName);
        }

    }

    public void printFooter() {
        System.out.println("        </classes>\n    </test>");
    }

    public void printHeader(String suiteName) {
        String last = suiteName.substring(suiteName.lastIndexOf('.')+1);
        System.out.println("    <test name=\"" + last + "\" >\n        <classes>");
    }

    public void processSuite(String suiteName) throws ClassNotFoundException {
        printHeader(suiteName);
        String filename = "/home/van/workspace/mifos-gazelle/mifos/src/test/java/" + suiteName;
        filename = filename.replace('.','/') + ".java";
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String str;
            while ((str = in.readLine()) != null) {
                processFileLine(str);
            }
            in.close();
        } catch (IOException e) {
        }
        printFooter();
    }
    public void processFileLine(String line) throws ClassNotFoundException {
        StringTokenizer st = new StringTokenizer(line,"() ;{}");
        while (st.hasMoreTokens()) {
            String word = st.nextToken();
            //System.out.println(word);

            if (word.equals("import")) {
                String fullName = st.nextToken();
                String shortName = fullName.substring(fullName.lastIndexOf(".")+1);
                shortToFullClassName.put(shortName,fullName);
                //System.out.println("put:" + shortName + "," + fullName);
            }
            if (word.endsWith(".addTestSuite")) {
                String testName = st.nextToken();
                String className = testName.substring(0,testName.indexOf("."));
                //System.out.println(className);
                System.out.println("            <class name=\"" + shortToFullClassName.get(className) + "\" />");
            }
            if (word.endsWith("public")) {
                readingClasses = false;
            }
            if (word.endsWith("@Suite.SuiteClasses")) {
                readingClasses = true;
                if (st.hasMoreTokens()) {
                    word = st.nextToken();
                }
            }
            if (readingClasses && !word.endsWith("@Suite.SuiteClasses")) {
                String testName = word;
                String className = testName.substring(0,testName.indexOf("."));
                //System.out.println(className);
                System.out.println("            <class name=\"" + shortToFullClassName.get(className) + "\" />");
            }
        }
    }

}
