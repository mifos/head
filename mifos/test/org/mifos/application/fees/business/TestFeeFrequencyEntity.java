package org.mifos.application.fees.business;

import junit.framework.TestCase;

import org.mifos.application.fees.util.helpers.FeeFrequencyType;

public class TestFeeFrequencyEntity extends TestCase {

	public TestFeeFrequencyEntity() {
		super();
	}

	public TestFeeFrequencyEntity(String names) {
		super(names);
	}

	public void testBuildFrequencyForOneTimeFees() {
		FeeFrequencyEntity feeFrequencyEntity = new FeeFrequencyEntity();
		feeFrequencyEntity.getFeeFrequencyType().setFeeFrequencyTypeId(
				FeeFrequencyType.ONETIME.getValue());
		assertNotNull("The fee meeting should not be null", feeFrequencyEntity
				.getFeeMeetingFrequency());
		feeFrequencyEntity.buildFeeFrequency();
		assertNull("The fee meeting should be null", feeFrequencyEntity
				.getFeeMeetingFrequency());

	}

	public void testBuildFrequencyForPeriodicFees() {
		FeeFrequencyEntity feeFrequencyEntity = new FeeFrequencyEntity();
		feeFrequencyEntity.getFeeFrequencyType().setFeeFrequencyTypeId(
				FeeFrequencyType.PERIODIC.getValue());
		assertNotNull("The fee payment should not be null", feeFrequencyEntity
				.getFeePayment());
		feeFrequencyEntity.buildFeeFrequency();
		assertNull("The fee payment should be null", feeFrequencyEntity
				.getFeePayment());

	}
}
