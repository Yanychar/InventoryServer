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
					"user.deleted = false"
	),
	@NamedQuery( name = "listUsersDeleted", 
		query = "SELECT user FROM OrgUser user " +
					"WHERE user.organisation = :org AND " +
					"user.deleted = true"
	),
	@NamedQuery( name = "listUsersAll", 
		query = "SELECT user FROM OrgUser user " +
					"WHERE user.organisation = :org"
	),
	@NamedQuery( name = "listByFIO", 
		query = "SELECT user FROM OrgUser user " +
					"WHERE user.organisation = :org AND " +
					  "lower( user.firstName ) = lower( :firstname ) AND " + 
					  "lower( user.lastName ) = lower( :lastname )"
	),
})
public class OrgUser extends Person {

	@ManyToOne
	private Organisation organisation;

	public Organisation getOrganisation() { return organisation; }
	public void setOrganisation( Organisation organisation ) { this.organisation = organisation; }
	
	
	
}
