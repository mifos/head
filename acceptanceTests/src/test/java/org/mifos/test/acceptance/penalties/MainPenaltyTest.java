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

package org.mifos.test.acceptance.penalties;

import static org.mifos.test.acceptance.framework.admin.PenaltyFormParameters.FREQUENCY_NONE;
import static org.mifos.test.acceptance.framework.admin.PenaltyFormParameters.PERIOD_NONE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.EditPenaltyPreviewPage;
import org.mifos.test.acceptance.framework.admin.NewPenaltyPreviewPage;
import org.mifos.test.acceptance.framework.admin.PenaltyFormPage;
import org.mifos.test.acceptance.framework.admin.PenaltyFormParameters;
import org.mifos.test.acceptance.framework.admin.ViewPenaltiesPage;
import org.mifos.test.acceptance.framework.admin.ViewPenaltyPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.PenaltyHelper;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, groups = {"penalties", "acceptance", "ui", "no_db_unit"})
@SuppressWarnings("PMD")
public class MainPenaltyTest extends UiTestCaseBase {
    private static final String EDIT_CATEGORY_SAVINGS = "Savings";
    private final static boolean CREATE_PAGE = true;
    private final static boolean EDIT_PAGE = false;
    private final static String AMOUNT_PENALTY_NAME = "Amount Saving Penalty";
    private final static String RATE_PENALTY_NAME = "Rate Loan Penalty";
    
    private Map<String, String> errors;
    private String category;
    private NavigationHelper navigationHelper;

    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        navigationHelper = new NavigationHelper(selenium);

        errors = new HashMap<String, String>();
        errors.put("applies", "Please select Loans/Savings to which penalties apply.");
        errors.put("name", "Please specify Penalty Name.");
        errors.put("duration", "Please specify Grace Period Duration.");
        errors.put("glcode", "Please specify GL Code.");
        errors.put("min", "Please specify Cumulative Penalty Limit (Minimum).");
        errors.put("max", "Please specify Cumulative Penalty Limit (Maximum).");
        errors.put("amount", "Please specify Amount.");
        errors.put("frequency", "Please specify Penalty Application Frequency.");
        errors.put("rateOrAmount", "Please specify either rate or amount.");
        errors.put("formula", "Please specify rate along with formula.");
        
        errors.put("invalidDuration", "The Grace Period Duration is invalid because only positive numbers are allowed.");
        errors.put("invalidMin", "The Cumulative Penalty Limit (Minimum) is invalid because only positive numbers and decimal separator are allowed.");
        errors.put("invalidMax", "The Cumulative Penalty Limit (Maximum) is invalid because only positive numbers and decimal separator are allowed.");
        errors.put("invalidAmount", "The Amount is invalid because only positive numbers and decimal separator are allowed.");
        errors.put("invalidRate", "The Rate is invalid because only positive numbers and decimal separator are allowed.");
        
        errors.put("minGreaterMax", "Cumulative Penalty Limit (Minimum) can't be greater than Cumulative Penalty Limit (Maximum).");
        
        errors.put("beforeDecimalDuration", "The Grace Period Duration is invalid because the number of digits before the decimal separator exceeds the allowed number 14.");
        errors.put("beforeDecimalMin", "The Cumulative Penalty Limit (Minimum) is invalid because the number of digits before the decimal separator exceeds the allowed number 14.");
        errors.put("beforeDecimalMax", "The Cumulative Penalty Limit (Maximum) is invalid because the number of digits before the decimal separator exceeds the allowed number 14.");
        errors.put("beforeDecimalAmount", "The Amount is invalid because the number of digits before the decimal separator exceeds the allowed number 14.");
        errors.put("beforeDecimalRate", "The Rate is invalid because the number of digits before the decimal separator exceeds the allowed number 14.");
        
        errors.put("afterDecimalMin", "The Cumulative Penalty Limit (Minimum) is invalid because Only 1 digit(s) after decimal separator is allowed.");
        errors.put("afterDecimalMax", "The Cumulative Penalty Limit (Maximum) is invalid because Only 1 digit(s) after decimal separator is allowed.");
        errors.put("afterDecimalAmount", "The Amount is invalid because Only 1 digit(s) after decimal separator is allowed.");
        errors.put("afterDecimalRate", "The Rate is invalid because Only 1 digit(s) after decimal separator is allowed.");
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @Test(enabled = true)
    public void verifyCreateAndEditPenalty() throws Exception {
        final PenaltyFormParameters param = new PenaltyFormParameters();
        ViewPenaltiesPage penaltiesPage = navigationHelper.navigateToAdminPage().navigateToViewPenaltiesPage();
        
        int startLoanPenaltyCount = penaltiesPage.getLoanPenaltiesCount();
        int startSavingsPenaltyCount = penaltiesPage.getSavingPenaltiesCount();
        
        for (int i = 0; i < 2; ++i) {
            category = "";
            penaltiesPage.verifyLoanPenaltiesCount(startLoanPenaltyCount);
            penaltiesPage.verifySavingPenaltiesCount(startSavingsPenaltyCount + i);

            PenaltyFormPage newPenaltyPage = penaltiesPage.navigateToDefineNewPenaltyPage();
            
            for (int j = 0; j < 2; ++j) {
                verifyErrorsWithEmptyForm(newPenaltyPage, param, CREATE_PAGE);
                verifyErrorsWithoutSelectFrequency(newPenaltyPage, param, CREATE_PAGE);
                verifyErrorsWithIncorrectValue(newPenaltyPage, param, CREATE_PAGE);
                verifyErrorsWithMinGreaterThanMax(newPenaltyPage, param, CREATE_PAGE);
                verifyErrorsForLoanPenaltyWithoutAmount(newPenaltyPage, param, CREATE_PAGE);
                
                verifyErrorsForLoanPenaltyWithIncorrectRate(newPenaltyPage, param);
                verifyErrorsForLoanPenaltyWithoutFormula(newPenaltyPage, param);

                final NewPenaltyPreviewPage newPreviewPage = (NewPenaltyPreviewPage) fillFormAndGotoPreviewPage(newPenaltyPage, param, i, CREATE_PAGE);

                if (j == 0) {
                    newPenaltyPage = newPreviewPage.navigateToEditPenaltyInformationPage();
                } else if (j == 1) {
                    penaltiesPage = newPreviewPage.submit().navigateToViewPenaltiesPage();
                }
            }

            ViewPenaltyPage penaltyPage = null;

            if (i == 0) {
                penaltiesPage.verifyLoanPenaltiesCount(startLoanPenaltyCount);
                penaltiesPage.verifySavingPenaltiesCount(startSavingsPenaltyCount + 1);
                penaltyPage = penaltiesPage.navigateToViewPenaltyPage(AMOUNT_PENALTY_NAME);
            } else if (i == 1) {
                penaltiesPage.verifyLoanPenaltiesCount(startLoanPenaltyCount + 1);
                penaltiesPage.verifySavingPenaltiesCount(startSavingsPenaltyCount + 1);
                penaltyPage = penaltiesPage.navigateToViewPenaltyPage(RATE_PENALTY_NAME);
            }

            penaltyPage.verifyData(createData(param, i));

            final PenaltyFormPage editPenaltyPage = penaltyPage.navigateToEditPenaltyPage();
            category = param.getApplies();

            for (int j = 0; j < 2; ++j) {
                verifyErrorsWithEmptyForm(editPenaltyPage, param, EDIT_PAGE);
                verifyErrorsWithoutSelectFrequency(editPenaltyPage, param, EDIT_PAGE);
                verifyErrorsWithIncorrectValue(editPenaltyPage, param, EDIT_PAGE);
                verifyErrorsWithMinGreaterThanMax(editPenaltyPage, param, EDIT_PAGE);

                if (i == 0) {
                    verifyErrorsForLoanPenaltyWithoutAmount(newPenaltyPage, param, EDIT_PAGE);
                } else {
                    verifyErrorsForLoanPenaltyWithIncorrectRate(editPenaltyPage, param);
                    verifyErrorsForLoanPenaltyWithoutFormula(editPenaltyPage, param);
                }

                final EditPenaltyPreviewPage editPreviewPage = (EditPenaltyPreviewPage) fillFormAndGotoPreviewPage(editPenaltyPage, param, i, EDIT_PAGE);

                if (j == 0) {
                    newPenaltyPage = editPreviewPage.navigateToEditPenaltyInformationPage();
                } else if (j == 1) {
                    penaltyPage = editPreviewPage.submit();
                }
            }
            
            penaltyPage.verifyData(createData(param, i));
            
            penaltiesPage = navigationHelper.navigateToAdminPage().navigateToViewPenaltiesPage();
        }

        penaltiesPage = navigationHelper.navigateToAdminPage().navigateToViewPenaltiesPage();
        
        penaltiesPage.verifyLoanPenaltiesCount(startLoanPenaltyCount + 1);
        penaltiesPage.verifySavingPenaltiesCount(startSavingsPenaltyCount + 1);
        
        penaltiesPage.verifyInActivePenaltyLabel(2);
        penaltiesPage.verifyInActivePenaltyLabel(4);
    }
    
    @Test(enabled = true, dependsOnMethods={"verifyCreateAndEditPenalty"})
    public void checkPermissionsForPenalties() throws Exception {
        String penaltyName = "Penalty Permission";
        String accessDenied = "Access Denied";
        String youAreNotAllowedToAccessThisPage = "You are not allowed to access this page.";
        
        navigationHelper.navigateToAdminPage().navigateToViewRolesPage()
            .navigateToManageRolePage("Admin").disablePermission("0_6").submitAndGotoViewRolesPage();
        
        navigationHelper.navigateToAdminPage().navigateToDefineNewPenaltyPage();
        Assert.assertTrue(selenium.isTextPresent(accessDenied));
        Assert.assertTrue(selenium.isTextPresent(youAreNotAllowedToAccessThisPage));
        
        navigationHelper.navigateToAdminPage().navigateToViewPenaltiesPage().navigateToDefineNewPenaltyPage();
        Assert.assertTrue(selenium.isTextPresent(accessDenied));
        Assert.assertTrue(selenium.isTextPresent(youAreNotAllowedToAccessThisPage));
        
        navigationHelper.navigateToAdminPage().navigateToViewRolesPage()
            .navigateToManageRolePage("Admin").enablePermission("0_6_0").submitAndGotoViewRolesPage();
        
        new PenaltyHelper(selenium).createAmountPenalty(penaltyName, PERIOD_NONE, "", FREQUENCY_NONE, "1", "999", "5");
        
        navigationHelper.navigateToAdminPage().navigateToViewPenaltiesPage()
            .navigateToViewPenaltyPage(penaltyName).navigateToEditPenaltyPage();
        Assert.assertTrue(selenium.isTextPresent(accessDenied));
        Assert.assertTrue(selenium.isTextPresent(youAreNotAllowedToAccessThisPage));
        
        navigationHelper.navigateToAdminPage().navigateToViewRolesPage()
        .navigateToManageRolePage("Admin").enablePermission("0_6_1").submitAndGotoViewRolesPage();
        
        new PenaltyHelper(selenium).editAmountPenalty(penaltyName, penaltyName, PERIOD_NONE, "", FREQUENCY_NONE, "1", "9999", "3");
    }
    
    private List<String> createData(final PenaltyFormParameters param, final int i) {
        final List<String> data = new ArrayList<String>();

        data.add("Admin / View penalties / " + param.getName());
        data.add(param.getName());
        data.add("Edit penalty");
        data.add("Penalty Details");
        data.add("Penalty Name: " + param.getName());
        data.add("Penalty Applies To: " + param.getApplies());
        data.add("Grace Period Type: " + param.getPeriod());
        data.add("Grace Period Duration: " + StringUtil.formatNumber(param.getDuration()));
        data.add("Cumulative Penalty Limit (Minimum): " + StringUtil.formatNumber(param.getMin()));
        data.add("Cumulative Penalty Limit (Maximum): " + StringUtil.formatNumber(param.getMax()));
        data.add("Interest Calculation");

        if (i == 0) {
            data.add("Amount: " + StringUtil.formatNumber(param.getAmount()));
        } else if (i == 1) {
            data.add("Calculate Penalty As: " + StringUtil.formatNumber(param.getRate()) + " % of " + param.getFormula());
        }

        data.add("Penalty Application Frequency: " + param.getFrequency());
        data.add("Accounting Details");
        data.add("GL Code: " + param.getGlCode());
        data.add("Status: " + param.getStatus());

        return data;
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private MifosPage fillFormAndGotoPreviewPage(final PenaltyFormPage penaltyFormPage, final PenaltyFormParameters parameters,
            final int i, final boolean isCreatePage) throws Exception {
        parameters.setFrequency(PenaltyFormParameters.FREQUENCY_DAILY);
        parameters.setGlCode("31102");
        parameters.setDuration("1");
        parameters.setMax("15");
        parameters.setMin("1");

        if (i == 0) {
            parameters.setName(AMOUNT_PENALTY_NAME);
            parameters.setApplies(PenaltyFormParameters.APPLIES_SAVINGS);
            parameters.setPeriod(PenaltyFormParameters.PERIOD_DAYS);
            parameters.setAmount("2200.5");
            parameters.setRate("");
        } else if (i == 1) {
            parameters.setName(RATE_PENALTY_NAME);
            parameters.setApplies(PenaltyFormParameters.APPLIES_LOANS);
            parameters.setPeriod(PenaltyFormParameters.PERIOD_INSTALLMENTS);
            parameters.setRate("7.5");
            parameters.setFormula(PenaltyFormParameters.FORMULA_OUTSTANDING_LOAN);
            parameters.setAmount("");
        }

        MifosPage previewPage = null;

        if (isCreatePage) {
            previewPage = penaltyFormPage.fillParameters(parameters)
                    .submitPageAndGotoPenaltyPreviewPage(NewPenaltyPreviewPage.class);
            
            ((NewPenaltyPreviewPage) previewPage).verifyData(parameters);
        } else {
            parameters.setStatus(PenaltyFormParameters.STATUS_INACTIVE);
            
            previewPage = penaltyFormPage.fillParameters(parameters)
                    .submitPageAndGotoPenaltyPreviewPage(EditPenaltyPreviewPage.class);
            
            ((EditPenaltyPreviewPage) previewPage).verifyData(parameters);
        }

        return previewPage;
    }

    private void verifyErrorsForLoanPenaltyWithoutFormula(final PenaltyFormPage penaltyFormPage,
            final PenaltyFormParameters parameters) {
        parameters.setRate("8.5");
        parameters.setFormula(PenaltyFormParameters.LIST_SELECT);
        
        penaltyFormPage.fillParameters(parameters).submitPageToDisplayErrors();
        
        penaltyFormPage.verifyErrors(new String[] { this.errors.get("name"),
                this.errors.get("glcode"), this.errors.get("minGreaterMax"), this.errors.get("frequency"),
                this.errors.get("beforeDecimalDuration"), this.errors.get("formula") });
    }

    private void verifyErrorsForLoanPenaltyWithIncorrectRate(final PenaltyFormPage penaltyFormPage,
            final PenaltyFormParameters parameters) {
        final String[] selectedErrors1 = new String[] { this.errors.get("name"),
                this.errors.get("glcode"), this.errors.get("minGreaterMax"), this.errors.get("frequency"),
                this.errors.get("beforeDecimalDuration"), this.errors.get("invalidRate") };
        
        final String[] selectedErrors2 = new String[] { this.errors.get("name"),
                this.errors.get("glcode"), this.errors.get("minGreaterMax"), this.errors.get("frequency"),
                this.errors.get("beforeDecimalDuration"), this.errors.get("beforeDecimalRate") };
        
        final String[] selectedErrors3 = new String[] { this.errors.get("name"),
                this.errors.get("glcode"), this.errors.get("minGreaterMax"), this.errors.get("frequency"),
                this.errors.get("beforeDecimalDuration"), this.errors.get("afterDecimalRate") };

        parameters.setFormula(PenaltyFormParameters.FORMULA_OVERDUE_AMOUNT);
        
        for (int k = 0; k < 4; ++k) {
            switch(k) {
            case 0: parameters.setRate("fdgfd"); break;
            case 1: parameters.setRate("-8"); break;
            case 2: parameters.setRate("123456789012345"); break;
            case 3: parameters.setRate("1.123456"); break;
            }

            penaltyFormPage.fillParameters(parameters).submitPageToDisplayErrors();
            
            switch(k) {
            case 0: 
            case 1: penaltyFormPage.verifyErrors(selectedErrors1); break;
            case 2: penaltyFormPage.verifyErrors(selectedErrors2); break;
            case 3: penaltyFormPage.verifyErrors(selectedErrors3); break;
            }
        }
    }

    private void verifyErrorsForLoanPenaltyWithoutAmount(final PenaltyFormPage penaltyFormPage,
            final PenaltyFormParameters parameters, final boolean isCreatePage) {
        if (isCreatePage) {
            parameters.setApplies(PenaltyFormParameters.APPLIES_LOANS);
        }

        parameters.setAmount("");
        
        penaltyFormPage.fillParameters(parameters).submitPageToDisplayErrors();
        
        if (category.equalsIgnoreCase(EDIT_CATEGORY_SAVINGS)) {
            penaltyFormPage.verifyErrors(new String[] { this.errors.get("name"),
                    this.errors.get("glcode"), this.errors.get("frequency"), this.errors.get("beforeDecimalDuration"),
                    this.errors.get("minGreaterMax"), this.errors.get("amount") });
        } else {
            penaltyFormPage.verifyErrors(new String[] { this.errors.get("name"),
                    this.errors.get("glcode"), this.errors.get("frequency"), this.errors.get("beforeDecimalDuration"),
                    this.errors.get("minGreaterMax"), this.errors.get("rateOrAmount") });
        }
    }

    private void verifyErrorsWithMinGreaterThanMax(final PenaltyFormPage penaltyFormPage, final PenaltyFormParameters parameters,
            final boolean isCreatePage) {
        parameters.setMax("1");
        parameters.setMin("15");
        
        penaltyFormPage.fillParameters(parameters).submitPageToDisplayErrors();
        
        if (isCreatePage) {
            penaltyFormPage.verifyErrors(new String[] { this.errors.get("applies"), this.errors.get("name"),
                            this.errors.get("glcode"), this.errors.get("afterDecimalAmount"),
                            this.errors.get("frequency"), this.errors.get("beforeDecimalDuration"),
                            this.errors.get("minGreaterMax") });
        } else {
            if (category.equalsIgnoreCase(EDIT_CATEGORY_SAVINGS)) {
                penaltyFormPage.verifyErrors(new String[] { this.errors.get("name"),
                        this.errors.get("glcode"), this.errors.get("afterDecimalAmount"), this.errors.get("frequency"),
                        this.errors.get("beforeDecimalDuration"), this.errors.get("minGreaterMax") });
            } else {
                penaltyFormPage.verifyErrors(new String[] { this.errors.get("name"),
                        this.errors.get("glcode"), this.errors.get("frequency"), this.errors.get("beforeDecimalDuration"),
                        this.errors.get("minGreaterMax") });
            }
        }
    }

    private void verifyErrorsWithIncorrectValue(final PenaltyFormPage penaltyFormPage, final PenaltyFormParameters parameters,
            final boolean isCreatePage) {
        final String[] selectedErrorsCreate1 = new String[] { this.errors.get("applies"), this.errors.get("name"),
                this.errors.get("glcode"), this.errors.get("invalidMin"),
                this.errors.get("invalidMax"), this.errors.get("invalidAmount"), this.errors.get("frequency"),
                this.errors.get("invalidDuration") };
        
        final String[] selectedErrorsCreate2 = new String[] { this.errors.get("applies"), this.errors.get("name"),
                this.errors.get("glcode"), this.errors.get("beforeDecimalMin"),
                this.errors.get("beforeDecimalMax"), this.errors.get("beforeDecimalAmount"), this.errors.get("frequency"),
                this.errors.get("beforeDecimalDuration") };
        
        final String[] selectedErrorsCreate3 = new String[] { this.errors.get("applies"), this.errors.get("name"),
                this.errors.get("glcode"), this.errors.get("afterDecimalMin"),
                this.errors.get("afterDecimalMax"), this.errors.get("afterDecimalAmount"), this.errors.get("frequency"),
                this.errors.get("beforeDecimalDuration") };

        final String[] selectedErrorsSaving1 = new String[] { this.errors.get("name"),
                this.errors.get("glcode"), this.errors.get("invalidMin"), this.errors.get("invalidMax"),
                this.errors.get("invalidAmount"), this.errors.get("frequency"),
                this.errors.get("invalidDuration") };
        
        final String[] selectedErrorsSaving2 = new String[] { this.errors.get("name"),
                this.errors.get("glcode"), this.errors.get("beforeDecimalMin"), this.errors.get("beforeDecimalMax"),
                this.errors.get("beforeDecimalAmount"), this.errors.get("frequency"),
                this.errors.get("beforeDecimalDuration") };
        
        final String[] selectedErrorsSaving3 = new String[] { this.errors.get("name"),
                this.errors.get("glcode"), this.errors.get("afterDecimalMin"), this.errors.get("afterDecimalMax"),
                this.errors.get("afterDecimalAmount"), this.errors.get("frequency"),
                this.errors.get("beforeDecimalDuration") };

        final String[] selectedErrorsLoan1 = new String[] { this.errors.get("name"),
                this.errors.get("glcode"), this.errors.get("invalidMin"), this.errors.get("invalidMax"),
                this.errors.get("frequency"), this.errors.get("invalidDuration") };
        
        final String[] selectedErrorsLoan2 = new String[] { this.errors.get("name"),
                this.errors.get("glcode"), this.errors.get("beforeDecimalMin"), this.errors.get("beforeDecimalMax"),
                this.errors.get("frequency"), this.errors.get("beforeDecimalDuration") };
        
        final String[] selectedErrorsLoan3 = new String[] { this.errors.get("name"),
                this.errors.get("glcode"), this.errors.get("afterDecimalMin"), this.errors.get("afterDecimalMax"),
                this.errors.get("frequency"), this.errors.get("beforeDecimalDuration") };
        
        for (int k = 0; k < 4; ++k) {
            switch (k) {
            case 0:
                parameters.setPeriod(PenaltyFormParameters.PERIOD_DAYS);
                parameters.setDuration("dshjgfhdsjklf");
                parameters.setMin("dsgffdsg");
                parameters.setMax("fdgdfg");
                parameters.setAmount("dsgfdfg");
                break;
            case 1:
                parameters.setPeriod(PenaltyFormParameters.PERIOD_DAYS);
                parameters.setDuration("-3");
                parameters.setMin("-5");
                parameters.setMax("-10");
                parameters.setAmount("-15");
                break;
            case 2:
                parameters.setPeriod(PenaltyFormParameters.PERIOD_DAYS);
                parameters.setDuration("123456789012345");
                parameters.setMin("123456789012345");
                parameters.setMax("123456789012345");
                parameters.setAmount("123456789012345");
                break;
            case 3:
                parameters.setMin("1.123456");
                parameters.setMax("1.123456");
                parameters.setAmount("1.123456");
                break;
            }

            penaltyFormPage.fillParameters(parameters).submitPageToDisplayErrors();
            
            if (isCreatePage) {
                switch(k) {
                case 0:
                case 1: penaltyFormPage.verifyErrors(selectedErrorsCreate1); break;
                case 2: penaltyFormPage.verifyErrors(selectedErrorsCreate2); break;
                case 3: penaltyFormPage.verifyErrors(selectedErrorsCreate3); break;
                }
            } else {
                if (category.equalsIgnoreCase(EDIT_CATEGORY_SAVINGS)) {
                    switch(k) {
                    case 0:
                    case 1: penaltyFormPage.verifyErrors(selectedErrorsSaving1); break;
                    case 2: penaltyFormPage.verifyErrors(selectedErrorsSaving2); break;
                    case 3: penaltyFormPage.verifyErrors(selectedErrorsSaving3); break;
                    }
                } else {
                    switch(k) {
                    case 0:
                    case 1: penaltyFormPage.verifyErrors(selectedErrorsLoan1); break;
                    case 2: penaltyFormPage.verifyErrors(selectedErrorsLoan2); break;
                    case 3: penaltyFormPage.verifyErrors(selectedErrorsLoan3); break;
                    }
                }
            }
        }
    }

    private void verifyErrorsWithoutSelectFrequency(final PenaltyFormPage penaltyFormPage, final PenaltyFormParameters parameters, final boolean isCreatePage) {
        parameters.setFrequency(PenaltyFormParameters.LIST_SELECT);
        
        penaltyFormPage.fillParameters(parameters).submitPageToDisplayErrors();
        
        if (isCreatePage) {
            penaltyFormPage.verifyErrors(new String[] { this.errors.get("applies"), this.errors.get("name"),
                    this.errors.get("glcode"), this.errors.get("min"),
                    this.errors.get("max"), this.errors.get("amount"), this.errors.get("frequency") });
        } else {
            if (category.equalsIgnoreCase(EDIT_CATEGORY_SAVINGS)) {
                penaltyFormPage.verifyErrors(new String[] { this.errors.get("name"),
                        this.errors.get("glcode"), this.errors.get("min"), this.errors.get("max"),
                        this.errors.get("amount"), this.errors.get("frequency") });
            } else {
                penaltyFormPage.verifyErrors(new String[] { this.errors.get("name"),
                        this.errors.get("glcode"), this.errors.get("min"), this.errors.get("max"),
                        this.errors.get("rateOrAmount"), this.errors.get("frequency") });
            }
        }
    }

    private void verifyErrorsWithEmptyForm(final PenaltyFormPage penaltyFormPage, final PenaltyFormParameters parameters, final boolean isCreatePage) {
        parameters.setToDefault();
        penaltyFormPage.fillParameters(parameters).submitPageToDisplayErrors();
        
        if (isCreatePage) {
            penaltyFormPage.verifyErrors(new String[] { this.errors.get("applies"), this.errors.get("name"),
                    this.errors.get("glcode"), this.errors.get("min"),
                    this.errors.get("max"), this.errors.get("amount") });
        } else {
            if (category.equalsIgnoreCase(EDIT_CATEGORY_SAVINGS)) {
                penaltyFormPage.verifyErrors(new String[] { this.errors.get("name"),
                        this.errors.get("glcode"), this.errors.get("min"), this.errors.get("max"),
                        this.errors.get("amount") });
            } else {
                penaltyFormPage.verifyErrors(new String[] { this.errors.get("name"),
                        this.errors.get("glcode"), this.errors.get("min"), this.errors.get("max"),
                        this.errors.get("rateOrAmount") });
            }
        }
    }
}
