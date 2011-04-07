package org.mifos.application.messagecustomizer;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.ejb.QueryHints;

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
	    resultClass = CustomizedText.class,
	    hints = { @QueryHint(name=QueryHints.HINT_CACHEABLE,value="true") }
	  )

@Entity
@Table(name = "customized_text")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
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
