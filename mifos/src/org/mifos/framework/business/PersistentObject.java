package org.mifos.framework.business;

import java.io.Serializable;
import java.util.Date;

public abstract class PersistentObject implements Serializable{
	
	protected Date createdDate;
	protected Short createdBy;
	protected Date updatedDate;
	protected Short updatedBy;  
	protected Integer versionNo;
	
	public Short getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Short createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Short getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(Short updatedBy) {
		this.updatedBy = updatedBy;
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	public Integer getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}
}
