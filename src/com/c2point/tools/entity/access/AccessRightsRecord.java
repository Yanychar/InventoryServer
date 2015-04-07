package com.c2point.tools.entity.access;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.SimplePojo;
import com.c2point.tools.entity.person.OrgUser;

@Entity
@NamedQueries({
	@NamedQuery(name = "findAccessRecords", query = 
			"SELECT record FROM AccessRightsRecord record " +
				"WHERE record.user = :user AND record.deleted = false ORDER BY record.function ASC"),
})
public class AccessRightsRecord extends SimplePojo {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( AccessRightsRecord.class.getName()); 

	private OrgUser				user;
	
	@Enumerated( EnumType.ORDINAL )
	private FunctionalityType	function;
	@Enumerated( EnumType.ORDINAL )
	private OwnershipType		type;
	@Enumerated( EnumType.ORDINAL )
	private PermissionType		permission;
	
	
	public AccessRightsRecord() {
		super();
		
	}

	public AccessRightsRecord( OrgUser user, FunctionalityType function, OwnershipType type, PermissionType permission ) {
		
	}

	public OrgUser getUser() { return user; }
	public void setUser(OrgUser user) { this.user = user; }

	public FunctionalityType getFunction() { return function; }
	public void setFunction(FunctionalityType function) { this.function = function; }

	public OwnershipType getType() { return type; }
	public void setType(OwnershipType type) { this.type = type; }

	public PermissionType getPermission() { return permission; }
	public void setPermission(PermissionType permission) { this.permission = permission; }
	
	public String toString() {
		return "Access Record: ( " + getFunction() + ", " + getType() + ", " + getPermission() + " )";
	}
	
}
