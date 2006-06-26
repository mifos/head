package org.mifos.application.customer.client.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

public class ClientPosition extends ValueObject{
private int clientId;
private int titleId;
private int levelId;
public int getClientId() {
	return clientId;
}
public void setClientId(int clientId) {
	this.clientId = clientId;
}
public int getLevelId() {
	return levelId;
}
public void setLevelId(int levelId) {
	this.levelId = levelId;
}
public int getTitleId() {
	return titleId;
}
public void setTitleId(int titleId) {
	this.titleId = titleId;
}
}
