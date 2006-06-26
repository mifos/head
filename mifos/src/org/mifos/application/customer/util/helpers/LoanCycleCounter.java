/**
 * 
 */
package org.mifos.application.customer.util.helpers;

import java.io.Serializable;

import org.mifos.framework.util.valueobjects.ReturnType;

/**
 * @author ashishsm
 *
 */
public class LoanCycleCounter  implements Serializable{

	private String offeringName;
	
	private int counter;
	
	public LoanCycleCounter(){
		
	}
	
	public LoanCycleCounter(String offeringName){
		this.offeringName = offeringName;
	}
	
	public LoanCycleCounter(String offeringName,int counter){
		this.offeringName = offeringName;
		this.counter = counter;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public String getOfferingName() {
		return offeringName;
	}

	public void setOfferingName(String offeringName) {
		this.offeringName = offeringName;
	}
	
	/** 
	 * This method return true even if the offering name is same , it does not bother about the counter value.
	 */
	public boolean equals(Object obj){
		if(null != obj){
			LoanCycleCounter otherObj = (LoanCycleCounter)obj;
			return this.offeringName.equals(otherObj.offeringName);
		}
		return super.equals(obj);
		
	}

	public void incrementCounter() {
		this.counter++;
		
	}
}
