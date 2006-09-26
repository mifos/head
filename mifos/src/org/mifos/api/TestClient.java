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

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: TestClient <loanid>");
		} else {
			TestClient client = new TestClient();
			Integer id = Integer.valueOf(args[0]);
			
			try {
				client.executeFindLoan(id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
