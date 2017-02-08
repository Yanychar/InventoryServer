package com.c2point.tools.entity.tool;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.SimplePojo;
import com.c2point.tools.entity.organisation.Organisation;

@Entity
@NamedQueries({
	@NamedQuery( name = "listTop", 
		query = "SELECT category FROM Category category "
					+ "WHERE category.parent = null AND "
					+ "category.org = :org AND " 
					+ "category.deleted = false "
					+ "ORDER BY category.name ASC"
		),
	@NamedQuery( name = "listTopNotEmpty", 
		query = "SELECT category FROM Category category "
					+ "JOIN Tool tool "
					+ "WHERE category.parent = null AND "
					+ "category.org = :org AND "
					+ "tool.category = category AND "
					+ "category.deleted = false "
					+ "GROUP BY category.id "
					+ "ORDER BY category.name ASC"
		),
})

public class Category extends SimplePojo {

	private static Logger logger = LogManager.getLogger( Category.class.getName());

	private String code;
	private String name;
	
	@ManyToOne
	private Organisation org;
	
	@Transient
	private boolean	topCategoryFlag;  // Just for Top Category "All Categories" in UI
	
	// Now Parent-Childs attributes
	@ManyToOne
//    @JoinColumn(name="CUST_ID", nullable=false)	
	private Category		parent;
	
	@OneToMany( mappedBy = "parent",
			cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH },
			fetch=FetchType.LAZY )
	private List<Category>	childs;

	public Category() {
		super();
	}
	
	public Category( String name ) {
		
		this( "", name );
		
	}
	
	public Category( String code, String name ) {
		
		this( code, name, false );
		
	}
	
	public Category( String code, String name, boolean topCategory ) {
		super();
		
		setCode( code );
		setName( name );
		
		setTopCategoryFlag( topCategory );
	}
	
	public String getCode() { return code; }
	public void setCode( String code ) { this.code = code; }

	public String getName() { return name; }
	public void setName( String name ) { this.name = name; }

	public Category getParent() { return parent; }
	public void setParent( Category parent ) { this.parent = parent; }

	public List<Category> getChilds() { return childs; }
	public void setChilds(List<Category> childs) { this.childs = childs; }
	
	public boolean hasChilds() { return ( this.childs != null && this.childs.size() > 0 ); }

	public boolean isTopCategoryFlag() { return topCategoryFlag; }
	public void setTopCategoryFlag( boolean topCategoryFlag ) { this.topCategoryFlag = topCategoryFlag; }

	public Organisation getOrg() { return org; }
	public void setOrg( Organisation org ) { this.org = org; }

	public String toString( boolean withChilds ) {
		
		return toString( withChilds, 0 );
	}
	
	public String toString() {
		
		return toString( false, 0 );
	}

	private String toString( boolean withChilds, int ident ) {
		
		String output = 
				StringUtils.repeat( ' ', ident )
				+ "Category[" + getId() + ", '" + getCode() + "', " + getName() + "]. "
				+ ( isTopCategoryFlag() ? "Top Category. " : "" ) 
				+ "Num of childs: " + ( childs != null ? childs.size() : 0 );
		
		if ( withChilds ) {
			
			
			for ( Category child : getChilds()) {
				
				output = output 
						+ "\n"
						+ child.toString( true, ident + 2 );
				
			}
		}
		
		return output;
	}
	
}
