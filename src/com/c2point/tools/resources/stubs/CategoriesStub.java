package com.c2point.tools.resources.stubs;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.c2point.tools.entity.tool.Category;

@XmlRootElement
public class CategoriesStub extends ArrayList<CategoryStub> {

	private static final long serialVersionUID = 1L;

	public CategoriesStub() {
		
	}

	public CategoriesStub( List<Category> list ) {
		
		for ( Category cat : list ) {
			
			add( new CategoryStub( cat ));
		}
		
	}

	public String toString( boolean withChilds ) {
		
		return toString( withChilds, 0 );
	}
	
	private String toString( boolean withChilds, int ident ) {
		
		String output = "";
		
		for ( CategoryStub member : this ) {
			output = output + member.toString( withChilds ) + "\n" ;
		}
		
		return output;
	}
	
}
