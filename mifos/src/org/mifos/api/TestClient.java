package org.mifos.api;

import org.mifos.api.MifosService;
import org.mifos.framework.exceptions.AppNotConfiguredException;
import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.exceptions.LoggerConfigurationException;


public class TestClient {
	public void executeFindLoan(Integer id) throws LoggerConfigurationException, HibernateStartUpException, AppNotConfiguredException, Exception {
		MifosService ms = new MifosService();
		
		System.out.println("Initializing MifosService...");
		ms.init();
		System.out.println("Done.");
		
		Loan l = ms.getLoan(id);
		
		StringBuffer msg = new StringBuffer();
		msg.append("\n\n");
		msg.append(l.getBorrowerName());
		msg.append(" owes ");
		msg.append(l.getLoanBalance());
		msg.append(" ").append(l.getLoanCurrency()).append("S");
		msg.append("\n\n");
		
		System.out.println(msg);
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: TestClient <loanid>");
		} else {
			TestClient tc = new TestClient();
			Integer id = Integer.valueOf(args[0]);
			
			try {
				tc.executeFindLoan(id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
