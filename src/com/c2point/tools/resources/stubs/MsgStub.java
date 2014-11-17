package com.c2point.tools.resources.stubs;

import javax.xml.bind.annotation.XmlType;

import org.joda.time.format.DateTimeFormat;

import com.c2point.tools.entity.msg.Message;
import com.c2point.tools.entity.msg.MessageStatus;
import com.c2point.tools.entity.msg.MessageType;

@XmlType(propOrder = { "id", "type", "status", "date", "from", "to", "item", "text" })
public class MsgStub {
	
	private long	id;
	
	private MessageType		type;
	private MessageStatus	status;

    private String			date;
    
	private OrgUserStub		from;
	private OrgUserStub		to;
	
	private ToolItemStub	item;
	
	private String			text;
	
	protected MsgStub() {}

	public MsgStub( Message msg ) {
		
		setId( msg.getId());

		setType( msg.getType());
		setStatus( msg.getStatus());
		setDate( msg.getDate().toString( DateTimeFormat.forPattern( "ddMMyyyy" )));
		setFrom( new OrgUserStub( msg.getFrom()));
		setTo( new OrgUserStub( msg.getTo()));
		setItem( msg.getItem() != null ? new ToolItemStub( msg.getItem()) : null );
		setText( msg.getText());
		
	}

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

    public MessageType getType() { return type; }
	public void setType( MessageType type ) { this.type = type; }

	public MessageStatus getStatus() { return status; }
	public void setStatus(MessageStatus status) { this.status = status; }

	public String getDate() { return date; }
	public void setDate( String date ) { this.date = date; }

	public OrgUserStub getFrom() { return from; }
	public void setFrom( OrgUserStub from ) { this.from = from; }

	public OrgUserStub getTo() { return to; }
	public void setTo( OrgUserStub to ) { this.to = to; }

	public ToolItemStub getItem() { return item; }
	public void setItem( ToolItemStub item ) { this.item = item; }

	public String getText() { return text; }
	public void setText(String text) { this.text = text; }


	
	
	
	
	public String toString() {
		
		String output = 
				"MessageStub[" + getId() + ", '" + getType() + "', " + getStatus() + "]"
				+ ( getDate() != null ? "\n  Date: "+getDate() : "" )
				+ ( getFrom() != null ? "\n  From: "+getFrom() : "" )
				+ ( getTo() != null ? "\n  To:"+getTo() : "" )
				+ ( getItem() != null ? "\n  Tool Item:"+getItem() : "" )
				+ ( getText() != null ? "\n  Msg Text:"+getText() : "" )
				;
		
		return output;
	}
	
	
	
}
