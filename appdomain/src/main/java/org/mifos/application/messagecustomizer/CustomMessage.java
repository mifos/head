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
			    query="from CustomMessage"
			  ),
	  @NamedQuery(
	          name = "findCustomMessageByOldMessage",
	          query = "from CustomMessage message where message.oldMessage=:oldMessage"
	  )	  
})

	  @NamedNativeQuery(
	    name="allMessagesNative",
	    query="select * from custom_message order by char_length(old_message) desc",
	    resultClass = CustomMessage.class
	  )

@Entity
@Table(name = "custom_message")
public class CustomMessage {

    @Id
	private String oldMessage;
	private String newMessage;
	
	public CustomMessage(String oldMessage, String newMessage) {
		this.oldMessage = oldMessage;
		this.newMessage = newMessage;
	}
	
	protected CustomMessage() {}

	public String getOldMessage() {
		return oldMessage;
	}

	public void setOldMessage(String oldMessage) {
		this.oldMessage = oldMessage;
	}

	public String getNewMessage() {
		return newMessage;
	}

	public void setNewMessage(String newMessage) {
		this.newMessage = newMessage;
	}
	
	
}
