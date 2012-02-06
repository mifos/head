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
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, groups = {"penalties", "acceptance", "ui"})
@SuppressWarnings("PMD")
public class MainPenaltyTest extends UiTestCaseBase {
    private static final String EDIT_CATEGORY_SAVINGS = "Savings";
    private static final String ERR_MIN_GREATER_MAX = "minGreaterMax";
    private static final String ERR_INVALID_RATE = "invalidRate";
    private static final String ERR_INVALID_AMOUNT = "invalidAmount";
    private static final String ERR_INVALID_MAX = "invalidMax";
    private static final String ERR_INVALID_MIN = "invalidMin";
    private static final String ERR_INVALID_DURATION = "invalidDuration";
    private static final String ERR_RATE_OR_AMOUNT = "rateOrAmount";
    private static final String ERR_FREQUENCY = "frequency";
    private static final String ERR_AMOUNT = "amount";
    private static final String ERR_MAX = "max";
    private static final String ERR_MIN = "min";
    private static final String ERR_GLCODE = "glcode";
    private static final String ERR_PERIOD = "period";
    private static final String ERR_NAME = "name";
    private final static String ERR_APPLIES = "applies";
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
        errors.put(ERR_APPLIES, "Please select Loans/Savings to which penalties apply.");
        errors.put(ERR_NAME, "Please specify Penalty Name.");
        errors.put(ERR_PERIOD, "Please specify Grace Period Type.");
        errors.put(ERR_GLCODE, "Please specify GL Code.");
        errors.put(ERR_MIN, "Please specify Cumulative Penalty Limit (Minimum).");
        errors.put(ERR_MAX, "Please specify Cumulative Penalty Limit (Maximum).");
        errors.put(ERR_AMOUNT, "Please specify Amount.");
        errors.put(ERR_FREQUENCY, "Please specify Penalty Application Frequency.");
        errors.put(ERR_RATE_OR_AMOUNT, "Please specify either rate or amount.");
        errors.put(ERR_INVALID_DURATION, "The Grace Period Duration is invalid because only positive numbers are allowed.");
        errors.put(ERR_INVALID_MIN, "The Cumulative Penalty Limit (Minimum) is invalid because only positive numbers are allowed.");
        errors.put(ERR_INVALID_MAX, "The Cumulative Penalty Limit (Maximum) is invalid because only positive numbers are allowed.");
        errors.put(ERR_INVALID_AMOUNT, "The Amount is invalid because only positive numbers and decimal separator are allowed.");
        errors.put(ERR_INVALID_RATE, "Please specify rate along with formula.");
        errors.put(ERR_MIN_GREATER_MAX, "Cumulative Penalty Limit (Minimum) can't be greater than Cumulative Penalty Limit (Maximum).");
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @Test(enabled = true)
    public void verifyCreateAndEditPenalty() throws Exception {
        final PenaltyFormParameters param = new PenaltyFormParameters();
        ViewPenaltiesPage penaltiesPage = null;
        
        for (int i = 0; i < 2; ++i) {
            category = "";
            penaltiesPage = navigationHelper.navigateToAdminPage().navigateToViewPenaltiesPage();
            
            penaltiesPage.verifyLoanPenaltiesCount(0);
            penaltiesPage.verifySavingPenaltiesCount(i);

            PenaltyFormPage newPenaltyPage = penaltiesPage.navigateToDefineNewPenaltyPage();
            
            for (int j = 0; j < 2; ++j) {
                verifyErrorsWithEmptyForm(newPenaltyPage, param, CREATE_PAGE);
                verifyErrorsWithoutSelectFrequency(newPenaltyPage, param, CREATE_PAGE);
                verifyErrorsWithIncorrectValue(newPenaltyPage, param, CREATE_PAGE);
                verifyErrorsWithMinGreaterThanMax(newPenaltyPage, param, CREATE_PAGE);
                verifyErrorsWithoutAmount(newPenaltyPage, param, CREATE_PAGE);
                
                verifyErrorsForLoanPenaltyWithIncorrectRate(newPenaltyPage, param);
                verifyErrorsForLoanPenaltyWithoutFormula(newPenaltyPage, param);

                final NewPenaltyPreviewPage newPreviewPage = (NewPenaltyPreviewPage) fillFormAndGotoPreviewPage(newPenaltyPage, param, i, j, CREATE_PAGE);

                if (j == 0) {
                    newPenaltyPage = newPreviewPage.navigateToEditPenaltyInformationPage();
                } else if (j == 1) {
                    penaltiesPage = newPreviewPage.submit().navigateToViewPenaltiesPage();
                }
            }

            ViewPenaltyPage penaltyPage = null;

            if (i == 0) {
                penaltiesPage.verifyLoanPenaltiesCount(0);
                penaltiesPage.verifySavingPenaltiesCount(1);
                penaltyPage = penaltiesPage.navigateToViewPenaltyPage(AMOUNT_PENALTY_NAME);
            } else if (i == 1) {
                penaltiesPage.verifyLoanPenaltiesCount(1);
                penaltiesPage.verifySavingPenaltiesCount(1);
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
                    verifyErrorsWithoutAmount(newPenaltyPage, param, EDIT_PAGE);
                } else {
                    verifyErrorsForLoanPenaltyWithIncorrectRate(editPenaltyPage, param);
                    verifyErrorsForLoanPenaltyWithoutFormula(editPenaltyPage, param);
                }

                final EditPenaltyPreviewPage editPreviewPage = (EditPenaltyPreviewPage) fillFormAndGotoPreviewPage(editPenaltyPage, param, i, j, EDIT_PAGE);

                if (j == 0) {
                    newPenaltyPage = editPreviewPage.navigateToEditPenaltyInformationPage();
                } else if (j == 1) {
                    penaltyPage = editPreviewPage.submit();
                }
            }
            
            penaltyPage.verifyData(createData(param, i));
        }

        penaltiesPage = navigationHelper.navigateToAdminPage().navigateToViewPenaltiesPage();
        
        penaltiesPage.verifyLoanPenaltiesCount(1);
        penaltiesPage.verifySavingPenaltiesCount(1);
        
        penaltiesPage.verifyInActivePenaltyLabel(2);
        penaltiesPage.verifyInActivePenaltyLabel(4);
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
            final int i, final int j, final boolean isCreatePage) throws Exception {
        if (j == 0) {
            parameters.setDuration("");
        } else if (j == 1) {
            parameters.setDuration("1");
        }

        parameters.setFrequency(PenaltyFormParameters.FREQUENCY_DAILY);
        parameters.setGlCode("31102");
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
        
        penaltyFormPage.fillParameters(parameters).submitPageToDisplayErrors();
        
        penaltyFormPage.verifyErrors(new String[] { this.errors.get(ERR_NAME), this.errors.get(ERR_PERIOD),
                this.errors.get(ERR_GLCODE), this.errors.get(ERR_MIN_GREATER_MAX), this.errors.get(ERR_FREQUENCY),
                this.errors.get(ERR_INVALID_DURATION) });
    }

    private void verifyErrorsForLoanPenaltyWithIncorrectRate(final PenaltyFormPage penaltyFormPage,
            final PenaltyFormParameters parameters) {
        final String[] selectedErrors = new String[] { this.errors.get(ERR_NAME), this.errors.get(ERR_PERIOD),
                this.errors.get(ERR_GLCODE), this.errors.get(ERR_MIN_GREATER_MAX), this.errors.get(ERR_FREQUENCY),
                this.errors.get(ERR_INVALID_DURATION) };

        for (int k = 0; k < 2; ++k) {
            if (k == 0) {
                parameters.setRate("fdgfd");
            } else if (k == 1) {
                parameters.setRate("-8");
            }

            penaltyFormPage.fillParameters(parameters).submitPageToDisplayErrors();

            penaltyFormPage.verifyErrors(selectedErrors);
        }
    }

    private void verifyErrorsWithoutAmount(final PenaltyFormPage penaltyFormPage,
            final PenaltyFormParameters parameters, final boolean isCreatePage) {
        if (isCreatePage) {
            parameters.setApplies(PenaltyFormParameters.APPLIES_LOANS);
        }

        parameters.setAmount("");
        
        penaltyFormPage.fillParameters(parameters).submitPageToDisplayErrors();
        
        if (category.equalsIgnoreCase(EDIT_CATEGORY_SAVINGS)) {
            penaltyFormPage.verifyErrors(new String[] { this.errors.get(ERR_NAME), this.errors.get(ERR_PERIOD),
                    this.errors.get(ERR_GLCODE), this.errors.get(ERR_FREQUENCY), this.errors.get(ERR_INVALID_DURATION),
                    this.errors.get(ERR_MIN_GREATER_MAX), this.errors.get(ERR_AMOUNT) });
        } else {
            penaltyFormPage.verifyErrors(new String[] { this.errors.get(ERR_NAME), this.errors.get(ERR_PERIOD),
                    this.errors.get(ERR_GLCODE), this.errors.get(ERR_FREQUENCY), this.errors.get(ERR_INVALID_DURATION),
                    this.errors.get(ERR_MIN_GREATER_MAX), this.errors.get(ERR_RATE_OR_AMOUNT) });
        }
    }

    private void verifyErrorsWithMinGreaterThanMax(final PenaltyFormPage penaltyFormPage, final PenaltyFormParameters parameters,
            final boolean isCreatePage) {
        parameters.setMax("1");
        parameters.setMin("15");
        
        penaltyFormPage.fillParameters(parameters).submitPageToDisplayErrors();
        
        if (isCreatePage) {
            penaltyFormPage.verifyErrors(new String[] { this.errors.get(ERR_APPLIES), this.errors.get(ERR_NAME),
                            this.errors.get(ERR_PERIOD), this.errors.get(ERR_GLCODE), this.errors.get(ERR_INVALID_AMOUNT),
                            this.errors.get(ERR_FREQUENCY), this.errors.get(ERR_INVALID_DURATION),
                            this.errors.get(ERR_MIN_GREATER_MAX) });
        } else {
            if (category.equalsIgnoreCase(EDIT_CATEGORY_SAVINGS)) {
                penaltyFormPage.verifyErrors(new String[] { this.errors.get(ERR_NAME), this.errors.get(ERR_PERIOD),
                        this.errors.get(ERR_GLCODE), this.errors.get(ERR_INVALID_AMOUNT), this.errors.get(ERR_FREQUENCY),
                        this.errors.get(ERR_INVALID_DURATION), this.errors.get(ERR_MIN_GREATER_MAX) });
            } else {
                penaltyFormPage.verifyErrors(new String[] { this.errors.get(ERR_NAME), this.errors.get(ERR_PERIOD),
                        this.errors.get(ERR_GLCODE), this.errors.get(ERR_FREQUENCY), this.errors.get(ERR_INVALID_DURATION),
                        this.errors.get(ERR_MIN_GREATER_MAX) });
            }
        }
    }

    private void verifyErrorsWithIncorrectValue(final PenaltyFormPage penaltyFormPage, final PenaltyFormParameters parameters,
            final boolean isCreatePage) {
        final String[] selectedErrorsCreate = new String[] { this.errors.get(ERR_APPLIES), this.errors.get(ERR_NAME),
                this.errors.get(ERR_PERIOD), this.errors.get(ERR_GLCODE), this.errors.get(ERR_INVALID_MIN),
                this.errors.get(ERR_INVALID_MAX), this.errors.get(ERR_INVALID_AMOUNT), this.errors.get(ERR_FREQUENCY),
                this.errors.get(ERR_INVALID_DURATION) };

        final String[] selectedErrorsSaving = new String[] { this.errors.get(ERR_NAME), this.errors.get(ERR_PERIOD),
                this.errors.get(ERR_GLCODE), this.errors.get(ERR_INVALID_MIN), this.errors.get(ERR_INVALID_MAX),
                this.errors.get(ERR_INVALID_AMOUNT), this.errors.get(ERR_FREQUENCY),
                this.errors.get(ERR_INVALID_DURATION) };

        final String[] selectedErrorsLoan = new String[] { this.errors.get(ERR_NAME), this.errors.get(ERR_PERIOD),
                this.errors.get(ERR_GLCODE), this.errors.get(ERR_INVALID_MIN), this.errors.get(ERR_INVALID_MAX),
                this.errors.get(ERR_FREQUENCY), this.errors.get(ERR_INVALID_DURATION) };
        
        for (int k = 0; k < 2; ++k) {
            if (k == 0) {
                parameters.setDuration("dshjgfhdsjklf");
                parameters.setMin("dsgffdsg");
                parameters.setMax("fdgdfg");
                parameters.setAmount("dsgfdfg");
            } else if (k == 1) {
                parameters.setDuration("-3");
                parameters.setMin("-5");
                parameters.setMax("-10");
                parameters.setAmount("-15");
            }

            penaltyFormPage.fillParameters(parameters).submitPageToDisplayErrors();
            
            if (isCreatePage) {
                penaltyFormPage.verifyErrors(selectedErrorsCreate);
            } else {
                if (category.equalsIgnoreCase(EDIT_CATEGORY_SAVINGS)) {
                    penaltyFormPage.verifyErrors(selectedErrorsSaving);
                } else {
                    penaltyFormPage.verifyErrors(selectedErrorsLoan);
                }
            }
        }
    }

    private void verifyErrorsWithoutSelectFrequency(final PenaltyFormPage penaltyFormPage, final PenaltyFormParameters parameters, final boolean isCreatePage) {
        parameters.setFrequency(PenaltyFormParameters.LIST_SELECT);
        
        penaltyFormPage.fillParameters(parameters).submitPageToDisplayErrors();
        
        if (isCreatePage) {
            penaltyFormPage.verifyErrors(new String[] { this.errors.get(ERR_APPLIES), this.errors.get(ERR_NAME),
                    this.errors.get(ERR_PERIOD), this.errors.get(ERR_GLCODE), this.errors.get(ERR_MIN),
                    this.errors.get(ERR_MAX), this.errors.get(ERR_AMOUNT), this.errors.get(ERR_FREQUENCY) });
        } else {
            if (category.equalsIgnoreCase(EDIT_CATEGORY_SAVINGS)) {
                penaltyFormPage.verifyErrors(new String[] { this.errors.get(ERR_NAME), this.errors.get(ERR_PERIOD),
                        this.errors.get(ERR_GLCODE), this.errors.get(ERR_MIN), this.errors.get(ERR_MAX),
                        this.errors.get(ERR_AMOUNT), this.errors.get(ERR_FREQUENCY) });
            } else {
                penaltyFormPage.verifyErrors(new String[] { this.errors.get(ERR_NAME), this.errors.get(ERR_PERIOD),
                        this.errors.get(ERR_GLCODE), this.errors.get(ERR_MIN), this.errors.get(ERR_MAX),
                        this.errors.get(ERR_RATE_OR_AMOUNT), this.errors.get(ERR_FREQUENCY) });
            }
        }
    }

    private void verifyErrorsWithEmptyForm(final PenaltyFormPage penaltyFormPage, final PenaltyFormParameters parameters, final boolean isCreatePage) {
        parameters.setToDefault();
        penaltyFormPage.fillParameters(parameters).submitPageToDisplayErrors();
        
        if (isCreatePage) {
            penaltyFormPage.verifyErrors(new String[] { this.errors.get(ERR_APPLIES), this.errors.get(ERR_NAME),
                    this.errors.get(ERR_PERIOD), this.errors.get(ERR_GLCODE), this.errors.get(ERR_MIN),
                    this.errors.get(ERR_MAX), this.errors.get(ERR_AMOUNT) });
        } else {
            if (category.equalsIgnoreCase(EDIT_CATEGORY_SAVINGS)) {
                penaltyFormPage.verifyErrors(new String[] { this.errors.get(ERR_NAME), this.errors.get(ERR_PERIOD),
                        this.errors.get(ERR_GLCODE), this.errors.get(ERR_MIN), this.errors.get(ERR_MAX),
                        this.errors.get(ERR_AMOUNT) });
            } else {
                penaltyFormPage.verifyErrors(new String[] { this.errors.get(ERR_NAME), this.errors.get(ERR_PERIOD),
                        this.errors.get(ERR_GLCODE), this.errors.get(ERR_MIN), this.errors.get(ERR_MAX),
                        this.errors.get(ERR_RATE_OR_AMOUNT) });
            }
        }
    }
}
