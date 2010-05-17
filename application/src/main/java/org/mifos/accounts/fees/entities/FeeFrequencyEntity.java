package org.mifos.accounts.fees.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.exceptions.FeeException;
import org.mifos.accounts.fees.util.helpers.FeeConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.business.AbstractEntity;

@Entity
@Table(name = "FEE_FREQUENCY")
public class FeeFrequencyEntity extends AbstractEntity {

    @Id
    @GeneratedValue
    @Column(name = "FEE_FREQUENCY_ID", nullable = false)
    private final Short feeFrequencyId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FEE_FREQUENCYTYPE_ID")
    private final FeeFrequencyTypeEntity feeFrequencyType;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FREQUENCY_PAYMENT_ID")
    private final FeePaymentEntity feePayment;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FEE_ID")
    private final FeeEntity fee;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FREQUENCY_MEETING_ID")
    private final MeetingBO feeMeetingFrequency;

    public FeeFrequencyEntity() {
        this.feeFrequencyId = null;
        this.feeFrequencyType = null;
        this.feePayment = null;
        this.fee = null;
        this.feeMeetingFrequency = null;
    }

    public FeeFrequencyEntity(FeeFrequencyTypeEntity feeFrequencyType, FeeEntity fee, FeePaymentEntity feePayment,
            MeetingBO feeFrequency) throws FeeException {
        validateFields(feeFrequencyType, fee, feePayment, feeFrequency);
        this.feeFrequencyId = null;
        this.feeFrequencyType = feeFrequencyType;
        this.fee = fee;
        this.feePayment = feePayment;
        this.feeMeetingFrequency = feeFrequency;
    }

    public FeeFrequencyTypeEntity getFeeFrequencyType() {
        return feeFrequencyType;
    }

    public MeetingBO getFeeMeetingFrequency() {
        return feeMeetingFrequency;
    }

    public FeePaymentEntity getFeePayment() {
        return feePayment;
    }

    public boolean isPeriodic() {
        return getFeeFrequencyType().isPeriodic();
    }

    public boolean isOneTime() {
        return getFeeFrequencyType().isOneTime();
    }

    public boolean isTimeOfDisbursement() {
        return isOneTime() && getFeePayment().isTimeOfDisbursement();
    }

    private void validateFields(FeeFrequencyTypeEntity frequencyType, FeeEntity fee, FeePaymentEntity feePayment,
            MeetingBO feeFrequency) throws FeeException {
        if (fee == null) {
            throw new FeeException(FeeConstants.INVALID_FEE);
        }
        if (frequencyType == null) {
            throw new FeeException(FeeConstants.INVALID_FEE_FREQUENCY_TYPE);
        }
        if (frequencyType.isOneTime() && feePayment == null) {
            throw new FeeException(FeeConstants.INVALID_FEE_PAYEMENT_TYPE);
        }
        if (frequencyType.isPeriodic() && feeFrequency == null) {
            throw new FeeException(FeeConstants.INVALID_FEE_FREQUENCY);
        }
    }
}