/**
 *
 */
package org.mifos.application.customer.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author sumeethaec
 *
 */
public class CustomerChecklist extends ValueObject {
	private int checklistId;
	private String text;
	private int answerType;
	/**
	 * Method which returns the answerType
	 * @return Returns the answerType.
	 */
	public int getAnswerType() {
		return answerType;
	}
	/**
	 * Method which sets the answerType
	 * @param answerType The answerType to set.
	 */
	public void setAnswerType(int answerType) {
		this.answerType = answerType;
	}
	/**
	 * Method which returns the checklistId
	 * @return Returns the checklistId.
	 */
	public int getChecklistId() {
		return checklistId;
	}
	/**
	 * Method which sets the checklistId
	 * @param checklistId The checklistId to set.
	 */
	public void setChecklistId(int checklistId) {
		this.checklistId = checklistId;
	}
	/**
	 * Method which returns the text
	 * @return Returns the text.
	 */
	public String getText() {
		return text;
	}
	/**
	 * Method which sets the text
	 * @param text The text to set.
	 */
	public void setText(String text) {
		this.text = text;
	}
}
