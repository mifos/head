
   package org.mifos.application.customer.util.valueobjects;

    import org.mifos.framework.util.valueobjects.ValueObject;
    import java.util.Set;
    import java.util.List;

    public class CustomerInformation
    {

        private String customerId;
        private String customerName;
        
        public CustomerInformation(String customerId, String customerName)
        {
        	this.customerId = customerId;
        	this.customerName = customerName;


	}



	public String getCustomerId()
        {
             return customerId;
        }

	public java.lang.String getCustomerName()
        {
             return customerName;
        }





}



