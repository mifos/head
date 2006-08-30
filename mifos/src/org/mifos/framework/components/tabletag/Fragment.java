/**
 * 
 */
package org.mifos.framework.components.tabletag;

public class Fragment {

	public Fragment() {
	}
	
	/** Used to set the value of fragmentName */
	private String fragmentName;

	/** Used to set the value of fragmentType */
	private String fragmentType;

/** Used to set the value of fragmentName */
	private String bold;

	/** Used to set the value of fragmentType */
	private String italic;

	/**
	 * @return Returns the bold.
	 */
	public String getBold() {
		return bold;
	}

	/**
	 * @param bold The bold to set.
	 */
	public void setBold(String bold) {
		this.bold = bold;
	}

	/**
	 * @return Returns the fragmentName.
	 */
	public String getFragmentName() {
		return fragmentName;
	}

	/**
	 * @param fragmentName The fragmentName to set.
	 */
	public void setFragmentName(String fragmentName) {
		this.fragmentName = fragmentName;
	}

	/**
	 * @return Returns the fragmentType.
	 */
	public String getFragmentType() {
		return fragmentType;
	}

	/**
	 * @param fragmentType The fragmentType to set.
	 */
	public void setFragmentType(String fragmentType) {
		this.fragmentType = fragmentType;
	}

	/**
	 * @return Returns the italic.
	 */
	public String getItalic() {
		return italic;
	}

	/**
	 * @param italic The italic to set.
	 */
	public void setItalic(String italic) {
		this.italic = italic;
	}

}
