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
 
package org.mifos.api;


public class TestClient {
	public void executeFindLoan(Integer id) throws Exception {
		MifosService service = new MifosService();
		
		System.out.println("Initializing MifosService...");
		service.init();
		System.out.println("Done.");
		
		Loan loan = service.getLoan(id);
		
		StringBuilder message = new StringBuilder();
		message.append("\n\n");
		message.append(loan.getBorrowerName());
		message.append(" owes ");
		message.append(loan.getLoanBalance());
		message.append(" ").append(loan.getLoanCurrency()).append("S");
		message.append("\n\n");
		
		System.out.println(message);
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("Usage: TestClient <loanid>");
		} else {
			TestClient client = new TestClient();
			Integer id = Integer.valueOf(args[0]);
			
			client.executeFindLoan(id);
		}
	}
	
}
