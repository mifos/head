package org.mifos.application.messagecustomizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

@NamedQueries( {
	  @NamedQuery(
			    name="allMessages",
			    query="from CustomizedText"
			  ),
	  @NamedQuery(
	          name = "findCustomMessageByOldMessage",
	          query = "from CustomizedText message where message.originalText=:originalText"
	  )	  
})

	  @NamedNativeQuery(
	    name="allMessagesNative",
	    query="select * from customized_text order by char_length(original_text) desc",
	    resultClass = CustomizedText.class
	  )

@Entity
@Table(name = "customized_text")
public class CustomizedText {

    @Id
	private String originalText;
	private String customText;
	
	protected CustomizedText() {}
	
	public CustomizedText(String originalText, String customText) {
		super();
		this.originalText = originalText;
		this.customText = customText;
	}

	public String getOriginalText() {
		return originalText;
	}

	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}

	public String getCustomText() {
		return customText;
	}

	public void setCustomText(String customText) {
		this.customText = customText;
	}
	

}
