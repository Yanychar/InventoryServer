package com.c2point.tools.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlTransient;

// @XmlTransient
@MappedSuperclass
public class SimplePojo {

	@Id
	@GeneratedValue( strategy=GenerationType.AUTO )
	@Column( name = "id", nullable = false, insertable = true, updatable = false )
	protected long id;
	
	@Column(nullable = false)
	@Version
	protected Long consistencyVersion;

	@Column(name = "deleted", nullable = false)
	private boolean deleted = false;
	
	@XmlTransient
	public long getId() {
		return id;
	}

	public void setId( long id ) {
		this.id = id;
	}
	
	@XmlTransient
	public Long getConsistencyVersion() {
		return consistencyVersion;
	}

	public void setConsistencyVersion(Long consistencyVersion) {
		this.consistencyVersion = consistencyVersion;
	}

	@XmlTransient
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted() {
		setDeleted( true );
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof SimplePojo ) {
			if ((( SimplePojo ) obj).getId() <= 0 && this.getId() <= 0 && obj == this) {
				return true;
			} else if ((( SimplePojo ) obj).getId() > 0 && ((( SimplePojo ) obj).getId() == getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Long.toString( getId()).hashCode();
	}

	
	public void setDeleted( boolean deleted ) {
		this.deleted = deleted;
	}
	
	
}
