package org.mifos.application.customer.group;

public class BasicGroupInfo {
	Integer groupId;
	Short branchId;
	String searchId;
	String groupName;
	
	public BasicGroupInfo()
	{
	}
	
	public BasicGroupInfo(Integer groupId, Short branchId, String searchId, String groupName)
	{
		this.groupId = groupId;
		this.branchId = branchId;
		this.searchId = searchId;
		this.groupName = groupName;
	}
	
	public Short getBranchId() {
		return branchId;
	}
	public void setBranchId(Short branchId) {
		this.branchId = branchId;
	}
	public Integer getGroupId() {
		return groupId;
	}
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	public String getSearchId() {
		return searchId;
	}
	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	

}
