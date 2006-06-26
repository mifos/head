package org.mifos.framework.util.valueobjects;

import java.io.Serializable;

public class SearchResults implements Serializable, MasterType {
	
	private String resultName;
	private Object value;
	
	public SearchResults(){
		
	}
	public String getResultName() {
		return resultName;
	}
	public void setResultName(String name) {
		this.resultName = name;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
	public SearchResults(String resultName , Object value){
		this.resultName = resultName;
		this.value = value;
	}

}
