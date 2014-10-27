package com.c2point.tools.resources.stubs;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import com.c2point.tools.entity.msg.Message;

@XmlRootElement
public class MsgListStub extends ArrayList<MsgStub> {

	private static final long serialVersionUID = 1L;

	public MsgListStub() {
		
	}

	public MsgListStub( Collection<Message> list ) {
		
		for ( Message item : list ) {
			
			add( new MsgStub( item ));
		}
		
	}

	public String toString() {
		
		String output = "";
		
		for ( MsgStub member : this ) {
			output = output + member.toString() + "\n" ;
		}
		
		return output;
	}
	
}
