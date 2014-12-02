package com.c2point.tools.entity.person;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.c2point.tools.entity.organisation.Organisation;

@Entity
@NamedQueries({
	@NamedQuery( name = "listUsersCurrent", 
		query = "SELECT user FROM OrgUser user " +
					"WHERE user.organisation = :org AND " +
					"user.deleted = false " +
					"ORDER BY user.lastName ASC"
	),
	@NamedQuery( name = "listUsersDeleted", 
		query = "SELECT user FROM OrgUser user " +
					"WHERE user.organisation = :org AND " +
					"user.deleted = true " +
					"ORDER BY user.lastName ASC"
),
	@NamedQuery( name = "listUsersAll", 
		query = "SELECT user FROM OrgUser user " +
					"WHERE user.organisation = :org " +
					"ORDER BY user.lastName ASC"
),
	@NamedQuery( name = "listByFIO", 
		query = "SELECT user FROM OrgUser user " +
					"WHERE user.organisation = :org AND " +
					  "trim( lower( user.firstName )) = trim( lower( :firstname )) AND " + 
					  "trim( lower( user.lastName )) = trim( lower( :lastname )) " +
					"ORDER BY user.lastName ASC"
					  
	),
	@NamedQuery( name = "countAll", 
		query = "SELECT COUNT( user.id ) FROM OrgUser user " +
					"WHERE user.organisation = :org"
	),
})
public class OrgUser extends Person {

	@ManyToOne
	private Organisation organisation;

	public Organisation getOrganisation() { return organisation; }
	public void setOrganisation( Organisation organisation ) { this.organisation = organisation; }
	
	
	
}
