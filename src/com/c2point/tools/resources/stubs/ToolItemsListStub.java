package com.c2point.tools.resources.stubs;

import java.util.ArrayList;
import java.util.Collection;
import javax.xml.bind.annotation.XmlRootElement;

import com.c2point.tools.entity.repository.ToolItem;

@XmlRootElement
public class ToolItemsListStub extends ArrayList<ToolItemStub> {

	private static final long serialVersionUID = 1L;

	public ToolItemsListStub() {
		
	}

	public ToolItemsListStub( Collection<ToolItem> list ) {
		
		for ( ToolItem item : list ) {
			
			add( new ToolItemStub( item ));
		}
		
	}

	public String toString() {
		
		String output = "";
		
		for ( ToolItemStub member : this ) {
			output = output + member.toString() + "\n" ;
		}
		
		return output;
	}
	
}
