/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
 
package org.mifos.test.acceptance.user;

import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/*
 * Corresponds to story 661 in mingle
 * http://mingle.mifos.org:7070/projects/cheetah/cards/661
 */
@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"smoke","user","acceptance","ui"})
public class DefaultAdminUserCanLoginTest extends UiTestCaseBase {

	private AppLauncher appLauncher;
	
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
	@BeforeMethod
	public void setUp() throws Exception {
		super.setUp();
		appLauncher = new AppLauncher(selenium);
	}

	@AfterMethod
	public void logOut() {
		(new MifosPage(selenium)).logout();
	}
	
	public void canLaunchMifosTest() {
		appLauncher
			.launchMifos()
				.verifyPage();
	}
	
	public void defaultAdminLoginSuccessTest() {
		appLauncher
			.launchMifos()
				.loginSuccessfullyUsingDefaultCredentials()
					.verifyPage();
	}

	public void userLoginFailureBadPasswordTest() {
		appLauncher
			.launchMifos()
				.loginFailedAs("mifos", "mifos3")
					.verifyFailedLoginBadPassword();
	}


	public void userLoginFailureNoPasswordTest() {
		appLauncher
			.launchMifos()
				.loginFailedAs("mifos", "")
					.verifyFailedLoginNoPassword();
	}

	public void userLoginFailureNoUsernameTest() {
		appLauncher
			.launchMifos()
				.loginFailedAs("", "abc")
					.verifyFailedLoginNoUsername();
	}
}
