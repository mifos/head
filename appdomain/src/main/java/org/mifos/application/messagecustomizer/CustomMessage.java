package org.mifos.application.messagecustomizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries( {
	  @NamedQuery(
	    name="allMessages",
	    query="from CustomMessage "
	  ),
	  @NamedQuery(
	          name = "findCustomMessageByOldMessage",
	          query = "from CustomMessage message where message.oldMessage=:oldMessage"
	  )	  
})

@Entity
@Table(name = "custom_message")
public class CustomMessage {

    @Id
    @GeneratedValue
    @Column(name="id")
    private Short id;	
	private String oldMessage;
	private String newMessage;
	
	public CustomMessage(String oldMessage, String newMessage) {
		this.oldMessage = oldMessage;
		this.newMessage = newMessage;
	}

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
