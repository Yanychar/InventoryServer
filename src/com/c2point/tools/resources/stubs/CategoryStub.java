package com.c2point.tools.resources.stubs;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import com.c2point.tools.entity.tool.Category;

@XmlType(propOrder = { "id", "code", "name", "childs" })
public class CategoryStub {
	
	private long	id;
	private String	code;
	private String	name;

	@XmlElement( name = "childs" )
	private List<CategoryStub>	childs;
	
	protected CategoryStub() {}

	public CategoryStub( Category category ) {
		
		setId( category.getId());
		setCode( category.getCode());
		setName( category.getName());
		
		List<Category>	origChilds = category.getChilds();
		if ( origChilds != null && origChilds.size() > 0 ) {
			
			setChilds( new ArrayList<CategoryStub>());
			
			for ( Category cat : origChilds ) {
				
				getChilds().add( new CategoryStub( cat ));
			}
		}
		
	}

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	public String getCode() { return code;}
	public void setCode(String code) { this.code = code; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public List<CategoryStub> getChilds() { return childs; }
	public void setChilds(List<CategoryStub> childs) { this.childs = childs; }
	

	public String toString() {
		
		return toString( false, 0 );
	}

	public String toString( boolean withChilds ) {
		
		return toString( withChilds, 0 );
	}
	
	private String toString( boolean withChilds, int ident ) {
		
		String output = 
				StringUtils.repeat( ' ', ident )
				+ "CategoryStub[" + getId() + ", '" + getCode() + "', " + getName() + "]. "
				+ "Num of childs: " + ( childs != null ? childs.size() : 0 );
		
		if ( childs != null && childs.size() > 0 ) {
			for ( CategoryStub member : childs ) {
				output = output + member.toString( withChilds ) + "\n" ;
			}
		}
		
		return output;
	}
	
	
	
}
