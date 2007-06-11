package org.mifos.application.master.business;

import java.io.Serializable;

/**
 * An individual item from a database based list (aka lookup values). 
 * This class serves the same role as the {@link CustomValueListElement} class,
 * but it is used for both fixed an custom lists.
 */
public class BusinessActivityEntity implements Serializable{

	private Integer id;

	private String name;

	public BusinessActivityEntity(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
