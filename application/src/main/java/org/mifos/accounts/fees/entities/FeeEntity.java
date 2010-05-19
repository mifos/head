package org.mifos.accounts.fees.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.business.FeeStatusEntity;
import org.mifos.accounts.fees.exceptions.FeeException;
import org.mifos.accounts.fees.util.helpers.FeeChangeType;
import org.mifos.accounts.fees.util.helpers.FeeConstants;
import org.mifos.accounts.fees.util.helpers.FeeLevel;
import org.mifos.accounts.fees.util.helpers.RateAmountFlag;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.framework.exceptions.PropertyNotFoundException;

@Entity
@Table(name = "FEES")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="DISCRIMINATOR",discriminatorType=DiscriminatorType.STRING)
@NamedQueries( {
  @NamedQuery(
    name="FeeEntity.retrieveCustomerFees",
    query="from FeeEntity fee where fee.categoryType.id in (1,2,3,4) order by fee.feeName"
  ),
  @NamedQuery(
    name="FeeEntity.retrieveProductFees",
    query="from FeeEntity fee where fee.categoryType.id in (5) order by fee.feeName"
  )
})
public abstract class FeeEntity extends org.mifos.framework.business.AbstractEntity {
    /**
    * NOTE: This is a replacement entity for FeeBO. Not be used with HibernateUtil.
    * Don't code with any org.mifos.framework.persistence.Persistence instance.
    * This entity is just a POJO, mapping to FEES table.
    * This entity does not have any behavior, nor it manages any transaction on its own!
    * Fee can't be created without feeFrequency. You will have to call setFeeFrequency()
    * before creating a new fee or saving a fee.
    */
    @Id
    @GeneratedValue
    @Column(name = "FEE_ID", nullable = false)
    private Short feeId;

    @Column(name = "FEE_NAME")
    private String feeName;

    @Column(name = "UPDATE_FLAG")
    private Short changeType;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "CREATED_BY")
    private Short createdBy;

    @Column(name = "UPDATED_DATE")
    private Date updatedDate;

    @Column(name = "UPDATED_BY")
    private Short updatedBy;

    @Version
    @Column(name = "VERSION_NO")
    private Integer versionNo;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="STATUS")
    private FeeStatusEntity feeStatus;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="OFFICE_ID")
    private OfficeBO office;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="CATEGORY_ID")
    private CategoryTypeEntity categoryType;

    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL,optional=true,mappedBy="fee")
    @PrimaryKeyJoinColumn
    private FeeFrequencyEntity feeFrequency;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="GLCODE_ID")
    private GLCodeEntity glCode;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FEE_ID", updatable = false)
    private Set<FeeLevelEntity> feeLevels;

    @Transient
    private boolean customerDefaultFee;

    /**
     * default constructor is hibernate's requirement and should not be
     * used to create a valid Fee object.
     */
    protected FeeEntity() {
        this.feeId = null;
        this.office = null;
        this.feeName = null;
        this.categoryType = null;
        this.feeFrequency = null;
        this.glCode = null;
        this.feeLevels = null;
    }

    public FeeEntity(String feeName, CategoryTypeEntity categoryType, GLCodeEntity glCode,
            boolean isCustomerDefaultFee, OfficeBO office) throws FeeException {

        validateFeeName(feeName);
        validateGLCode(glCode);
        validateCategory(categoryType);

        /** Note: Fee can't be created without feeFrequency.
         * Call setFeeFrequency() before creating a new fee or saving a fee.
         * This has been done to remove references to FeePayment and MeetingBO*/
        //this.feeFrequency = new FeeFrequencyEntity(feeFrequencyType, this, feePayment, null);

        this.feeName = feeName;
        this.categoryType = categoryType;
        this.glCode = glCode;

        this.feeId = null;
        this.feeLevels = new HashSet<FeeLevelEntity>();
        this.office = office;
        this.changeType = FeeChangeType.NOT_UPDATED.getValue();
        //FIXME: see mifos-dev list discussion. Should this be replaced with just a column in fee_table?
        this.customerDefaultFee = isCustomerDefaultFee;
    }


    public void addFeeLevel(FeeLevel level) {
        feeLevels.add(new FeeLevelEntity(this, level));
    }

    public Short getChangeType() {
        return this.changeType;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public Short getCreatedBy() {
        return this.createdBy;
    }

    public Date getUpdatedDate() {
        return this.updatedDate;
    }

    public Short getUpdatedBy() {
        return this.updatedBy;
    }

    public Integer getVersionNo() {
        return this.versionNo;
    }

    public FeeStatusEntity getFeeStatus() {
        return this.feeStatus;
    }

    public OfficeBO getOffice() {
        return this.office;
    }

    public CategoryTypeEntity getCategoryType() {
        return this.categoryType;
    }

    public FeeFrequencyEntity getFeeFrequency() {
        return this.feeFrequency;
    }

    public GLCodeEntity getGlCode() {
        return this.glCode;
    }

    public Set<FeeLevelEntity> getFeeLevels() {
        return this.feeLevels;
    }

    public Short getFeeId() {
        return this.feeId;
    }

    public String getFeeName() {
        return this.feeName;
    }

    private void validateCategory(final CategoryTypeEntity categoryType) throws FeeException {
        if (categoryType == null) {
            throw new FeeException(FeeConstants.INVALID_FEE_CATEGORY);
        }
    }

    private void validateGLCode(final GLCodeEntity glCode) throws FeeException {
        if (glCode == null) {
            throw new FeeException(FeeConstants.INVALID_GLCODE);
        }
    }


    private void validateFeeName(String feeName) throws FeeException {
        if (StringUtils.isBlank(feeName)) {
            throw new FeeException(FeeConstants.INVALID_FEE_NAME);
        }
    }

    public FeeChangeType getFeeChangeType() throws PropertyNotFoundException {
        return FeeChangeType.getFeeChangeType(this.changeType);
    }

    public void updateFeeChangeType(final FeeChangeType updateFlag) {
        this.changeType = updateFlag.getValue();
    }

    /** FIXME: createdData, createdBy, freeFrequency, feeStatus fields are set from outside
     * Not a good practice for entities that are to be possibly immutable. However,
     * 1) for feeFrequency and feeStatus fields its done to remove external dependencies
     * 2) for createdData, createdBy fields, its done to eventually assist in
     * removing such audit info from entity persistence
     * */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setCreatedBy(Short createdBy) {
        this.createdBy = createdBy;
    }

    public void setFeeFrequency(FeeFrequencyEntity feeFrequency) {
        this.feeFrequency = feeFrequency;
    }

    public void setFeeStatus(FeeStatusEntity feeStatus) {
        this.feeStatus = feeStatus;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void setUpdatedBy(Short updatedBy) {
        this.updatedBy = updatedBy;
    }

    public boolean isCustomerDefaultFee() {
        //FIXME: What is the need for Feelevel?
        //return this.customerDefaultFee;
        return (feeLevels != null) ? (feeLevels.size() > 0) : false;
    }

}