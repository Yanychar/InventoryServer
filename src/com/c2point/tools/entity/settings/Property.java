package com.c2point.tools.entity.settings;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import com.c2point.tools.entity.SimplePojo;
import com.c2point.tools.entity.organisation.Organisation;

@NamedQueries({
	@NamedQuery( name = "getProperties", 
		query = "SELECT property FROM Property property " +
					"WHERE property.organisation = :org AND " +
					"property.deleted = false"
	),
	@NamedQuery( name = "getProperty", 
	query = "SELECT property FROM Property property " +
				"WHERE property.organisation = :org AND " +
				"property.name = :name AND " +
				"property.deleted = false"
),
})

@Entity
public class Property  extends SimplePojo {
	@ManyToOne
	private Organisation 	organisation;

	@Enumerated( EnumType.STRING )
	private PropertyType	type;

	private String			name;
	private String			value;

	protected Property() { 
	}
	public Property( Organisation org ) {
		super();
		setOrganisation( org );
	}
	
	public Property( Organisation org, String name, PropertyType type, String value ) {
		super();
		setOrganisation( org );
		setName( name );
		setType( type );
		setValue( value );
	}
	
	public Organisation getOrganisation() { return organisation; }
	public void setOrganisation( Organisation organisation ) { this.organisation = organisation; }

	public PropertyType getType() { return this.type; }
	public void setType( PropertyType type ) { this.type = type; }
	
	public String getName() { return name; }
	public void setName( String name ) { this.name = name; }
	
	public String getValue() { return value; }
	public void setValue( String value ) { this.value = value; }

}
