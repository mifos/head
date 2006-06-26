package org.mifos.application.accounts.loan.util.helpers;

import java.util.Comparator;

import org.mifos.application.accounts.loan.business.LoanActivityView;



public class EqualsObjectComparator implements Comparator {
	 public int compare(Object o1,Object o2)
	   {
	   	LoanActivityView e1 = (LoanActivityView)o1;
	   	LoanActivityView e2 = (LoanActivityView)o2;
	   	if(e1.getTimeStamp().getTime() < e2.getTimeStamp().getTime())
	   	  return 1;
	   	if(e1.getTimeStamp().getTime() == e2.getTimeStamp().getTime())
	   	  return -1;

		if(e1.getTimeStamp().getTime() > e2.getTimeStamp().getTime())
	   	  return -1;

	   	  return 10;

	   }

	    public boolean equals(Object obj)
	    {
	          return true;
	    }
}
