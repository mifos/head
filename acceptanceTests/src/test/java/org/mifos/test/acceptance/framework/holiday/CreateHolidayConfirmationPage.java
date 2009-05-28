package org.mifos.test.acceptance.framework.holiday;

import org.mifos.test.acceptance.framework.MifosPage;
import com.thoughtworks.selenium.Selenium;

public class CreateHolidayConfirmationPage extends MifosPage {
    public CreateHolidayConfirmationPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("Review_holidayCreation");
    }

    public ViewHolidaysPage navigateToViewHolidaysPage() {
        selenium.click("holiday.button.submit");
        waitForPageToLoad();
        return new ViewHolidaysPage(selenium);
    }
}
